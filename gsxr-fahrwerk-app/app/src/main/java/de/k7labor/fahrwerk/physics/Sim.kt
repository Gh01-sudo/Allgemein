package de.k7labor.fahrwerk.physics

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.tanh

/** Straßenhöhe (m) unter Rad 0 (vorn) bzw. 1 (hinten) an der Stelle x (m) zur Zeit t (s). */
fun interface Road {
    fun height(wheel: Int, x: Double, t: Double): Double
}

val FLAT_ROAD = Road { _, _, _ -> 0.0 }

/** Längskraft-Beiwert nach Magic Formula (Pacejka), mit leichter Lastabhängigkeit des Reibwerts. */
fun tyreLongForce(kappa: Double, fz: Double, mu: Double): Double {
    if (fz <= 0) return 0.0
    val muEff = mu * (1 - K7.MU_LOAD_SENS * (fz / K7.FZ_NOM - 1)).coerceIn(0.85, 1.1)
    val bk = K7.PAC_B * kappa
    return fz * muEff * sin(K7.PAC_C * atan(bk - K7.PAC_E * (bk - atan(bk))))
}

/**
 * Ebenes Mehrkörpermodell (Halbfahrzeug) der GSX-R 1000 K7.
 *
 * Freiheitsgrade q: 0 = x (Schwingendrehpunkt, m), 1 = z (m), 2 = Nickwinkel θ (rad, + = Nase hoch),
 * 3 = Gabeleinfederung s (m, entlang der Gabelachse), 4 = Schwingenwinkel φ (rad, + = Achse tiefer).
 * Dazu Raddrehzahlen und Reifenschlupf mit Einlaufverhalten.
 * Körper: gefederte Masse (Maschine + Fahrer), Vorderrad mit Gleitrohren, Hinterrad, Schwinge.
 * Kräfte: Federn, Luftpolster, Dämpfer, Anschläge, Reifen senkrecht und längs, Bremsmomente mit
 * Abstützung, Kettenzug entlang der echten Kettenlinie, Luftwiderstand, Schwerkraft.
 * Integration: semi-implizites Euler-Verfahren mit 0,2 ms.
 */
class Sim(val veh: Vehicle, val road: Road, var mu: Double, val lockX: Boolean = false) {
    val q = DoubleArray(5)
    val qd = DoubleArray(5)
    val qdd = DoubleArray(5)
    var omF = 0.0
    var omR = 0.0
    var kapF = 0.0
    var kapR = 0.0
    var t = 0.0

    // Eingänge
    var brakeF = 0.0
    var brakeR = 0.0
    var drive = 0.0
    var gEff = G
    var gear = 2

    // Ausgänge
    var fzF = 0.0
    var fzR = 0.0
    var fxF = 0.0
    var fxR = 0.0
    var sMm = 0.0
    var xRint = 0.0
    var reF = veh.rf
    var reR = veh.rr
    var deltaF = 0.0
    var deltaR = 0.0
    var roadF = 0.0
    var roadR = 0.0
    var forkF = 0.0
    var rearF = 0.0
    var chainF = 0.0

    private var dPrevF = Double.NaN
    private var dPrevR = Double.NaN
    private val m = Array(5) { DoubleArray(5) }
    private val rhs = DoubleArray(5)
    private val jx = DoubleArray(5)
    private val jz = DoubleArray(5)
    private val sRef = K7.REF_SAG_F_MM / 1000

    // Weltlagen (für Ausgabe und Grafik)
    var pfX = 0.0; var pfZ = 0.0; var prX = 0.0; var prZ = 0.0
    var vfX = 0.0; var vrX = 0.0

    private fun clearJ() { jx.fill(0.0); jz.fill(0.0) }

    private fun addMass(mass: Double, bx: Double, bz: Double, gravity: Double) {
        for (i in 0 until 5) {
            val ji = jx[i]; val ki = jz[i]
            if (ji == 0.0 && ki == 0.0) continue
            for (j in 0 until 5) m[i][j] += mass * (ji * jx[j] + ki * jz[j])
            rhs[i] += -mass * (ji * bx + ki * bz) - ki * mass * gravity
        }
    }

    private fun addForce(fx: Double, fz: Double) {
        for (i in 0 until 5) rhs[i] += jx[i] * fx + jz[i] * fz
    }

