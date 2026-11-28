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
