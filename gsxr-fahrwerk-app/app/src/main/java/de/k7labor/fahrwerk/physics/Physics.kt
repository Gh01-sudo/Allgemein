package de.k7labor.fahrwerk.physics

import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.round
import kotlin.math.sin

data class Sags(val sagF: Double, val sagR: Double, val freeF: Double, val freeR: Double, val loadF: Double, val loadR: Double)

data class Pose(
    val rakeDeg: Double, val trailMm: Double, val wheelbaseMm: Double, val seatMm: Double, val pitchDeg: Double,
    val frontAxle: Pair<Double, Double>, val rearAxle: Pair<Double, Double>, val pivot: Pair<Double, Double>
)

data class RunAll(val results: Map<ScenarioId, ScenarioResult>, val sags: Sags, val staticScore: Double, val overall: Double)

object Physics {

    /** Grundvorspannung so bestimmen, dass Serie mit 80 kg 36 / 32 mm Durchhang ergibt. */
    val calibration: Calibration by lazy { calibrate() }

    fun calibrate(): Calibration {
        var p0F = 15.0
        var p0R = 8.0
        for (i in 0 until 40) {
            val sim = Sim(Vehicle(Setup.STOCK, Calibration(p0F, p0R)), FLAT_ROAD, 1.0)
            sim.settleStatic()
            val dF = sim.sMm - K7.REF_SAG_RIDER_F
            val dR = sim.xRint - K7.REF_SAG_RIDER_R
            if (abs(dF) < 0.01 && abs(dR) < 0.01) break
            p0F += dF * 0.9
            p0R += dR / K7.LR0 * 0.9
        }
        return Calibration(p0F, p0R)
    }

    fun staticState(st: Setup, cal: Calibration = calibration): Sim {
        val sim = Sim(Vehicle(st, cal), FLAT_ROAD, st.profile.mu)
        sim.settleStatic()
        return sim
    }

    fun sags(st: Setup, cal: Calibration = calibration): Sags {
        val withRider = staticState(st, cal)
        // ohne Fahrer: direkt gebaut, weil der Regler für das Fahrergewicht erst bei 50 kg beginnt
        val free = staticState(Setup(st.values + mapOf("rider" to 0.0, "passenger" to 0.0, "luggage" to 0.0), st.profile), cal)
        return Sags(withRider.sMm, withRider.xRint, free.sMm, free.xRint, withRider.fzF / G, withRider.fzR / G)
    }

    fun pose(veh: Vehicle, x: Double, z: Double, th: Double, s: Double, phi: Double): Pose {
        val c = cos(th); val sn = sin(th)
        val sRef = K7.REF_SAG_F_MM / 1000
        val bfx = veh.afX + veh.u0x * (s - sRef)
        val bfz = veh.afZ + veh.u0z * (s - sRef)
        val fx = x + c * bfx - sn * bfz
        val fz = z + sn * bfx + c * bfz
        val beta = th + phi + Math.PI
        val rx = x + veh.ls * cos(beta)
        val rz = z + veh.ls * sin(beta)
        val rake = veh.eps + th
        // Lenkachse: liegt um den Offset hinter der Vorderachse, Richtung (−sin, cos)
        val off = K7.offsetMm / 1000
        val nx = cos(rake); val nz = sin(rake)
        val ax = fx - nx * off; val az = fz - nz * off
        val ux = -sin(rake); val uz = cos(rake)
        val tg = -az / uz
        val groundX = ax + ux * tg
        val trail = groundX - fx
        val seatZ = z + sn * veh.seatX + c * veh.seatZ
        // Pitch zum Boden: Linie der Radachsen gegenüber Bezugslage
        val pitch = atan2(fz - rz, fx - rx) - atan2(veh.rf - veh.rr, veh.wb)
        return Pose(rake / DEG, trail * 1000, (fx - rx) * 1000, seatZ * 1000, pitch / DEG, fx to fz, rx to rz, x to z)
    }

    /** Werksgeometrie in der Bezugslage (Kontrolle der Kinematik). */
    fun referencePose(): Pose {
        val veh = Vehicle(Setup.STOCK, calibration)
        return pose(veh, 0.0, veh.pivotZ, 0.0, K7.REF_SAG_F_MM / 1000, veh.phiRef)
    }