    /** Baut Massenmatrix und rechte Seite. static = ohne Dämpfung und Längskräfte (Gleichgewicht). */
    private fun assemble(dt: Double, static: Boolean) {
        for (r in m) r.fill(0.0)
        rhs.fill(0.0)
        val v = veh
        val th = q[2]; val c = cos(th); val s = sin(th)
        val thd = qd[2]; val sd = qd[3]; val phd = qd[4]
        val g = gEff

        // --- gefederte Masse ---
        val wcx = c * v.cgX - s * v.cgZ
        val wcz = s * v.cgX + c * v.cgZ
        clearJ(); jx[0] = 1.0; jz[1] = 1.0; jx[2] = -wcz; jz[2] = wcx
        addMass(v.mS, -thd * thd * wcx, -thd * thd * wcz, g)
        m[2][2] += v.iS

        // --- Vorderrad (gleitet entlang der Gabelachse) ---
        val bfx = v.afX + v.u0x * (q[3] - sRef)
        val bfz = v.afZ + v.u0z * (q[3] - sRef)
        val wfx = c * bfx - s * bfz
        val wfz = s * bfx + c * bfz
        val rux = c * v.u0x - s * v.u0z
        val ruz = s * v.u0x + c * v.u0z
        pfX = q[0] + wfx; pfZ = q[1] + wfz
        vfX = qd[0] - thd * wfz + sd * rux
        val vfZ = qd[1] + thd * wfx + sd * ruz
        clearJ(); jx[0] = 1.0; jz[1] = 1.0; jx[2] = -wfz; jz[2] = wfx; jx[3] = rux; jz[3] = ruz
        val bfxA = -thd * thd * wfx - 2 * thd * sd * ruz
        val bfzA = -thd * thd * wfz + 2 * thd * sd * rux
        addMass(v.mUf, bfxA, bfzA, g)
        m[2][2] += 0.3 // Gleitrohre/Bremse drehen beim Nicken mit

        // Gabel: innere Kraft auf die Einfederung
        sMm = q[3] * 1000
        forkF = v.forkForce(sMm, if (static) 0.0 else sd)
        rhs[3] -= forkF

        // Reifen vorn
        roadF = road.height(0, pfX, t)
        deltaF = v.rf + roadF - pfZ
        fzF = 0.0
        if (deltaF > 0) {
            fzF = v.ktF * deltaF
            if (!static && !dPrevF.isNaN()) fzF += K7.CT_F * (deltaF - dPrevF) / dt
            if (fzF < 0) fzF = 0.0
        }
        dPrevF = deltaF
        reF = v.rf - max(deltaF, 0.0) / 3
        fxF = 0.0
        if (!static && !lockX) {
            val vx = vfX
            val ref = max(abs(vx), 1.0)
            val kappa = ((omF * reF - vx) / ref).coerceIn(-1.0, 3.0)
            kapF += dt * (ref / K7.RELAX_LEN) * (kappa - kapF)
            fxF = tyreLongForce(kapF, fzF, mu)
        }
        addForce(fxF, fzF)

        // --- Hinterrad und Schwinge ---
        val beta = th + q[4] + PI
        val ex = cos(beta); val ez = sin(beta)
        val ls = v.ls
        prX = q[0] + ls * ex; prZ = q[1] + ls * ez
        vrX = qd[0] - (thd + phd) * ls * ez
        clearJ(); jx[0] = 1.0; jz[1] = 1.0; jx[2] = -ls * ez; jz[2] = ls * ex; jx[4] = -ls * ez; jz[4] = ls * ex
        val w2 = (thd + phd) * (thd + phd)
        addMass(v.mWr, -w2 * ls * ex, -w2 * ls * ez, g)

        // Reifen hinten
        roadR = road.height(1, prX, t)
        deltaR = v.rr + roadR - prZ
        fzR = 0.0
        if (deltaR > 0) {
            fzR = v.ktR * deltaR
            if (!static && !dPrevR.isNaN()) fzR += K7.CT_R * (deltaR - dPrevR) / dt
            if (fzR < 0) fzR = 0.0
        }
        dPrevR = deltaR
        reR = v.rr - max(deltaR, 0.0) / 3
        fxR = 0.0
        if (!static && !lockX) {
            val vx = vrX
            val ref = max(abs(vx), 1.0)
            val kappa = ((omR * reR - vx) / ref).coerceIn(-1.0, 3.0)
            kapR += dt * (ref / K7.RELAX_LEN) * (kappa - kapR)
            fxR = tyreLongForce(kapR, fzR, mu)
        }
        addForce(fxR, fzR)

        // Kettenzug: Kraft am Hinterrad entlang des oberen Kettentrums
        chainF = 0.0
        var cdx = 0.0; var cdz = 0.0; var t2x = 0.0; var t2z = 0.0
        if (drive > 0 && !static) {
            val csx = q[0] + c * v.csX - s * v.csZ
            val csz = q[1] + s * v.csX + c * v.csZ
            val dx = csx - prX; val dz = csz - prZ
            val l = hypot(dx, dz)
            val ux = dx / l; val uz = dz / l
            val nd = ((v.rSprF - v.rSprR) / l).coerceIn(-0.99, 0.99)
            val pr = sqrt(1 - nd * nd)
            val nx = nd * ux - pr * uz
            val nz = nd * uz + pr * ux
            val t1x = prX + v.rSprR * nx; val t1z = prZ + v.rSprR * nz
            t2x = csx + v.rSprF * nx; t2z = csz + v.rSprF * nz
            val tl = hypot(t2x - t1x, t2z - t1z)
            cdx = (t2x - t1x) / tl; cdz = (t2z - t1z) / tl
            chainF = drive / v.rSprR
            addForce(chainF * cdx, chainF * cdz)
        }

        // Federbein: innere Kraft auf den Schwingenwinkel
        val phi = q[4]
        xRint = v.rearInternal(phi)
        val xDot = if (static) 0.0 else -ls * cos(phi) * phd
        rearF = v.rearForce(xRint, xDot)
        rhs[4] += rearF * ls * cos(phi)

        // Schwinge (Schwerpunkt Mitte)
        clearJ(); jx[0] = 1.0; jz[1] = 1.0; jx[2] = -ls / 2 * ez; jz[2] = ls / 2 * ex; jx[4] = -ls / 2 * ez; jz[4] = ls / 2 * ex
        addMass(v.mSa, -w2 * ls / 2 * ex, -w2 * ls / 2 * ez, g)
        m[2][2] += v.iSa; m[2][4] += v.iSa; m[4][2] += v.iSa; m[4][4] += v.iSa

        if (!static) {
            // Bremsmomente stützen sich an Gabel (vorn) bzw. Schwinge (hinten) ab
            val tbF = brakeF * tanh(omF * reF / 0.3)
            val tbR = brakeR * tanh(omR * reR / 0.3)
            rhs[2] -= tbF
            rhs[2] -= tbR
            rhs[4] -= tbR
            // Gegenkraft des Kettenzugs am Ritzel (Rahmen)
            if (chainF > 0) {
                val wx = t2x - q[0]; val wz = t2z - q[1]
                clearJ(); jx[0] = 1.0; jz[1] = 1.0; jx[2] = -wz; jz[2] = wx
                addForce(-chainF * cdx, -chainF * cdz)
            }
            // Luftwiderstand am Druckpunkt
            val vx = qd[0]
            val fa = -0.5 * K7.RHO * K7.CDA * vx * abs(vx)
            val ax = c * v.aeroX - s * v.aeroZ; val az = s * v.aeroX + c * v.aeroZ
            clearJ(); jx[0] = 1.0; jz[1] = 1.0; jx[2] = -az; jz[2] = ax
            addForce(fa, 0.0)
        }
    }

