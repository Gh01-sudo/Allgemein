package de.k7labor.fahrwerk.physics

import kotlin.math.abs
import kotlin.math.roundToLong

/** Gruppen der Einsteller in der Oberfläche. */
enum class Group(val title: String) {
    LOAD("Fahrer & Beladung"),
    FORK("Gabel vorn (KYB 43 mm USD)"),
    SHOCK("Federbein hinten"),
    TYRE("Reifen"),
    SPRING("Federn tauschen (Was-wäre-wenn)")
}

/** Ein einstellbarer Wert mit Grenzen, Raster und Serienwert. */
data class Control(
    val key: String,
    val group: Group,
    val label: String,
    val min: Double,
    val max: Double,
    val step: Double,
    val unit: String,
    val decimals: Int,
    val hint: String,
    val stock: Double
) {
    val isLoad get() = group == Group.LOAD

    /** Wert auf Grenzen begrenzen und auf das Raster runden (Review-Punkt 8). */
    fun sanitize(v: Double): Double {
        val c = v.coerceIn(min, max)
        val n = ((c - min) / step).roundToLong()
        val r = min + n * step
        // Rundungsrauschen entfernen (z. B. 2.4999999)
        val scale = Math.pow(10.0, (decimals + 2).toDouble())
        return (Math.round(r * scale) / scale).coerceIn(min, max)
    }
}

enum class Profile(
    val label: String,
    val sagF: Double,
    val sagR: Double,
    val sagWinF: Pair<Double, Double>,
    val sagWinR: Pair<Double, Double>,
    /** Dämpfungs-Versatz gegenüber Serie in Klicks bzw. Umdrehungen (+ = offener). */
    val offFReb: Double, val offFLS: Double, val offFHS: Double,
    val offRReb: Double, val offRLS: Double, val offRHS: Double,
    val pF: Double, val pR: Double,
    /** Gewichtung der Teilnoten: Komfort, Haftung, Kontrolle, Reserve. */
    val wComfort: Double, val wGrip: Double, val wControl: Double, val wReserve: Double,
    /** Reibwert Straße (Annahme). */
    val mu: Double
) {
    KOMFORT("Straße komfortabel", 38.0, 33.0, 35.0 to 40.0, 30.0 to 35.0, 1.0, 2.0, 0.5, 1.0, 2.0, 0.5, 2.5, 2.9, .35, .25, .20, .20, 1.05),
    SPORT("Landstraße sportlich", 34.0, 30.0, 32.0 to 36.0, 28.0 to 32.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 2.5, 2.9, .15, .35, .30, .20, 1.10),
    TRACK("Rennstrecke", 30.0, 26.0, 28.0 to 32.0, 23.0 to 28.0, -2.0, -3.0, -0.5, -2.0, -3.0, -0.5, 2.2, 2.0, .05, .40, .35, .20, 1.25),
    REGEN("Nässe", 38.0, 33.0, 35.0 to 40.0, 30.0 to 35.0, 2.0, 3.0, 1.0, 2.0, 3.0, 1.0, 2.5, 2.9, .20, .45, .20, .15, 0.70)
}

object Controls {
    const val CLICKS_MAX = 20.0
    const val HS_MAX = 4.0

