package de.k7labor.fahrwerk.data

import de.k7labor.fahrwerk.physics.K7
import de.k7labor.fahrwerk.physics.Profile
import de.k7labor.fahrwerk.physics.fmt
import kotlin.math.abs

/** Auswertung der Durchhang-Messung mit Plausibilitätsprüfung (Review-Punkt 10). */
data class SagResult(
    val valid: Boolean, val error: String?, val free: Double, val rider: Double, val delta: Double,
    val target: Double, val window: Pair<Double, Double>, val freeWindow: Pair<Double, Double>,
    val action: String, val springNote: String?, val newValue: Double?
)

object SagAdvice {
    fun evaluate(front: Boolean, m1: Double, m2: Double, m3: Double, profile: Profile, currentPre: Double): SagResult {
        val target = if (front) profile.sagF else profile.sagR
        val win = if (front) profile.sagWinF else profile.sagWinR
        val fw = if (front) 20.0 to 30.0 else 5.0 to 15.0
        val err = when {
            !(m1.isFinite() && m2.isFinite() && m3.isFinite()) -> "Bitte alle drei Werte eintragen."
            m1 <= 0 || m2 <= 0 || m3 <= 0 -> "Messwerte müssen größer als 0 sein."
            m2 > m1 -> "Wert 2 (ohne Fahrer) ist größer als Wert 1 (ausgefedert). Messpunkte vertauscht?"
            m3 > m2 -> "Wert 3 (mit Fahrer) ist größer als Wert 2 (ohne Fahrer). Messpunkte vertauscht?"
            m1 - m3 > (if (front) K7.TRAVEL_F_MM else K7.TRAVEL_R_MM) -> "Der Durchhang wäre größer als der ganze Federweg. Bitte Messung prüfen."
            else -> null
        }
        if (err != null) return SagResult(false, err, 0.0, 0.0, 0.0, target, win, fw, "", null, null)
        val free = m1 - m2
        val rider = m1 - m3
        val delta = rider - target
        val action: String
        var newVal: Double? = null
        if (abs(delta) < 1) {
            action = "Passt. Vorspannung so lassen."
        } else if (front) {
            // mehr Durchhang als Ziel → mehr Vorspannung → weniger Ringe sichtbar
            val wanted = currentPre - delta / K7.RING_MM
            val capped = (Math.round(wanted * 2) / 2.0).coerceIn(0.0, 8.0)
            newVal = capped
            val turn = abs(capped - currentPre)
            action = if (turn < 0.25) "Der Einsteller ist bereits am Ende (${fmt(currentPre, 1)} Ringe). Vorspannung allein reicht nicht, Federn prüfen."
            else "${if (delta > 0) "Mehr" else "Weniger"} Vorspannung: Einsteller um ${fmt(turn, 1)} Ringe ${if (delta > 0) "hineindrehen" else "herausdrehen"}, von ${fmt(currentPre, 1)} auf ${fmt(capped, 1)} Ringe sichtbar. Beide Holme gleich." +
                (if (abs(wanted - capped) > 0.3) " Für das volle Ziel wären ${fmt(wanted, 1)} Ringe nötig, das liegt außerhalb des Einstellbereichs." else "")
        } else {
            val mm = delta / K7.LR0
            val wanted = currentPre + mm
            val capped = (Math.round(wanted * 2) / 2.0).coerceIn(-5.0, 12.0)
            newVal = capped
            action = "${if (delta > 0) "Mehr" else "Weniger"} Vorspannung: Feder um ${fmt(abs(capped - currentPre), 1)} mm ${if (delta > 0) "zusätzlich spannen" else "entspannen"} (1 mm an der Feder ≈ ${fmt(K7.LR0, 2)} mm am Rad)." +
                (if (abs(wanted - capped) > 0.3) " Für das volle Ziel wären ${fmt(wanted - currentPre, 1)} mm nötig, außerhalb des Einstellbereichs." else "")
        }
        var note: String? = null
        if (free < fw.first - 3 && rider > win.first) note = "Wenig freier Durchhang bei viel Fahrer-Durchhang: Die Feder ist für dein Gewicht zu weich. Vorspannung allein löst das nicht sauber."
        if (free > fw.second + 5 && rider <= win.second) note = "Viel freier Durchhang bei passendem Fahrer-Durchhang: Die Feder ist für dein Gewicht eher zu hart."
        return SagResult(true, null, free, rider, delta, target, win, fw, action, note, newVal)
    }
}