    /** Ein Zeitschritt. */
    fun step(dt: Double) {
        assemble(dt, false)
        solveInPlace(if (lockX) 1 else 0)
        if (lockX) qdd[0] = 0.0
        val tbF = brakeF * tanh(omF * reF / 0.3)
        val tbR = brakeR * tanh(omR * reR / 0.3)
        val rollF = K7.ROLL_RES * fzF * reF * tanh(omF * 2)
        val rollR = K7.ROLL_RES * fzR * reR * tanh(omR * 2)
        val iR = K7.I_WHEEL_R + K7.I_ENGINE * K7.overallRatio(gear).let { it * it }
        if (!lockX) {
            omF += dt * (-tbF - reF * fxF - rollF) / K7.I_WHEEL_F
            omR += dt * (drive - tbR - reR * fxR - rollR) / iR
            if (omF < 0) omF = 0.0
            if (omR < 0) omR = 0.0
        }
        for (i in 0 until 5) {
            qd[i] += qdd[i] * dt
            q[i] += qd[i] * dt
        }
        t += dt
    }

    private val work = Array(5) { DoubleArray(6) }

    /** Gauß-Elimination mit Pivotsuche ohne Speicheranforderung je Schritt. */
    private fun solveInPlace(from: Int) {
        val k = 5 - from
        for (i in 0 until k) {
            for (j in 0 until k) work[i][j] = m[i + from][j + from]
            work[i][k] = rhs[i + from]
        }
        for (col in 0 until k) {
            var piv = col
            for (r in col + 1 until k) if (abs(work[r][col]) > abs(work[piv][col])) piv = r
            if (piv != col) { val tmp = work[piv]; work[piv] = work[col]; work[col] = tmp }
            val d = work[col][col]
            for (r in col + 1 until k) {
                val f = work[r][col] / d
                if (f == 0.0) continue
                for (cc in col..k) work[r][cc] -= f * work[col][cc]
            }
        }
        for (r in k - 1 downTo 0) {
            var sum = work[r][k]
            for (cc in r + 1 until k) sum -= work[r][cc] * qdd[cc + from]
            qdd[r + from] = sum / work[r][r]
        }
    }