    val all: List<Control> = listOf(
        Control("rider", Group.LOAD, "Fahrer inkl. Ausrüstung", 50.0, 140.0, 1.0, "kg", 0, "Mit Helm, Kombi, Stiefeln", 80.0),
        Control("passenger", Group.LOAD, "Sozius inkl. Ausrüstung", 0.0, 110.0, 1.0, "kg", 0, "0 = solo", 0.0),
        Control("luggage", Group.LOAD, "Gepäck am Heck", 0.0, 15.0, 1.0, "kg", 0, "Hecktasche", 0.0),
        Control("fuel", Group.LOAD, "Tankfüllung", 10.0, 100.0, 5.0, "%", 0, "Voll ≈ 13 kg Kraftstoff", 100.0),

        Control("fPre", Group.FORK, "Federvorspannung", 0.0, 8.0, 0.5, "Ringe", 1, "Sichtbare Ringe am Einsteller, weniger Ringe = mehr Vorspannung", 4.0),
        Control("fReb", Group.FORK, "Zugstufe", 0.0, CLICKS_MAX, 1.0, "Kl. offen", 0, "Oben, Klicks ab ganz zu", 6.0),
        Control("fLS", Group.FORK, "Druckstufe Low-Speed", 0.0, CLICKS_MAX, 1.0, "Kl. offen", 0, "Unten, Schlitzschraube", 14.0),
        Control("fHS", Group.FORK, "Druckstufe High-Speed", 0.0, HS_MAX, 0.25, "Umdr. offen", 2, "Unten, Sechskant", 3.0),
        Control("fProt", Group.FORK, "Gabel durchgesteckt", -5.0, 10.0, 0.5, "mm", 1, "+ = Front tiefer, schärferes Einlenken", 0.0),
        Control("fOil", Group.FORK, "Ölstand gegenüber Serie", -10.0, 15.0, 1.0, "mm", 0, "+ = weniger Luftpolster, mehr Endprogression", 0.0),

        Control("rPre", Group.SHOCK, "Federvorspannung", -5.0, 12.0, 0.5, "mm", 1, "Federweg-Verkürzung gegenüber Serie", 0.0),
        Control("rReb", Group.SHOCK, "Zugstufe", 0.0, CLICKS_MAX, 1.0, "Kl. offen", 0, "Unten am Federbein", 11.0),
        Control("rLS", Group.SHOCK, "Druckstufe Low-Speed", 0.0, CLICKS_MAX, 1.0, "Kl. offen", 0, "Ausgleichsbehälter, Schraube", 14.0),
        Control("rHS", Group.SHOCK, "Druckstufe High-Speed", 0.0, HS_MAX, 0.25, "Umdr. offen", 2, "Ausgleichsbehälter, Sechskant", 3.0),
        Control("rShim", Group.SHOCK, "Heckhöhe (Längenänderung Federbein)", -4.0, 8.0, 0.5, "mm", 1, "+ = Heck höher; 1 mm am Federbein ≈ 2 mm an der Achse", 0.0),

        Control("pF", Group.TYRE, "Luftdruck vorn (kalt)", 1.8, 3.0, 0.05, "bar", 2, "Suzuki: 2,50 bar kalt", 2.5),
        Control("pR", Group.TYRE, "Luftdruck hinten (kalt)", 1.8, 3.2, 0.05, "bar", 2, "Suzuki: 2,90 bar kalt", 2.9),

        Control("kF", Group.SPRING, "Gabelfeder je Holm", 8.0, 11.5, 0.01, "N/mm", 2, "Serie ≈ 9,57 N/mm (0,976 kg/mm)", 9.57),
        Control("kR", Group.SPRING, "Federbeinfeder", 85.0, 120.0, 1.0, "N/mm", 0, "Serie ≈ 99 N/mm (10,1 kg/mm)", 99.0)
    )

    val byKey: Map<String, Control> = all.associateBy { it.key }
    val keys: List<String> = all.map { it.key }
    fun get(key: String): Control = byKey.getValue(key)
}

/**
 * Komplettes Setup: Beladung, alle Einsteller und Einsatzprofil.
 * Werte liegen immer innerhalb der Grenzen und auf dem Raster.
 */
data class Setup(val values: Map<String, Double>, val profile: Profile = Profile.SPORT) {

    operator fun get(key: String): Double = values[key] ?: Controls.get(key).stock

    fun with(key: String, v: Double): Setup {
        val c = Controls.get(key)
        return copy(values = values + (key to c.sanitize(v)))
    }

    fun withAll(other: Map<String, Double>): Setup {
        var s = this
        for ((k, v) in other) if (Controls.byKey.containsKey(k) && v.isFinite()) s = s.with(k, v)
        return s
    }

    /** Einsteller auf Serie, Beladung und Profil bleiben (wie im Original gewünscht). */
    fun toStockKeepLoad(): Setup {
        val m = values.toMutableMap()
        for (c in Controls.all) if (!c.isLoad) m[c.key] = c.stock
        return copy(values = m)
    }

    fun isChanged(key: String): Boolean = abs(get(key) - Controls.get(key).stock) > 1e-9

    val rider get() = get("rider")
    val passenger get() = get("passenger")
    val luggage get() = get("luggage")
    val fuel get() = get("fuel")
    val fPre get() = get("fPre")
    val fReb get() = get("fReb")
    val fLS get() = get("fLS")
    val fHS get() = get("fHS")
    val fProt get() = get("fProt")
    val fOil get() = get("fOil")
    val rPre get() = get("rPre")
    val rReb get() = get("rReb")
    val rLS get() = get("rLS")
    val rHS get() = get("rHS")
    val rShim get() = get("rShim")
    val pF get() = get("pF")
    val pR get() = get("pR")
    val kF get() = get("kF")
    val kR get() = get("kR")

    companion object {
        val STOCK = Setup(Controls.all.associate { it.key to it.stock }, Profile.SPORT)
    }
}
