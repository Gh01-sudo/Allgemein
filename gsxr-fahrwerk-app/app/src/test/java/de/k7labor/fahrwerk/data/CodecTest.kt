package de.k7labor.fahrwerk.data

import de.k7labor.fahrwerk.physics.Profile
import de.k7labor.fahrwerk.physics.Setup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CodecTest {
    @Test fun invalidNumberIsIgnoredNotZeroed() {
        val r = SetupCodec.parse("""{"setup":{"rider":"abc","fReb":4}}""", Setup.STOCK)
        val s = r.setup!!
        assertEquals(80.0, s.rider, 1e-9)
        assertEquals(4.0, s.fReb, 1e-9)
        assertTrue(r.messages.any { it.contains("ignoriert") })
    }

    @Test fun roundsAndClamps() {
        val r = SetupCodec.parse("""{"setup":{"fReb":3.3,"rider":500,"pF":"2,47"}}""", Setup.STOCK)
        val s = r.setup!!
        assertEquals(3.0, s.fReb, 1e-9)
        assertEquals(140.0, s.rider, 1e-9)
        assertEquals(2.45, s.pF, 1e-9)
        assertEquals(3, r.messages.size)
    }

    @Test fun legacyProfileAndRoundTrip() {
        val r = SetupCodec.parse("""{"setup":{"fLS":10},"profile":"track"}""", Setup.STOCK)
        assertEquals(Profile.TRACK, r.setup!!.profile)
        val txt = SetupCodec.export(r.setup!!, "Test")
        val back = SetupCodec.parse(txt, Setup.STOCK)
        assertEquals(r.setup, back.setup)
        assertEquals("Test", back.name)
    }

    @Test fun garbageRejected() {
        assertNull(SetupCodec.parse("hallo", Setup.STOCK).setup)
        assertNull(SetupCodec.parse("""{"x":1}""", Setup.STOCK).setup)
    }

    @Test fun brokenLibraryEntrySkipped() {
        val good = SetupCodec.libraryToJson(listOf(SavedSetup("a", "A", Setup.STOCK, 1)))
        val mixed = good.dropLast(1) + """,{"profile":"unbekannt","setup":"kaputt"},42]"""
        val lib = SetupCodec.libraryFromJson(mixed)
        assertEquals(1, lib.size)
        assertEquals("a", lib[0].id)
        assertTrue(SetupCodec.libraryFromJson("{kaputt").isEmpty())
    }

    @Test fun sagValidation() {
        val bad = SagAdvice.evaluate(true, 140.0, 157.0, 144.0, Profile.SPORT, 4.0)
        assertTrue(!bad.valid && bad.error!!.contains("vertauscht"))
        val ok = SagAdvice.evaluate(true, 180.0, 157.0, 144.0, Profile.SPORT, 4.0)
        assertTrue(ok.valid)
        assertEquals(36.0, ok.rider, 1e-9)
        assertNotNull(ok.newValue)
        assertEquals(2.0, ok.newValue!!, 1e-9) // 2 mm zu viel Durchhang → 2 Ringe weniger sichtbar
        val capped = SagAdvice.evaluate(true, 180.0, 157.0, 130.0, Profile.SPORT, 1.0)
        assertEquals(0.0, capped.newValue!!, 1e-9)
        assertTrue(capped.action.contains("außerhalb"))
    }
}
