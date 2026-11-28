package de.k7labor.fahrwerk.ui.bike

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import de.k7labor.fahrwerk.physics.K7
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

/** Abbildung Welt (Meter) → Zeichenfläche (Pixel) vor Zoom und Verschiebung. */
class Mapper(val camX: Double, val sc: Double, val w: Float, val groundY: Float) {
    fun p(v: V2) = Offset(((v.x - camX) * sc + w / 2).toFloat(), (groundY - v.z * sc).toFloat())
    fun p(x: Double, z: Double) = Offset(((x - camX) * sc + w / 2).toFloat(), (groundY - z * sc).toFloat())
    fun inv(o: Offset) = V2((o.x - w / 2) / sc + camX, (groundY - o.y) / sc)
    fun len(m: Double) = (m * sc).toFloat()
}

/** Feste Farben des Motorrads (physische Farben, unabhängig vom Hell/Dunkel-Modus). */
class BikeColors(
    val paint: Color = Color(0xFF2C3A55),
    val paintLight: Color = Color(0xFF4F6690),
    val panel: Color = Color(0xFFDADEE4),
    val accent: Color,
    val dark: Color = Color(0xFF1C1D20),
    val seat: Color = Color(0xFF151515),
    val metal: Color = Color(0xFFB5BAC0),
    val metalDark: Color = Color(0xFF6C7279),
    val alu: Color = Color(0xFF9EA5AD),
    val gold: Color = Color(0xFFC8A03A),
    val tyre: Color = Color(0xFF121212),
    val rim: Color = Color(0xFF2A2D33),
    val disc: Color = Color(0xFFA9AEB4),
    val exhaust: Color = Color(0xFF8E7B6A),
    val muffler: Color = Color(0xFFBFC4CA),
    val leather: Color = Color(0xFF26272B),
    val glass: Color = Color(0x663A4A5A),
    val lamp: Color = Color(0xFFF3F0DA),
    val tail: Color = Color(0xFFD32F2F),
    val front: Color,
    val rear: Color,
    val highlight: Color,
    val text: Color
)

class SceneOptions(
    val technical: Boolean,
    val selected: BikePart?,
    val riderKg: Double,
    val passengerKg: Double,
    val roadK: Double,
    val sky: Color,
    val skyLow: Color,
    val hill: Color
)

private fun smooth(pts: List<Offset>, closed: Boolean = true): Path {
    val path = Path()
    if (pts.size < 3) { pts.firstOrNull()?.let { path.moveTo(it.x, it.y) }; pts.drop(1).forEach { path.lineTo(it.x, it.y) }; return path }
    fun mid(a: Offset, b: Offset) = Offset((a.x + b.x) / 2, (a.y + b.y) / 2)
    val n = pts.size
    if (closed) {
        val start = mid(pts[n - 1], pts[0])
        path.moveTo(start.x, start.y)
        for (i in 0 until n) {
            val c = pts[i]; val e = mid(pts[i], pts[(i + 1) % n])
            path.quadraticTo(c.x, c.y, e.x, e.y)
        }
        path.close()
    } else {
        path.moveTo(pts[0].x, pts[0].y)
        for (i in 1 until n - 1) { val e = mid(pts[i], pts[i + 1]); path.quadraticTo(pts[i].x, pts[i].y, e.x, e.y) }
        path.lineTo(pts[n - 1].x, pts[n - 1].y)
    }
    return path
}

/** Kanten leicht brechen (Chaikin mit kleinem Schnitt): Verkleidung bleibt kantig wie echte Kunststoffteile. */
private fun crisp(pts: List<Offset>, cut: Float = 0.12f): Path {
    var q = pts
    repeat(2) {
        val r = ArrayList<Offset>(q.size * 2)
        for (i in q.indices) {
            val a = q[i]; val b = q[(i + 1) % q.size]
            r += Offset(a.x + (b.x - a.x) * cut, a.y + (b.y - a.y) * cut)
            r += Offset(a.x + (b.x - a.x) * (1 - cut), a.y + (b.y - a.y) * (1 - cut))
        }
        q = r
    }
    val p = Path(); p.moveTo(q[0].x, q[0].y); for (o in q.drop(1)) p.lineTo(o.x, o.y); p.close()
    return p
}

private fun hash(i: Int): Double {
    var h = i * 374761393 + 668265263
    h = (h xor (h ushr 13)) * 1274126177
    return ((h xor (h ushr 16)) and 0xFFFF) / 65535.0
}