    fun staticPose(st: Setup): Pose {
        val sim = staticState(st)
        return pose(sim.veh, sim.q[0], sim.q[1], sim.q[2], sim.q[3], sim.q[4])
    }

    fun staticScore(st: Setup, sg: Sags): Double {
        val p = st.profile
        fun dev(v: Double, w: Pair<Double, Double>) = if (v < w.first) w.first - v else if (v > w.second) v - w.second else 0.0
        return (100 - dev(sg.sagF, p.sagWinF) * 6 - dev(sg.sagR, p.sagWinR) * 6).coerceIn(0.0, 100.0)
    }

    fun runAll(st: Setup, speedKmh: Double, ids: List<ScenarioId> = ScenarioId.entries, cancelled: () -> Boolean = { false }): RunAll {
        val res = LinkedHashMap<ScenarioId, ScenarioResult>()
        for (id in ids) {
            if (cancelled()) break
            res[id] = runScenario(id, st, speedKmh, calibration)
        }
        val sg = sags(st)
        val ss = staticScore(st, sg)
        val scored = res.values.filter { it.id.scored }
        val dyn = if (scored.isEmpty()) 0.0 else scored.sumOf { it.score } / scored.size
        return RunAll(res, sg, ss, 0.75 * dyn + 0.25 * ss)
    }

    // ---------------- Empfehlung ----------------

    data class Note(val level: Level, val title: String, val text: String)
    enum class Level { INFO, WARN, BAD }
    data class Recommendation(val setup: Setup, val notes: List<Note>, val kFrec: Double, val kRrec: Double)

    /**
     * Vorschlag aus Ziel-Durchhang, Profil und Fahrergewicht. Ölstand wird nur schrittweise (+5 mm)
     * und nur bei echter Durchschlaggefahr (über 95 % Federweg beim Bremsen) empfohlen (Review-Punkt 5).
     */
    fun recommend(st: Setup, speedKmh: Double): Recommendation {
        val p = st.profile
        val notes = mutableListOf<Note>()
        var out = st
        // Vorspannung vorn: Ziel-Durchhang per Sekantenverfahren über das volle Modell
        val ringsRaw = solvePreload(st, true, p.sagF)
        out = out.with("fPre", ringsRaw)
        val rRaw = solvePreload(out, false, p.sagR)
        out = out.with("rPre", rRaw)
        // Dämpfung
        val wcorr = round((st.rider + st.passenger * 0.6 - 80) / 12)
        val s = Setup.STOCK
        out = out.with("fReb", s.fReb + p.offFReb - wcorr)
            .with("fLS", s.fLS + p.offFLS - wcorr)
            .with("fHS", s.fHS + p.offFHS)
            .with("rReb", s.rReb + p.offRReb - wcorr - (if (st.passenger > 0) 2 else 0))
            .with("rLS", s.rLS + p.offRLS - wcorr - (if (st.passenger > 0) 1 else 0))
            .with("rHS", s.rHS + p.offRHS)
        val two = st.passenger > 0 && p != Profile.TRACK
        out = out.with("pF", if (two) 2.5 else p.pF).with("pR", if (two) 2.9 else p.pR)
        // Durchschlagreserve Gabel beim Bremsen prüfen
        val r = runScenario(ScenarioId.BRAKE, out, speedKmh, calibration)
        if (r.front.usage > 0.95) {
            if (out.fOil < 10) {
                out = out.with("fOil", out.fOil + 5)
                notes += Note(Level.INFO, "Ölstand Gabel +5 mm prüfen", "Beim starken Bremsen nutzt die Gabel ${fmt(r.front.maxX, 0)} von 125 mm. Mehr Öl bringt Endprogression gegen Durchschlagen, ohne das erste Federwegsdrittel zu ändern. Werkstattarbeit: bei ausgebauten Federn und ganz eingefahrener Gabel messen.")
            } else {
                out = out.with("fLS", out.fLS - 1)
            }
        }
        val ref = Vehicle.REF_LOADS
        val now = sags(st)
        val kFrec = K7.K_F_STOCK * (now.loadF - K7.M_UNSPRUNG_F) / ref.first
        val kRrec = K7.K_R_STOCK * (now.loadR - K7.M_WHEEL_R - K7.M_SWINGARM / 2) / ref.second
        if (ringsRaw < 0) notes += Note(Level.BAD, "Gabelfedern zu weich für ${fmt(st.rider, 0)} kg", "Für ${fmt(p.sagF, 0)} mm Durchhang bräuchte es ${fmt(-ringsRaw, 1)} Ringe mehr Vorspannung als möglich. Richtwert je Holm: ${fmt(kFrec, 2)} N/mm (${fmt(kFrec / 9.81, 3)} kg/mm).")
        if (ringsRaw > 8) notes += Note(Level.WARN, "Gabelfedern eher zu hart", "Selbst mit minimaler Vorspannung wird der Ziel-Durchhang nicht erreicht. Richtwert je Holm: ${fmt(kFrec, 2)} N/mm.")
        if (rRaw > 12) notes += Note(Level.BAD, "Federbeinfeder zu weich für die Zuladung", "Es wären ${fmt(rRaw, 1)} mm Vorspannung nötig. Richtwert: ${fmt(kRrec, 0)} N/mm (${fmt(kRrec / 9.81, 1)} kg/mm).")
        if (rRaw < -5) notes += Note(Level.WARN, "Federbeinfeder eher zu hart", "Richtwert: ${fmt(kRrec, 0)} N/mm.")
        return Recommendation(out, notes, kFrec, kRrec)
    }

