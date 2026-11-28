package de.k7labor.fahrwerk.physics

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

enum class ScenarioId(val title: String, val short: String, val scored: Boolean, val info: String) {
    BUMP("Bodenwelle 25 mm", "Bodenwelle", true, "Einzelne, 60 cm lange Welle bei konstantem Tempo"),
    POTHOLE("Schlagloch 30 mm", "Schlagloch", true, "45 cm langes, 30 mm tiefes Loch mit Kanten"),
    RIPPLES("Bremsrippen 6 mm", "Rippen", true, "7 Wellen à 1,1 m, wie vor Ampeln und Kurven"),
    BRAKE("Starke Bremsung 0,8 g", "Bremsen", true, "Fahrer bremst auf 0,8 g, 85 % vorn"),
    ACCEL("Beschleunigung 0,6 g", "Beschleunigen", true, "Gas auf, Ziel 0,6 g, Motorkennlinie und Gänge"),
    CORNER("Kurve 45° mit Wellen", "Kurve 45°", true, "Schräglage 45°, zwei 15-mm-Wellen in der Kurve"),
    COUNTRY("Landstraße, ISO-Klasse B", "Landstraße", true, "Zufällige Unebenheit nach ISO 8608 Klasse B (gute Straße)"),
    FULLBRAKE("Vollbremsung 1,1 g ohne ABS", "Vollbremsung", false, "Grenzbereich: Stoppie oder blockierendes Vorderrad")
}

const val T_SIM = 3.0
const val DT = 0.0002
const val REC = 10   // jeden 10. Schritt aufzeichnen = 2 ms

/** Aufgezeichneter Verlauf einer Fahrsituation. */
class Trace(n: Int) {
    val t = FloatArray(n)
    val sF = FloatArray(n)      // Gabelweg mm
    val xR = FloatArray(n)      // Radweg hinten mm (innen)
    val fzF = FloatArray(n)
    val fzR = FloatArray(n)
    val fxF = FloatArray(n)
    val fxR = FloatArray(n)
    val pitch = FloatArray(n)   // Grad
    val seatAcc = FloatArray(n) // m/s² senkrecht
    val speed = FloatArray(n)   // km/h
    val slipF = FloatArray(n)
    val slipR = FloatArray(n)
    val qx = FloatArray(n); val qz = FloatArray(n); val qth = FloatArray(n); val qs = FloatArray(n); val qphi = FloatArray(n)
    val roadF = FloatArray(n); val roadR = FloatArray(n)
    val lean = FloatArray(n)
    var size = 0
}

data class AxleKpi(
    val usage: Double, val maxX: Double, val minX: Double, val static: Double,
    val bottom: Boolean, val topout: Boolean, val cv: Double, val liftMs: Double,
    val settle: Double, val over: Double
)

data class VehKpi(
    val seatRms: Double, val maxPitch: Double, val minPitch: Double,
    val meanDecel: Double, val frontLockMs: Double, val rearLockMs: Double, val stoppieMs: Double,
    val wheelieMs: Double, val spinMs: Double, val distance: Double, val vEnd: Double
)

data class SubScores(val comfort: Double, val grip: Double, val control: Double, val reserve: Double)

class ScenarioResult(
    val id: ScenarioId, val trace: Trace, val front: AxleKpi, val rear: AxleKpi, val veh: VehKpi,
    val sF: SubScores, val sR: SubScores, val score: Double, val tEnd: Double, val staticF: Double, val staticR: Double,
    /** Straßenhöhe über x (m), für die Darstellung. */
    val road: (Double) -> Double
)

