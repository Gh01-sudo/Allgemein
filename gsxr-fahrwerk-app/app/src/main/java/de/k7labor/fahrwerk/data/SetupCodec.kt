package de.k7labor.fahrwerk.data

import de.k7labor.fahrwerk.physics.Controls
import de.k7labor.fahrwerk.physics.Profile
import de.k7labor.fahrwerk.physics.Setup
import de.k7labor.fahrwerk.physics.fmt
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.math.abs

data class SavedSetup(val id: String, val name: String, val setup: Setup, val created: Long)

data class ImportResult(val setup: Setup?, val name: String?, val messages: List<String>)

/**
 * Lesen und Schreiben von Setups als JSON. Jeder Wert wird geprüft:
 * keine Zahl → verworfen (Review-Punkt 2), außerhalb der Grenzen → begrenzt, nicht auf dem Raster → gerundet (Punkt 8).
 */
object SetupCodec {
    const val FORMAT = "k7-fahrwerkslabor"

    fun toJson(s: Setup, name: String? = null): JSONObject {
        val o = JSONObject()
        o.put("app", FORMAT)
        o.put("version", 1)
        if (name != null) o.put("name", name)
        o.put("profile", s.profile.name)
        val v = JSONObject()
        for (c in Controls.all) v.put(c.key, s[c.key])
        o.put("setup", v)
        return o
    }

    fun export(s: Setup, name: String? = null): String = toJson(s, name).toString(2)

    fun parse(text: String, base: Setup): ImportResult {
        val msgs = mutableListOf<String>()
        val root = try {
            JSONObject(text.trim())
        } catch (e: JSONException) {
            return ImportResult(null, null, listOf("Kein gültiges Setup: Der Text ist kein JSON."))
        }
        val obj = root.optJSONObject("setup") ?: root
        var out = base
        var found = 0
        for (c in Controls.all) {
            if (!obj.has(c.key)) continue
            val raw = obj.opt(c.key)
            val num = when (raw) {
                is Number -> raw.toDouble()
                is String -> raw.replace(',', '.').trim().toDoubleOrNull()
                else -> null
            }
            if (num == null || !num.isFinite()) {
                msgs += "„${c.label}“ ignoriert: „$raw“ ist keine Zahl."
                continue
            }
            val clean = c.sanitize(num)
            if (abs(clean - num) > 1e-9) {
                msgs += if (num < c.min || num > c.max) "„${c.label}“ auf ${fmt(clean, c.decimals)} ${c.unit} begrenzt (erlaubt ${fmt(c.min, c.decimals)} bis ${fmt(c.max, c.decimals)})."
                else "„${c.label}“ auf ${fmt(clean, c.decimals)} ${c.unit} gerundet (Raster ${fmt(c.step, 2)})."
            }
            out = out.with(c.key, clean)
            found++
        }
        val profRaw = (root.opt("profile") ?: obj.opt("profile"))?.toString()
        if (profRaw != null) {
            val p = profileFrom(profRaw)
            if (p != null) out = out.copy(profile = p) else msgs += "Unbekanntes Profil „$profRaw“ ignoriert."
        }
        if (found == 0) return ImportResult(null, null, msgs + "Keine gültigen Einstellwerte gefunden.")
        val name = root.optString("name", "").takeIf { it.isNotBlank() }?.take(60)
        return ImportResult(out, name, msgs)
    }

    /** Auch die Profilnamen des alten Web-Dashboards (komfort, sport, track, regen) werden erkannt. */
    fun profileFrom(s: String): Profile? = Profile.entries.firstOrNull { it.name.equals(s.trim(), ignoreCase = true) }

    fun libraryToJson(lib: List<SavedSetup>): String {
        val arr = JSONArray()
        for (e in lib) {
            val o = toJson(e.setup, e.name)
            o.put("id", e.id)
            o.put("created", e.created)
            arr.put(o)
        }
        return arr.toString()
    }

    /** Beschädigte Einträge werden übersprungen statt die ganze Bibliothek zu verlieren (Review-Punkt 11). */
    fun libraryFromJson(text: String?): List<SavedSetup> {
        if (text.isNullOrBlank()) return emptyList()
        val arr = try { JSONArray(text) } catch (e: JSONException) { return emptyList() }
        val out = mutableListOf<SavedSetup>()
        for (i in 0 until arr.length()) {
            val o = arr.optJSONObject(i) ?: continue
            val r = parse(o.toString(), Setup.STOCK)
            val s = r.setup ?: continue
            val id = o.optString("id", "").ifBlank { "legacy-$i" }
            out += SavedSetup(id, r.name ?: "Setup ${i + 1}", s, o.optLong("created", 0L))
        }
        return out
    }
}
