package de.k7labor.fahrwerk

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.k7labor.fahrwerk.data.Content
import de.k7labor.fahrwerk.data.ImportResult
import de.k7labor.fahrwerk.data.SagApplied
import de.k7labor.fahrwerk.data.SagInput
import de.k7labor.fahrwerk.data.SavedSetup
import de.k7labor.fahrwerk.data.SetupCodec
import de.k7labor.fahrwerk.data.Store
import de.k7labor.fahrwerk.physics.Checks
import de.k7labor.fahrwerk.physics.Controls
import de.k7labor.fahrwerk.physics.Frf
import de.k7labor.fahrwerk.physics.Physics
import de.k7labor.fahrwerk.physics.Profile
import de.k7labor.fahrwerk.physics.Rig
import de.k7labor.fahrwerk.physics.RunAll
import de.k7labor.fahrwerk.physics.ScenarioId
import de.k7labor.fahrwerk.physics.Setup
import de.k7labor.fahrwerk.physics.ShakeMode
import de.k7labor.fahrwerk.physics.fmt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.math.abs

enum class Tab(val title: String, val glyph: String) {
    SETUP("Setup", "⚙"), RIG("Prüfstand", "📈"), WORKSHOP("Werkstatt", "🔧"), LIBRARY("Setups", "💾"), DATA("Daten", "📋")
}

/** Ein Vergleichspartner: „Aktuell“, „Serie“ oder ein gespeichertes Setup (stabile ID, Review-Punkt 11). */
data class CmpRef(val id: String, val name: String)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class LabViewModel(app: Application) : AndroidViewModel(app) {
    private val store = Store(app)