/** Straßenprofil nach ISO 8608 (Klasse B), fest vorberechnet (gleicher Zufallswert = gleiche Straße). */
object IsoRoad {
    private const val STEP = 0.01
    private const val LEN = 260.0
    private val table: FloatArray by lazy {
        val n = (LEN / STEP).toInt() + 2
        val out = FloatArray(n)
        val rnd = Random(8608)
        val gd0 = 64e-6   // m³, Klasse B (geometrisches Mittel)
        val n0 = 0.1
        val k = 160
        val nMin = 0.05; val nMax = 6.0
        val amps = DoubleArray(k); val freqs = DoubleArray(k); val phases = DoubleArray(k)
        for (i in 0 until k) {
            val a = nMin * (nMax / nMin).pow(i.toDouble() / k)
            val b = nMin * (nMax / nMin).pow((i + 1.0) / k)
            val nc = sqrt(a * b)
            val gd = gd0 * (nc / n0).pow(-2.0)
            amps[i] = sqrt(2 * gd * (b - a))
            freqs[i] = 2 * PI * nc
            phases[i] = rnd.nextDouble() * 2 * PI
        }
        for (j in 0 until n) {
            val x = j * STEP
            var h = 0.0
            for (i in 0 until k) h += amps[i] * sin(freqs[i] * x + phases[i])
            out[j] = h.toFloat()
        }
        // Start bei 0 einblenden
        val fade = (3.0 / STEP).toInt()
        for (j in 0 until min(fade, n)) out[j] = (out[j] * smooth(j.toDouble() / fade)).toFloat()
        out
    }

    fun height(x: Double): Double {
        if (x <= 0) return 0.0
        val f = x / STEP
        val i = f.toInt()
        if (i >= table.size - 1) return table.last().toDouble()
        val r = f - i
        return table[i] * (1 - r) + table[i + 1] * r
    }
}

fun smooth(x0: Double): Double { val x = x0.coerceIn(0.0, 1.0); return x * x * (3 - 2 * x) }

/** Reifen umhüllt kurze Kanten: Mittelwert über die Aufstandslänge (ca. 10 cm). */
private fun enveloped(f: (Double) -> Double): (Double) -> Double = { x -> (f(x - 0.05) + 2 * f(x) + f(x + 0.05)) / 4 }

class ScenarioDef(
    val road: (Double) -> Double,
    val tEnd: Double,
    val lean: (Double) -> Double = { 0.0 },
    val brakeDecel: (Double) -> Double = { 0.0 },
    val accel: (Double) -> Double = { 0.0 }
)

fun scenarioDef(id: ScenarioId, v: Double, xFront0: Double, xRear0: Double): ScenarioDef {
    val d = xFront0 + max(v * 0.4, 3.0)
    val wbm = K7.WHEELBASE_MM / 1000
    val ramp = { t: Double, a: Double, t0: Double, t1: Double -> a * smooth((t - t0) / 0.3) * (1 - smooth((t - t1) / 0.25)) }
    val texture = { x: Double -> 0.0008 * sin(x * 7.3) * sin(x * 2.1) }
    return when (id) {
        ScenarioId.BUMP -> {
            val l = 0.6
            ScenarioDef(enveloped { x -> if (x in d..d + l) 0.025 * (1 - cos(2 * PI * (x - d) / l)) / 2 else 0.0 }, (d + l + wbm - xFront0) / v)
        }
        ScenarioId.POTHOLE -> {
            val l = 0.45; val e = 0.02
            ScenarioDef(enveloped { x -> -0.03 * smooth((x - d) / e) * (1 - smooth((x - d - l + e) / e)) }, (d + l + wbm - xFront0) / v)
        }
        ScenarioId.RIPPLES -> {
            val w = 1.1; val l = 7 * w   // ganze Wellen, kein Sprung am Ende (Review-Punkt 9)
            ScenarioDef({ x -> if (x in d..d + l) 0.006 * (1 - cos(2 * PI * (x - d) / w)) / 2 else 0.0 }, (d + l + wbm - xFront0) / v)
        }
        ScenarioId.BRAKE -> ScenarioDef(texture, 2.4, brakeDecel = { t -> ramp(t, 0.8 * G, 0.4, 2.2) })
        ScenarioId.FULLBRAKE -> ScenarioDef(texture, 2.4, brakeDecel = { t -> ramp(t, 1.1 * G, 0.4, 2.2) })
        ScenarioId.ACCEL -> ScenarioDef(texture, 2.0, accel = { t -> ramp(t, 0.6 * G, 0.4, 1.8) })
        ScenarioId.CORNER -> {
            val lean = 45 * DEG
            val dc = xFront0 + max(v * 0.9, 6.0)
            val l = 1.0
            val bump = { x: Double -> if (x in 0.0..l) 0.015 * (1 - cos(2 * PI * x / l)) / 2 else 0.0 }
            ScenarioDef(
                { x -> (bump(x - dc) + bump(x - dc - 6)) * cos(lean) },
                max(0.7, (dc + 6 + l + wbm - xFront0) / v),
                lean = { t -> lean * smooth((t - 0.1) / 0.5) }
            )
        }
        ScenarioId.COUNTRY -> ScenarioDef({ x -> IsoRoad.height(x - xRear0 + 0.5) }, T_SIM)
    }
}

