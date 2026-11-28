package de.k7labor.fahrwerk.ui

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import de.k7labor.fahrwerk.MainActivity
import de.k7labor.fahrwerk.physics.Checks
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/** Rendert die Bildschirme in einer Android-Umgebung (Robolectric) und legt PNGs ab. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], qualifiers = "w411dp-h914dp-xxhdpi")
class ScreenshotTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    private fun shot(name: String) {
        rule.waitForIdle()
        val bmp = rule.onRoot().captureToImage().asAndroidBitmap()
        val dir = File(System.getProperty("screenshotDir") ?: "build/screenshots").apply { mkdirs() }
        File(dir, "$name.png").outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    /** Hauptthread-Uhr und echte Rechenzeit gemeinsam vorlaufen lassen. */
    private fun pump(times: Int, sleepMs: Long = 250) {
        repeat(times) {
            org.robolectric.shadows.ShadowLooper.idleMainLooper(300, java.util.concurrent.TimeUnit.MILLISECONDS)
            rule.mainClock.advanceTimeBy(300)
            Thread.sleep(sleepMs)
        }
        rule.waitForIdle()
    }

    private fun waitCompute() {
        // Der Setup-Index ist jetzt ein Canvas ohne Text: fertig, sobald die Beschreibung einen Wert trägt
        val ready = hasContentDescription("Setup-Index", substring = true) and !hasContentDescription("wird gerechnet", substring = true)
        for (i in 0 until 240) {
            pump(1)
            val done = runCatching { rule.onNode(ready).assertExists(); true }.getOrDefault(false)
            if (done) break
        }
        pump(4)
    }

    @Test fun allScreens() {
        rule.mainClock.autoAdvance = false
        waitCompute()
        shot("1-setup")
        rule.onNodeWithText("Technik").performClick(); pump(2); shot("1c-setup-technik")
        rule.onNodeWithText("Prüfstand").performClick()
        pump(6)
        shot("2-pruefstand-fahrt")
        rule.onNodeWithText("Technik").performClick(); pump(2); shot("2a-technik")
        // Chip liegt unterhalb des sichtbaren Bereichs in einer waagerecht scrollenden Zeile: Klick-Aktion direkt auslösen
        rule.onNodeWithText("Gabel vorn").performSemanticsAction(SemanticsActions.OnClick); pump(2); shot("2c-gabel")
        rule.onNodeWithText("Außen").performClick(); pump(2)
        rule.onNodeWithText("Bremsen").performClick(); pump(6); shot("2b-bremsen")
        rule.onNodeWithText("Dämpfer").performClick(); pump(2); shot("3-daempfer")
        rule.onNodeWithText("Feder").performClick(); pump(2); shot("4-feder")
        rule.onNodeWithText("Rütteln").performClick()
        pump(80, 400)
        shot("5-ruetteln")
        rule.onNodeWithText("Werkstatt").performClick(); pump(2); shot("6-durchhang")
        rule.onNodeWithText("Diagnose").performClick(); pump(2); shot("7a")
        rule.onNodeWithText("Gabel taucht beim Bremsen", substring = true).performClick(); pump(2); shot("7-diagnose")
        rule.onNodeWithText("Setups").performClick(); pump(2); shot("8-setups")
        rule.onNodeWithText("Daten").performClick(); pump(2); shot("9-daten")
        rule.onNodeWithText("Prüfungen").performClick()
        pump(60, 400)
        shot("10-pruefungen")
        println(Checks.run().joinToString("\n") { "${if (it.ok) "OK " else "XX "} ${it.name}: ${it.value}" })
    }
}
