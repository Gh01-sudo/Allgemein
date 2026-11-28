package de.k7labor.fahrwerk.physics

import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sqrt

/** Selbstprüfungen des Modells, wie ein Kalibrierprotokoll am Prüfstand. */
object Checks {
    data class Check(val name: String, val value: String, val ok: Boolean, val hint: String = "")

    fun run(): List<Check> {
        val out = mutableListOf<Check>()
        val disp = PI / 4 * 73.4 * 73.4 * 59.0 * 4 / 1000
        out += Check("Hubraum aus Bohrung × Hub", "${fmt(disp, 1)} cm³", abs(disp - 998.6) < 0.2)
        out += Check("Reifenradius vorn 120/70 R17", "${fmt(K7.RF_MM, 1)} mm = 215,9 + 0,70 × 120", abs(K7.RF_MM - (215.9 + 84)) < 0.05)
        out += Check("Reifenradius hinten 190/50 R17", "${fmt(K7.RR_MM, 1)} mm = 215,9 + 0,50 × 190", abs(K7.RR_MM - (215.9 + 95)) < 0.05)
        out += Check("Federraten-Umrechnung", "0,976 kg/mm × 9,81 = ${fmt(0.976 * 9.81, 2)} N/mm · 10,1 kg/mm = ${fmt(10.1 * 9.81, 1)} N/mm", abs(0.976 * 9.81 - K7.K_F_STOCK) < 0.01)

        val p0 = Physics.referencePose()
        out += Check(
            "Kinematik trifft Werksgeometrie in der Bezugslage",
            "${fmt(p0.rakeDeg, 2)}° · ${fmt(p0.trailMm, 1)} mm · ${fmt(p0.wheelbaseMm, 0)} mm · Sitz ${fmt(p0.seatMm, 0)} mm",
            abs(p0.rakeDeg - K7.RAKE_DEG) < 0.01 && abs(p0.trailMm - K7.TRAIL_MM) < 0.2 && abs(p0.wheelbaseMm - K7.WHEELBASE_MM) < 0.5 && abs(p0.seatMm - K7.SEAT_H_MM) < 0.5
        )
        val sg = Physics.sags(Setup.STOCK)
        out += Check("Kalibrierung Serie 80 kg (Ziel 36 / 32 mm)", "${fmt(sg.sagF, 1)} / ${fmt(sg.sagR, 1)} mm", abs(sg.sagF - 36) < 0.3 && abs(sg.sagR - 32) < 0.3)
        out += Check("Freier Durchhang Serie (Plausibilität)", "${fmt(sg.freeF, 1)} / ${fmt(sg.freeR, 1)} mm", sg.freeF in 12.0..32.0 && sg.freeR in 3.0..20.0, "Zielbereich frei: vorn 20–30, hinten 5–15 mm")

        val sim = Physics.staticState(Setup.STOCK)
        val sum = (sim.fzF + sim.fzR) / G
        out += Check("Gleichgewicht: Radlasten = Gesamtgewicht", "${fmt(sum, 1)} kg von ${fmt(sim.veh.mTotal, 1)} kg · vorn ${fmt(sim.fzF / (sim.fzF + sim.fzR) * 100, 1)} %", abs(sum - sim.veh.mTotal) < 0.05 * 1)

        val brake = runScenario(ScenarioId.BRAKE, Setup.STOCK, 100.0, Physics.calibration)
        val tr = brake.trace
        var fz = 0.0; var n = 0
        for (i in 0 until tr.size) if (tr.t[i] in 1.2f..2.0f) { fz += tr.fzF[i]; n++ }
        fz /= maxOf(n, 1)
        // Verzögerung in demselben Fenster aus dem Tempo
        val i1 = tr.t.indexOfFirst { it >= 1.2f }; val i2 = tr.t.indexOfFirst { it >= 2.0f }
        val a = (tr.speed[i1] - tr.speed[i2]) / 3.6 / (tr.t[i2] - tr.t[i1]) / G
        val veh = sim.veh
        val h = sim.veh.let { v ->
            // Gesamtschwerpunkthöhe in der statischen Lage
            val cgW = v.cgZ + sim.q[1]
            (v.mS * cgW + v.mUf * v.rf + v.mWr * v.rr + v.mSa * (sim.q[1] + v.rr) / 2) / v.mTotal
        }
        val expect = brake.staticF + veh.mTotal * a * G * h / veh.wb
        out += Check(
            "Lastverlagerung Bremsen (Kräftebilanz)",
            "Vorderradlast ${fmt(fz / 1000, 2)} kN bei ${fmt(a, 2)} g, Handrechnung ${fmt(expect / 1000, 2)} kN",
            abs(fz - expect) / expect < 0.08, "Handrechnung: statisch + m · a · h / Radstand (ohne Nick- und Luftkräfte)"
        )

        val fine = runScenario(ScenarioId.BUMP, Setup.STOCK, 80.0, Physics.calibration, DT / 2)
        val norm = runScenario(ScenarioId.BUMP, Setup.STOCK, 80.0, Physics.calibration)
        val dev = abs(fine.front.maxX - norm.front.maxX) / norm.front.maxX * 100
        out += Check("Zeitschritt 0,2 ms gegen 0,1 ms", "Gabelweg Bodenwelle ${fmt(norm.front.maxX, 2)} gegen ${fmt(fine.front.maxX, 2)} mm (${fmt(dev, 2)} %)", dev < 1.0, "Numerische Genauigkeit der Integration")

        val frf = Rig.shake(Setup.STOCK, ShakeMode.HEAVE)
        val (fBody, _) = frf.peak(frf.travelF, 0.8, 5.0)
        out += Check("Aufbau-Eigenfrequenz vorn beim Heben (Rüttelprüfstand)", "${fmt(fBody, 2)} Hz", fBody in 1.3..3.5, "Typisch für Supersportler: 1,5–3 Hz")
        val ksV = 2 * K7.K_F_STOCK * 1000 / (cos(K7.rake) * cos(K7.rake))
        val fHop = sqrt((tyreStiffness(true, 2.5) + ksV) / K7.M_UNSPRUNG_F) / (2 * PI)
        out += Check("Rad-Eigenfrequenz vorn (Handrechnung)", "${fmt(fHop, 1)} Hz", fHop in 12.0..22.0, "√((Reifen + Feder) / ungefederte Masse) / 2π")

        val acc = runScenario(ScenarioId.ACCEL, Setup.STOCK, 80.0, Physics.calibration)
        var squat = 0.0; var cnt = 0
        for (i in 0 until acc.trace.size) if (acc.trace.t[i] in 1.0f..1.6f) { squat += acc.trace.xR[i]; cnt++ }
        squat = squat / maxOf(cnt, 1) - acc.trace.xR[0]
        out += Check("Kettenzug beim Beschleunigen (Anti-Squat)", "Heck ${if (squat >= 0) "federt ${fmt(squat, 1)} mm ein" else "hebt sich ${fmt(-squat, 1)} mm"} bei 0,6 g", abs(squat) < 15, "Ergebnis der Kettenlinie; hängt stark von der angenommenen Ritzelposition ab")
        return out
    }
}
