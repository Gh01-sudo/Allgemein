package de.k7labor.fahrwerk.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.k7labor.fahrwerk.physics.K7
import de.k7labor.fahrwerk.physics.Physics
import de.k7labor.fahrwerk.physics.ScenarioResult
import de.k7labor.fahrwerk.physics.Setup
import de.k7labor.fahrwerk.physics.Vehicle
import de.k7labor.fahrwerk.physics.fmt
import kotlin.math.cos
import kotlin.math.sin

/**
 * Maßstäbliche Seitenansicht aus dem aufgezeichneten Verlauf. Die Übertreibung (×3) vergrößert nur
 * die Bewegung in der Zeichnung; alle Zahlen sind echte Werte (Review-Punkt 7).
 */
@Composable
fun BikeView(res: ScenarioResult, setup: Setup, frame: Int, exaggerate: Boolean, modifier: Modifier = Modifier) {
    val veh = remember(setup) { Vehicle(setup, Physics.calibration) }
    val measurer = rememberTextMeasurer()
    val lab = LocalLabColors.current
    val fg = MaterialTheme.colorScheme.onSurface
    val muted = MaterialTheme.colorScheme.onSurfaceVariant
    val primary = MaterialTheme.colorScheme.primary
    val tr = res.trace
    val i = frame.coerceIn(0, tr.size - 1)
    val k = if (exaggerate) 3.0 else 1.0
    fun ex(arr: FloatArray) = arr[0] + (arr[i] - arr[0]) * k
    val x = tr.qx[i].toDouble()
    val z = ex(tr.qz).toDouble(); val th = ex(tr.qth).toDouble(); val s = ex(tr.qs).toDouble(); val phi = ex(tr.qphi).toDouble()
    val pose = Physics.pose(veh, x, z, th, s, phi)
    val real = Physics.pose(veh, x, tr.qz[i].toDouble(), tr.qth[i].toDouble(), tr.qs[i].toDouble(), tr.qphi[i].toDouble())
    val desc = "Seitenansicht: Gabel ${fmt(tr.sF[i].toDouble(), 0)} mm, Federbein ${fmt(tr.xR[i].toDouble(), 0)} mm, Nickwinkel ${fmt(tr.pitch[i].toDouble(), 1)} Grad"

    Canvas(modifier.fillMaxWidth().height(230.dp).semantics { contentDescription = desc }) {
        val span = 2.55
        val sc = size.width / span.toFloat()
        val cx = (pose.frontAxle.first + pose.rearAxle.first) / 2
        val groundY = size.height - 26.dp.toPx()
        fun P(wx: Double, wz: Double) = Offset(((wx - cx) * sc + size.width / 2).toFloat(), (groundY - wz * sc).toFloat())
        val c = cos(th); val sn = sin(th)
        fun B(bx: Double, bz: Double) = P(x + c * bx - sn * bz, z + sn * bx + c * bz)

        // Straße
        val road = Path()
        val n = 120
        for (j in 0..n) {
            val wx = cx - span / 2 + span * j / n
            val hgt = res.road(wx) * k
            val p = P(wx, hgt)
            if (j == 0) road.moveTo(p.x, p.y) else road.lineTo(p.x, p.y)
        }
        road.lineTo(size.width, size.height); road.lineTo(0f, size.height); road.close()
        drawPath(road, lab.road.copy(alpha = 0.25f), style = Fill)

        val (fx, fz) = pose.frontAxle
        val (rx, rz) = pose.rearAxle
        // Räder
        for ((ax, az, r) in listOf(Triple(fx, fz, veh.rf), Triple(rx, rz, veh.rr))) {
            drawCircle(fg, radius = (r * sc).toFloat(), center = P(ax, az), style = Stroke(width = 9.dp.toPx() * (r / 0.3).toFloat() * 0.6f))
            drawCircle(muted, radius = (0.2159 * sc).toFloat(), center = P(ax, az), style = Stroke(width = 2.dp.toPx()))
            drawCircle(fg, radius = 3.dp.toPx(), center = P(ax, az))
        }
        // Gabel entlang der Lenkachse (versetzt um den Offset)
        val rake = pose.rakeDeg * Math.PI / 180
        val ux = -sin(rake); val uz = cos(rake)
        val top = Pair(fx + ux * 0.62, fz + uz * 0.62)
        drawLine(lab.front, P(fx, fz), P(fx + ux * 0.30, fz + uz * 0.30), 7.dp.toPx(), StrokeCap.Round)
        drawLine(lab.front.copy(alpha = 0.7f), P(fx + ux * 0.22, fz + uz * 0.22), P(top.first, top.second), 4.dp.toPx(), StrokeCap.Round)
        // Rahmen (körperfest)
        // Silhouette in Weltkoordinaten der Bezugslage (x ab Hinterachse), körperfest mitbewegt
        val outline = listOf(
            1.13 to 0.86, 0.95 to 0.93, 0.72 to 0.86, 0.49 to 0.81, 0.12 to 0.92, 0.20 to 0.80,
            veh.pivotX to veh.pivotZ, 0.85 to 0.30, 1.10 to 0.42, 1.12 to 0.70
        )
        val frame = Path()
        outline.forEachIndexed { j, (wx, wz) -> val p = B(wx - veh.pivotX, wz - veh.pivotZ); if (j == 0) frame.moveTo(p.x, p.y) else frame.lineTo(p.x, p.y) }
        frame.close()
        drawPath(frame, primary.copy(alpha = 0.18f), style = Fill)
        drawPath(frame, primary, style = Stroke(width = 2.dp.toPx()))
        // Schwinge und Federbein
        val (px, pz) = pose.pivot
        drawLine(lab.rear, P(px, pz), P(rx, rz), 6.dp.toPx(), StrokeCap.Round)
        val beta = th + phi + Math.PI
        val sx = px + 0.22 * veh.ls * cos(beta); val sz = pz + 0.22 * veh.ls * sin(beta)
        val shockTop = B(-0.06, 0.26)
        val bottom = P(sx, sz)
        drawLine(lab.rear.copy(alpha = 0.7f), bottom, shockTop, 3.dp.toPx())
        val spring = Path()
        val m = 9
        for (j in 0..m) {
            val f = 0.15f + 0.7f * j / m
            val bx = bottom.x + (shockTop.x - bottom.x) * f; val by = bottom.y + (shockTop.y - bottom.y) * f
            val off = if (j % 2 == 0) 6.dp.toPx() else -6.dp.toPx()
            if (j == 0) spring.moveTo(bx + off, by) else spring.lineTo(bx + off, by)
        }
        drawPath(spring, lab.rear, style = Stroke(width = 2.dp.toPx()))
        // Fahrerschwerpunkt
        val riderX = K7.RIDER_X_SHARE * veh.wb - veh.pivotX; val riderZ = K7.RIDER_H - veh.pivotZ
        if (setup.rider > 0) drawCircle(lab.extra, radius = 6.dp.toPx(), center = B(riderX, riderZ))
        drawCircle(fg, radius = 4.dp.toPx(), center = B(veh.cgX, veh.cgZ))

        // Beschriftung mit echten Werten
        val st = TextStyle(fontSize = 11.sp, color = fg)
        val lines = listOf(
            "t ${fmt(tr.t[i].toDouble(), 2)} s · ${fmt(tr.speed[i].toDouble(), 0)} km/h",
            "Gabel ${fmt(tr.sF[i].toDouble(), 0)} / 125 mm · Heck ${fmt(tr.xR[i].toDouble(), 0)} / 130 mm",
            "Radlast vorn ${fmt(tr.fzF[i] / 1000.0, 2)} kN · hinten ${fmt(tr.fzR[i] / 1000.0, 2)} kN",
            "Nicken ${fmt(tr.pitch[i].toDouble(), 2)}° · Lenkkopf ${fmt(real.rakeDeg, 2)}° · Nachlauf ${fmt(real.trailMm, 0)} mm" + if (tr.lean[i] > 0.5f) " · Schräglage ${fmt(tr.lean[i].toDouble(), 0)}°" else ""
        )
        var y = 4.dp.toPx()
        for (l in lines) { val t = measurer.measure(l, st); drawText(t, topLeft = Offset(6.dp.toPx(), y)); y += t.size.height }
        if (exaggerate) {
            val t = measurer.measure("Bewegung ×3 gezeichnet, Zahlen echt", TextStyle(fontSize = 10.sp, color = lab.warn))
            drawText(t, topLeft = Offset(size.width - t.size.width - 6.dp.toPx(), size.height - t.size.height - 2.dp.toPx()))
        }
    }
}
