package de.k7labor.fahrwerk.ui.bike

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.k7labor.fahrwerk.data.Content
import de.k7labor.fahrwerk.physics.Control
import de.k7labor.fahrwerk.physics.Controls
import de.k7labor.fahrwerk.physics.K7
import de.k7labor.fahrwerk.physics.Physics
import de.k7labor.fahrwerk.physics.Setup
import de.k7labor.fahrwerk.physics.Vehicle
import de.k7labor.fahrwerk.physics.fmt
import de.k7labor.fahrwerk.physics.shockStroke
import de.k7labor.fahrwerk.ui.BodyText
import de.k7labor.fahrwerk.ui.ControlRow
import de.k7labor.fahrwerk.ui.KeyValue
import de.k7labor.fahrwerk.ui.LocalLabColors
import de.k7labor.fahrwerk.ui.SectionCard
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/** Auswahlleiste für Bauteile (auch für Bedienung ohne Antippen im Bild). */
@Composable
fun PartChips(selected: BikePart?, onSelect: (BikePart?) -> Unit) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        for (p in BikePart.entries) FilterChip(selected = selected == p, onClick = { onSelect(if (selected == p) null else p) }, label = { Text(p.title) })
    }
}

/** Live-Messwerte eines Bauteils im aktuellen Augenblick. */
fun partValues(p: BikePart, veh: Vehicle, s: SceneState): List<Pair<String, String>> {
    val pose = Physics.pose(veh, s.x, s.z, s.th, s.s, s.phi)
    val total = (s.fzF + s.fzR).coerceAtLeast(1.0)
    return when (p) {
        BikePart.FORK -> listOf(
            "Federweg" to "${fmt(s.sFmm, 1)} mm (${fmt(s.sFmm / K7.TRAVEL_F_MM * 100, 0)} %)",
            "Radlast vorn" to "${fmt(s.fzF / 1000, 2)} kN",
            "Lenkkopf / Nachlauf" to "${fmt(pose.rakeDeg, 2)}° / ${fmt(pose.trailMm, 0)} mm"
        )
        BikePart.FRONT_WHEEL -> listOf(
            "Radlast" to "${fmt(s.fzF / 1000, 2)} kN",
            "Längskraft" to "${fmt(s.fxF / 1000, 2)} kN",
            "Luftdruck kalt" to "${fmt(veh.st.pF, 2)} bar"
        )
        BikePart.SHOCK -> listOf(
            "Radweg" to "${fmt(s.xRmm, 1)} mm (${fmt(s.xRmm / K7.TRAVEL_R_MM * 100, 0)} %)",
            "Federbeinhub" to "${fmt(shockStroke(s.xRmm.coerceAtLeast(0.0)), 1)} mm",
            "Radlast hinten" to "${fmt(s.fzR / 1000, 2)} kN"
        )
        BikePart.SWINGARM -> listOf(
            "Schwingenwinkel" to "${fmt(s.phi * 180 / PI, 2)}°",
            "Radweg" to "${fmt(s.xRmm, 1)} mm",
            "Antrieb / Bremse hinten" to "${fmt(s.fxR / 1000, 2)} kN"
        )
        BikePart.REAR_WHEEL -> listOf(
            "Radlast" to "${fmt(s.fzR / 1000, 2)} kN",
            "Längskraft" to "${fmt(s.fxR / 1000, 2)} kN",
            "Luftdruck kalt" to "${fmt(veh.st.pR, 2)} bar"
        )
        BikePart.RIDER -> listOf(
            "Gesamtmasse" to "${fmt(veh.mTotal, 0)} kg",
            "Lastverteilung v / h" to "${fmt(s.fzF / total * 100, 0)} / ${fmt(s.fzR / total * 100, 0)} %",
            "Sitzhöhe" to "${fmt(pose.seatMm, 0)} mm"
        )
        BikePart.CHASSIS -> listOf(
            "Nicken" to "${fmt(s.pitchDeg, 2)}°",
            "Lenkkopf / Nachlauf" to "${fmt(pose.rakeDeg, 2)}° / ${fmt(pose.trailMm, 0)} mm",
            "Radstand" to "${fmt(pose.wheelbaseMm, 0)} mm"
        )
    }
}

