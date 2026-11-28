package de.k7labor.fahrwerk.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import de.k7labor.fahrwerk.CmpRef
import de.k7labor.fahrwerk.LabViewModel
import de.k7labor.fahrwerk.physics.Controls
import de.k7labor.fahrwerk.physics.Group
import de.k7labor.fahrwerk.physics.RunAll
import de.k7labor.fahrwerk.physics.ScenarioId
import de.k7labor.fahrwerk.physics.fmt
import java.text.DateFormat
import java.util.Date
import kotlin.math.abs

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LibraryScreen(vm: LabViewModel) {
    val lib by vm.library.collectAsState()
    val st by vm.setup.collectAsState()
    val a by vm.cmpA.collectAsState()
    val b by vm.cmpB.collectAsState()
    val runs by vm.cmpRuns.collectAsState()
    val ctx = LocalContext.current
    var name by rememberSaveable { mutableStateOf("") }
    var confirmDelete by remember { mutableStateOf<String?>(null) }
    var importOpen by remember { mutableStateOf(false) }
    var importMsgs by remember { mutableStateOf<List<String>?>(null) }

    val createDoc = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) {
            val ok = runCatching { ctx.contentResolver.openOutputStream(uri)?.use { it.write(vm.exportCurrent().toByteArray()) } != null }.getOrDefault(false)
            vm.toast(if (ok) "Setup als Datei gespeichert" else "Speichern fehlgeschlagen")
        }
    }
    val openDoc = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val txt = runCatching { ctx.contentResolver.openInputStream(uri)?.use { s -> s.readBytes().take(200_000).toByteArray().decodeToString() } }.getOrNull()
            if (txt == null) vm.toast("Datei konnte nicht gelesen werden") else importMsgs = vm.importText(txt, saveAsNew = true).messages.ifEmpty { listOf("Setup importiert und in der Bibliothek gespeichert.") }
        }
    }

    LazyColumn(Modifier.fillMaxWidth()) {
        item {
            SectionCard("Aktuelles Setup speichern") {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = name, onValueChange = { name = it.take(60) }, label = { Text("Name, z. B. Hockenheim trocken") }, singleLine = true, modifier = Modifier.weight(1f))
                    Button(onClick = { vm.saveToLibrary(name); name = "" }, modifier = Modifier.padding(start = 8.dp)) { Text("Speichern") }
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                    OutlinedButton(onClick = { createDoc.launch("k7-setup.json") }) { Text("Als Datei exportieren") }
                    OutlinedButton(onClick = {
                        val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, vm.summaryText() + "\n\n" + vm.exportCurrent())
                        ctx.startActivity(Intent.createChooser(send, "Setup teilen"))
                    }) { Text("Teilen") }
                    OutlinedButton(onClick = {
                        // Zwischenablage gibt es auf Android immer (Review-Punkt 12)
                        val cm = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cm.setPrimaryClip(ClipData.newPlainText("K7 Setup", vm.summaryText()))
                        vm.toast("Text kopiert")
                    }) { Text("Text kopieren") }
                    OutlinedButton(onClick = { openDoc.launch(arrayOf("application/json", "text/plain", "*/*")) }) { Text("Datei importieren") }
                    OutlinedButton(onClick = { importOpen = true }) { Text("Text einfügen") }
                }
                BodyText("Alles bleibt auf dem Handy. Dateien aus dem alten Web-Labor (JSON) lassen sich ebenfalls importieren.")
            }
        }
        item {
            SectionCard("Bibliothek (${lib.size})") {
                if (lib.isEmpty()) BodyText("Noch keine Setups gespeichert.")
                lib.forEachIndexed { i, e ->
                    if (i > 0) HorizontalDivider()
                    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        androidx.compose.foundation.layout.Column(Modifier.weight(1f)) {
                            Text(e.name, style = MaterialTheme.typography.bodyLarge)
                            BodyText("${e.setup.profile.label} · ${fmt(e.setup.rider, 0)} kg" + if (e.created > 0) " · " + DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(e.created)) else "")
                        }
                        TextButton(onClick = { vm.loadFromLibrary(e.id) }) { Text("Laden") }
                        TextButton(onClick = { confirmDelete = e.id }) { Text("Löschen") }
                    }
                }
            }
        }
        item {
            SectionCard("Vergleich") {
                val options = listOf(CmpRef("cur", "Aktuell"), CmpRef("stock", "Serie")) + lib.map { CmpRef(it.id, it.name) }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Picker("A", a, options, Modifier.weight(1f)) { vm.cmpA.value = it }
                    Picker("B", b, options, Modifier.weight(1f)) { vm.cmpB.value = it }
                }
                BodyText("Beladung und Profil kommen immer vom aktuellen Setup, damit der Vergleich fair ist. Die Tabelle rechnet bei jeder Änderung neu.")
                Spacer(Modifier.height(6.dp))
                val sa = vm.resolve(a.id, st, lib); val sb = vm.resolve(b.id, st, lib)
                if (sa != null && sb != null) {
                    val fa = sa; val fb = sb
                    TableRow(listOf("Größe", a.name.take(14), b.name.take(14)), header = true)
                    val lab = LocalLabColors.current
                    for (c in Controls.all.filter { !it.isLoad }) {
                        val diff = abs(fa[c.key] - fb[c.key]) > 1e-9
                        val prefix = when (c.group) { Group.FORK -> "Gabel"; Group.SHOCK -> "Heck"; Group.TYRE -> "Reifen"; Group.SPRING -> "Feder"; Group.LOAD -> "" }
                        val label = if (c.label.startsWith(prefix)) c.label else "$prefix: ${c.label}"
                        TableRow(listOf(label, "${fmt(fa[c.key], c.decimals)} ${c.unit}", "${fmt(fb[c.key], c.decimals)} ${c.unit}"), colors = listOf(null, if (diff) lab.extra else null, if (diff) lab.extra else null))
                    }
                    val ra: RunAll? = runs[a.id]; val rb: RunAll? = runs[b.id]
                    if (ra != null && rb != null) {
                        Spacer(Modifier.height(6.dp))
                        TableRow(listOf("Ergebnis", a.name.take(14), b.name.take(14)), header = true)
                        TableRow(listOf("Setup-Index", fmt(ra.overall, 0), fmt(rb.overall, 0)))
                        TableRow(listOf("Durchhang v / h", "${fmt(ra.sags.sagF, 0)} / ${fmt(ra.sags.sagR, 0)}", "${fmt(rb.sags.sagF, 0)} / ${fmt(rb.sags.sagR, 0)} mm"))
                        for (id in ScenarioId.entries) {
                            val x = ra.results[id]; val y = rb.results[id]
                            if (x != null && y != null) TableRow(listOf(id.short, fmt(x.score, 0), fmt(y.score, 0)))
                        }
                    } else BodyText("Rechnet…")
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    confirmDelete?.let { id ->
        val e = lib.firstOrNull { it.id == id }
        AlertDialog(
            onDismissRequest = { confirmDelete = null },
            title = { Text("Setup löschen?") },
            text = { Text("„${e?.name ?: ""}“ wird aus der Bibliothek entfernt.") },
            confirmButton = { Button(onClick = { vm.deleteFromLibrary(id); confirmDelete = null }) { Text("Löschen") } },
            dismissButton = { TextButton(onClick = { confirmDelete = null }) { Text("Abbrechen") } }
        )
    }
    if (importOpen) ImportDialog(onDismiss = { importOpen = false }) { text, save ->
        importMsgs = vm.importText(text, save).messages.ifEmpty { listOf("Setup importiert.") }
        importOpen = false
    }
    importMsgs?.let { msgs ->
        AlertDialog(
            onDismissRequest = { importMsgs = null },
            title = { Text("Import") },
            text = { LazyColumn(Modifier.heightIn(max = 360.dp)) { items(msgs.size) { BodyText("• " + msgs[it]) } } },
            confirmButton = { TextButton(onClick = { importMsgs = null }) { Text("OK") } }
        )
    }
}

@Composable
private fun Picker(label: String, cur: CmpRef, options: List<CmpRef>, modifier: Modifier, onPick: (CmpRef) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box(modifier) {
        OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) { Text("$label: ${cur.name}", maxLines = 1) }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            for (o in options) DropdownMenuItem(text = { Text(o.name) }, onClick = { onPick(o); open = false })
        }
    }
}

@Composable
private fun ImportDialog(onDismiss: () -> Unit, onImport: (String, Boolean) -> Unit) {
    val text: MutableState<String> = remember { mutableStateOf("") }
    var save by remember { mutableStateOf(true) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Setup-Text einfügen") },
        text = {
            androidx.compose.foundation.layout.Column {
                OutlinedTextField(value = text.value, onValueChange = { text.value = it.take(200_000) }, modifier = Modifier.fillMaxWidth().heightIn(min = 140.dp), label = { Text("JSON") })
                Row(verticalAlignment = Alignment.CenterVertically) { Checkbox(checked = save, onCheckedChange = { save = it }); Text("Auch in der Bibliothek speichern") }
                BodyText("Ungültige Werte werden ignoriert, alles andere auf Grenzen und Raster gebracht.")
            }
        },
        confirmButton = { Button(onClick = { onImport(text.value, save) }, enabled = text.value.isNotBlank()) { Text("Importieren") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Abbrechen") } }
    )
}