    /** Hinweise aus dem Rechenlauf, wie ein Prüfstandsprotokoll. */
    fun insights(st: Setup, run: RunAll): List<Note> {
        val out = mutableListOf<Note>()
        val p = st.profile
        val sg = run.sags
        fun win(v: Double, w: Pair<Double, Double>) = v >= w.first && v <= w.second
        if (!win(sg.sagF, p.sagWinF)) out += Note(Level.WARN, "Durchhang vorn ${fmt(sg.sagF, 1)} mm", "Ziel für „${p.label}“: ${fmt(p.sagWinF.first, 0)}–${fmt(p.sagWinF.second, 0)} mm. ${if (sg.sagF > p.sagWinF.second) "Mehr Vorspannung (weniger Ringe sichtbar)." else "Weniger Vorspannung (mehr Ringe sichtbar)."}")
        if (!win(sg.sagR, p.sagWinR)) out += Note(Level.WARN, "Durchhang hinten ${fmt(sg.sagR, 1)} mm", "Ziel für „${p.label}“: ${fmt(p.sagWinR.first, 0)}–${fmt(p.sagWinR.second, 0)} mm. ${if (sg.sagR > p.sagWinR.second) "Federbein mehr vorspannen." else "Federbein weniger vorspannen."}")
        if (sg.freeF < 15) out += Note(Level.WARN, "Wenig freier Durchhang vorn (${fmt(sg.freeF, 1)} mm)", "Die Gabel steht ohne Fahrer fast ganz ausgefedert. Das Rad kann in Senken nicht nachfedern. Ursache meist: Feder für das Fahrergewicht zu weich und mit viel Vorspannung ausgeglichen.")
        for ((id, r) in run.results) {
            if (!id.scored) continue
            if (r.front.bottom) out += Note(Level.BAD, "Gabel schlägt durch: ${id.short}", "Genutzt ${fmt(r.front.maxX, 0)} von 125 mm. Druckstufe vorn schließen oder Ölstand prüfen.")
            if (r.rear.bottom) out += Note(Level.BAD, "Federbein schlägt durch: ${id.short}", "Genutzt ${fmt(r.rear.maxX, 0)} von 130 mm Radweg. Vorspannung oder Druckstufe hinten erhöhen.")
            if (r.front.liftMs > 30 || r.rear.liftMs > 30) out += Note(Level.WARN, "Rad ohne Bodenkontakt: ${id.short}", "Vorn ${fmt(r.front.liftMs, 0)} ms, hinten ${fmt(r.rear.liftMs, 0)} ms ohne Last. Kurzes Abheben über Kanten ist bei Tempo normal; viel davon kostet Haftung.")
        }
        run.results[ScenarioId.BRAKE]?.let { r ->
            if (r.veh.frontLockMs > 20) out += Note(Level.BAD, "Vorderrad blockiert bei 0,8 g", "Ohne ABS rutscht das Vorderrad ${fmt(r.veh.frontLockMs, 0)} ms. Reibwert oder Luftdruck prüfen.")
            if (r.veh.stoppieMs > 20) out += Note(Level.WARN, "Hinterrad hebt beim Bremsen ab", "${fmt(r.veh.stoppieMs, 0)} ms ohne Last hinten.")
            if (r.rear.minX < 0) out += Note(Level.INFO, "Heck federt beim Bremsen ganz aus", "Bei 0,8 g normal. Die gedämpfte Ausfederbegrenzung verhindert ein Hüpfen; kritisch wird es erst, wenn das Rad dabei die Last verliert.")
        }
        run.results[ScenarioId.FULLBRAKE]?.let { r ->
            if (r.veh.frontLockMs > 20 || r.veh.stoppieMs > 20) out += Note(Level.INFO, "Grenzbereich Vollbremsung 1,1 g", "Vorderrad blockiert ${fmt(r.veh.frontLockMs, 0)} ms, Hinterrad abgehoben ${fmt(r.veh.stoppieMs, 0)} ms. Die K7 hat kein ABS: Grenze liegt beim Fahrer.")
        }
        run.results[ScenarioId.ACCEL]?.let { r ->
            if (r.veh.wheelieMs > 20) out += Note(Level.WARN, "Wheelie beim Beschleunigen", "Vorderrad ${fmt(r.veh.wheelieMs, 0)} ms ohne Last.")
            if (r.veh.spinMs > 20) out += Note(Level.WARN, "Hinterrad dreht durch", "${fmt(r.veh.spinMs, 0)} ms mit mehr als 25 % Schlupf.")
        }
        // Luftdruck (Review-Punkt 16: schon ab 0,05 bar Abweichung melden)
        val two = st.passenger > 0
        val wantF = if (p == Profile.TRACK && !two) p.pF else 2.5
        val wantR = if (p == Profile.TRACK && !two) p.pR else 2.9
        if (abs(st.pF - wantF) > 0.025 || abs(st.pR - wantR) > 0.025) out += Note(Level.INFO, "Luftdruck weicht ab", "Eingestellt ${fmt(st.pF, 2)} / ${fmt(st.pR, 2)} bar kalt, Richtwert ${fmt(wantF, 2)} / ${fmt(wantR, 2)} bar kalt${if (p == Profile.TRACK) " (Rennstrecke: Reifenhersteller-Angabe für warme Reifen beachten)" else " (Suzuki)"}.")
        if (out.isEmpty()) out += Note(Level.INFO, "Keine Auffälligkeiten", "Durchhang im Zielfenster, keine Durchschläge, kein Blockieren.")
        return out
    }