/**
 * Fährt eine Situation mit dem vollen Mehrkörpermodell. Der „Fahrer“ ist ein Regler:
 * hält das Tempo, bremst bzw. beschleunigt auf die Zielverzögerung ohne ABS oder Traktionskontrolle.
 */
fun runScenario(id: ScenarioId, st: Setup, speedKmh: Double, cal: Calibration, dt: Double = DT): ScenarioResult {
    val veh = Vehicle(st, cal)
    val v0 = speedKmh / 3.6
    // Gleichgewicht im Stand bestimmen
    val probe = Sim(veh, FLAT_ROAD, st.profile.mu)
    probe.settleStatic()
    val xF0 = probe.pfX; val xR0 = probe.prX
    val sc = scenarioDef(id, v0, xF0, xR0)
    val road = Road { _, x, _ -> sc.road(x) }
    val sim = Sim(veh, road, st.profile.mu)
    sim.settleStatic()
    sim.startRolling(v0)
    val staticF = sim.fzF; val staticR = sim.fzR

    val nSteps = Math.round(T_SIM / dt).toInt()
    val rec = Math.round(REC * DT / dt).toInt().coerceAtLeast(1)
    val tr = Trace(nSteps / rec + 1)
    val mTot = veh.mTotal
    var vRef = v0
    var integ = 0.0
    var brakingDone = false
    var prevSeatVz = 0.0
    var seatAccAcc = 0.0
    var seatCount = 0
    val seatBx = veh.seatX; val seatBz = veh.seatZ
    // Bremsweg und mittlere Verzögerung
    var tBrakeStart = -1.0; var xBrakeStart = 0.0

    for (i in 0..nSteps) {
        val t = i * dt
        val v = sim.qd[0]
        val lean = sc.lean(t)
        sim.gEff = G / cos(lean)
        val drag = 0.5 * K7.RHO * K7.CDA * v * abs(v) + K7.ROLL_RES * mTot * G
        val aBrake = if (brakingDone) 0.0 else sc.brakeDecel(t)
        val aAcc = sc.accel(t)
        vRef += (-aBrake + aAcc) * dt
        if (aBrake > 0 && v < 4.0) brakingDone = true
        val err = vRef - v
        integ = (integ + err * dt).coerceIn(-2.0, 2.0)
        if (aBrake > 0.05) {
            if (tBrakeStart < 0) { tBrakeStart = t; xBrakeStart = sim.q[0] }
            val fb = max(0.0, mTot * aBrake - drag - mTot * (3.0 * err + 2.0 * integ))
            sim.brakeF = 0.85 * fb * sim.reF
            // hinten dosiert der Fahrer so, dass das Rad nicht blockiert
            sim.brakeR = min(0.15 * fb, 0.8 * st.profile.mu * sim.fzR) * sim.reR
            sim.drive = 0.0
        } else {
            sim.brakeF = 0.0; sim.brakeR = 0.0
            val fd = drag + mTot * aAcc + mTot * (3.0 * err + 2.0 * integ)
            val (fMax, gear) = K7.maxDrive(v, sim.reR)
            sim.gear = gear
            sim.drive = fd.coerceIn(0.0, fMax) * sim.reR
        }
        sim.step(dt)

        // Sitzbeschleunigung (senkrecht)
        val c = cos(sim.q[2]); val s = sin(sim.q[2])
        val wx = c * seatBx - s * seatBz
        val seatVz = sim.qd[1] + sim.qd[2] * wx
        val acc = if (i == 0) 0.0 else (seatVz - prevSeatVz) / dt
        prevSeatVz = seatVz
        seatAccAcc += acc; seatCount++

        if (i % rec == 0) {
            val k = tr.size
            if (k < tr.t.size) {
                tr.t[k] = t.toFloat(); tr.sF[k] = sim.sMm.toFloat(); tr.xR[k] = sim.xRint.toFloat()
                tr.fzF[k] = sim.fzF.toFloat(); tr.fzR[k] = sim.fzR.toFloat()
                tr.fxF[k] = sim.fxF.toFloat(); tr.fxR[k] = sim.fxR.toFloat()
                tr.pitch[k] = (sim.q[2] / DEG).toFloat()
                tr.seatAcc[k] = if (i == 0) 0f else (seatAccAcc / seatCount).toFloat()
                tr.speed[k] = (sim.qd[0] * 3.6).toFloat()
                tr.slipF[k] = sim.kapF.toFloat(); tr.slipR[k] = sim.kapR.toFloat()
                tr.qx[k] = sim.q[0].toFloat(); tr.qz[k] = sim.q[1].toFloat(); tr.qth[k] = sim.q[2].toFloat()
                tr.qs[k] = sim.q[3].toFloat(); tr.qphi[k] = sim.q[4].toFloat()
                tr.roadF[k] = sim.roadF.toFloat(); tr.roadR[k] = sim.roadR.toFloat()
                tr.lean[k] = (lean / DEG).toFloat()
                tr.size = k + 1
            }
            seatAccAcc = 0.0; seatCount = 0
        }
    }
    val front = axleKpi(tr, tr.sF, tr.fzF, K7.TRAVEL_F_MM, staticF, sc.tEnd)
    val rear = axleKpi(tr, tr.xR, tr.fzR, K7.TRAVEL_R_MM, staticR, sc.tEnd)
    val longitudinal = id == ScenarioId.BRAKE || id == ScenarioId.FULLBRAKE || id == ScenarioId.ACCEL
    val vk = vehKpi(tr, staticF, staticR, tBrakeStart, xBrakeStart, sim, longitudinal)
    val sF = subScores(front, vk, id, true)
    val sR = subScores(rear, vk, id, false)
    val p = st.profile
    fun avg(f: (SubScores) -> Double) = (f(sF) + f(sR)) / 2
    val score = p.wComfort * avg { it.comfort } + p.wGrip * avg { it.grip } + p.wControl * avg { it.control } + p.wReserve * avg { it.reserve }
    return ScenarioResult(id, tr, front, rear, vk, sF, sR, score, sc.tEnd, staticF, staticR, sc.road)
}

