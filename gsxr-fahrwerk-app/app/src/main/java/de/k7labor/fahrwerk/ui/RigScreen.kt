package de.k7labor.fahrwerk.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import de.k7labor.fahrwerk.LabViewModel
import de.k7labor.fahrwerk.physics.K7
import de.k7labor.fahrwerk.physics.Physics
import de.k7labor.fahrwerk.physics.Rig
import de.k7labor.fahrwerk.physics.ScenarioId
import de.k7labor.fahrwerk.physics.ScenarioResult
import de.k7labor.fahrwerk.physics.ShakeMode
import de.k7labor.fahrwerk.physics.fmt
import de.k7labor.fahrwerk.physics.Vehicle
import de.k7labor.fahrwerk.ui.bike.BikePart
import de.k7labor.fahrwerk.ui.bike.BikeScene
import de.k7labor.fahrwerk.ui.bike.Knob
import de.k7labor.fahrwerk.ui.bike.PartChips
import de.k7labor.fahrwerk.ui.bike.PartPanel
import de.k7labor.fahrwerk.ui.bike.SceneHint
import de.k7labor.fahrwerk.ui.bike.SceneState
import de.k7labor.fahrwerk.data.Content
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription

private val RIG_TABS = listOf("Fahrt", "Rütteln", "Dämpfer", "Feder")

