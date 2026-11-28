package de.k7labor.fahrwerk.physics

import kotlin.math.PI
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin

/** Anregungsarten des Rüttelprüfstands (wie ein Hydropuls-Prüfstand mit zwei Stempeln). */
enum class ShakeMode(val title: String, val front: Double, val rear: Double) {
    FRONT("Nur vorn", 1.0, 0.0),
    REAR("Nur hinten", 0.0, 1.0),
    HEAVE("Beide gleich (Heben)", 1.0, 1.0),
    PITCH("Gegenläufig (Nicken)", 1.0, -1.0)
}

class Frf(
    val freq: DoubleArray,
    /** Sitzbeschleunigung je mm Anregung (m/s² pro mm). */
    val seat: DoubleArray,
    /** Radlastschwankung in % der statischen Last je mm. */
    val tyreF: DoubleArray, val tyreR: DoubleArray,
    /** Federweg-Amplitude je mm Anregung (mm/mm). */
    val travelF: DoubleArray, val travelR: DoubleArray
) {
    fun peak(arr: DoubleArray, fMin: Double, fMax: Double): Pair<Double, Double> {
        var best = -1.0; var bf = 0.0
        for (i in freq.indices) if (freq[i] in fMin..fMax && arr[i] > best) { best = arr[i]; bf = freq[i] }
        return bf to best
    }
}

object Rig {
    const val AMP_MM = 3.0
    val FREQS: DoubleArray = DoubleArray(30) { i -> 0.5 * (25.0 / 0.5).pow(i / 29.0) }

    fun shake(st: Setup, mode: ShakeMode, cal: Calibration = Physics.calibration, cancelled: () -> Boolean = { false }): Frf {
        val n = FREQS.size
        val seat = DoubleArray(n); val tf = DoubleArray(n); val trr = DoubleArray(n); val sf = DoubleArray(n); val sr = DoubleArray(n)
        val veh = Vehicle(st, cal)
        val amp = AMP_MM / 1000
        for ((k, f) in FREQS.withIndex()) {
            if (cancelled()) break
            val w = 2 * PI * f
            val road = Road { wheel, _, t ->
                val a = if (wheel == 0) mode.front else mode.rear
                // weich einblenden, damit kein Anfangsstoß entsteht
                a * amp * sin(w * t) * smooth(t / 0.3)
            }
            val sim = Sim(veh, road, 1.0, lockX = true)
            sim.settleStatic()
            val fz0F = sim.fzF; val fz0R = sim.fzR; val s0 = sim.sMm; val x0 = sim.xRint
            val settle = max(1.5, 3 / f)
            val cycles = max(2.0, ceil(0.4 * f))
            val tMeas = cycles / f
            val steps = ((settle + tMeas) / DT).toInt()
            val iStart = (settle / DT).toInt()
            // Fourier-Projektion auf die Anregungsfrequenz (erste Harmonische)
            val acc = DoubleArray(10)
            for (i in 0 until steps) {
                sim.step(DT)
                if (i >= iStart) {
                    val t = sim.t
                    val cs = cos(w * t); val sn = sin(w * t)
                    val th = sim.q[2]
                    val wx = cos(th) * veh.seatX - sin(th) * veh.seatZ
                    val wz = sin(th) * veh.seatX + cos(th) * veh.seatZ
                    val az = sim.qdd[1] + sim.qdd[2] * wx - sim.qd[2] * sim.qd[2] * wz
                    acc[0] += az * cs; acc[1] += az * sn
                    acc[2] += (sim.fzF - fz0F) * cs; acc[3] += (sim.fzF - fz0F) * sn
                    acc[4] += (sim.fzR - fz0R) * cs; acc[5] += (sim.fzR - fz0R) * sn
                    acc[6] += (sim.sMm - s0) * cs; acc[7] += (sim.sMm - s0) * sn
                    acc[8] += (sim.xRint - x0) * cs; acc[9] += (sim.xRint - x0) * sn
                }
            }
            val m = steps - iStart
            fun ampl(a: Int) = 2.0 / m * hypot(acc[a], acc[a + 1])
            seat[k] = ampl(0) / AMP_MM
            tf[k] = ampl(2) / fz0F * 100 / AMP_MM
            trr[k] = ampl(4) / fz0R * 100 / AMP_MM
            sf[k] = ampl(6) / AMP_MM
            sr[k] = ampl(8) / AMP_MM
        }
        return Frf(FREQS.copyOf(), seat, tf, trr, sf, sr)
    }

    /** Dämpferprüfstand: Kraft über Kolbengeschwindigkeit. Vorn beide Holme axial, hinten an der Kolbenstange. */
    fun damperCurve(st: Setup, front: Boolean, cal: Calibration = Physics.calibration): Pair<DoubleArray, DoubleArray> {
        val veh = Vehicle(st, cal)
        val vMax = if (front) 1.5 else 0.8
        val n = 121
        val v = DoubleArray(n) { -vMax + 2 * vMax * it / (n - 1) }
        val d = if (front) veh.damperF else veh.damperR
        return v to DoubleArray(n) { d.force(v[it]) }
    }

    /** Kraft-Weg-Schleife bei sinusförmigem Hub (wie auf einem Dämpferprüfstand). */
    fun damperLoop(st: Setup, front: Boolean, strokeMm: Double, freq: Double, cal: Calibration = Physics.calibration): Pair<DoubleArray, DoubleArray> {
        val veh = Vehicle(st, cal)
        val d = if (front) veh.damperF else veh.damperR
        val n = 181
        val w = 2 * PI * freq
        val a = strokeMm / 2 / 1000
        val x = DoubleArray(n); val f = DoubleArray(n)
        for (i in 0 until n) {
            val ph = 2 * PI * i / (n - 1)
            x[i] = a * sin(ph) * 1000
            f[i] = d.force(a * w * cos(ph))
        }
        return x to f
    }

    /** Federprüfstand: Federkraft über Federweg (vorn axial beide Holme inkl. Luftpolster, hinten am Rad). */
    fun springCurve(st: Setup, front: Boolean, cal: Calibration = Physics.calibration): Pair<DoubleArray, DoubleArray> {
        val veh = Vehicle(st, cal)
        val travel = if (front) K7.TRAVEL_F_MM else K7.TRAVEL_R_MM
        val n = 131
        val x = DoubleArray(n) { travel * it / (n - 1) }
        return x to DoubleArray(n) { if (front) veh.forkSpring(x[it]) else veh.rearSpring(x[it]) }
    }
}
