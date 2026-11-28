package de.k7labor.fahrwerk.ui.bike

import de.k7labor.fahrwerk.physics.DEG
import de.k7labor.fahrwerk.physics.K7
import de.k7labor.fahrwerk.physics.ScenarioResult
import de.k7labor.fahrwerk.physics.Sim
import de.k7labor.fahrwerk.physics.Vehicle
import de.k7labor.fahrwerk.physics.shockStroke
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.math.sqrt

/** Zweidimensionaler Punkt in Metern (Welt: x nach vorn, z nach oben). */
data class V2(val x: Double, val z: Double) {
    operator fun plus(o: V2) = V2(x + o.x, z + o.z)
    operator fun minus(o: V2) = V2(x - o.x, z - o.z)
    operator fun times(f: Double) = V2(x * f, z * f)
    val len get() = hypot(x, z)
    fun norm(): V2 { val l = len; return if (l < 1e-12) V2(0.0, 0.0) else V2(x / l, z / l) }
}

/**
 * Zustand für ein Bild: verallgemeinerte Koordinaten des Modells plus Messwerte.
 * Alle Werte sind echte Rechenwerte; nur [exaggerate] vergrößert die gezeichnete Bewegung.
 */
data class SceneState(
    val x: Double, val z: Double, val th: Double, val s: Double, val phi: Double,
    val sFmm: Double, val xRmm: Double,
    val fzF: Double, val fzR: Double, val fxF: Double, val fxR: Double,
    val speedKmh: Double, val t: Double, val leanDeg: Double,
    val pitchDeg: Double
) {
    companion object {
        /** Ein Bild aus dem Verlauf. Mit Faktor k > 1 wird die Bewegung um den Startwert herum vergrößert. */
        fun fromTrace(res: ScenarioResult, frame: Int, k: Double = 1.0): SceneState {
            val tr = res.trace
            val i = frame.coerceIn(0, tr.size - 1)
            fun ex(a: FloatArray) = a[0] + (a[i] - a[0]) * k
            return SceneState(
                tr.qx[i].toDouble(), ex(tr.qz), ex(tr.qth), ex(tr.qs), ex(tr.qphi),
                tr.sF[i].toDouble(), tr.xR[i].toDouble(),
                tr.fzF[i].toDouble(), tr.fzR[i].toDouble(), tr.fxF[i].toDouble(), tr.fxR[i].toDouble(),
                tr.speed[i].toDouble(), tr.t[i].toDouble(), tr.lean[i].toDouble(), tr.pitch[i].toDouble()
            )
        }

        /** Statische Ruhelage (Stand mit Fahrer). Nicken wie im Verlauf: θ in Grad (im Stand nicht null). */
        fun fromSim(sim: Sim) = SceneState(
            sim.q[0], sim.q[1], sim.q[2], sim.q[3], sim.q[4], sim.sMm, sim.xRint,
            sim.fzF, sim.fzR, 0.0, 0.0, 0.0, 0.0, 0.0, sim.q[2] / DEG
        )
    }
}

/** Antippbare Bauteile mit Erklärung und zugehörigen Einstellern. */
enum class BikePart(val title: String, val keys: List<String>, val info: String) {
    FORK(
        "Gabel vorn", listOf("fPre", "fReb", "fLS", "fHS", "fOil", "fProt", "kF"),
        "Upside-down-Gabel mit 43 mm Standrohren und 125 mm Federweg. Oben am Deckel sitzen Vorspannung (Ringe) und Zugstufe, unten am Achsfuß die Druckstufe (Low-Speed: Schlitzschraube, High-Speed: Sechskant). Beide Holme immer gleich einstellen."
    ),
    FRONT_WHEEL(
        "Vorderrad und Reifen", listOf("pF"),
        "120/70 ZR17 auf 17-Zoll-Felge, zwei Bremsscheiben mit radial montierten Sätteln. Der Reifen ist selbst eine Feder (rund 175 N/mm bei 2,5 bar): Mehr Luftdruck macht ihn härter und lässt das Rad auf kleinen Wellen eher springen."
    ),
    SHOCK(
        "Federbein hinten", listOf("rPre", "rReb", "rLS", "rHS", "rShim", "kR"),
        "Federbein mit Ausgleichsbehälter, über eine progressive Umlenkung an der Schwinge. 1 mm Weg am Federbein entspricht rund 2,25 mm am Rad (ausgefedert), zum Ende rund 1,9 mm. Zugstufe unten am Federbein, Druckstufe am Behälter."
    ),
    SWINGARM(
        "Schwinge und Kette", emptyList(),
        "Die Schwinge dreht um den Drehpunkt hinter dem Motor. Der Kettenzug zieht entlang der oberen Kette und stemmt das Heck beim Beschleunigen gegen das Einfedern (Anti-Squat). Die Heckhöhe ändert den Winkel und damit diesen Effekt."
    ),
    REAR_WHEEL(
        "Hinterrad und Reifen", listOf("pR"),
        "190/50 ZR17. Überträgt Antrieb und Hinterradbremse. Die Haftgrenze folgt der Magic Formula: Die Kraft steigt mit dem Schlupf bis rund 13 % und fällt danach ab."
    ),
    RIDER(
        "Fahrer und Beladung", listOf("rider", "passenger", "luggage", "fuel"),
        "Der Fahrer ist starr mit dem Motorrad verbunden (Annahme). Sein Schwerpunkt liegt bei rund 40 % des Radstands vor der Hinterachse und 0,92 m hoch. Mehr Gewicht heißt mehr Durchhang; dann mehr Vorspannung."
    ),
    CHASSIS(
        "Rahmen und Geometrie", listOf("fProt", "rShim"),
        "Aluminium-Brückenrahmen. Lenkkopfwinkel, Nachlauf und Radstand ändern sich mit jedem Millimeter Federweg. Gabel durchstecken oder das Heck anheben macht die Lenkung schneller und das Motorrad unruhiger."
    )
}

