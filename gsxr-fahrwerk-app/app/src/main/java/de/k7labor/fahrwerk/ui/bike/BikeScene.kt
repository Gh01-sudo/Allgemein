package de.k7labor.fahrwerk.ui.bike

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.k7labor.fahrwerk.physics.Physics
import de.k7labor.fahrwerk.physics.Vehicle
import de.k7labor.fahrwerk.physics.fmt
import de.k7labor.fahrwerk.physics.shockStroke
import de.k7labor.fahrwerk.ui.LocalLabColors
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sqrt

/** Letzte Abbildung, damit Antippen auf das gezeichnete Bild zurückgerechnet werden kann. */
private class SceneLayout { var mapper: Mapper? = null; var frame: BikeFrame? = null }

/**
 * Interaktive Seitenansicht.
 * - Antippen: Bauteil oder Einsteller wählen. Doppeltippen: Zoom zurück.
 * - Zwei Finger: zoomen und verschieben. Gezoomt: ein Finger verschiebt.
 * - Ungezoomt waagrecht wischen: Zeitleiste ([onScrub] bekommt den Anteil der Breite).
 *
 * @param draw gezeichneter Zustand (ggf. mit vergrößerter Bewegung), [real] echte Werte für die Beschriftung.
 */
@Composable
fun BikeScene(
    veh: Vehicle,
    draw: SceneState,
    real: SceneState,
    road: (Double) -> Double,
    roadK: Double,
    ghost: Pair<Vehicle, SceneState>?,
    technical: Boolean,
    selected: BikePart?,
    onSelect: (BikePart?) -> Unit,
    onScrub: ((Float) -> Unit)?,
    exaggerated: Boolean,
    modifier: Modifier = Modifier,
    height: Dp = 300.dp,
    showHeader: Boolean = true
) {
    val lab = LocalLabColors.current
    val scheme = MaterialTheme.colorScheme
    val dark = scheme.surface.luminanceSimple() < 0.4f
    val colors = remember(lab, scheme.tertiary, dark) {
        BikeColors(
            accent = Color(0xFFE53935), front = lab.front, rear = lab.rear,
            highlight = scheme.tertiary, text = if (dark) Color(0xFFECECEC) else Color(0xFF202020)
        )
    }
    val sky = if (dark) Color(0xFF1B2430) else Color(0xFFCFE3F3)
    val skyLow = if (dark) Color(0xFF2C3440) else Color(0xFFEFF5F8)
    val hill = if (dark) Color(0xFF34404A) else Color(0xFFB4C8B0)
    val measurer = rememberTextMeasurer()
    val layout = remember { SceneLayout() }
    var zoom by remember { mutableFloatStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }
    var boxSize by remember { mutableStateOf(Size.Zero) }
    val slop = LocalViewConfiguration.current.touchSlop
    val curSelected by rememberUpdatedState(selected)
    val curOnSelect by rememberUpdatedState(onSelect)
    val curOnScrub by rememberUpdatedState(onScrub)

    fun clampPan(p: Offset, z: Float): Offset {
        if (boxSize.width <= 0f) return p
        return Offset(p.x.coerceIn(boxSize.width * (1 - z), 0f), p.y.coerceIn(boxSize.height * (1 - z), 0f))
    }
    fun zoomAt(c: Offset, factor: Float) {
        val nz = (zoom * factor).coerceIn(1f, 6f)
        val f = nz / zoom
        pan = clampPan(Offset(c.x - (c.x - pan.x) * f, c.y - (c.y - pan.y) * f), nz)
        zoom = nz
    }

    val desc = "Seitenansicht ${if (technical) "Technik" else "außen"}: Gabel ${fmt(real.sFmm, 0)} mm, Heck ${fmt(real.xRmm, 0)} mm, Radlast vorn ${fmt(real.fzF / 1000, 2)} kN, hinten ${fmt(real.fzR / 1000, 2)} kN" +
        (selected?.let { ", gewählt: ${it.title}" } ?: "")

    Box(modifier.fillMaxWidth().height(height).clipToBounds()) {
        Canvas(
            Modifier.fillMaxWidth().height(height)
                .semantics { contentDescription = desc }
                .onSizeChanged { boxSize = it.toSize() }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { pos ->
                            val m = layout.mapper; val fr = layout.frame
                            if (m != null && fr != null) {
                                val base = Offset((pos.x - pan.x) / zoom, (pos.y - pan.y) / zoom)
                                val w = m.inv(base)
                                val tol = 0.055 / sqrt(zoom.toDouble())
                                val hit = fr.hit(w, tol)
                                curOnSelect(if (hit == curSelected) null else hit)
                            }
                        },
                        onDoubleTap = { zoom = 1f; pan = Offset.Zero }
                    )
                }
                .pointerInput(onScrub != null) {
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false)
                        var mode = 0  // 0 offen, 1 Zeitleiste, 2 Zoom, 3 Verschieben, -1 abgegeben (Liste scrollt)
                        var acc = Offset.Zero
                        do {
                            val ev = awaitPointerEvent()
                            val pressed = ev.changes.count { it.pressed }
                            if (pressed >= 2 && mode != -1) mode = 2
                            when (mode) {
                                0 -> {
                                    acc += ev.changes.first().positionChange()
                                    if (acc.getDistance() > slop) {
                                        mode = when {
                                            zoom > 1.01f -> 3
                                            curOnScrub != null && abs(acc.x) > abs(acc.y) * 1.3f -> 1
                                            else -> -1
                                        }
                                    }
                                }
                                1 -> {
                                    val dx = ev.changes.first().positionChange().x
                                    if (size.width > 0) curOnScrub?.invoke(dx / size.width)
                                    ev.changes.forEach { it.consume() }
                                }
                                2 -> {
                                    val z = ev.calculateZoom(); val pn = ev.calculatePan(); val cen = ev.calculateCentroid()
                                    // Beim Loslassen des letzten Fingers ist der Mittelpunkt „unspecified“ (NaN): nicht zoomen, sonst wird pan NaN
                                    if (cen.isSpecified && z.isFinite() && z > 0f) zoomAt(cen, z)
                                    pan = clampPan(pan + pn, zoom)
                                    ev.changes.forEach { it.consume() }
                                }
                                3 -> {
                                    pan = clampPan(pan + ev.changes.first().positionChange(), zoom)
                                    ev.changes.forEach { it.consume() }
                                }
                            }
                        } while (ev.changes.any { it.pressed })
                    }
                }
        ) {
            val span = 2.6
            val groundY = size.height - 34.dp.toPx()
            val sc = min(size.width / span, (groundY - 10.dp.toPx()) / 1.5)
            val frame = BikeFrame(veh, draw)
            val m = Mapper((frame.frontAxle.x + frame.rearAxle.x) / 2, sc, size.width, groundY)
            layout.mapper = m; layout.frame = frame
            val ground: (Double) -> Double = { road(it) * roadK }
            val opts = SceneOptions(technical, selected, veh.st.rider, veh.st.passenger, roadK, sky, skyLow, hill)

            withTransform({ translate(pan.x, pan.y); scale(zoom, zoom, Offset.Zero) }) {
                drawRoad(m, road, opts, size)
                ghost?.let { (gv, gs) -> drawGhost(BikeFrame(gv, gs), m, Color(0xCC8A8F96)) }
                drawBike(frame, m, colors, opts, ground)
            }

            fun scr(v: V2): Offset { val b = m.p(v); return Offset(b.x * zoom + pan.x, b.y * zoom + pan.y) }
            // Einsteller-Markierungen (bleiben beim Zoomen gleich groß)
            BikeFrame.ADJUSTERS.forEachIndexed { i, a ->
                val p = scr(frame.adjusterPos(i))
                val sel = selected == a.part
                drawCircle(if (sel) colors.highlight else Color(0xE6FFFFFF), 8.dp.toPx(), p)
                drawCircle(Color(0xFF202020), 8.dp.toPx(), p, style = Stroke(1.5.dp.toPx()))
                drawLine(Color(0xFF202020), Offset(p.x, p.y - 5.dp.toPx()), Offset(p.x, p.y - 1.dp.toPx()), 2.dp.toPx())
            }
            // Beschriftung mit echten Werten
            val txt = colors.text
            val bg = if (dark) Color(0xB3121212) else Color(0xCCFFFFFF)
            fun label(text: String, at: Offset, color: Color = txt, bold: Boolean = false) {
                val t = measurer.measure(text, TextStyle(fontSize = 10.sp, color = color, fontWeight = if (bold) FontWeight.SemiBold else FontWeight.Normal))
                // Text breiter als die Fläche (große Schrift, schmales Fenster): links anlegen statt leerer Bereich → Absturz
                val tl = Offset(
                    at.x.coerceIn(2f, (size.width - t.size.width - 2f).coerceAtLeast(2f)),
                    at.y.coerceIn(2f, (size.height - t.size.height - 2f).coerceAtLeast(2f))
                )
                drawRoundRect(bg, topLeft = Offset(tl.x - 3, tl.y - 1), size = Size(t.size.width + 6f, t.size.height + 2f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f))
                drawText(t, topLeft = tl)
            }
            if (showHeader) {
                val pose = Physics.pose(veh, real.x, real.z, real.th, real.s, real.phi)
                val head = buildList {
                    if (real.speedKmh > 0.5) add("t ${fmt(real.t, 2)} s · ${fmt(real.speedKmh, 0)} km/h" + if (real.leanDeg > 0.5) " · Schräglage ${fmt(real.leanDeg, 0)}°" else "")
                    add("Nicken ${fmt(real.pitchDeg, 2)}° · Lenkkopf ${fmt(pose.rakeDeg, 2)}° · Nachlauf ${fmt(pose.trailMm, 0)} mm")
                }
                var y = 4.dp.toPx()
                for (l in head) { label(l, Offset(6.dp.toPx(), y)); y += 15.dp.toPx() }
            }
            // Messwerte an den Bauteilen
            val fork = scr(frame.forkCap)
            label("Gabel ${fmt(real.sFmm, 0)} / 125 mm", Offset(fork.x - 30.dp.toPx(), fork.y - 58.dp.toPx()), colors.front, true)
            val sh = scr(frame.shockTop)
            label("Heck ${fmt(real.xRmm, 0)} / 130 mm" + if (technical) " · Federbein ${fmt(shockStroke(real.xRmm.coerceAtLeast(0.0)), 0)} mm" else "", Offset(sh.x - 40.dp.toPx(), sh.y - 34.dp.toPx()), colors.rear, true)
            val fa = scr(V2(frame.frontAxle.x, ground(frame.frontAxle.x)))
            val ra = scr(V2(frame.rearAxle.x, ground(frame.rearAxle.x)))
            label("${fmt(real.fzF / 1000, 2)} kN", Offset(fa.x - 20.dp.toPx(), fa.y + 6.dp.toPx()), colors.front)
            label("${fmt(real.fzR / 1000, 2)} kN", Offset(ra.x - 20.dp.toPx(), ra.y + 6.dp.toPx()), colors.rear)
            if (ghost != null) label("- - Serie", Offset(size.width - 62.dp.toPx(), 4.dp.toPx()), Color(0xFF8A8F96))
            if (exaggerated) label("Bewegung ×3 gezeichnet, Zahlen echt", Offset(6.dp.toPx(), size.height), lab.warn)
            if (zoom > 1.01f) label("Zoom ${fmt(zoom.toDouble(), 1)}×", Offset(size.width - 70.dp.toPx(), 62.dp.toPx()))
        }
        androidx.compose.foundation.layout.Row(Modifier.align(Alignment.TopEnd).padding(end = 4.dp, top = 24.dp)) {
            FilledTonalIconButton(onClick = { zoomAt(Offset(boxSize.width / 2, boxSize.height / 2), 1.5f) }, modifier = Modifier.size(34.dp).semantics { contentDescription = "Hineinzoomen" }) { Text("+") }
            FilledTonalIconButton(onClick = { zoomAt(Offset(boxSize.width / 2, boxSize.height / 2), 1 / 1.5f) }, modifier = Modifier.size(34.dp).semantics { contentDescription = "Herauszoomen" }) { Text("−") }
            if (zoom > 1.01f) FilledTonalIconButton(onClick = { zoom = 1f; pan = Offset.Zero }, modifier = Modifier.size(34.dp).semantics { contentDescription = "Zoom zurücksetzen" }) { Text("⟲") }
        }
    }
}

private fun Color.luminanceSimple() = 0.2126f * red + 0.7152f * green + 0.0722f * blue

/** Kleine Hinweiszeile unter der Szene. */
@Composable
fun SceneHint(text: String) {
    Text(
        text, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 4.dp).background(MaterialTheme.colorScheme.surfaceContainerHighest, RoundedCornerShape(6.dp)).padding(horizontal = 6.dp, vertical = 2.dp)
    )
}