@Composable
fun RigScreen(vm: LabViewModel) {
    var sub by rememberSaveable { mutableIntStateOf(0) }
    Column(Modifier.fillMaxWidth()) {
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)) {
            RIG_TABS.forEachIndexed { i, t ->
                SegmentedButton(selected = sub == i, onClick = { sub = i }, shape = SegmentedButtonDefaults.itemShape(i, RIG_TABS.size)) { Text(t, maxLines = 1) }
            }
        }
        when (sub) {
            0 -> DriveRig(vm)
            1 -> ShakeRig(vm)
            2 -> DamperRig(vm)
            else -> SpringRig(vm)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DriveRig(vm: LabViewModel) {
    val run by vm.run.collectAsState()
    val stock by vm.stockRun.collectAsState()
    val busy by vm.busy.collectAsState()
    val st by vm.setup.collectAsState()
    val speed by vm.speed.collectAsState()
    val scen by vm.scenario.collectAsState()
    val lab = LocalLabColors.current
    val res = run?.results?.get(scen)
    val sres = stock?.results?.get(scen)

    val calm by vm.calm.collectAsState()
    var playing by remember { mutableStateOf(!calm) }
    var exaggerate by rememberSaveable { mutableStateOf(false) }
    var rate by rememberSaveable { mutableFloatStateOf(0.25f) }
    var technical by rememberSaveable { mutableStateOf(false) }
    var ghostOn by rememberSaveable { mutableStateOf(true) }
    var selName by rememberSaveable { mutableStateOf<String?>(null) }
    val selected = selName?.let { n -> BikePart.entries.firstOrNull { it.name == n } }
    var tPlay by remember { mutableFloatStateOf(0f) }
    var speedDraft by remember(speed) { mutableFloatStateOf(speed.toFloat()) }
    // Fahrzeug immer zu dem Setup bauen, mit dem der gezeigte Verlauf gerechnet wurde
    val runSt = run?.setup ?: st
    val stockRunSt = stock?.setup ?: st.toStockKeepLoad()
    val veh = remember(runSt) { Vehicle(runSt, Physics.calibration) }
    val stockVeh = remember(stockRunSt) { Vehicle(stockRunSt, Physics.calibration) }
    LaunchedEffect(calm) { if (calm) playing = false }

    // Genau eine Animationsschleife, sie läuft nur solange dieser Bildschirm sichtbar ist (Review-Punkte 14/15)
    LaunchedEffect(playing, res) {
        if (!playing || res == null) return@LaunchedEffect
        var last = 0L
        while (true) {
            withFrameNanos { now ->
                if (last != 0L) {
                    val dt = (now - last) / 1e9f * rate
                    tPlay += dt
                    val tMax = res.trace.t[res.trace.size - 1]
                    if (tPlay > tMax) tPlay = 0f
                }
                last = now
            }
        }
    }

    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            if (busy) LinearProgressIndicator(Modifier.fillMaxWidth())
            SectionCard("Fahrsituation") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (id in ScenarioId.entries) FilterChip(selected = scen == id, onClick = { vm.scenario.value = id; tPlay = 0f }, label = { Text(id.short) })
                }
                BodyText(scen.title + ": " + scen.info)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Tempo ${fmt(speedDraft.toDouble(), 0)} km/h", style = MaterialTheme.typography.bodyMedium)
                    Slider(
                        value = speedDraft, onValueChange = { speedDraft = it }, onValueChangeFinished = { vm.setSpeed(speedDraft.toDouble()) },
                        valueRange = 30f..250f, steps = 21, modifier = Modifier.weight(1f).padding(start = 8.dp)
                    )
                }
            }
        }
        item {
            SectionCard("Prüfstand-Ansicht") {
                if (res == null) BodyText("Rechnet…") else {
                    val tr = res.trace
                    val tMax = tr.t[tr.size - 1]
                    val frame = frameAt(tr.t, tr.size, tPlay)
                    val k = if (exaggerate) 3.0 else 1.0
                    val real = SceneState.fromTrace(res, frame)
                    val drawn = if (exaggerate) SceneState.fromTrace(res, frame, k) else real
                    val ghost = if (ghostOn && sres != null) stockVeh to SceneState.fromTrace(sres, frameAt(sres.trace.t, sres.trace.size, tPlay), k) else null
                    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                        SegmentedButton(selected = !technical, onClick = { technical = false }, shape = SegmentedButtonDefaults.itemShape(0, 2)) { Text("Außen") }
                        SegmentedButton(selected = technical, onClick = { technical = true }, shape = SegmentedButtonDefaults.itemShape(1, 2)) { Text("Technik") }
                    }
                    BikeScene(
                        veh, drawn, real, res.road, k, ghost, technical, selected, { selName = it?.name },
                        onScrub = { d -> tPlay = (tPlay + d * tMax).coerceIn(0f, tMax); playing = false },
                        exaggerated = exaggerate
                    )
                    SceneHint("Bauteil oder weißen Einsteller antippen · waagrecht wischen = Zeit · zwei Finger = Zoom")
                    Slider(value = tPlay.coerceIn(0f, tMax), onValueChange = { tPlay = it; playing = false }, valueRange = 0f..tMax,
                        modifier = Modifier.semantics { contentDescription = "Zeitleiste"; stateDescription = "${fmt(tPlay.toDouble(), 2)} von ${fmt(tMax.toDouble(), 2)} Sekunden" })
                    val dtS = if (tr.size > 1) tr.t[1] - tr.t[0] else 0.01f
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilledTonalIconButton(onClick = { playing = false; tPlay = (tPlay - dtS).coerceAtLeast(0f) }, modifier = Modifier.semantics { contentDescription = "Ein Bild zurück" }) { Text("⏮") }
                        FilledTonalButton(onClick = { playing = !playing }) { Text(if (playing) "Pause" else "Abspielen") }
                        FilledTonalIconButton(onClick = { playing = false; tPlay = (tPlay + dtS).coerceAtMost(tMax) }, modifier = Modifier.semantics { contentDescription = "Ein Bild vor" }) { Text("⏭") }
                        Text("${fmt(tPlay.toDouble(), 3)} s", style = MaterialTheme.typography.labelLarge)
                    }
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        for ((r, l) in listOf(1f to "Echtzeit", 0.25f to "¼", 0.1f to "⅒")) FilterChip(selected = rate == r, onClick = { rate = r }, label = { Text(l) })
                    }
                    FlowRow(verticalArrangement = Arrangement.Center, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) { Switch(checked = ghostOn, onCheckedChange = { ghostOn = it }); Text(" Serie als Geisterbild") }
                        Row(verticalAlignment = Alignment.CenterVertically) { Switch(checked = exaggerate, onCheckedChange = { exaggerate = it }); Text(" Bewegung ×3") }
                        Row(verticalAlignment = Alignment.CenterVertically) { Switch(checked = calm, onCheckedChange = { vm.setCalm(it) }); Text(" Ruhige Darstellung") }
                    }
                    if (calm) BodyText("Ruhige Darstellung: Nichts läuft von selbst. Mit „Abspielen“, der Zeitleiste oder Wischen im Bild weiterschalten.")
                    Spacer(Modifier.height(6.dp))
                    PartChips(selected) { selName = it?.name }
                }
            }
        }
        if (res != null && selected != null) item {
            val frame = frameAt(res.trace.t, res.trace.size, tPlay)
            PartPanel(selected, veh, SceneState.fromTrace(res, frame), st, { k, v -> vm.set(k, v) }, { k, d -> vm.step(k, d) })
        }
        if (res != null && sres != null) {
            item { KpiTable(res, sres) }
            item {
                SectionCard("Federweg") {
                    val n = res.trace.size
                    LineChart(
                        listOf(
                            Series("Gabel", res.trace.t.toD(n), res.trace.sF.toD(n), lab.front),
                            Series("Heck (Rad)", res.trace.t.toD(n), res.trace.xR.toD(n), lab.rear),
                            Series("Gabel Serie", sres.trace.t.toD(sres.trace.size), sres.trace.sF.toD(sres.trace.size), lab.front.copy(alpha = 0.55f), dashed = true),
                            Series("Heck Serie", sres.trace.t.toD(sres.trace.size), sres.trace.xR.toD(sres.trace.size), lab.rear.copy(alpha = 0.55f), dashed = true)
                        ), "Zeit s", "Federweg mm", yMin = -5.0, yMax = 135.0, cursorX = tPlay.toDouble(), onScrub = { tPlay = it.toFloat(); playing = false },
                        hLines = listOf(K7.TRAVEL_F_MM to "Gabel Ende 125", K7.TRAVEL_R_MM to "Heck Ende 130")
                    )
                }
            }
            item {
                SectionCard("Radlast") {
                    val n = res.trace.size
                    LineChart(
                        listOf(
                            Series("vorn", res.trace.t.toD(n), DoubleArray(n) { res.trace.fzF[it] / 1000.0 }, lab.front),
                            Series("hinten", res.trace.t.toD(n), DoubleArray(n) { res.trace.fzR[it] / 1000.0 }, lab.rear),
                            Series("vorn Serie", sres.trace.t.toD(sres.trace.size), DoubleArray(sres.trace.size) { sres.trace.fzF[it] / 1000.0 }, lab.front.copy(alpha = 0.5f), dashed = true),
                            Series("hinten Serie", sres.trace.t.toD(sres.trace.size), DoubleArray(sres.trace.size) { sres.trace.fzR[it] / 1000.0 }, lab.rear.copy(alpha = 0.5f), dashed = true)
                        ), "Zeit s", "Radlast kN", yMin = 0.0, cursorX = tPlay.toDouble(), onScrub = { tPlay = it.toFloat(); playing = false }, yDecimals = 1
                    )
                    BodyText("Je ruhiger die Radlast, desto mehr Haftung. Null heißt: Rad ohne Bodenkontakt.")
                }
            }
            item {
                SectionCard("Nicken und Tempo") {
                    val n = res.trace.size
                    LineChart(
                        listOf(
                            Series("Nickwinkel °", res.trace.t.toD(n), res.trace.pitch.toD(n), lab.extra),
                            Series("Serie", sres.trace.t.toD(sres.trace.size), sres.trace.pitch.toD(sres.trace.size), lab.stock, dashed = true)
                        ), "Zeit s", "Nicken ° (+ = Front hoch)", cursorX = tPlay.toDouble(), onScrub = { tPlay = it.toFloat(); playing = false }, yDecimals = 1, height = 150.dp
                    )
                    if (scen == ScenarioId.BRAKE || scen == ScenarioId.FULLBRAKE || scen == ScenarioId.ACCEL) {
                        LineChart(
                            listOf(
                                Series("Schlupf vorn %", res.trace.t.toD(n), DoubleArray(n) { res.trace.slipF[it] * 100.0 }, lab.front),
                                Series("Schlupf hinten %", res.trace.t.toD(n), DoubleArray(n) { res.trace.slipR[it] * 100.0 }, lab.rear)
                            ), "Zeit s", "Reifenschlupf % (− = Bremsen)", cursorX = tPlay.toDouble(), onScrub = { tPlay = it.toFloat(); playing = false }, height = 150.dp
                        )
                        BodyText("Haftmaximum bei rund ±13 % Schlupf. Unter −25 % gilt das Rad als blockierend, über +25 % als durchdrehend.")
                    }
                }
            }
        }
        item {
            val r = run; val s = stock
            if (r != null && s != null) SectionCard("Alle Situationen") {
                TableRow(listOf("Situation", "Note", "Serie", "Gabel", "Heck"), header = true)
                for (id in ScenarioId.entries) {
                    val a = r.results[id] ?: continue
                    val b = s.results[id] ?: continue
                    Row(Modifier.clickable(role = Role.Button, onClickLabel = "Situation anzeigen") { vm.scenario.value = id; tPlay = 0f }) {
                        TableRow(
                            listOf(id.short + if (!id.scored) " *" else "", fmt(a.score, 0), fmt(b.score, 0), "${fmt(a.front.usage * 100, 0)} %", "${fmt(a.rear.usage * 100, 0)} %"),
                            colors = listOf(null, scoreColor(a.score), null, null, null)
                        )
                    }
                }
                BodyText("* Vollbremsung zeigt den Grenzbereich und fließt nicht in den Setup-Index ein. Antippen zeigt die Situation.")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

/** Index des ersten Bildes mit Zeit ≥ t (binäre Suche). */
private fun frameAt(ts: FloatArray, n: Int, t: Float): Int {
    if (n <= 0) return 0
    if (t <= ts[0]) return 0
    if (t >= ts[n - 1]) return n - 1
    var lo = 0; var hi = n - 1
    while (hi - lo > 1) { val m = (lo + hi) / 2; if (ts[m] < t) lo = m else hi = m }
    return hi
}

@Composable
private fun scoreColor(v: Double) = LocalLabColors.current.let { if (v >= 80) it.good else if (v >= 62) it.warn else it.bad }

@Composable
private fun KpiTable(r: ScenarioResult, s: ScenarioResult) {
    SectionCard("Messwerte", trailing = { ScorePill(r.score) }) {
        TableRow(listOf("Größe", "Aktuell", "Serie"), header = true)
        fun row(name: String, a: String, b: String) = Triple(name, a, b)
        val rows = mutableListOf(
            row("Gabel genutzt", "${fmt(r.front.maxX, 0)} mm", "${fmt(s.front.maxX, 0)} mm"),
            row("Heck genutzt (Rad)", "${fmt(r.rear.maxX, 0)} mm", "${fmt(s.rear.maxX, 0)} mm"),
            row("Radlastschwankung vorn", "${fmt(r.front.cv * 100, 0)} %", "${fmt(s.front.cv * 100, 0)} %"),
            row("Radlastschwankung hinten", "${fmt(r.rear.cv * 100, 0)} %", "${fmt(s.rear.cv * 100, 0)} %"),
            row("Ohne Bodenkontakt v / h", "${fmt(r.front.liftMs, 0)} / ${fmt(r.rear.liftMs, 0)} ms", "${fmt(s.front.liftMs, 0)} / ${fmt(s.rear.liftMs, 0)} ms"),
            row("Sitzbeschleunigung (Effektivwert)", "${fmt(r.veh.seatRms, 2)} m/s²", "${fmt(s.veh.seatRms, 2)} m/s²"),
            row("Nickwinkel min / max", "${fmt(r.veh.minPitch, 1)} / ${fmt(r.veh.maxPitch, 1)}°", "${fmt(s.veh.minPitch, 1)} / ${fmt(s.veh.maxPitch, 1)}°"),
            row("Beruhigungszeit v / h", "${fmt(r.front.settle, 2)} / ${fmt(r.rear.settle, 2)} s", "${fmt(s.front.settle, 2)} / ${fmt(s.rear.settle, 2)} s")
        )
        if (r.id == ScenarioId.BRAKE || r.id == ScenarioId.FULLBRAKE) {
            rows += row("Mittlere Verzögerung", "${fmt(r.veh.meanDecel, 2)} g", "${fmt(s.veh.meanDecel, 2)} g")
            rows += row("Bremsweg im Verlauf", "${fmt(r.veh.distance, 1)} m", "${fmt(s.veh.distance, 1)} m")
            rows += row("Vorderrad blockiert", "${fmt(r.veh.frontLockMs, 0)} ms", "${fmt(s.veh.frontLockMs, 0)} ms")
            rows += row("Hinterrad abgehoben", "${fmt(r.veh.stoppieMs, 0)} ms", "${fmt(s.veh.stoppieMs, 0)} ms")
        }
        if (r.id == ScenarioId.ACCEL) {
            rows += row("Vorderrad abgehoben", "${fmt(r.veh.wheelieMs, 0)} ms", "${fmt(s.veh.wheelieMs, 0)} ms")
            rows += row("Hinterrad dreht durch", "${fmt(r.veh.spinMs, 0)} ms", "${fmt(s.veh.spinMs, 0)} ms")
            rows += row("Endtempo", "${fmt(r.veh.vEnd, 0)} km/h", "${fmt(s.veh.vEnd, 0)} km/h")
        }
        for ((a, b, c) in rows) TableRow(listOf(a, b, c))
        Spacer(Modifier.height(6.dp))
        TableRow(listOf("Teilnoten v / h", "Aktuell", "Serie"), header = true)
        TableRow(listOf("Komfort", fmt(r.sF.comfort, 0), fmt(s.sF.comfort, 0)))
        TableRow(listOf("Haftung", "${fmt(r.sF.grip, 0)} / ${fmt(r.sR.grip, 0)}", "${fmt(s.sF.grip, 0)} / ${fmt(s.sR.grip, 0)}"))
        TableRow(listOf("Kontrolle", "${fmt(r.sF.control, 0)} / ${fmt(r.sR.control, 0)}", "${fmt(s.sF.control, 0)} / ${fmt(s.sR.control, 0)}"))
        TableRow(listOf("Reserve", "${fmt(r.sF.reserve, 0)} / ${fmt(r.sR.reserve, 0)}", "${fmt(s.sF.reserve, 0)} / ${fmt(s.sR.reserve, 0)}"))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ShakeRig(vm: LabViewModel) {
    val mode by vm.shakeMode.collectAsState()
    val frf by vm.frf.collectAsState()
    val busy by vm.frfBusy.collectAsState()
    val lab = LocalLabColors.current
    DisposableEffect(Unit) { vm.shakeActive.value = true; onDispose { vm.shakeActive.value = false } }
    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            SectionCard("Rüttelprüfstand") {
                BodyText("Das Motorrad steht mit beiden Rädern auf Hydraulikstempeln. Die Stempel bewegen sich sinusförmig mit ±${fmt(Rig.AMP_MM, 0)} mm von 0,5 bis 25 Hz. Gemessen wird, was am Sitz und an den Reifen ankommt, jeweils je mm Anregung.")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (m in ShakeMode.entries) FilterChip(selected = mode == m, onClick = { vm.shakeMode.value = m }, label = { Text(m.title) })
                }
                if (busy) { LinearProgressIndicator(Modifier.fillMaxWidth().padding(top = 6.dp)); BodyText("Prüflauf mit 30 Frequenzen läuft…") }
            }
        }
        val pair = frf
        if (pair != null) {
            val (cur, stk) = pair
            item {
                SectionCard("Sitzbeschleunigung (Komfort)") {
                    LineChart(listOf(Series("Aktuell", cur.freq, cur.seat, lab.extra), Series("Serie", stk.freq, stk.seat, lab.stock, dashed = true)), "Frequenz Hz", "m/s² je mm", logX = true, yMin = 0.0, yDecimals = 2)
                    val (fb, vb) = cur.peak(cur.seat, 0.8, 6.0)
                    BodyText("Spitze im Aufbau-Bereich bei ${fmt(fb, 1)} Hz (${fmt(vb, 2)} m/s² je mm). Kleiner ist komfortabler. Menschen sind bei 4–8 Hz senkrecht am empfindlichsten.")
                }
            }
            item {
                SectionCard("Radlastschwankung (Haftung)") {
                    LineChart(
                        listOf(
                            Series("vorn", cur.freq, cur.tyreF, lab.front), Series("hinten", cur.freq, cur.tyreR, lab.rear),
                            Series("vorn Serie", stk.freq, stk.tyreF, lab.front.copy(alpha = 0.5f), dashed = true), Series("hinten Serie", stk.freq, stk.tyreR, lab.rear.copy(alpha = 0.5f), dashed = true)
                        ), "Frequenz Hz", "% der Radlast je mm", logX = true, yMin = 0.0, yDecimals = 1
                    )
                    BodyText("Kleiner ist besser: Der Reifen drückt gleichmäßiger auf die Straße. Ein Anstieg über 10 Hz kommt vom Rad selbst (Radeigenfrequenz); dort hilft weichere High-Speed-Druckstufe oder weniger Luftdruck.")
                }
            }
            item {
                SectionCard("Federweg je mm Anregung") {
                    LineChart(
                        listOf(
                            Series("Gabel", cur.freq, cur.travelF, lab.front), Series("Heck", cur.freq, cur.travelR, lab.rear),
                            Series("Gabel Serie", stk.freq, stk.travelF, lab.front.copy(alpha = 0.5f), dashed = true), Series("Heck Serie", stk.freq, stk.travelR, lab.rear.copy(alpha = 0.5f), dashed = true)
                        ), "Frequenz Hz", "mm je mm", logX = true, yMin = 0.0, yDecimals = 1
                    )
                    val (ff, _) = cur.peak(cur.travelF, 0.8, 5.0)
                    val (fr, _) = cur.peak(cur.travelR, 0.8, 5.0)
                    BodyText("Aufbau-Eigenfrequenz: vorn ${fmt(ff, 2)} Hz, hinten ${fmt(fr, 2)} Hz. Eine hohe, spitze Überhöhung heißt wenig Dämpfung; flach und breit heißt viel Dämpfung.")
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

/** Drehknöpfe direkt am Prüfstand: Kurve ändert sich beim Drehen sofort. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun KnobRow(vm: LabViewModel, st: de.k7labor.fahrwerk.physics.Setup, keys: List<String>) {
    FlowRow(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
        for (k in keys) {
            val c = de.k7labor.fahrwerk.physics.Controls.get(k)
            Knob(c, st[k], k in Content.UNSURE_STOCK, { vm.set(k, it) }, { vm.step(k, it) })
        }
    }
}

@Composable
private fun DamperRig(vm: LabViewModel) {
    val st by vm.setup.collectAsState()
    val lab = LocalLabColors.current
    val stock = remember(st) { st.toStockKeepLoad() }
    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            SectionCard("Dämpferprüfstand") {
                BodyText("Kraft über Kolbengeschwindigkeit, wie auf einem Dämpferprüfstand. Rechts Druckstufe (Einfedern), links Zugstufe (Ausfedern). Gabel: beide Holme zusammen, entlang der Gabel. Federbein: an der Kolbenstange.")
            }
        }
        for (front in listOf(true, false)) item {
            val cur = remember(st, front) { Rig.damperCurve(st, front) }
            val stk = remember(stock, front) { Rig.damperCurve(stock, front) }
            val loop = remember(st, front) { Rig.damperLoop(st, front, if (front) 40.0 else 20.0, 2.0) }
            val loopS = remember(stock, front) { Rig.damperLoop(stock, front, if (front) 40.0 else 20.0, 2.0) }
            val col = if (front) lab.front else lab.rear
            SectionCard(if (front) "Gabel (beide Holme)" else "Federbein") {
                KnobRow(vm, st, if (front) listOf("fReb", "fLS", "fHS") else listOf("rReb", "rLS", "rHS"))
                LineChart(
                    listOf(Series("Aktuell", cur.first, DoubleArray(cur.second.size) { cur.second[it] / 1000 }, col), Series("Serie", stk.first, DoubleArray(stk.second.size) { stk.second[it] / 1000 }, lab.stock, dashed = true)),
                    "Kolbengeschwindigkeit m/s", "Kraft kN", yDecimals = 1
                )
                LineChart(
                    listOf(Series("Aktuell", loop.first, DoubleArray(loop.second.size) { loop.second[it] / 1000 }, col), Series("Serie", loopS.first, DoubleArray(loopS.second.size) { loopS.second[it] / 1000 }, lab.stock, dashed = true)),
                    "Hub mm", "Kraft kN", yDecimals = 1, height = 170.dp
                )
                BodyText("Kraft-Weg-Schleife bei ${if (front) "40" else "20"} mm Hub und 2 Hz. Die Fläche der Schleife ist die Energie, die der Dämpfer pro Takt in Wärme umwandelt.")
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun SpringRig(vm: LabViewModel) {
    val st by vm.setup.collectAsState()
    val run by vm.run.collectAsState()
    val lab = LocalLabColors.current
    val stock = remember(st) { st.toStockKeepLoad() }
    LazyColumn(Modifier.fillMaxWidth()) {
        item { SectionCard("Federprüfstand") { BodyText("Federkraft über Federweg. Gabel: Stahlfedern beider Holme plus Luftpolster (steigt am Ende stark an, Ölstand verschiebt das). Heck: am Rad gemessen, mit progressiver Umlenkung. Der letzte Bereich ist der Anschlag.") } }
        for (front in listOf(true, false)) item {
            val cur = remember(st, front) { Rig.springCurve(st, front) }
            val stk = remember(stock, front) { Rig.springCurve(stock, front) }
            val col = if (front) lab.front else lab.rear
            SectionCard(if (front) "Gabel" else "Federbein (am Rad)") {
                KnobRow(vm, st, if (front) listOf("fPre", "kF", "fOil") else listOf("rPre", "kR", "rShim"))
                LineChart(
                    listOf(Series("Aktuell", cur.first, DoubleArray(cur.second.size) { cur.second[it] / 1000 }, col), Series("Serie", stk.first, DoubleArray(stk.second.size) { stk.second[it] / 1000 }, lab.stock, dashed = true)),
                    "Federweg mm", "Kraft kN", yMin = 0.0, yDecimals = 1
                )
                run?.let { r ->
                    BodyText(if (front) "Statisch mit Fahrer: ${fmt(r.sags.sagF, 1)} mm, ohne Fahrer ${fmt(r.sags.freeF, 1)} mm." else "Statisch mit Fahrer: ${fmt(r.sags.sagR, 1)} mm, ohne Fahrer ${fmt(r.sags.freeR, 1)} mm.")
                }
                if (front) BodyText("Kalibrierte Grundvorspannung je Feder in Serie: ${fmt(Physics.calibration.p0F, 1)} mm (Annahme aus der Durchhang-Kalibrierung).")
                else BodyText("Kalibrierte Grundvorspannung Federbein in Serie: ${fmt(Physics.calibration.p0R, 1)} mm.")
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}
