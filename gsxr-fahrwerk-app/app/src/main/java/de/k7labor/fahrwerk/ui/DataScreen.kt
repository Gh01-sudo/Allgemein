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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import de.k7labor.fahrwerk.BuildConfig
import de.k7labor.fahrwerk.LabViewModel
import de.k7labor.fahrwerk.data.Content
import de.k7labor.fahrwerk.data.Status
import de.k7labor.fahrwerk.physics.K7

@Composable
fun DataScreen(vm: LabViewModel) {
    var sub by rememberSaveable { mutableIntStateOf(0) }
    Column(Modifier.fillMaxWidth()) {
        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)) {
            listOf("Datenblatt", "Annahmen", "Prüfungen", "Über").forEachIndexed { i, t ->
                SegmentedButton(selected = sub == i, onClick = { sub = i }, shape = SegmentedButtonDefaults.itemShape(i, 4)) { Text(t, maxLines = 1) }
            }
        }
        when (sub) {
            0 -> SheetTab()
            1 -> AssumptionTab()
            2 -> CheckTab(vm)
            else -> AboutTab()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SheetTab() {
    var q by rememberSaveable { mutableStateOf("") }
    var cat by rememberSaveable { mutableStateOf("Alle") }
    var stat by rememberSaveable { mutableStateOf<String?>(null) }
    val uri = LocalUriHandler.current
    val cats = listOf("Alle") + Content.DATA.map { it.cat }.distinct()
    val rows = Content.DATA.filter { (cat == "Alle" || it.cat == cat) && (stat == null || it.status.name == stat) && it.matches(q) }
    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            SectionCard("Suzuki GSX-R 1000 K7 (2007–2008)") {
                OutlinedTextField(value = q, onValueChange = { q = it.take(40) }, label = { Text("Suchen") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) { for (c in cats) FilterChip(selected = cat == c, onClick = { cat = c }, label = { Text(c) }) }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(selected = stat == null, onClick = { stat = null }, label = { Text("Jeder Status") })
                    for (s in Status.entries) FilterChip(selected = stat == s.name, onClick = { stat = s.name }, label = { Text("${s.label} (${Content.DATA.count { it.status == s }})") })
                }
                BodyText("„unsicher“ heißt: Die Quellen widersprechen sich. Vor dem Verstellen den Ist-Zustand an deiner Maschine zählen und notieren.")
            }
        }
        if (rows.isEmpty()) item { SectionCard { BodyText("Keine Treffer. Suchbegriff oder Filter ändern.") } }
        items(rows) { r ->
            val lab = LocalLabColors.current
            val (bg, fg) = when (r.status) {
                Status.OK2 -> lab.good.copy(alpha = 0.16f) to lab.good
                Status.OK1 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.14f) to MaterialTheme.colorScheme.primary
                Status.CONF -> lab.warn.copy(alpha = 0.18f) to lab.warn
                else -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
            }
            SectionCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${r.cat} · ${r.label}", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Pill(r.status.label, bg, fg)
                }
                Text(r.value, style = MaterialTheme.typography.bodyLarge)
                if (r.note.isNotBlank()) BodyText(r.note)
                for (k in r.sources) {
                    val s = Content.SOURCES[k] ?: continue
                    Text(
                        "↗ ${s.title}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline,
                        modifier = Modifier.padding(top = 2.dp).clickable(role = Role.Button, onClickLabel = "Quelle öffnen") { runCatching { uri.openUri(s.url) } }
                    )
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun AssumptionTab() {
    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            SectionCard("Modellannahmen") {
                BodyText("Diese Werte sind nicht gemessen, sondern geschätzt oder aus typischen Werten abgeleitet. Sie bestimmen, wie genau die Simulation dein Motorrad trifft. Mit eigenen Messungen (Durchhang, Kabelbinder, Klicks zählen) wird das Ergebnis besser.")
            }
        }
        item {
            SectionCard {
                K7.assumptions.forEachIndexed { i, a ->
                    if (i > 0) HorizontalDivider(Modifier.padding(vertical = 4.dp))
                    Text(a.name, style = MaterialTheme.typography.titleSmall)
                    Text(a.value, style = MaterialTheme.typography.bodyMedium)
                    BodyText(a.why)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CheckTab(vm: LabViewModel) {
    val checks by vm.checks.collectAsState()
    val lab = LocalLabColors.current
    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            SectionCard("Rechenprüfungen") {
                BodyText("Das Modell prüft sich beim Start selbst: Formeln nachgerechnet, Kräftebilanz, Kalibrierung und Rechengenauigkeit.")
                if (checks.isEmpty()) BodyText("Prüfung läuft…")
                for (c in checks) {
                    Row(Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
                        Pill(if (c.ok) "OK" else "prüfen", (if (c.ok) lab.good else lab.bad).copy(alpha = 0.16f), if (c.ok) lab.good else lab.bad)
                        Column(Modifier.padding(start = 8.dp)) {
                            Text(c.name, style = MaterialTheme.typography.bodyMedium)
                            Text(c.value, style = MaterialTheme.typography.bodySmall)
                            if (c.hint.isNotBlank()) BodyText(c.hint)
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AboutTab() {
    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            SectionCard("K7 Fahrwerkslabor ${BuildConfig.VERSION_NAME}") {
                BodyText("Fahrwerksprüfstand für die Suzuki GSX-R 1000 K7 im Handy. Gerechnet wird ein ebenes Mehrkörpermodell: gefederte Masse mit Fahrer (Heben und Nicken), Vorderrad an der schrägen Gabel, Hinterrad an der Schwinge mit progressiver Umlenkung, Reifen mit Federung, Dämpfung und Haftgrenze (Magic Formula), Kettenzug entlang der echten Kettenlinie, Bremsmomente, Motorkennlinie mit Getriebe und Luftwiderstand. Zeitschritt 0,2 ms.")
                Spacer(Modifier.height(6.dp))
                Text("Was das Modell nicht kann", style = MaterialTheme.typography.titleSmall)
                BodyText("• Keine Seitenkräfte, kein Lenken, kein Pendeln oder Lenkerschlagen als Schwingung. Kurven sind als Last in der Fahrzeugebene nachgebildet.\n• Reibung in Gabel und Federbein (Losbrechkraft) ist nicht enthalten.\n• Dämpferkennlinien und viele Massen sind Annahmen (Register „Annahmen“). Ohne Messung am echten Motorrad ist das Ergebnis eine fundierte Schätzung, keine Messung.\n• Der Fahrer ist starr mit dem Motorrad verbunden.")
                Spacer(Modifier.height(6.dp))
                Text("Sicherheit", style = MaterialTheme.typography.titleSmall)
                BodyText("Fahrwerksänderungen verändern das Fahrverhalten. Immer in kleinen Schritten ändern, beide Gabelholme gleich einstellen, auf einer bekannten Strecke vorsichtig probefahren. Ölstand und Federn: Werkstattarbeit. Diese App ersetzt keine Fachwerkstatt.")
                Spacer(Modifier.height(6.dp))
                BodyText("Alle Daten bleiben auf dem Handy. Keine Internetverbindung, keine Werbung, kein Konto. Suzuki und GSX-R sind Marken der Suzuki Motor Corporation; diese App ist ein privates Projekt ohne Verbindung zu Suzuki.")
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