/** Himmel, Hügel mit Parallaxe und Straße mit Asphalt, Mittellinie und Fugen. */
fun DrawScope.drawRoad(m: Mapper, road: (Double) -> Double, o: SceneOptions, size: Size) {
    val strip = m.len(0.22)
    drawRect(Brush.verticalGradient(listOf(o.sky, o.skyLow), startY = 0f, endY = m.groundY), size = Size(size.width, m.groundY))
    // Zwei Hügelketten, langsamer als die Straße (Tiefe)
    for ((par, amp, col) in listOf(Triple(0.06, 0.55, o.hill.copy(alpha = o.hill.alpha * 0.6f)), Triple(0.16, 0.35, o.hill))) {
        val hp = Path()
        hp.moveTo(0f, m.groundY - strip)
        val steps = 48
        for (j in 0..steps) {
            val sx = size.width * j / steps
            val wx = m.camX * par + (sx - size.width / 2) / m.sc
            val h = amp * (0.55 + 0.25 * sin(wx * 0.9) + 0.15 * sin(wx * 2.3 + 1.3) + 0.05 * sin(wx * 5.1))
            hp.lineTo(sx, (m.groundY - strip - h * m.sc * 0.5).toFloat())
        }
        hp.lineTo(size.width, m.groundY - strip); hp.close()
        drawPath(hp, col)
    }
    val x0 = m.inv(Offset(0f, 0f)).x - 0.1
    val x1 = m.inv(Offset(size.width, 0f)).x + 0.1
    val n = 140
    val k = o.roadK
    fun surf(wx: Double) = road(wx) * k
    // Fahrbahnoberfläche (nach hinten laufender Streifen) und Seitenansicht des Belags
    val top = Path(); val face = Path()
    for (j in 0..n) {
        val wx = x0 + (x1 - x0) * j / n
        val p = m.p(wx, surf(wx))
        if (j == 0) { top.moveTo(p.x, p.y - strip); face.moveTo(p.x, p.y) } else { top.lineTo(p.x, p.y - strip); face.lineTo(p.x, p.y) }
    }
    for (j in n downTo 0) { val wx = x0 + (x1 - x0) * j / n; val p = m.p(wx, surf(wx)); top.lineTo(p.x, p.y) }
    top.close()
    face.lineTo(size.width, size.height); face.lineTo(0f, size.height); face.close()
    drawPath(top, Brush.verticalGradient(listOf(Color(0xFF55585C), Color(0xFF66696D)), startY = m.groundY - strip, endY = m.groundY))
    drawPath(face, Brush.verticalGradient(listOf(Color(0xFF3B3D40), Color(0xFF2A2B2D)), startY = m.groundY, endY = size.height))
    // Gesteinskörnung: feste Punkte in Weltkoordinaten, laufen mit der Fahrt
    val cell = 0.035
    var ci = floor(x0 / cell).toInt()
    while (ci * cell < x1) {
        for (row in 0 until 4) {
            val hsh = hash(ci * 7 + row * 1013)
            val wx = (ci + hash(ci * 13 + row * 31)) * cell
            val base = m.p(wx, surf(wx))
            val dy = if (row < 2) -((0.08 + 0.84 * hash(ci * 17 + row * 57)) * strip).toFloat() else (6.0 + 30.0 * hash(ci * 19 + row)).toFloat()
            val c = if (hsh > 0.5) Color(0x33FFFFFF) else Color(0x33000000)
            drawCircle(c, radius = (1.0 + 1.6 * hsh).toFloat(), center = Offset(base.x, base.y + dy))
        }
        ci++
    }
    // Leitlinie (Landstraße: 6 m Strich, 12 m Lücke) auf der Fahrbahn
    var d = floor(x0 / 18.0) * 18.0
    while (d < x1) {
        val a = max(d, x0); val b = min(d + 6.0, x1)
        if (b > a) {
            val pa = m.p(a, surf(a)); val pb = m.p(b, surf(b))
            drawLine(Color(0xDDF2F2F2), Offset(pa.x, pa.y - strip * 0.55f), Offset(pb.x, pb.y - strip * 0.55f), strip * 0.12f)
        }
        d += 18.0
    }
    // Querfugen alle 4,5 m
    var f = floor(x0 / 4.5) * 4.5
    while (f < x1) { val p = m.p(f, surf(f)); drawLine(Color(0x55000000), Offset(p.x + strip * 0.35f, p.y - strip), p, 1.5f); f += 4.5 }
    // Kante
    val edge = Path()
    for (j in 0..n) { val wx = x0 + (x1 - x0) * j / n; val p = m.p(wx, surf(wx)); if (j == 0) edge.moveTo(p.x, p.y) else edge.lineTo(p.x, p.y) }
    drawPath(edge, Color(0xFF8A8D91), style = Stroke(width = 2f))
}

/** Reifen, der sich an der Fahrbahn abplattet (Einfederung des Reifens sichtbar). */
private fun DrawScope.tyrePath(m: Mapper, axle: V2, r: Double, ground: (Double) -> Double): Path {
    val pts = ArrayList<Offset>(64)
    for (j in 0 until 64) {
        val a = 2 * PI * j / 64
        var px = axle.x + r * cos(a); var pz = axle.z + r * sin(a)
        val g = ground(px)
        if (pz < g) pz = g
        pts += m.p(px, pz)
    }
    val p = Path(); p.moveTo(pts[0].x, pts[0].y); for (q in pts.drop(1)) p.lineTo(q.x, q.y); p.close()
    return p
}