    /** Vorspannung (Ringe vorn bzw. mm hinten) für einen Ziel-Durchhang, ohne Begrenzung. */
    fun solvePreload(st: Setup, front: Boolean, target: Double): Double {
        fun sag(x: Double): Double {
            val vals = st.values + ((if (front) "fPre" else "rPre") to x)
            val s = staticState(Setup(vals, st.profile))
            return if (front) s.sMm else s.xRint
        }
        var x0 = if (front) st.fPre else st.rPre
        var f0 = sag(x0) - target
        var x1 = x0 + if (front) 1.0 else 2.0
        var f1 = sag(x1) - target
        for (i in 0 until 12) {
            if (abs(f1) < 0.05 || abs(f1 - f0) < 1e-9) break
            val x2 = x1 - f1 * (x1 - x0) / (f1 - f0)
            x0 = x1; f0 = f1; x1 = x2.coerceIn(-40.0, 40.0); f1 = sag(x1) - target
        }
        return x1
    }
}

fun fmt(v: Double, d: Int = 0): String {
    val x = if (v.isFinite()) v else 0.0
    val s = String.format(java.util.Locale.GERMANY, "%,.${d}f", x)
    return if (s.trimStart('-').all { it == '0' || it == ',' || it == '.' }) s.trimStart('-') else s
}
