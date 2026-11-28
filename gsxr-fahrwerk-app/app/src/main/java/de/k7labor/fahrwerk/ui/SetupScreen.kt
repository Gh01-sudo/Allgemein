package de.k7labor.fahrwerk.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.k7labor.fahrwerk.LabViewModel
import de.k7labor.fahrwerk.data.Content
import de.k7labor.fahrwerk.physics.Controls
import de.k7labor.fahrwerk.physics.Group
import de.k7labor.fahrwerk.physics.Physics
import de.k7labor.fahrwerk.physics.Profile
import de.k7labor.fahrwerk.physics.fmt
import kotlin.math.abs

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SetupScreen(vm: LabViewModel) {
    val st by vm.setup.collectAsState()
    val run by vm.run.collectAsState()
    val stock by vm.stockRun.collectAsState()
    val busy by vm.busy.collectAsState()
    val rec by vm.recommendation.collectAsState()
    val recBusy by vm.recBusy.collectAsState()
    val pose = remember(st) { Physics.staticPose(st) }
    val stockPose = remember(st.values["rider"], st.values["passenger"], st.values["luggage"], st.values["fuel"]) { Physics.staticPose(st.toStockKeepLoad()) }

    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            if (busy) LinearProgressIndicator(Modifier.fillMaxWidth()) else Spacer(Modifier.height(4.dp))
            SectionCard("Ergebnis") {
                Row(Modifier.fillMaxWidth()) {
                    KeyValue("Setup-Index", run?.let { "${fmt(it.overall, 0)} / 100" } ?: "…", Modifier.weight(1f), sub = stock?.let { "Serie ${fmt(it.overall, 0)}" })
                    KeyValue("Durchhang vorn", run?.let { "${fmt(it.sags.sagF, 1)} mm" } ?: "…", Modifier.weight(1f), sub = "Ziel ${fmt(st.profile.sagWinF.first, 0)}–${fmt(st.profile.sagWinF.second, 0)}")
                    KeyValue("Durchhang hinten", run?.let { "${fmt(it.sags.sagR, 1)} mm" } ?: "…", Modifier.weight(1f), sub = "Ziel ${fmt(st.profile.sagWinR.first, 0)}–${fmt(st.profile.sagWinR.second, 0)}")
                }
                Row(Modifier.fillMaxWidth()) {
                    KeyValue("Lenkkopf", "${fmt(pose.rakeDeg, 2)}°", Modifier.weight(1f), sub = "Serie ${fmt(stockPose.rakeDeg, 2)}°")
                    KeyValue("Nachlauf", "${fmt(pose.trailMm, 1)} mm", Modifier.weight(1f), sub = "Serie ${fmt(stockPose.trailMm, 1)}")
                    KeyValue("Radstand", "${fmt(pose.wheelbaseMm, 0)} mm", Modifier.weight(1f), sub = "Serie ${fmt(stockPose.wheelbaseMm, 0)}")
                }
                BodyText("Geometrie im Stand mit Fahrer. Werksangabe ohne Fahrer: 23,75° · 98 mm · 1415 mm. Sitzhöhe mit Fahrer ${fmt(pose.seatMm, 0)} mm.")
                Spacer(Modifier.height(10.dp))
                Text("Einsatzprofil", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (p in Profile.entries) FilterChip(selected = st.profile == p, onClick = { vm.setProfile(p) }, label = { Text(p.label) })
                }
                Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { vm.loadStock() }, modifier = Modifier.weight(1f)) { Text("Serie laden") }
                    Button(onClick = { vm.requestRecommendation() }, enabled = !recBusy, modifier = Modifier.weight(1f)) { Text(if (recBusy) "Rechne…" else "Empfehlung anzeigen") }
                }
            }
        }
        for (g in Group.entries) {
            item(key = g.name) {
                val items = Controls.all.filter { it.group == g }
                val nChanged = items.count { !it.isLoad && st.isChanged(it.key) }
                val tag = if (g == Group.LOAD) "${fmt(st.rider + st.passenger + st.luggage, 0)} kg Zuladung" else if (nChanged == 0) "Serie" else "$nChanged geändert"
                CollapsibleCard(g.title, tag, initiallyOpen = g != Group.SPRING) {
                    if (g == Group.FORK || g == Group.SHOCK) BodyText("Klicks und Umdrehungen zählst du immer ab „ganz zu“ (im Uhrzeigersinn bis Anschlag, nicht mit Gewalt). Werte mit „unsicher“ sind Serienwerte, bei denen sich die Quellen widersprechen.")
                    if (g == Group.SPRING) BodyText("Nur zum Ausprobieren: andere Federn kosten Geld und Werkstattzeit. Vor dem Kauf Durchhang messen.")
                    for (c in items) ControlRow(c, st[c.key], c.key in Content.UNSURE_STOCK, { vm.set(c.key, it) }, { vm.step(c.key, it) })
                }
            }
        }
        item {
            val r = run
            SectionCard("Prüfprotokoll") {
                if (r == null) BodyText("Rechnet…") else for (n in Physics.insights(st, r)) NoteCard(n)
                BodyText("Alle Ergebnisse sind Modellrechnungen mit gekennzeichneten Annahmen (Register „Daten“). Änderungen am echten Motorrad immer in kleinen Schritten und mit Probefahrt prüfen.")
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    rec?.let { r ->
        val diffs = Controls.all.filter { !it.isLoad && abs(r.setup[it.key] - st[it.key]) > 1e-9 }
        AlertDialog(
            onDismissRequest = { vm.dismissRecommendation() },
            title = { Text("Empfehlung für „${st.profile.label}“") },
            text = {
                LazyColumn {
                    if (diffs.isEmpty()) item { BodyText("Dein Setup entspricht bereits der Empfehlung.") }
                    items(diffs) { c -> TableRow(listOf(c.label, "${fmt(st[c.key], c.decimals)} → ${fmt(r.setup[c.key], c.decimals)} ${c.unit}"), weights = listOf(1.4f, 1f)) }
                    items(r.notes) { NoteCard(it) }
                    item { BodyText("Vorspannung wird mit dem vollen Modell auf den Ziel-Durchhang gerechnet. Dämpfung: Profil-Versatz und Gewichtskorrektur gegenüber Serie.") }
                }
            },
            confirmButton = { Button(onClick = { vm.applyRecommendation() }, enabled = diffs.isNotEmpty()) { Text("Übernehmen") } },
            dismissButton = { TextButton(onClick = { vm.dismissRecommendation() }) { Text("Abbrechen") } }
        )
    }
}