private fun DrawScope.wheel(m: Mapper, c: BikeColors, axle: V2, r: Double, rot: Double, front: Boolean, ground: (Double) -> Double, tech: Boolean) {
    val alpha = if (tech) 0.55f else 1f
    drawPath(tyrePath(m, axle, r, ground), c.tyre.copy(alpha = alpha))
    // Flanke und Profilrillen (drehen sich mit)
    drawCircle(Color(0xFF2B2B2B).copy(alpha = alpha), radius = m.len(r - 0.012), center = m.p(axle), style = Stroke(m.len(0.008)))
    for (j in 0 until 10) {
        val a = rot + 2 * PI * j / 10
        val a2 = a + 0.10
        val p1 = m.p(axle.x + (r - 0.004) * cos(a), axle.z + (r - 0.004) * sin(a))
        val p2 = m.p(axle.x + (r - 0.022) * cos(a2), axle.z + (r - 0.022) * sin(a2))
        drawLine(Color(0xFF3A3A3A).copy(alpha = alpha), p1, p2, m.len(0.006))
    }
    val rim = 0.2159
    drawCircle(c.rim.copy(alpha = alpha), radius = m.len(rim), center = m.p(axle))
    drawCircle(c.accent.copy(alpha = alpha), radius = m.len(rim - 0.006), center = m.p(axle), style = Stroke(m.len(0.004)))
    drawCircle(Color(0xFF0D0D0D).copy(alpha = alpha), radius = m.len(rim - 0.018), center = m.p(axle))
    // Speichen: 3 Doppelspeichen
    for (j in 0 until 3) for (sgn in listOf(-1, 1)) {
        val a = rot + 2 * PI * j / 3 + sgn * 0.09
        drawLine(c.rim.copy(alpha = alpha), m.p(axle.x + 0.05 * cos(a - sgn * 0.12), axle.z + 0.05 * sin(a - sgn * 0.12)), m.p(axle.x + (rim - 0.02) * cos(a), axle.z + (rim - 0.02) * sin(a)), m.len(0.022), StrokeCap.Round)
    }
    // Bremsscheibe mit Bohrungen
    val dr = if (front) 0.155 else 0.110
    drawCircle(c.disc.copy(alpha = alpha), radius = m.len(dr), center = m.p(axle), style = Stroke(m.len(if (front) 0.038 else 0.030)))
    val holes = if (front) 18 else 12
    for (j in 0 until holes) {
        val a = rot + 2 * PI * j / holes
        val rr = dr - (if (front) 0.004 else 0.003) * (j % 2)
        drawCircle(Color(0xFF55595E).copy(alpha = alpha), radius = m.len(0.0045), center = m.p(axle.x + rr * cos(a), axle.z + rr * sin(a)))
    }
    drawCircle(c.metalDark.copy(alpha = alpha), radius = m.len(dr - 0.04), center = m.p(axle), style = Stroke(m.len(0.012)))
    drawCircle(c.metal.copy(alpha = alpha), radius = m.len(0.042), center = m.p(axle))
    drawCircle(c.metalDark.copy(alpha = alpha), radius = m.len(0.016), center = m.p(axle))
}

private fun DrawScope.caliper(m: Mapper, col: Color, axle: V2, r: Double, a0: Double, a1: Double, w: Double) {
    val p = Path()
    val n = 10
    for (j in 0..n) { val a = a0 + (a1 - a0) * j / n; val q = m.p(axle.x + r * cos(a), axle.z + r * sin(a)); if (j == 0) p.moveTo(q.x, q.y) else p.lineTo(q.x, q.y) }
    drawPath(p, col, style = Stroke(m.len(w), cap = StrokeCap.Round))
    drawPath(p, Color(0x33000000), style = Stroke(m.len(w * 0.35), cap = StrokeCap.Round))
}

private fun DrawScope.tube(m: Mapper, a: V2, b: V2, w: Double, col: Color, shine: Boolean = true) {
    drawLine(col, m.p(a), m.p(b), m.len(w), StrokeCap.Butt)
    if (shine) {
        val n = (b - a).norm(); val off = V2(-n.z, n.x) * (w * 0.22)
        drawLine(Color(0x55FFFFFF), m.p(a + off), m.p(b + off), m.len(w * 0.18), StrokeCap.Butt)
    }
}