private fun axleKpi(tr: Trace, x: FloatArray, fz: FloatArray, travel: Double, static: Double, tEnd: Double): AxleKpi {
    val dtr = DT * REC
    val n = tr.size
    val i0 = (0.25 / dtr).toInt()
    val iE = min(n - 1, (tEnd / dtr).toInt())
    val w = (0.15 / dtr).toInt()
    val pre = DoubleArray(n + 1)
    for (i in 0 until n) pre[i + 1] = pre[i] + fz[i]
    var mx = -1e9; var mn = 1e9; var f2 = 0.0; var cnt = 0; var lift = 0.0; var minAfter = 1e9
    for (i in i0 until n) {
        mx = max(mx, x[i].toDouble()); mn = min(mn, x[i].toDouble())
        val a = max(0, i - w); val b = min(n, i + w + 1)
        val ma = (pre[b] - pre[a]) / (b - a)
        f2 += (fz[i] - ma).pow(2); cnt++
        if (fz[i] < 0.05 * static) lift += dtr * 1000
        if (i >= iE) minAfter = min(minAfter, x[i].toDouble())
    }
    val x0 = x[i0].toDouble()
    val fin = x[n - 1].toDouble()
    var settle = 0.0
    for (i in n - 1 downTo iE) if (abs(x[i] - fin) > 1.5) { settle = tr.t[i] - tEnd; break }
    return AxleKpi(
        usage = mx / travel, maxX = mx, minX = mn, static = static,
        bottom = mx > travel - 3, topout = mn < 1.0, cv = sqrt(f2 / max(cnt, 1)) / static, liftMs = lift,
        settle = max(0.0, settle), over = max(0.0, x0 - minAfter)
    )
}