/** Erklärung, Live-Werte und Einsteller des gewählten Bauteils. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PartPanel(part: BikePart, veh: Vehicle, state: SceneState, setup: Setup, onSet: (String, Double) -> Unit, onStep: (String, Int) -> Unit) {
    SectionCard(part.title) {
        BodyText(part.info)
        Row(Modifier.fillMaxWidth().padding(top = 4.dp)) {
            for ((k, v) in partValues(part, veh, state)) KeyValue(k, v, Modifier.weight(1f))
        }
        if (part.keys.isNotEmpty()) {
            val cs = part.keys.map { Controls.get(it) }
            val knobs = cs.filter { !it.isLoad }
            if (knobs.isNotEmpty()) {
                BodyText("Zum Verstellen mit dem Finger um den Knopf kreisen (je Raste ein Klick bzw. ein Schritt) oder −/+ tippen. Im Uhrzeigersinn heißt wie am Motorrad: Dämpfung zu, mehr Vorspannung. Die Rechnung läuft sofort neu.")
                FlowRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (c in knobs) Knob(c, setup[c.key], c.key in Content.UNSURE_STOCK, { onSet(c.key, it) }, { onStep(c.key, it) })
                }
            }
            for (c in cs.filter { it.isLoad }) ControlRow(c, setup[c.key], false, { onSet(c.key, it) }, { onStep(c.key, it) })
        }
    }
}

/**
 * Drehknopf wie am echten Einsteller: Drehen um die Mitte verstellt in Rasten.
 * Eine Raste = ein Schritt des Einstellers (Klick, Viertelumdrehung, 0,5 mm …).
 */