private fun DrawScope.coil(m: Mapper, a: V2, b: V2, w: Double, turns: Int, col: Color, wire: Double) {
    val d = b - a; val n = d.norm(); val perp = V2(-n.z, n.x)
    val path = Path()
    val steps = turns * 2
    for (j in 0..steps) {
        val f = j.toDouble() / steps
        val side = if (j % 2 == 0) -0.5 else 0.5
        val p = m.p(a + d * f + perp * (w * side))
        if (j == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
    }
    drawPath(path, col, style = Stroke(m.len(wire), cap = StrokeCap.Round, join = StrokeJoin.Round))
}

/**
 * Zeichnet das komplette Motorrad. Außenansicht: lackierte Verkleidung, Fahrer. Technikansicht:
 * Verkleidung durchsichtig, Federn, Dämpfer, Kette, Motor und Kraftpfeile sichtbar.
 */
fun DrawScope.drawBike(f: BikeFrame, m: Mapper, c: BikeColors, o: SceneOptions, ground: (Double) -> Double) {
    val tech = o.technical
    val veh = f.veh
    fun B(x: Double, z: Double) = m.p(f.body(x, z))
    fun poly(vararg pts: Pair<Double, Double>) = crisp(pts.map { B(it.first, it.second) })
    val rotF = -f.st.x / veh.rf
    val rotR = -f.st.x / veh.rr
    val bodyAlpha = if (tech) 0.16f else 1f

    // Schatten
    val sh = m.p(f.cg.x, ground(f.cg.x))
    drawOval(Color(0x40000000), topLeft = Offset(sh.x - m.len(0.85), sh.y - m.len(0.11) - m.len(0.02)), size = Size(m.len(1.7), m.len(0.05)))

    // ---- Auspuff ----
    val header = smooth(listOf(B(1.18, 0.62), B(1.17, 0.40), B(1.07, 0.25), B(0.72, 0.22), B(0.53, 0.28), B(0.44, 0.45), B(0.34, 0.60)), closed = false)
    drawPath(header, c.exhaust.copy(alpha = if (tech) 0.5f else 1f), style = Stroke(m.len(0.045), cap = StrokeCap.Round))
    drawPath(header, Color(0x33FFFFFF), style = Stroke(m.len(0.012), cap = StrokeCap.Round))

    // ---- Hinterrad, Schwinge, Kette, Federbein ----
    wheel(m, c, f.rearAxle, veh.rr, rotR, false, ground, tech)
    val sa = (f.rearAxle - f.pivot).norm(); val nUp = V2(-sa.z, sa.x).let { if (it.z < 0) it * -1.0 else it }
    // Bremssattel hinten unter der Schwinge
    val aSa = BikeFrame.angleOf(f.pivot - f.rearAxle)
    caliper(m, c.metalDark, f.rearAxle, 0.10, aSa - 0.95, aSa - 0.55, 0.03)
    // Kette (nur Technik: sitzt auf der linken Seite)
    if (tech) {
        val rs = veh.rSprF; val rr = veh.rSprR
        drawCircle(c.metalDark, radius = m.len(rr), center = m.p(f.rearAxle), style = Stroke(m.len(0.012)))
        drawCircle(c.metalDark, radius = m.len(rs), center = m.p(f.countershaft), style = Stroke(m.len(0.010)))
        val (t1, t2) = BikeFrame.chainUpper(f.countershaft, rs, f.rearAxle, rr)
        drawLine(c.highlight.copy(alpha = 0.9f), m.p(t1), m.p(t2), m.len(0.010), StrokeCap.Round)
        val lo = BikeFrame.chainUpper(f.countershaft, -rs, f.rearAxle, -rr)
        drawLine(c.metalDark, m.p(lo.first), m.p(lo.second), m.len(0.008), StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f)))
    }
    // Schwinge (gebogen, nach hinten schmaler)
    run {
        val w0 = 0.075; val w1 = 0.05
        val mid = f.pivot + (f.rearAxle - f.pivot) * 0.45 + nUp * 0.03
        val pts = listOf(
            m.p(f.pivot + nUp * (w0 / 2)), m.p(mid + nUp * 0.035), m.p(f.rearAxle + nUp * (w1 / 2) + sa * 0.03),
            m.p(f.rearAxle - nUp * (w1 / 2) + sa * 0.03), m.p(mid - nUp * 0.04), m.p(f.pivot - nUp * (w0 / 2))
        )
        val p = smooth(pts)
        drawPath(p, Brush.linearGradient(listOf(c.metal, c.alu, c.metalDark), m.p(f.pivot + nUp * 0.04), m.p(f.pivot - nUp * 0.04)))
        drawPath(p, c.metalDark, style = Stroke(1.2f))
        drawCircle(c.metalDark, radius = m.len(0.022), center = m.p(f.rearAxle + sa * 0.015))
    }
    // Umlenkung
    val bracket = f.pivot + sa * (0.24 * veh.ls) - nUp * 0.035
    val frameAnchor = f.body(0.60, 0.30)
    drawLine(c.metalDark, m.p(f.shockBottom), m.p(f.linkPoint), m.len(0.022), StrokeCap.Round)
    drawLine(c.metalDark, m.p(f.linkPoint), m.p(bracket), m.len(0.022), StrokeCap.Round)
    drawLine(c.metalDark, m.p(f.shockBottom), m.p(bracket), m.len(0.016), StrokeCap.Round)
    drawLine(c.dark, m.p(f.linkPoint), m.p(frameAnchor), m.len(0.016), StrokeCap.Round)
    // Federbein
    run {
        val d = (f.shockBottom - f.shockTop).norm()
        val bodyEnd = f.shockTop + d * 0.17
        tube(m, bodyEnd, f.shockBottom - d * 0.02, 0.016, c.metal)                  // Kolbenstange
        tube(m, f.shockTop, bodyEnd, 0.048, c.dark)                                 // Dämpferkörper
        val res0 = f.shockTop + d * 0.02 + f.dir(0.055, 0.0); val res1 = res0 + d * 0.13
        tube(m, res0, res1, 0.036, c.metalDark)                                    // Ausgleichsbehälter
        drawLine(c.dark, m.p(f.shockTop + d * 0.03), m.p(res0), m.len(0.01))
        val seat0 = f.shockTop + d * 0.05; val seat1 = f.shockBottom - d * 0.035
        coil(m, seat0, seat1, 0.062, 9, c.rear, 0.011)
        tube(m, seat0 - d * 0.006, seat0 + d * 0.006, 0.075, c.gold, false)        // Vorspannmutter
        tube(m, seat1 - d * 0.006, seat1 + d * 0.006, 0.070, c.metalDark, false)
        drawCircle(c.metalDark, radius = m.len(0.014), center = m.p(f.shockTop))
        drawCircle(c.metalDark, radius = m.len(0.014), center = m.p(f.shockBottom))
    }

    // ---- Motor und Rahmen ----
    run {
        val eng = poly(0.98 to 0.66, 0.78 to 0.72, 0.60 to 0.60, 0.56 to 0.36, 0.70 to 0.27, 0.98 to 0.30, 1.08 to 0.45)
        if (tech) { drawPath(eng, c.metalDark.copy(alpha = 0.55f)); drawPath(eng, c.dark, style = Stroke(1.5f)) }
        else { drawPath(eng, Brush.linearGradient(listOf(Color(0xFF4A4E55), Color(0xFF26282C)), B(0.8, 0.72), B(0.8, 0.28))); drawPath(eng, Color(0xFF111111), style = Stroke(1.2f)) }
        drawCircle(c.metal.copy(alpha = if (tech) 0.6f else 0.8f), radius = m.len(0.07), center = B(0.70, 0.40))
        drawCircle(Color(0xFF1A1A1A), radius = m.len(0.03), center = B(0.70, 0.40))
    }
    val spar = smooth(listOf(B(1.12, 0.86), B(0.86, 0.79), B(0.66, 0.62), B(0.60, 0.44)), closed = false)
    drawPath(spar, Brush.linearGradient(listOf(c.alu, c.metal), B(1.1, 0.86), B(0.6, 0.44)), style = Stroke(m.len(0.07), cap = StrokeCap.Round))
    drawPath(spar, c.metalDark, style = Stroke(m.len(0.006)))
    drawCircle(c.metalDark, radius = m.len(0.026), center = m.p(f.pivot))
    drawCircle(c.metal, radius = m.len(0.012), center = m.p(f.pivot))
    // Fußraste
    drawCircle(c.metalDark, radius = m.len(0.018), center = B(0.42, 0.43))
    drawLine(c.metalDark, B(0.42, 0.43), B(0.50, 0.40), m.len(0.012), StrokeCap.Round)

    // ---- Gabel und Vorderrad ----
    wheel(m, c, f.frontAxle, veh.rf, rotF, true, ground, tech)
    // Kotflügel vorn (an der Gabel)
    run {
        val p = Path(); val n = 12
        val base = BikeFrame.angleOf(f.forkUp)
        for (j in 0..n) {
            val a = base - 0.95 + 1.6 * j / n
            val q = m.p(f.frontAxle.x + (veh.rf + 0.018) * cos(a), f.frontAxle.z + (veh.rf + 0.018) * sin(a))
            if (j == 0) p.moveTo(q.x, q.y) else p.lineTo(q.x, q.y)
        }
        drawPath(p, c.paint.copy(alpha = if (tech) 0.35f else 1f), style = Stroke(m.len(0.03), cap = StrokeCap.Round))
    }
    val up = f.forkUp
    val outerLen = 0.42
    val outerBot = f.forkCap - up * (0.035 + outerLen)
    val foot = f.frontAxle + up * 0.09
    // Bremssattel vorn (radial, hinter dem Gabelfuß)
    val aUp = BikeFrame.angleOf(up)
    caliper(m, c.gold, f.frontAxle, 0.135, aUp + 0.25, aUp + 0.70, 0.04)
    // Standrohr (gleitet ins Tauchrohr), Achsfuß
    tube(m, f.frontAxle + up * 0.03, outerBot + up * 0.02, 0.043, c.gold)
    tube(m, f.frontAxle - up * 0.03, foot, 0.06, c.dark)
    drawCircle(c.metal, radius = m.len(0.018), center = m.p(f.frontAxle))
    if (tech) {
        // Tauchrohr durchsichtig: Feder, Ölstand, Dämpferkartusche
        val capIn = f.forkCap - up * 0.035
        val rodTop = f.frontAxle + up * 0.33
        val air = (0.11 - veh.st.fOil / 1000) - (f.st.s - K7.REF_SAG_F_MM / 1000) * K7.AIR_PISTON / K7.OIL_AREA
        val oilTop = capIn - up * air.coerceIn(0.02, 0.4)
        tube(m, outerBot, oilTop, 0.054, Color(0x66C98A1E), false)
        tube(m, outerBot, capIn, 0.058, Color(0x22FFFFFF), false)
        drawLine(c.metalDark, m.p(outerBot + V2(-up.z, up.x) * 0.029), m.p(capIn + V2(-up.z, up.x) * 0.029), 1.5f)
        drawLine(c.metalDark, m.p(outerBot - V2(-up.z, up.x) * 0.029), m.p(capIn - V2(-up.z, up.x) * 0.029), 1.5f)
        tube(m, foot, rodTop, 0.012, c.metal)
        tube(m, rodTop - up * 0.01, rodTop + up * 0.012, 0.03, c.metalDark, false)
        coil(m, rodTop + up * 0.012, capIn, 0.036, 14, c.front, 0.006)
    } else {
        tube(m, outerBot, f.forkCap - up * 0.035, 0.058, Color(0xFF2E3238))
        tube(m, outerBot - up * 0.006, outerBot + up * 0.012, 0.062, Color(0xFF111111), false)   // Staubdichtung
    }
    tube(m, f.forkCap - up * 0.035, f.forkCap, 0.046, c.gold, false)                             // Gabeldeckel
    // Gabelbrücken, Lenkkopf, Lenkerstummel
    val lowerClamp = f.clamp - up * 0.16
    drawLine(c.alu, m.p(f.clamp), m.p(f.steeringHead), m.len(0.03), StrokeCap.Round)
    drawLine(c.alu, m.p(lowerClamp), m.p(lowerClamp + (f.steeringHead - f.clamp)), m.len(0.04), StrokeCap.Round)
    drawLine(c.metalDark, m.p(f.steeringHead), m.p(f.steeringHead - up * 0.16), m.len(0.035), StrokeCap.Round)
    val grip = f.clamp - up * 0.03 + f.dir(-0.10, -0.035)
    drawLine(c.dark, m.p(f.clamp - up * 0.03), m.p(grip), m.len(0.022), StrokeCap.Round)

    // ---- Verkleidung ----
    val paintBrush = Brush.linearGradient(listOf(c.paintLight, c.paint, c.paint), B(0.8, 1.05), B(0.8, 0.45))
    fun body(p: Path, brush: Brush, solid: Color) {
        if (tech) { drawPath(p, solid.copy(alpha = bodyAlpha)); drawPath(p, solid.copy(alpha = 0.7f), style = Stroke(1.6f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f)))) }
        else { drawPath(p, brush); drawPath(p, Color(0x66000000), style = Stroke(1.2f)) }
    }
    val tail = poly(0.62 to 0.80, 0.40 to 0.80, 0.12 to 0.88, -0.13 to 0.955, -0.115 to 0.895, 0.10 to 0.80, 0.32 to 0.72, 0.52 to 0.70)
    body(tail, paintBrush, c.paint)
    val mufflerP = poly(0.34 to 0.63, 0.31 to 0.70, 0.04 to 0.765, -0.03 to 0.725, -0.01 to 0.66, 0.29 to 0.585)
    if (!tech) { drawPath(mufflerP, Brush.linearGradient(listOf(Color(0xFFE3E6EA), c.muffler, Color(0xFF5D636A)), B(0.2, 0.76), B(0.2, 0.60))); drawPath(mufflerP, c.metalDark, style = Stroke(1.2f)) }
    else drawPath(mufflerP, c.muffler.copy(alpha = 0.6f), style = Stroke(1.4f))
    drawLine(c.dark.copy(alpha = if (tech) 0.4f else 1f), B(-0.03, 0.74), B(-0.01, 0.65), m.len(0.025), StrokeCap.Round)
    val seat = poly(0.63 to 0.885, 0.44 to 0.835, 0.30 to 0.845, 0.30 to 0.805, 0.48 to 0.80, 0.63 to 0.83)
    body(seat, Brush.linearGradient(listOf(Color(0xFF2A2A2A), c.seat), B(0.5, 0.86), B(0.5, 0.80)), c.seat)
    val tank = poly(1.11 to 0.93, 0.97 to 1.005, 0.78 to 0.99, 0.63 to 0.89, 0.66 to 0.82, 0.86 to 0.80, 1.06 to 0.81)
    body(tank, paintBrush, c.paint)
    val belly = poly(0.94 to 0.28, 0.62 to 0.255, 0.56 to 0.31, 0.86 to 0.40)
    body(belly, Brush.linearGradient(listOf(c.paint, c.dark), B(0.8, 0.4), B(0.8, 0.25)), c.paint)
    val side = poly(
        1.51 to 0.79, 1.48 to 0.92, 1.37 to 1.00, 1.22 to 0.985, 1.08 to 0.925, 0.98 to 0.84, 0.90 to 0.70,
        0.86 to 0.52, 0.89 to 0.36, 1.00 to 0.29, 1.16 to 0.33, 1.28 to 0.52, 1.42 to 0.68
    )
    body(side, paintBrush, c.paint)
    val panel = poly(1.20 to 0.42, 1.03 to 0.33, 0.93 to 0.39, 0.91 to 0.56, 1.00 to 0.66, 1.14 to 0.60)
    body(panel, Brush.linearGradient(listOf(Color.White, c.panel, Color(0xFFA9AEB5)), B(1.0, 0.66), B(1.0, 0.33)), c.panel)
    if (!tech) {
        // Zierstreifen, Lufteinlass, Kühlschlitze
        drawPath(smooth(listOf(B(1.47, 0.74), B(1.30, 0.66), B(1.12, 0.70), B(0.97, 0.80)), closed = false), c.accent, style = Stroke(m.len(0.012), cap = StrokeCap.Round))
        drawPath(smooth(listOf(B(0.60, 0.835), B(0.30, 0.79), B(0.02, 0.86)), closed = false), c.accent, style = Stroke(m.len(0.008), cap = StrokeCap.Round))
        for (j in 0 until 3) drawLine(Color(0x88000000), B(1.24 - j * 0.03, 0.80 - j * 0.025), B(1.13 - j * 0.03, 0.72 - j * 0.025), m.len(0.008), StrokeCap.Round)
        drawCircle(c.metalDark, radius = m.len(0.02), center = B(0.93, 0.995))
        drawLine(Color(0x55FFFFFF), B(1.05, 0.95), B(0.80, 0.975), m.len(0.008), StrokeCap.Round)
    }
    // Scheinwerfer, Scheibe, Rücklicht
    val lamp = poly(1.40 to 0.845, 1.475 to 0.80, 1.47 to 0.865, 1.415 to 0.885)
    drawPath(lamp, c.lamp.copy(alpha = if (tech) 0.4f else 1f)); drawPath(lamp, Color(0x88000000), style = Stroke(1f))
    val screen = poly(1.37 to 1.00, 1.215 to 1.135, 1.13 to 1.10, 1.25 to 0.985)
    drawPath(screen, c.glass); drawPath(screen, Color(0x55FFFFFF), style = Stroke(1.2f))
    drawLine(c.tail, B(-0.125, 0.945), B(-0.112, 0.905), m.len(0.016), StrokeCap.Round)
    // Kennzeichenhalter, Spiegel
    drawLine(c.dark.copy(alpha = if (tech) 0.4f else 1f), B(-0.06, 0.88), B(-0.21, 0.70), m.len(0.014), StrokeCap.Round)
    drawLine(c.dark, B(1.29, 0.99), B(1.25, 1.05), m.len(0.008))
    drawOval(c.paint, topLeft = B(1.21, 1.075), size = Size(m.len(0.08), m.len(0.035)))

    // ---- Fahrer ----
    if (o.passengerKg > 0) rider(m, f, c, tech, passenger = true)
    if (o.riderKg > 0) rider(m, f, c, tech, passenger = false)

    // ---- Technik: Schwerpunkte und Kräfte ----
    if (tech) {
        cgMark(m, f.cg, c.text)
        if (o.riderKg > 0) cgMark(m, f.riderCg, c.text.copy(alpha = 0.6f), 0.022)
        forceArrows(m, f, c, ground)
    }

    // Hervorhebung des gewählten Bauteils
    o.selected?.let { highlight(m, f, it, c.highlight) }
}

