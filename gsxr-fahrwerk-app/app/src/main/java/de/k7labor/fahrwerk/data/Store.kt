package de.k7labor.fahrwerk.data

import android.content.Context
import de.k7labor.fahrwerk.physics.Setup
import org.json.JSONArray
import org.json.JSONObject

/** Lokale Speicherung auf dem Handy (keine Internetverbindung, kein Konto). */
class Store(ctx: Context) {
    private val prefs = ctx.getSharedPreferences("k7lab", Context.MODE_PRIVATE)

    fun loadSetup(): Setup {
        val txt = prefs.getString("current", null) ?: return Setup.STOCK
        return SetupCodec.parse(txt, Setup.STOCK).setup ?: Setup.STOCK
    }

    fun saveSetup(s: Setup) { prefs.edit().putString("current", SetupCodec.export(s)).apply() }

    fun loadLibrary(): List<SavedSetup> = SetupCodec.libraryFromJson(prefs.getString("library", null))

    fun saveLibrary(lib: List<SavedSetup>) { prefs.edit().putString("library", SetupCodec.libraryToJson(lib)).apply() }

    fun loadSag(): SagInput {
        val txt = prefs.getString("sag", null) ?: return SagInput()
        return try {
            val o = JSONObject(txt)
            fun d(k: String, def: Double) = o.optDouble(k, def).takeIf { it.isFinite() } ?: def
            val dflt = SagInput()
            SagInput(d("L1", dflt.l1), d("L2", dflt.l2), d("L3", dflt.l3), d("R1", dflt.r1), d("R2", dflt.r2), d("R3", dflt.r3))
        } catch (e: Exception) { SagInput() }
    }

    fun saveSag(s: SagInput) {
        val o = JSONObject().put("L1", s.l1).put("L2", s.l2).put("L3", s.l3).put("R1", s.r1).put("R2", s.r2).put("R3", s.r3)
        prefs.edit().putString("sag", o.toString()).apply()
    }

    /** Übernommene Durchhang-Empfehlungen überleben einen Neustart, sonst schlüge dieselbe Messung die Änderung erneut vor. */
    fun loadSagApplied(): Map<Boolean, SagApplied> {
        val txt = prefs.getString("sagApplied", null) ?: return emptyMap()
        return try {
            val o = JSONObject(txt)
            val out = mutableMapOf<Boolean, SagApplied>()
            for ((name, front) in listOf("front" to true, "rear" to false)) {
                val e = o.optJSONObject(name) ?: continue
                val arr = e.optJSONArray("measured") ?: continue
                val measured = List(arr.length()) { arr.optDouble(it) }
                val from = e.optDouble("from")
                val to = e.optDouble("to")
                if (measured.size == 3 && measured.all { it.isFinite() } && from.isFinite() && to.isFinite()) out[front] = SagApplied(measured, from, to)
            }
            out
        } catch (e: Exception) { emptyMap() }
    }

    fun saveSagApplied(m: Map<Boolean, SagApplied>) {
        val o = JSONObject()
        for ((front, a) in m) {
            if (!(a.measured.all { it.isFinite() } && a.from.isFinite() && a.to.isFinite())) continue
            o.put(if (front) "front" else "rear", JSONObject().put("measured", JSONArray(a.measured)).put("from", a.from).put("to", a.to))
        }
        prefs.edit().putString("sagApplied", o.toString()).apply()
    }

    var speed: Double
        get() = prefs.getFloat("speed", 80f).toDouble().coerceIn(30.0, 250.0)
        set(v) { prefs.edit().putFloat("speed", v.toFloat()).apply() }
}

/** Zuletzt übernommene Durchhang-Empfehlung einer Achse: Messwerte und Vorspannung vorher/nachher. */
data class SagApplied(val measured: List<Double>, val from: Double, val to: Double)

/** Durchhang-Messwerte in mm: 1 = ausgefedert, 2 = ohne Fahrer, 3 = mit Fahrer. */
data class SagInput(
    val l1: Double = 180.0, val l2: Double = 157.0, val l3: Double = 144.0,
    val r1: Double = 600.0, val r2: Double = 589.0, val r3: Double = 568.0
)