private fun vehKpi(tr: Trace, sF: Double, sR: Double, tB: Double, xB: Double, sim: Sim, longitudinal: Boolean): VehKpi {
    val dtr = DT * REC
    val n = tr.size
    val i0 = (0.25 / dtr).toInt()
    var acc2 = 0.0; var mean = 0.0; var cnt = 0
    for (i in i0 until n) { mean += tr.seatAcc[i]; cnt++ }
    mean /= max(cnt, 1)
    for (i in i0 until n) acc2 += (tr.seatAcc[i] - mean).pow(2)
    var maxP = -1e9; var minP = 1e9; var lockF = 0.0; var lockR = 0.0; var stoppie = 0.0; var wheelie = 0.0; var spin = 0.0
    for (i in i0 until n) {
        maxP = max(maxP, tr.pitch[i].toDouble()); minP = min(minP, tr.pitch[i].toDouble())
        if (tr.slipF[i] < -0.25) lockF += dtr * 1000
        if (tr.slipR[i] < -0.25) lockR += dtr * 1000
        // Stoppie/Wheelie nur bei Längsdynamik; über Wellen ist ein kurzes Abheben „Radlast null“ (siehe Achs-Kennwerte)
        if (longitudinal && tr.fzR[i] < 0.03 * sR) stoppie += dtr * 1000
        if (longitudinal && tr.fzF[i] < 0.03 * sF) wheelie += dtr * 1000
        if (tr.slipR[i] > 0.25) spin += dtr * 1000
    }
    var meanDecel = 0.0
    var dist = 0.0
    if (tB >= 0) {
        // mittlere Verzögerung in der Haltephase (0,4 s nach Bremsbeginn bis 1,4 s danach)
        val iB = min(n - 2, ((tB + 0.4) / dtr).toInt())
        val iE = min(n - 1, iB + (1.0 / dtr).toInt())
        meanDecel = (tr.speed[iB] - tr.speed[iE]) / 3.6 / ((iE - iB) * dtr) / G
        dist = sim.q[0] - xB
    }
    return VehKpi(sqrt(acc2 / max(cnt, 1)), maxP, minP, meanDecel, lockF, lockR, stoppie, wheelie, spin, dist, tr.speed[n - 1].toDouble())
}

private fun subScores(k: AxleKpi, v: VehKpi, id: ScenarioId, front: Boolean): SubScores {
    val comfort = (100 - v.seatRms * 9).coerceIn(0.0, 100.0)
    var grip = 100 - k.cv * 120 - k.liftMs * 0.3
    if (front) grip -= v.frontLockMs * 0.3 else grip -= (v.rearLockMs + v.spinMs) * 0.2
    var control = 100 - k.settle * 60 - k.over * 1.0 - (if (k.topout && id != ScenarioId.BRAKE && id != ScenarioId.FULLBRAKE && id != ScenarioId.ACCEL) 10.0 else 0.0)
    control -= (v.stoppieMs + v.wheelieMs) * 0.1
    var reserve = 100.0
    if (k.usage > 0.85) reserve = 100 - (k.usage - 0.85) * 400
    if (k.bottom) reserve = min(reserve, 35.0)
    return SubScores(comfort, grip.coerceIn(0.0, 100.0), control.coerceIn(0.0, 100.0), reserve.coerceIn(0.0, 100.0))
}