private fun DrawScope.rider(m: Mapper, f: BikeFrame, c: BikeColors, tech: Boolean, passenger: Boolean) {
    val a = if (tech) 0.22f else 1f
    fun B(x: Double, z: Double) = m.p(f.body(x, z))
    val suit = c.leather.copy(alpha = a)
    val dx = if (passenger) -0.30 else 0.0
    val dz = if (passenger) 0.05 else 0.0
    val hip = B(0.47 + dx, 0.87 + dz)
    val knee = if (passenger) B(0.34, 0.74) else B(0.70, 0.70)
    val ankle = if (passenger) B(0.20, 0.58) else B(0.44, 0.47)
    val toe = if (passenger) B(0.27, 0.55) else B(0.51, 0.42)
    val shoulder = if (passenger) B(0.32, 1.22) else B(0.82, 1.11)
    val head = if (passenger) B(0.38, 1.35) else B(0.955, 1.205)
    val elbow = if (passenger) B(0.44, 1.05) else B(0.94, 0.99)
    val hand = if (passenger) B(0.52, 0.98) else B(1.065, 0.925)
    drawLine(suit, hip, knee, m.len(0.13), StrokeCap.Round)
    drawLine(suit, knee, ankle, m.len(0.095), StrokeCap.Round)
    drawLine(Color(0xFF0E0E0E).copy(alpha = a), ankle, toe, m.len(0.07), StrokeCap.Round)
    drawLine(suit, hip, shoulder, m.len(0.22), StrokeCap.Round)
    if (!tech) {
        drawLine(c.accent.copy(alpha = 0.85f), hip, shoulder, m.len(0.03), StrokeCap.Round)
        drawLine(Color(0xFF3C3E44), knee, Offset(knee.x + (ankle.x - knee.x) * 0.25f, knee.y + (ankle.y - knee.y) * 0.25f), m.len(0.115), StrokeCap.Round) // Knieschleifer
    }
    drawLine(suit, shoulder, elbow, m.len(0.085), StrokeCap.Round)
    drawLine(suit, elbow, hand, m.len(0.072), StrokeCap.Round)
    // Helm mit Visier
    drawCircle(c.paint.copy(alpha = a), radius = m.len(0.125), center = head)
    drawCircle(Color(0x33FFFFFF).copy(alpha = 0.3f * a), radius = m.len(0.10), center = Offset(head.x - m.len(0.03), head.y - m.len(0.04)))
    val vis = smooth(listOf(
        Offset(head.x + m.len(0.02), head.y - m.len(0.035)), Offset(head.x + m.len(0.13), head.y - m.len(0.01)),
        Offset(head.x + m.len(0.12), head.y + m.len(0.05)), Offset(head.x + m.len(0.03), head.y + m.len(0.03))
    ))
    drawPath(vis, Color(0xFF15181D).copy(alpha = a))
}

