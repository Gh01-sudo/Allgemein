package de.k7labor.fahrwerk.physics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class PhysicsTest {

    @Test fun calibrationHitsStockSag() {
        val sg = Physics.sags(Setup.STOCK)
        assertEquals(36.0, sg.sagF, 0.3)
        assertEquals(32.0, sg.sagR, 0.3)
        assertTrue("freier Durchhang vorn ${sg.freeF}", sg.freeF in 15.0..30.0)
        assertTrue("freier Durchhang hinten ${sg.freeR}", sg.freeR in 3.0..20.0)
    }

    @Test fun referenceGeometryIsFactory() {
        val p = Physics.referencePose()
        assertEquals(K7.RAKE_DEG, p.rakeDeg, 0.01)
        assertEquals(K7.TRAIL_MM, p.trailMm, 0.2)
        assertEquals(K7.WHEELBASE_MM, p.wheelbaseMm, 0.5)
        assertEquals(K7.SEAT_H_MM, p.seatMm, 0.5)
    }

    @Test fun staticWheelLoadsEqualWeight() {
        for (rider in listOf(50.0, 80.0, 120.0)) {
            val sim = Physics.staticState(Setup.STOCK.with("rider", rider))
            assertEquals(sim.veh.mTotal * G, sim.fzF + sim.fzR, 1.0)
        }
    }

    @Test fun moreRiderMoreSag() {
        val a = Physics.sags(Setup.STOCK.with("rider", 70.0))
        val b = Physics.sags(Setup.STOCK.with("rider", 100.0))
        assertTrue(b.sagF > a.sagF && b.sagR > a.sagR)
    }

    @Test fun preloadDirections() {
        val base = Physics.sags(Setup.STOCK)
        // weniger Ringe sichtbar = mehr Vorspannung = weniger Durchhang
        assertTrue(Physics.sags(Setup.STOCK.with("fPre", 2.0)).sagF < base.sagF)
        assertTrue(Physics.sags(Setup.STOCK.with("rPre", 4.0)).sagR < base.sagR)
    }

    @Test fun brakingReachesTargetAndTransfersLoad() {
        val r = runScenario(ScenarioId.BRAKE, Setup.STOCK, 100.0, Physics.calibration)
        assertEquals(0.8, r.veh.meanDecel, 0.05)
        assertTrue("Gabel taucht ein", r.front.maxX > 70)
        assertEquals(0.0, r.veh.frontLockMs, 0.1)
        assertTrue("Ausfedern hinten gedämpft, kein dauerhaftes Hüpfen: ${r.rear.liftMs}", r.rear.liftMs < 50)
    }

    @Test fun timestepConverged() {
        val a = runScenario(ScenarioId.BUMP, Setup.STOCK, 80.0, Physics.calibration)
        val b = runScenario(ScenarioId.BUMP, Setup.STOCK, 80.0, Physics.calibration, DT / 2)
        assertEquals(a.front.maxX, b.front.maxX, a.front.maxX * 0.01)
        assertEquals(a.rear.maxX, b.rear.maxX, a.rear.maxX * 0.01)
    }

    @Test fun extremeSetupsStayFinite() {
        val lo = Setup(Controls.all.associate { it.key to it.min })
        val hi = Setup(Controls.all.associate { it.key to it.max })
        for (s in listOf(lo, hi, hi.copy(profile = Profile.REGEN), lo.copy(profile = Profile.TRACK))) {
            val r = Physics.runAll(s, 250.0)
            assertTrue(r.overall.isFinite())
            for (x in r.results.values) {
                assertTrue("${x.id} ${x.front}", x.front.maxX.isFinite() && x.rear.maxX.isFinite() && x.score.isFinite())
                for (i in 0 until x.trace.size) assertTrue(x.trace.fzF[i].isFinite() && x.trace.sF[i].isFinite())
            }
        }
    }

    @Test fun lowSpeedStaysFinite() {
        val r = Physics.runAll(Setup.STOCK, 30.0)
        for (x in r.results.values) assertTrue(x.score.isFinite())
    }

    @Test fun moreCompressionDampingLessTravelOnBump() {
        val soft = runScenario(ScenarioId.BRAKE, Setup.STOCK.with("fLS", 20.0), 100.0, Physics.calibration)
        val hard = runScenario(ScenarioId.BRAKE, Setup.STOCK.with("fLS", 4.0), 100.0, Physics.calibration)
        assertTrue(hard.front.maxX < soft.front.maxX)
    }

    @Test fun shakerFindsBodyResonance() {
        val f = Rig.shake(Setup.STOCK, ShakeMode.HEAVE)
        val (fb, _) = f.peak(f.travelF, 0.8, 5.0)
        assertTrue("Aufbau vorn $fb Hz", fb in 1.3..3.5)
    }

    @Test fun recommendationHitsTargetSag() {
        val st = Setup.STOCK.with("rider", 86.0)
        val rec = Physics.recommend(st, 80.0)
        val sg = Physics.sags(rec.setup)
        assertTrue("vorn ${sg.sagF}", abs(sg.sagF - st.profile.sagF) < 1.0)
        assertTrue("hinten ${sg.sagR}", abs(sg.sagR - st.profile.sagR) < 1.5)
        // 110 kg: Vorspannung reicht nicht, Hinweis auf zu weiche Gabelfedern
        val heavy = Physics.recommend(Setup.STOCK.with("rider", 110.0), 80.0)
        assertTrue(heavy.notes.any { it.title.contains("zu weich") })
    }

    @Test fun stockValuesOnGrid() {
        for (c in Controls.all) assertEquals(c.key, c.stock, c.sanitize(c.stock), 1e-9)
    }

    @Test fun checksAllPass() {
        val failed = Checks.run().filter { !it.ok }
        assertTrue(failed.joinToString { "${it.name}: ${it.value}" }, failed.isEmpty())
    }

    @Test fun sanitizeRoundsToGrid() {
        val c = Controls.get("fReb")
        assertEquals(3.0, c.sanitize(3.3), 1e-9)
        assertEquals(20.0, c.sanitize(99.0), 1e-9)
        assertEquals(2.55, Controls.get("pF").sanitize(2.549), 1e-9)
    }
}