@Composable
fun Knob(c: Control, value: Double, unsure: Boolean, onSet: (Double) -> Unit, onStep: (Int) -> Unit) {
    val lab = LocalLabColors.current
    val scheme = MaterialTheme.colorScheme
    val cur by rememberUpdatedState(value)
    val curStep by rememberUpdatedState(onStep)
    var acc by remember { mutableFloatStateOf(0f) }
    val changed = abs(value - c.stock) > 1e-9
    // Wie am Motorrad: im Uhrzeigersinn = zudrehen (weniger Klicks offen) bzw. mehr Vorspannung (weniger Ringe)
    val closing = c.unit == "Kl. offen" || c.unit == "Umdr. offen" || c.unit == "Ringe"
    val cwDir = if (closing) -1 else 1
    fun dial(v: Double) = ((v - c.min) / (c.max - c.min)).coerceIn(0.0, 1.0).let { if (closing) 1 - it else it }
    val frac = dial(value)
    Column(Modifier.width(112.dp).padding(vertical = 4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(c.label, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center, maxLines = 2, minLines = 2)
        Canvas(
            Modifier.size(78.dp)
                .semantics { contentDescription = "${c.label} Drehknopf"; stateDescription = "${fmt(value, c.decimals)} ${c.unit}" }
                .pointerInput(c.key) {
                    // Drehen nur bei Bewegung um die Mitte herum; Wischen zur Mitte hin/weg bleibt Scrollen der Liste
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val cx = size.width / 2f; val cy = size.height / 2f
                        var last = atan2(down.position.y - cy, down.position.x - cx)
                        var rotating = false
                        var moved = Offset.Zero
                        acc = 0f
                        while (true) {
                            val ev = awaitPointerEvent()
                            val ch = ev.changes.firstOrNull { it.id == down.id } ?: break
                            if (!ch.pressed) break
                            if (ch.isConsumed) break
                            val p = ch.position
                            if (!rotating) {
                                moved += ch.positionChange()
                                if (moved.getDistance() > viewConfiguration.touchSlop) {
                                    val rx = down.position.x - cx; val ry = down.position.y - cy
                                    val rl = kotlin.math.hypot(rx, ry).coerceAtLeast(1f)
                                    val radial = abs((moved.x * rx + moved.y * ry) / rl)
                                    val tang = abs((moved.x * -ry + moved.y * rx) / rl)
                                    if (tang > radial) rotating = true else break
                                    last = atan2(p.y - cy, p.x - cx)
                                }
                                continue
                            }
                            ch.consume()
                            val a = atan2(p.y - cy, p.x - cx)
                            var d = a - last
                            if (d > PI) d -= (2 * PI).toFloat()
                            if (d < -PI) d += (2 * PI).toFloat()
                            last = a
                            acc += d
                            val detent = (PI / 9).toFloat()   // 20° je Raste; positiv = im Uhrzeigersinn
                            while (acc > detent) { curStep(cwDir); acc -= detent }
                            while (acc < -detent) { curStep(-cwDir); acc += detent }
                        }
                    }
                }
        ) {
            val r = size.minDimension / 2
            val cen = Offset(size.width / 2, size.height / 2)
            // Skala von −135° bis +135°
            val start = 135f
            drawArc(scheme.surfaceContainerHighest, start, 270f, false, Offset(cen.x - r + 4, cen.y - r + 4), androidx.compose.ui.geometry.Size(2 * r - 8, 2 * r - 8), style = Stroke(5f, cap = StrokeCap.Round))
            drawArc(if (changed) scheme.primary else lab.stock, start, (270 * frac).toFloat(), false, Offset(cen.x - r + 4, cen.y - r + 4), androidx.compose.ui.geometry.Size(2 * r - 8, 2 * r - 8), style = Stroke(5f, cap = StrokeCap.Round))
            // Serienmarke
            val sf = dial(c.stock)
            val sa = Math.toRadians(start + 270 * sf)
            drawLine(lab.warn, Offset(cen.x + (r - 12) * cos(sa).toFloat(), cen.y + (r - 12) * sin(sa).toFloat()), Offset(cen.x + r * cos(sa).toFloat(), cen.y + r * sin(sa).toFloat()), 3f)
            // Rändelung, dreht sich mit dem Wert
            val kr = r * 0.68f
            drawCircle(Brush.radialGradient(listOf(Color(0xFFE6E8EB), Color(0xFF8D9399)), center = Offset(cen.x - kr * 0.3f, cen.y - kr * 0.3f), radius = kr * 1.6f), kr, cen)
            val steps = ((value - c.min) / c.step).roundToInt() * cwDir
            for (j in 0 until 24) {
                val a = 2 * PI * j / 24 + steps * (PI / 9)
                drawLine(Color(0x55000000), Offset(cen.x + (kr - 6) * cos(a).toFloat(), cen.y + (kr - 6) * sin(a).toFloat()), Offset(cen.x + kr * cos(a).toFloat(), cen.y + kr * sin(a).toFloat()), 2f)
            }
            val pa = Math.toRadians(start + 270 * frac)
            drawLine(Color(0xFF202020), cen, Offset(cen.x + (kr - 8) * cos(pa).toFloat(), cen.y + (kr - 8) * sin(pa).toFloat()), 4f, StrokeCap.Round)
            drawCircle(Color(0xFF3A3D42), kr * 0.18f, cen)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            FilledTonalIconButton(onClick = { onStep(-1) }, modifier = Modifier.size(30.dp).semantics { contentDescription = "${c.label} verringern" }) { Text("−") }
            Text(
                "${fmt(cur, c.decimals)}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold,
                color = if (changed) scheme.primary else scheme.onSurface, modifier = Modifier.padding(horizontal = 6.dp)
            )
            FilledTonalIconButton(onClick = { onStep(1) }, modifier = Modifier.size(30.dp).semantics { contentDescription = "${c.label} erhöhen" }) { Text("+") }
        }
        Text(c.unit + if (closing) " · ↻ = zu" else " · ↻ = mehr", style = MaterialTheme.typography.labelSmall, color = scheme.onSurfaceVariant)
        Text("Serie ${fmt(c.stock, c.decimals)}" + if (unsure) " (unsicher)" else "", style = MaterialTheme.typography.labelSmall, color = if (unsure) lab.warn else scheme.onSurfaceVariant)
    }
}