private fun DrawScope.cgMark(m: Mapper, p: V2, col: Color, r: Double = 0.03) {
    val o = m.p(p); val rr = m.len(r)
    drawCircle(Color.White, rr, o); drawCircle(col, rr, o, style = Stroke(2f))
    drawArc(col, 0f, 90f, true, Offset(o.x - rr, o.y - rr), Size(rr * 2, rr * 2))
    drawArc(col, 180f, 90f, true, Offset(o.x - rr, o.y - rr), Size(rr * 2, rr * 2))
}

private fun DrawScope.arrow(a: Offset, b: Offset, col: Color, w: Float) {
    drawLine(col, a, b, w, StrokeCap.Round)
    val dx = b.x - a.x; val dy = b.y - a.y; val l = kotlin.math.sqrt(dx * dx + dy * dy)
    if (l < 4f) return
    val ux = dx / l; val uy = dy / l; val h = w * 3.2f
    val p = Path()
    p.moveTo(b.x + ux * h * 0.6f, b.y + uy * h * 0.6f)
    p.lineTo(b.x - uy * h * 0.6f, b.y + ux * h * 0.6f)
    p.lineTo(b.x + uy * h * 0.6f, b.y - ux * h * 0.6f); p.close()
    drawPath(p, col, style = Fill)
}