/** Einsteller am Bild: Position (Bezugslage, Meter ab Hinterachse) und welche Werte sie öffnen. */
data class Adjuster(val part: BikePart, val label: String, val keys: List<String>)

/**
 * Rechnet alle Zeichenpunkte für einen Zustand in Weltkoordinaten. Körperfeste Teile werden in der
 * Bezugslage beschrieben (x ab Hinterachse, z Höhe über Boden) und mit dem Fahrzeug mitbewegt.
 */
class BikeFrame(val veh: Vehicle, val st: SceneState) {
    private val c = cos(st.th)
    private val sn = sin(st.th)

    /** Bezugslage (x ab Hinterachse, z Höhe) → Welt. */
    fun body(rx: Double, rz: Double): V2 {
        val bx = rx - veh.pivotX; val bz = rz - veh.pivotZ
        return V2(st.x + c * bx - sn * bz, st.z + sn * bx + c * bz)
    }
    fun body(p: V2) = body(p.x, p.z)

    /** Richtung in der Bezugslage → Welt (nur gedreht). */
    fun dir(dx: Double, dz: Double) = V2(c * dx - sn * dz, sn * dx + c * dz)

    val pivot = V2(st.x, st.z)
    val frontAxle: V2
    val rearAxle: V2
    /** Gabelachse (Richtung von der Achse nach oben). */
    val forkUp: V2
    /** Obere Gabelbrücke auf der Gabelachse. */
    val clamp: V2
    val forkCap: V2
    val steeringHead: V2
    val shockTop: V2
    val shockBottom: V2
    val linkPoint: V2
    val shockLen: Double
    val countershaft: V2
    val cg: V2
    val riderCg: V2

    init {
        val sRef = K7.REF_SAG_F_MM / 1000
        val bfx = veh.afX + veh.u0x * (st.s - sRef)
        val bfz = veh.afZ + veh.u0z * (st.s - sRef)
        frontAxle = V2(st.x + c * bfx - sn * bfz, st.z + sn * bfx + c * bfz)
        val beta = st.th + st.phi + Math.PI
        rearAxle = V2(st.x + veh.ls * cos(beta), st.z + veh.ls * sin(beta))
        forkUp = dir(veh.u0x, veh.u0z)
        // Brücke körperfest: Achse der Bezugslage plus feste Länge entlang der Gabel (ohne Durchstecken)
        val clampRef = V2(veh.wb, veh.rf) + V2(veh.u0x, veh.u0z) * CLAMP_LEN
        clamp = body(clampRef)
        forkCap = body(clampRef + V2(veh.u0x, veh.u0z) * (0.035 + veh.st.fProt / 1000))
        val off = K7.offsetMm / 1000
        steeringHead = clamp + dir(-cos(veh.eps), -sin(veh.eps)) * off
        // Federbein: oben körperfest, unten an der Umlenkung unter der Schwinge
        shockTop = body(SHOCK_TOP)
        val sa = V2(cos(beta), sin(beta))        // Richtung Drehpunkt → Hinterachse
        val down = V2(-sa.z, sa.x)               // senkrecht zur Schwinge, nach unten
        linkPoint = pivot + sa * (0.20 * veh.ls) + down * 0.085
        shockLen = SHOCK_FREE_LEN - shockStroke(st.xRmm.coerceAtLeast(0.0)) / 1000
        val d = (linkPoint - shockTop).norm()
        shockBottom = shockTop + d * shockLen
        countershaft = body(veh.pivotX + veh.csX, veh.pivotZ + veh.csZ)
        cg = body(veh.pivotX + veh.cgX, veh.pivotZ + veh.cgZ)
        riderCg = body(K7.RIDER_X_SHARE * veh.wb, K7.RIDER_H)
    }

    /** Sichtbare Länge der Standrohre zwischen Achsfuß und Tauchrohr. */
    val forkGap get() = (clamp - frontAxle).len