    private val _setup = MutableStateFlow(store.loadSetup())
    val setup = _setup.asStateFlow()
    private val _speed = MutableStateFlow(store.speed)
    val speed = _speed.asStateFlow()
    private val _run = MutableStateFlow<RunAll?>(null)
    val run = _run.asStateFlow()
    private val _stockRun = MutableStateFlow<RunAll?>(null)
    val stockRun = _stockRun.asStateFlow()
    private val _busy = MutableStateFlow(true)
    val busy = _busy.asStateFlow()
    private val _library = MutableStateFlow(store.loadLibrary())
    val library = _library.asStateFlow()
    private val _sag = MutableStateFlow(store.loadSag())
    val sag = _sag.asStateFlow()
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 8)
    val messages = _messages.asSharedFlow()

    val tab = MutableStateFlow(Tab.SETUP)
    val scenario = MutableStateFlow(ScenarioId.BUMP)

    // Rüttelprüfstand: nur rechnen, wenn er offen ist
    val shakeMode = MutableStateFlow(ShakeMode.FRONT)
    val shakeActive = MutableStateFlow(false)
    private val _frf = MutableStateFlow<Pair<Frf, Frf>?>(null)
    val frf = _frf.asStateFlow()
    private val _frfBusy = MutableStateFlow(false)
    val frfBusy = _frfBusy.asStateFlow()

    // Empfehlung als Vorschau (erst nach Bestätigung übernehmen, Review-Punkt 17)
    private val _recommendation = MutableStateFlow<Physics.Recommendation?>(null)
    val recommendation = _recommendation.asStateFlow()
    private val _recBusy = MutableStateFlow(false)
    val recBusy = _recBusy.asStateFlow()

    // Vergleich
    val cmpA = MutableStateFlow(CmpRef("cur", "Aktuell"))
    val cmpB = MutableStateFlow(CmpRef("stock", "Serie"))
    private val _cmpRuns = MutableStateFlow<Map<String, RunAll>>(emptyMap())
    val cmpRuns = _cmpRuns.asStateFlow()

    private val _checks = MutableStateFlow<List<Checks.Check>>(emptyList())
    val checks = _checks.asStateFlow()

    private var stockKey: String? = null

    init {
        viewModelScope.launch {
            combine(_setup, _speed) { s, v -> s to v }.debounce(180).collectLatest { (s, v) ->
                _busy.value = true
                // currentCoroutineContext(): der Job dieses collectLatest-Durchlaufs. Ein bloßes
                // coroutineContext würde hier den äußeren launch-Job liefern, der nie abgebrochen wird.
                val job = currentCoroutineContext()[Job]
                val cancelled = { job?.isActive == false }
                val stockSt = s.toStockKeepLoad()
                val key = SetupCodec.export(stockSt) + v
                val res = withContext(Dispatchers.Default) {
                    val stock = if (key != stockKey || _stockRun.value == null) Physics.runAll(stockSt, v, cancelled = cancelled) else _stockRun.value!!
                    val cur = if (stockSt == s) stock else Physics.runAll(s, v, cancelled = cancelled)
                    stock to cur
                }
                if (cancelled()) return@collectLatest
                if (res.first.results.size == ScenarioId.entries.size) { _stockRun.value = res.first; stockKey = key }
                if (res.second.results.size == ScenarioId.entries.size) _run.value = res.second
                _busy.value = false
            }
        }
        viewModelScope.launch {
            combine(_setup, shakeMode, shakeActive) { s, m, a -> Triple(s, m, a) }.debounce(250).collectLatest { (s, m, a) ->
                if (!a) return@collectLatest
                _frfBusy.value = true
                val job = currentCoroutineContext()[Job]
                val cancelled = { job?.isActive == false }
                val r = withContext(Dispatchers.Default) {
                    Rig.shake(s, m, cancelled = cancelled) to Rig.shake(s.toStockKeepLoad(), m, cancelled = cancelled)
                }
                if (!cancelled()) { _frf.value = r; _frfBusy.value = false }
            }
        }
        viewModelScope.launch {
            combine(_setup, cmpA, cmpB, _library, _speed) { s, a, b, lib, v -> listOf(a, b) to Triple(s, lib, v) }.debounce(300).collectLatest { (refs, ctx) ->
                val (s, lib, v) = ctx
                val job = currentCoroutineContext()[Job]
                val map = mutableMapOf<String, RunAll>()
                for (r in refs) {
                    val st = resolve(r.id, s, lib) ?: continue
                    val res = withContext(Dispatchers.Default) { Physics.runAll(st, v, cancelled = { job?.isActive == false }) }
                    if (job?.isActive == false) return@collectLatest
                    map[r.id] = res
                }
                _cmpRuns.value = map
            }
        }
        viewModelScope.launch {
            _checks.value = withContext(Dispatchers.Default) { Checks.run() }
        }
    }

    /** Vergleichspartner auflösen; Beladung und Profil kommen immer vom aktuellen Setup. */
    fun resolve(id: String, cur: Setup = _setup.value, lib: List<SavedSetup> = _library.value): Setup? = when (id) {
        "cur" -> cur
        "stock" -> cur.toStockKeepLoad()
        else -> lib.firstOrNull { it.id == id }?.setup?.let { saved ->
            saved.withAll(Controls.all.filter { it.isLoad }.associate { it.key to cur[it.key] }).copy(profile = cur.profile)
        }
    }

    private fun update(s: Setup) {
        _setup.value = s
        store.saveSetup(s)
        _recommendation.value = null
    }

    fun set(key: String, v: Double) = update(_setup.value.with(key, v))

    fun step(key: String, dir: Int) {
        val c = Controls.get(key)
        set(key, _setup.value[key] + c.step * dir)
    }

    fun setProfile(p: Profile) = update(_setup.value.copy(profile = p))

    fun setSpeed(v: Double) { val x = (Math.round(v / 10) * 10.0).coerceIn(30.0, 250.0); _speed.value = x; store.speed = x }

    fun loadStock() { update(_setup.value.toStockKeepLoad()); toast("Serieneinstellung geladen (Beladung und Profil bleiben)") }

    fun requestRecommendation() {
        if (_recBusy.value) return
        _recBusy.value = true
        viewModelScope.launch {
            // Ändert sich das Setup während der Rechnung, neu rechnen: sonst würde „Übernehmen“
            // die inzwischen geänderten Werte (z. B. Fahrergewicht) stillschweigend zurücksetzen.
            var s: Setup
            var v: Double
            var r: Physics.Recommendation
            do {
                s = _setup.value; v = _speed.value
                r = withContext(Dispatchers.Default) { Physics.recommend(s, v) }
            } while (s != _setup.value || v != _speed.value)
            _recommendation.value = r
            _recBusy.value = false
        }
    }

    fun dismissRecommendation() { _recommendation.value = null }

    fun applyRecommendation() {
        val r = _recommendation.value ?: return
        update(r.setup)
        toast("Empfehlung übernommen. Mit Serie vergleichen und vorsichtig probefahren.")
    }

    /** Diagnose-Testknopf: meldet ehrlich, wenn der Einsteller schon am Ende ist (Review-Punkt 6). */
    fun applyDiag(a: Content.Action) {
        val c = Controls.get(a.key)
        val before = _setup.value[a.key]
        val after = c.sanitize(before + a.delta)
        if (abs(after - before) < 1e-9) {
            toast("${a.label}: geht nicht, „${c.label}“ steht schon bei ${fmt(before, c.decimals)} ${c.unit} (Grenze).")
            return
        }
        update(_setup.value.with(a.key, after))
        val partial = abs(after - before - a.delta) > 1e-9
        toast("${a.label}: jetzt ${fmt(after, c.decimals)} ${c.unit}${if (partial) " (nur bis zur Grenze möglich)" else ""}")
    }

    fun showScenario(id: ScenarioId) { scenario.value = id; tab.value = Tab.RIG }

    // --- Durchhang ---
    fun setSag(s: SagInput) { _sag.value = s; store.saveSag(s) }

    /** Zuletzt übernommene Durchhang-Empfehlung je Achse, gespeichert wie die Messwerte selbst. */
    private val _sagApplied = MutableStateFlow(store.loadSagApplied())
    val sagApplied = _sagApplied.asStateFlow()

    /**
     * Die Messung gehört zur Vorspannung [from]. Nach dem Übernehmen bezieht sich die Empfehlung weiter
     * auf diesen Wert, bis neu gemessen wird; sonst würde dieselbe Messung die Änderung erneut vorschlagen.
     */
    fun applySag(front: Boolean, measured: List<Double>, from: Double, newValue: Double) {
        val key = if (front) "fPre" else "rPre"
        set(key, newValue)
        _sagApplied.update { it + (front to SagApplied(measured, from, _setup.value[key])) }
        store.saveSagApplied(_sagApplied.value)
        toast("Vorspannung im Modell angepasst")
    }

    // --- Bibliothek ---
    fun saveToLibrary(name: String) {
        val n = name.trim().ifBlank { "Setup ${_library.value.size + 1}" }.take(60)
        val e = SavedSetup(UUID.randomUUID().toString(), n, _setup.value, System.currentTimeMillis())
        _library.update { it + e }
        store.saveLibrary(_library.value)
        toast("„$n“ gespeichert")
    }

    fun loadFromLibrary(id: String) {
        val e = _library.value.firstOrNull { it.id == id } ?: return
        update(e.setup)
        toast("„${e.name}“ geladen")
    }

    fun deleteFromLibrary(id: String) {
        val e = _library.value.firstOrNull { it.id == id } ?: return
        _library.update { l -> l.filterNot { it.id == id } }
        store.saveLibrary(_library.value)
        if (cmpA.value.id == id) cmpA.value = CmpRef("cur", "Aktuell")
        if (cmpB.value.id == id) cmpB.value = CmpRef("stock", "Serie")
        toast("„${e.name}“ gelöscht")
    }

    fun exportCurrent(): String = SetupCodec.export(_setup.value, "K7 Setup")

    fun importText(text: String, saveAsNew: Boolean): ImportResult {
        val r = SetupCodec.parse(text, _setup.value)
        val s = r.setup
        if (s != null) {
            update(s)
            if (saveAsNew) {
                val e = SavedSetup(UUID.randomUUID().toString(), r.name ?: "Import", s, System.currentTimeMillis())
                _library.update { it + e }
                store.saveLibrary(_library.value)
            }
            toast(if (r.messages.isEmpty()) "Setup importiert" else "Setup importiert, ${r.messages.size} Hinweis(e)")
        }
        return r
    }

    fun summaryText(): String {
        val s = _setup.value
        val r = _run.value
        val sb = StringBuilder("K7 Fahrwerkslabor – Setup\n")
        sb.append("Profil: ${s.profile.label}\n")
        for (c in Controls.all) sb.append("${c.group.title} · ${c.label}: ${fmt(s[c.key], c.decimals)} ${c.unit}${if (!c.isLoad && s.isChanged(c.key)) "  (Serie ${fmt(c.stock, c.decimals)})" else ""}\n")
        if (r != null) {
            sb.append("Durchhang mit Fahrer: vorn ${fmt(r.sags.sagF, 1)} mm, hinten ${fmt(r.sags.sagR, 1)} mm\n")
            sb.append("Setup-Index: ${fmt(r.overall, 0)} / 100\n")
        }
        sb.append("Modellrechnung, kein Ersatz für Probefahrt und Werkstatt.")
        return sb.toString()
    }

    fun toast(m: String) { _messages.tryEmit(m) }
}