/** Radlast (senkrecht) und Längskraft (waagrecht) am Aufstandspunkt; 1 kN = 12 cm. */
private fun DrawScope.forceArrows(m: Mapper, f: BikeFrame, c: BikeColors, ground: (Double) -> Double) {
    val s = 0.12 / 1000
    for ((axle, fz, fx, col) in listOf(
        Quad(f.frontAxle, f.st.fzF, f.st.fxF, c.front), Quad(f.rearAxle, f.st.fzR, f.st.fxR, c.rear)
    )) {
        val g = V2(axle.x, ground(axle.x))
        if (fz > 1) arrow(m.p(g), m.p(g + V2(0.0, fz * s)), col, m.len(0.014))
        if (abs(fx) > 50) arrow(m.p(g), m.p(g + V2(fx * s, 0.0)), col.copy(alpha = 0.8f), m.len(0.012))
    }
}

private data class Quad(val a: V2, val fz: Double, val fx: Double, val c: Color)

private fun DrawScope.highlight(m: Mapper, f: BikeFrame, p: BikePart, col: Color) {
    val glow = col.copy(alpha = 0.45f)
    val w = m.len(0.05)
    when (p) {
        BikePart.FORK -> drawLine(glow, m.p(f.frontAxle), m.p(f.forkCap), w * 1.4f, StrokeCap.Round)
        BikePart.SHOCK -> drawLine(glow, m.p(f.shockTop), m.p(f.shockBottom), w * 1.6f, StrokeCap.Round)
        BikePart.SWINGARM -> drawLine(glow, m.p(f.pivot), m.p(f.rearAxle), w * 1.6f, StrokeCap.Round)
        BikePart.FRONT_WHEEL -> drawCircle(glow, m.len(f.veh.rf), m.p(f.frontAxle), style = Stroke(w))
        BikePart.REAR_WHEEL -> drawCircle(glow, m.len(f.veh.rr), m.p(f.rearAxle), style = Stroke(w))
        BikePart.RIDER -> drawLine(glow, m.p(f.body(BikeFrame.RIDER_HIP)), m.p(f.body(BikeFrame.RIDER_HEAD)), w * 3.5f, StrokeCap.Round)
        BikePart.CHASSIS -> drawPath(smooth(BikeFrame.BODY_OUTLINE.map { m.p(f.body(it)) }), glow, style = Stroke(w * 0.6f))
    }
}

/** Geisterbild: Umriss der Serienabstimmung im selben Augenblick. */
fun DrawScope.drawGhost(f: BikeFrame, m: Mapper, col: Color) {
    val dash = PathEffect.dashPathEffect(floatArrayOf(10f, 7f))
    val st = Stroke(2.2f, pathEffect = dash)
    drawPath(smooth(BikeFrame.BODY_OUTLINE.map { m.p(f.body(it)) }), col, style = st)
    drawCircle(col, m.len(f.veh.rf), m.p(f.frontAxle), style = st)
    drawCircle(col, m.len(f.veh.rr), m.p(f.rearAxle), style = st)
    drawLine(col, m.p(f.frontAxle), m.p(f.forkCap), 3f, pathEffect = dash)
    drawLine(col, m.p(f.pivot), m.p(f.rearAxle), 3f, pathEffect = dash)
    drawLine(col, m.p(f.shockTop), m.p(f.shockBottom), 3f, pathEffect = dash)
}

fun lerpColor(a: Color, b: Color, t: Float) = lerp(a, b, t)
