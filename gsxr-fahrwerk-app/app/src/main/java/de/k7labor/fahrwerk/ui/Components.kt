package de.k7labor.fahrwerk.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.height
import androidx.compose.ui.text.drawText
import de.k7labor.fahrwerk.physics.Control
import de.k7labor.fahrwerk.physics.Physics
import de.k7labor.fahrwerk.physics.fmt
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun SectionCard(title: String? = null, modifier: Modifier = Modifier, trailing: (@Composable () -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
        Column(Modifier.padding(14.dp)) {
            if (title != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    trailing?.invoke()
                }
                Spacer(Modifier.size(8.dp))
            }
            content()
        }
    }
}

@Composable
fun CollapsibleCard(title: String, tag: String, initiallyOpen: Boolean, content: @Composable ColumnScope.() -> Unit) {
    var open by rememberSaveable(title) { mutableStateOf(initiallyOpen) }
    Card(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
        Row(
            Modifier.fillMaxWidth().clickable(role = Role.Button, onClickLabel = if (open) "zuklappen" else "aufklappen") { open = !open }.padding(14.dp)
                .semantics { stateDescription = if (open) "offen" else "zu" },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Pill(tag, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
            Text(if (open) "  ▲" else "  ▼", style = MaterialTheme.typography.labelLarge)
        }
        AnimatedVisibility(open) { Column(Modifier.padding(start = 14.dp, end = 14.dp, bottom = 12.dp)) { content() } }
    }
}

@Composable
fun Pill(text: String, bg: Color, fg: Color) {
    Box(Modifier.background(bg, RoundedCornerShape(50)).padding(horizontal = 8.dp, vertical = 2.dp)) {
        Text(text, color = fg, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun ScorePill(v: Double) {
    val lab = LocalLabColors.current
    val c = when { v >= 80 -> lab.good; v >= 62 -> lab.warn; else -> lab.bad }
    Pill(fmt(v, 0), c.copy(alpha = 0.18f), c)
}

@Composable
fun NoteCard(n: Physics.Note) {
    val lab = LocalLabColors.current
    val c = when (n.level) { Physics.Level.INFO -> MaterialTheme.colorScheme.primary; Physics.Level.WARN -> lab.warn; Physics.Level.BAD -> lab.bad }
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Box(Modifier.width(4.dp).size(width = 4.dp, height = 40.dp).background(c, RoundedCornerShape(2.dp)))
        Column(Modifier.padding(start = 10.dp)) {
            Text(n.title, style = MaterialTheme.typography.titleSmall, color = c)
            Text(n.text, style = MaterialTheme.typography.bodySmall)
        }
    }
}

/** Regler mit −/+ Knöpfen, Serienwert und Kennzeichnung für unsichere Serienwerte. */
@Composable
fun ControlRow(c: Control, value: Double, unsureStock: Boolean, onSet: (Double) -> Unit, onStep: (Int) -> Unit) {
    val lab = LocalLabColors.current
    val changed = !c.isLoad && abs(value - c.stock) > 1e-9
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(c.label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            Text(
                "${fmt(value, c.decimals)} ${c.unit}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (changed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            FilledTonalIconButton(onClick = { onStep(-1) }, modifier = Modifier.size(36.dp).semantics { contentDescription = "${c.label} verringern" }) { Text("−") }
            val steps = ((c.max - c.min) / c.step).roundToInt() - 1
            Slider(
                value = value.toFloat(),
                onValueChange = { onSet(it.toDouble()) },
                valueRange = c.min.toFloat()..c.max.toFloat(),
                steps = steps.coerceAtLeast(0),
                modifier = Modifier.weight(1f).padding(horizontal = 6.dp).semantics { contentDescription = c.label; stateDescription = "${fmt(value, c.decimals)} ${c.unit}" }
            )
            FilledTonalIconButton(onClick = { onStep(1) }, modifier = Modifier.size(36.dp).semantics { contentDescription = "${c.label} erhöhen" }) { Text("+") }
        }
        Row {
            Text(c.hint, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
            if (!c.isLoad) {
                Text(
                    "Serie ${fmt(c.stock, c.decimals)}" + if (unsureStock) " (unsicher)" else "",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (unsureStock) lab.warn else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun KeyValue(label: String, value: String, modifier: Modifier = Modifier, sub: String? = null) {
    Column(modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.titleMedium)
        if (sub != null) Text(sub, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun BodyText(t: String, modifier: Modifier = Modifier) {
    Text(t, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = modifier)
}

@Composable
fun TableRow(cells: List<String>, header: Boolean = false, weights: List<Float> = List(cells.size) { if (it == 0) 2f else 1f }, colors: List<Color?> = List(cells.size) { null }) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        cells.forEachIndexed { i, t ->
            Text(
                t, modifier = Modifier.weight(weights[i]),
                style = if (header) MaterialTheme.typography.labelMedium else MaterialTheme.typography.bodySmall,
                fontWeight = if (header) FontWeight.SemiBold else FontWeight.Normal,
                color = colors[i] ?: if (header) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


/** Halbkreis-Anzeige für den Setup-Index mit Marke für Serie. */
@Composable
fun ScoreGauge(value: Double?, stock: Double?, modifier: Modifier = Modifier) {
    val lab = LocalLabColors.current
    val track = MaterialTheme.colorScheme.surfaceContainerHighest
    val txt = MaterialTheme.colorScheme.onSurface
    val measurer = androidx.compose.ui.text.rememberTextMeasurer()
    val v = value ?: 0.0
    val col = when { v >= 80 -> lab.good; v >= 62 -> lab.warn; else -> lab.bad }
    androidx.compose.foundation.Canvas(
        modifier.size(width = 118.dp, height = 80.dp).semantics {
            contentDescription = "Setup-Index ${value?.let { fmt(it, 0) } ?: "wird gerechnet"} von 100" + (stock?.let { ", Serie ${fmt(it, 0)}" } ?: "")
        }
    ) {
        val sw = 11.dp.toPx()
        val d = size.width - sw
        val tl = androidx.compose.ui.geometry.Offset(sw / 2, sw / 2)
        val sz = androidx.compose.ui.geometry.Size(d, d)
        drawArc(track, 180f, 180f, false, tl, sz, style = androidx.compose.ui.graphics.drawscope.Stroke(sw, cap = androidx.compose.ui.graphics.StrokeCap.Round))
        if (value != null) drawArc(col, 180f, (180 * (v / 100).coerceIn(0.0, 1.0)).toFloat(), false, tl, sz, style = androidx.compose.ui.graphics.drawscope.Stroke(sw, cap = androidx.compose.ui.graphics.StrokeCap.Round))
        stock?.let {
            val a = Math.toRadians(180 + 180 * (it / 100).coerceIn(0.0, 1.0))
            val c = androidx.compose.ui.geometry.Offset(size.width / 2, sw / 2 + d / 2)
            val r0 = d / 2 - sw; val r1 = d / 2 + sw * 0.7f
            drawLine(lab.stock, androidx.compose.ui.geometry.Offset(c.x + r0 * kotlin.math.cos(a).toFloat(), c.y + r0 * kotlin.math.sin(a).toFloat()),
                androidx.compose.ui.geometry.Offset(c.x + r1 * kotlin.math.cos(a).toFloat(), c.y + r1 * kotlin.math.sin(a).toFloat()), 3.dp.toPx())
        }
        val t = measurer.measure(value?.let { fmt(it, 0) } ?: "…", androidx.compose.ui.text.TextStyle(fontSize = 26.sp, fontWeight = FontWeight.Bold, color = txt))
        drawText(t, topLeft = androidx.compose.ui.geometry.Offset(size.width / 2 - t.size.width / 2, sw / 2 + d / 2 - t.size.height * 0.95f))
        val s2 = measurer.measure("Setup-Index" + (stock?.let { " · Serie ${fmt(it, 0)}" } ?: ""), androidx.compose.ui.text.TextStyle(fontSize = 9.sp, color = txt.copy(alpha = 0.7f)))
        drawText(s2, topLeft = androidx.compose.ui.geometry.Offset(size.width / 2 - s2.size.width / 2, size.height - s2.size.height))
    }
}

/** Balken mit Zielfenster (grün) und aktuellem Wert. */
@Composable
fun SagBar(label: String, value: Double?, window: Pair<Double, Double>, max: Double) {
    val lab = LocalLabColors.current
    val track = MaterialTheme.colorScheme.surfaceContainerHighest
    val inWin = value != null && Physics.inSagWindow(value, window)
    Column(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Row {
            Text(label, style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
            Text(value?.let { "${fmt(it, 1)} mm" } ?: "…", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = if (inWin) lab.good else lab.warn)
        }
        androidx.compose.foundation.Canvas(Modifier.fillMaxWidth().height(12.dp).semantics { contentDescription = "$label, Ziel ${fmt(window.first, 0)} bis ${fmt(window.second, 0)} mm" }) {
            val r = androidx.compose.ui.geometry.CornerRadius(size.height / 2)
            drawRoundRect(track, cornerRadius = r)
            val x0 = (window.first / max * size.width).toFloat(); val x1 = (window.second / max * size.width).toFloat()
            drawRoundRect(lab.good.copy(alpha = 0.35f), topLeft = androidx.compose.ui.geometry.Offset(x0, 0f), size = androidx.compose.ui.geometry.Size(x1 - x0, size.height), cornerRadius = r)
            value?.let {
                val x = (it.coerceIn(0.0, max) / max * size.width).toFloat()
                drawCircle(if (inWin) lab.good else lab.warn, size.height * 0.62f, androidx.compose.ui.geometry.Offset(x, size.height / 2))
                drawCircle(androidx.compose.ui.graphics.Color.White, size.height * 0.25f, androidx.compose.ui.geometry.Offset(x, size.height / 2))
            }
        }
        Text("Ziel ${fmt(window.first, 0)}–${fmt(window.second, 0)} mm", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
