package de.k7labor.fahrwerk.ui

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.k7labor.fahrwerk.LabViewModel
import de.k7labor.fahrwerk.data.Content
import de.k7labor.fahrwerk.data.SagAdvice
import de.k7labor.fahrwerk.data.SagInput
import de.k7labor.fahrwerk.physics.Profile
import de.k7labor.fahrwerk.physics.fmt

@Composable
fun WorkshopScreen(vm: LabViewModel) {
    var sub by rememberSaveable { mutableIntStateOf(0) }
    Column(Modifier.fillMaxWidth()) {
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)) {
            listOf("Durchhang messen", "Diagnose").forEachIndexed { i, t ->
                SegmentedButton(selected = sub == i, onClick = { sub = i }, shape = SegmentedButtonDefaults.itemShape(i, 2)) { Text(t) }
            }
        }
        if (sub == 0) SagTab(vm) else DiagTab(vm)
    }
}

@Composable
private fun SagTab(vm: LabViewModel) {
    val st by vm.setup.collectAsState()
    val sag by vm.sag.collectAsState()
    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            SectionCard("So misst du den Durchhang") {
                BodyText("Zu zweit messen, Motorrad auf ebenem Boden. Vorn: Abstand Staubabstreifer bis Achsklemme (entlang des Gleitrohrs). Hinten: senkrecht von der Hinterachse zu einem festen Punkt am Heck.")
                BodyText("1 = ganz ausgefedert (Rad entlastet, z. B. Montageständer). 2 = Motorrad steht ohne Fahrer auf den Rädern. 3 = mit Fahrer in Fahrposition und voller Ausrüstung. Jeweils leicht einfedern und langsam zurückkommen lassen, Mittelwert aus „von oben“ und „von unten“ nehmen (Reibung).")
                Spacer(Modifier.height(6.dp))
                TableRow(listOf("Profil", "Mit Fahrer v", "Mit Fahrer h"), header = true)
                for (p in Profile.entries) TableRow(listOf(p.label + if (p == st.profile) " ◀" else "", "${fmt(p.sagWinF.first, 0)}–${fmt(p.sagWinF.second, 0)} mm", "${fmt(p.sagWinR.first, 0)}–${fmt(p.sagWinR.second, 0)} mm"))
                BodyText("Ohne Fahrer (frei): vorn 20–30 mm, hinten 5–15 mm.")
            }
        }
        item { SagCard(vm, true, sag, st.profile, st.fPre) }
        item { SagCard(vm, false, sag, st.profile, st.rPre) }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun SagCard(vm: LabViewModel, front: Boolean, sag: SagInput, profile: Profile, currentPre: Double) {
    val v = if (front) listOf(sag.l1, sag.l2, sag.l3) else listOf(sag.r1, sag.r2, sag.r3)
    val labels = listOf("1 ausgefedert", "2 ohne Fahrer", "3 mit Fahrer")
    // Texte separat halten, damit Kommas und halbe Eingaben nicht sofort überschrieben werden
    val texts = remember(front) { mutableStateOf(v.map { fmt(it, 1) }) }
    val parsed = texts.value.map { it.replace(',', '.').trim().toDoubleOrNull() ?: Double.NaN }
    val r = SagAdvice.evaluate(front, parsed[0], parsed[1], parsed[2], profile, currentPre)
    val lab = LocalLabColors.current
    SectionCard(if (front) "Gabel vorn" else "Federbein hinten") {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            for (i in 0..2) {
                OutlinedTextField(
                    value = texts.value[i],
                    onValueChange = { nv ->
                        val l = texts.value.toMutableList(); l[i] = nv.take(8); texts.value = l
                        val num = nv.replace(',', '.').trim().toDoubleOrNull()
                        if (num != null && num.isFinite()) {
                            val s = when {
                                front && i == 0 -> sag.copy(l1 = num); front && i == 1 -> sag.copy(l2 = num); front -> sag.copy(l3 = num)
                                i == 0 -> sag.copy(r1 = num); i == 1 -> sag.copy(r2 = num); else -> sag.copy(r3 = num)
                            }
                            vm.setSag(s)
                        }
                    },
                    label = { Text(labels[i]) }, suffix = { Text("mm") }, singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        if (!r.valid) Text(r.error ?: "", color = lab.bad, style = MaterialTheme.typography.bodyMedium)
        else {
            Row(Modifier.fillMaxWidth()) {
                KeyValue("Frei", "${fmt(r.free, 1)} mm", Modifier.weight(1f), sub = "Ziel ${fmt(r.freeWindow.first, 0)}–${fmt(r.freeWindow.second, 0)}")
                KeyValue("Mit Fahrer", "${fmt(r.rider, 1)} mm", Modifier.weight(1f), sub = "Ziel ${fmt(r.window.first, 0)}–${fmt(r.window.second, 0)}")
                KeyValue("Abweichung", "${if (r.delta >= 0) "+" else ""}${fmt(r.delta, 1)} mm", Modifier.weight(1f), sub = "zu ${fmt(r.target, 0)} mm")
            }
            Text(r.action, style = MaterialTheme.typography.bodyMedium)
            r.springNote?.let { Text(it, color = lab.warn, style = MaterialTheme.typography.bodySmall) }
            val nv = r.newValue
            Button(onClick = { if (nv != null) vm.applySag(front, nv) }, enabled = nv != null && kotlin.math.abs(nv - currentPre) > 1e-9, modifier = Modifier.padding(top = 6.dp)) { Text("Ins Modell übernehmen") }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DiagTab(vm: LabViewModel) {
    var selected by rememberSaveable { mutableStateOf(listOf<String>()) }
    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            SectionCard("Was fällt dir beim Fahren auf?") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (d in Content.DIAG) FilterChip(selected = d.id in selected, onClick = { selected = if (d.id in selected) selected - d.id else selected + d.id }, label = { Text(d.title) })
                }
                if (selected.isEmpty()) BodyText("Tipp: Vor der Fahrt einen Kabelbinder um ein Gabelgleitrohr und um die Kolbenstange des Federbeins legen. Danach zeigt er, wie viel Federweg du wirklich nutzt.")
            }
        }
        for (d in Content.DIAG.filter { it.id in selected }) item(key = d.id) {
            SectionCard(d.title) {
                Text("Wahrscheinliche Ursachen", style = MaterialTheme.typography.labelLarge)
                d.causes.forEachIndexed { i, c -> BodyText("${i + 1}. $c") }
                Spacer(Modifier.height(6.dp))
                Text("Im Modell testen", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (a in d.actions) OutlinedButton(onClick = { vm.applyDiag(a) }) { Text(a.label) }
                    Button(onClick = { vm.showScenario(d.scenario) }) { Text("Situation „${d.scenario.short}“ ansehen") }
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}