    /** Statisches Gleichgewicht (Newton-Verfahren) für z, θ, s, φ bei x = 0. */
    fun settleStatic(): Boolean {
        q[0] = 0.0; q[1] = veh.pivotZ - 0.012; q[2] = 0.0; q[3] = sRef; q[4] = veh.phiRef
        qd.fill(0.0)
        val brk = brakeF; val brR = brakeR; val drv = drive
        brakeF = 0.0; brakeR = 0.0; drive = 0.0
        val n = 4
        val jac = Array(n) { DoubleArray(n) }
        val r0 = DoubleArray(n)
        val dx = DoubleArray(n)
        var ok = false
        for (it in 0 until 60) {
            residual(r0)
            val norm = r0.maxOf { abs(it) }
            if (norm < 1e-4) { ok = true; break }
            for (j in 0 until n) {
                val h = -1e-6   // nach unten stören, damit der Reifenkontakt erhalten bleibt
                val keep = q[j + 1]
                q[j + 1] = keep + h
                val r1 = DoubleArray(n); residual(r1)
                q[j + 1] = keep
                for (i in 0 until n) jac[i][j] = (r1[i] - r0[i]) / h
            }
            val b = DoubleArray(n) { -r0[it] }
            solveSmall(jac, b, dx)
            // Schritt begrenzen (Kontakt und Anschläge sind nichtlinear)
            var scale = 1.0
            val lim = doubleArrayOf(0.01, 0.01, 0.01, 0.02)
            for (i in 0 until n) if (abs(dx[i]) * scale > lim[i]) scale = lim[i] / abs(dx[i])
            for (i in 0 until n) q[i + 1] += dx[i] * scale
        }
        residual(r0)
        dPrevF = deltaF; dPrevR = deltaR
        brakeF = brk; brakeR = brR; drive = drv
        return ok
    }

    private fun residual(out: DoubleArray) {
        assemble(1e-3, true)
        for (i in 0 until 4) out[i] = rhs[i + 1]
    }

    /** Startzustand mit Fahrgeschwindigkeit v0 (m/s). */
    fun startRolling(v0: Double) {
        qd.fill(0.0)
        qd[0] = v0
        omF = v0 / reF
        omR = v0 / reR
        kapF = 0.0; kapR = 0.0
    }

    companion object {
        fun solveSmall(a: Array<DoubleArray>, b: DoubleArray, x: DoubleArray) {
            val n = b.size
            val mtx = Array(n) { i -> DoubleArray(n + 1) { j -> if (j < n) a[i][j] else b[i] } }
            for (col in 0 until n) {
                var piv = col
                for (r in col + 1 until n) if (abs(mtx[r][col]) > abs(mtx[piv][col])) piv = r
                val tmp = mtx[piv]; mtx[piv] = mtx[col]; mtx[col] = tmp
                val d = mtx[col][col]
                if (abs(d) < 1e-12) { x.fill(0.0); return }
                for (r in col + 1 until n) {
                    val f = mtx[r][col] / d
                    for (cc in col until n + 1) mtx[r][cc] -= f * mtx[col][cc]
                }
            }
            for (r in n - 1 downTo 0) {
                var sum = mtx[r][n]
                for (cc in r + 1 until n) sum -= mtx[r][cc] * x[cc]
                x[r] = sum / mtx[r][r]
            }
        }
    }
}