    fun adjusterPos(a: Int): V2 = when (a) {
        0 -> forkCap                                       // Gabeldeckel: Vorspannung + Zugstufe
        1 -> frontAxle + forkUp * 0.07                     // Achsfuß: Druckstufe
        2 -> shockTop + (shockBottom - shockTop) * 0.30 + dir(0.045, 0.0) // Behälter: Druckstufe
        3 -> shockBottom                                   // unten: Zugstufe
        else -> shockTop + (shockBottom - shockTop) * 0.10 + dir(-0.055, 0.0) // Federteller: Vorspannung
    }

    /** Bauteil an einem Weltpunkt, Einsteller zuerst. */
    fun hit(p: V2, tol: Double): BikePart? {
        for (i in ADJUSTERS.indices) if ((adjusterPos(i) - p).len < tol * 1.2) return ADJUSTERS[i].part
        if (segDist(p, frontAxle, forkCap) < tol) return BikePart.FORK
        if (segDist(p, shockTop, shockBottom) < tol) return BikePart.SHOCK
        if (segDist(p, pivot, rearAxle) < tol) return BikePart.SWINGARM
        if ((p - frontAxle).len < veh.rf + tol * 0.3) return BikePart.FRONT_WHEEL
        if ((p - rearAxle).len < veh.rr + tol * 0.3) return BikePart.REAR_WHEEL
        val hip = body(RIDER_HIP); val head = body(RIDER_HEAD)
        if (segDist(p, hip, head) < 0.16) return BikePart.RIDER
        val inBody = BODY_OUTLINE.map { body(it) }
        if (inside(p, inBody)) return BikePart.CHASSIS
        return null
    }

    companion object {
        const val CLAMP_LEN = 0.70
        const val SHOCK_FREE_LEN = 0.330
        val SHOCK_TOP = V2(0.47, 0.70)
        val RIDER_HIP = V2(0.47, 0.88)
        val RIDER_HEAD = V2(0.955, 1.205)

        val ADJUSTERS = listOf(
            Adjuster(BikePart.FORK, "Vorspannung, Zugstufe", listOf("fPre", "fReb")),
            Adjuster(BikePart.FORK, "Druckstufe", listOf("fLS", "fHS")),
            Adjuster(BikePart.SHOCK, "Druckstufe", listOf("rLS", "rHS")),
            Adjuster(BikePart.SHOCK, "Zugstufe", listOf("rReb")),
            Adjuster(BikePart.SHOCK, "Vorspannung", listOf("rPre"))
        )

        /** Grobe Außenkontur (für Geisterbild und Trefferprüfung), Bezugslage. */
        val BODY_OUTLINE = listOf(
            V2(1.51, 0.79), V2(1.48, 0.92), V2(1.36, 1.00), V2(1.21, 1.13), V2(1.10, 0.95),
            V2(0.95, 1.00), V2(0.75, 0.98), V2(0.62, 0.88), V2(0.40, 0.82), V2(0.10, 0.90),
            V2(-0.13, 0.96), V2(-0.10, 0.88), V2(0.15, 0.76), V2(0.45, 0.62), V2(0.60, 0.40),
            V2(0.62, 0.26), V2(1.00, 0.28), V2(1.16, 0.33), V2(1.28, 0.52), V2(1.42, 0.68)
        )

        fun segDist(p: V2, a: V2, b: V2): Double {
            val ab = b - a
            val l2 = ab.x * ab.x + ab.z * ab.z
            if (l2 < 1e-12) return (p - a).len
            val t = (((p.x - a.x) * ab.x + (p.z - a.z) * ab.z) / l2).coerceIn(0.0, 1.0)
            return (p - (a + ab * t)).len
        }

        fun inside(p: V2, poly: List<V2>): Boolean {
            var res = false
            var j = poly.size - 1
            for (i in poly.indices) {
                val a = poly[i]; val b = poly[j]
                if ((a.z > p.z) != (b.z > p.z) && p.x < (b.x - a.x) * (p.z - a.z) / (b.z - a.z) + a.x) res = !res
                j = i
            }
            return res
        }

        /** Oberer Kettentrum: äußere Tangente der beiden Kettenräder (oben). */
        fun chainUpper(cs: V2, rs: Double, ra: V2, rr: Double): Pair<V2, V2> {
            val d = cs - ra
            val l = d.len
            val u = d.norm()
            val nd = ((rr - rs) / l).coerceIn(-0.99, 0.99)
            val pr = sqrt(1 - nd * nd)
            // Normale der Tangente: zeigt nach oben
            var n = V2(nd * u.x - pr * u.z, nd * u.z + pr * u.x)
            if (n.z < 0) n = V2(nd * u.x + pr * u.z, nd * u.z - pr * u.x)
            return Pair(ra + n * rr, cs + n * rs)
        }

        fun angleOf(v: V2) = atan2(v.z, v.x)
    }
}
