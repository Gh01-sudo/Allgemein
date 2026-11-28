package de.k7labor.fahrwerk.ui.bike

import de.k7labor.fahrwerk.physics.K7
import de.k7labor.fahrwerk.physics.Physics
import de.k7labor.fahrwerk.physics.Setup
import de.k7labor.fahrwerk.physics.Vehicle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs

class BikeGeometryTest {
    private val sim = Physics.staticState(Setup.STOCK)
    private val f = BikeFrame(sim.veh, SceneState.fromSim(sim))

    @Test fun axlesMatchPhysicsPose() {
        val p = Physics.pose(sim.veh, sim.q[0], sim.q[1], sim.q[2], sim.q[3], sim.q[4])
        assertEquals(p.frontAxle.first, f.frontAxle.x, 1e-9)
        assertEquals(p.frontAxle.second, f.frontAxle.z, 1e-9)
        assertEquals(p.rearAxle.first, f.rearAxle.x, 1e-9)
        assertEquals(p.rearAxle.second, f.rearAxle.z, 1e-9)
    }

    @Test fun forkShortensWithTravel() {
        val s0 = SceneState.fromSim(sim)
        val s1 = s0.copy(s = s0.s + 0.05)
        val g0 = BikeFrame(sim.veh, s0).forkGap
        val g1 = BikeFrame(sim.veh, s1).forkGap
        assertEquals(0.05, g0 - g1, 1e-9)
    }

    @Test fun shockShortensWithWheelTravel() {
        val s0 = SceneState.fromSim(sim)
        val a = BikeFrame(sim.veh, s0.copy(xRmm = 0.0)).shockLen
        val b = BikeFrame(sim.veh, s0.copy(xRmm = K7.TRAVEL_R_MM)).shockLen
        assertTrue("Federbein muss kürzer werden", b < a)
        assertTrue("Hub plausibel", (a - b) in 0.04..0.07)
    }

    @Test fun hitTestFindsParts() {
        val tol = 0.05
        val forkMid = f.frontAxle + (f.forkCap - f.frontAxle) * 0.6
        assertEquals(BikePart.FORK, f.hit(forkMid, tol))
        val armMid = f.pivot + (f.rearAxle - f.pivot) * 0.7
        assertEquals(BikePart.SWINGARM, f.hit(armMid, tol))
        val tyre = f.frontAxle + V2(0.0, -sim.veh.rf + 0.02)
        assertEquals(BikePart.FRONT_WHEEL, f.hit(tyre, tol))
        val rtyre = f.rearAxle + V2(-sim.veh.rr + 0.02, 0.0)
        assertEquals(BikePart.REAR_WHEEL, f.hit(rtyre, tol))
        assertEquals(null, f.hit(V2(f.pivot.x, 3.0), tol))
        for (i in BikeFrame.ADJUSTERS.indices) assertEquals(BikeFrame.ADJUSTERS[i].part, f.hit(f.adjusterPos(i), tol))
    }

    @Test fun chainTangentIsTangent() {
        val v = Vehicle(Setup.STOCK, Physics.calibration)
        val (a, b) = BikeFrame.chainUpper(f.countershaft, v.rSprF, f.rearAxle, v.rSprR)
        assertEquals(v.rSprR, (a - f.rearAxle).len, 1e-9)
        assertEquals(v.rSprF, (b - f.countershaft).len, 1e-9)
        val t = (b - a).norm(); val n = (a - f.rearAxle).norm()
        assertTrue(abs(t.x * n.x + t.z * n.z) < 1e-9)
        assertTrue("oberer Trum liegt oben", n.z > 0)
    }

    @Test fun traceFrameStateIsFinite() {
        val r = de.k7labor.fahrwerk.physics.runScenario(de.k7labor.fahrwerk.physics.ScenarioId.BUMP, Setup.STOCK, 80.0, Physics.calibration)
        for (i in 0 until r.trace.size step 50) {
            val fr = BikeFrame(sim.veh, SceneState.fromTrace(r, i, 3.0))
            assertTrue(fr.frontAxle.x.isFinite() && fr.shockBottom.z.isFinite())
        }
    }
}
