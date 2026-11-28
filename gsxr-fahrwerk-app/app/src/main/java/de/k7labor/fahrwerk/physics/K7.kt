package de.k7labor.fahrwerk.physics

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sin

const val G = 9.81
const val DEG = PI / 180.0

/**
 * Fahrzeugdaten der Suzuki GSX-R 1000 K7 (2007–2008) und alle Modellannahmen.
 * Einheiten: Längen in mm, wenn der Name auf "Mm" endet, sonst SI (m, kg, N, s).
 * Jede Annahme steht zusätzlich in [assumptions] und wird in der App angezeigt.
 */
object K7 {
    // ---- Geometrie (Werksangaben, siehe Datenblatt) ----
    const val RAKE_DEG = 23.75            // Lenkkopfwinkel
    const val TRAIL_MM = 98.0             // Nachlauf
    const val WHEELBASE_MM = 1415.0       // Radstand
    const val RF_MM = 299.9               // 120/70 ZR17: 215,9 + 0,70 · 120
    const val RR_MM = 310.9               // 190/50 ZR17: 215,9 + 0,50 · 190
    const val TRAVEL_F_MM = 125.0         // Federweg Gabel
    const val TRAVEL_R_MM = 130.0         // Radweg hinten (Quellen 130 / 135 mm, unsicher)
    const val SEAT_H_MM = 810.0           // Sitzhöhe ohne Fahrer

    // ---- Annahmen Geometrie ----
    const val SWINGARM_MM = 575.0         // Schwingenlänge (Annahme)
    const val SWINGARM_DEG = 10.0         // Schwingenneigung in der Bezugslage (Annahme)
    const val REF_SAG_F_MM = 23.0         // Bezugslage = Werksgeometrie, Gabel eingefedert ohne Fahrer (Annahme)
    const val REF_SAG_R_MM = 12.0         // dito hinten (Annahme)
    val SEAT_PT_MM = doubleArrayOf(490.0, 810.0)   // Sitzpunkt (x ab Hinterachse, Höhe) in Bezugslage

    // ---- Massen ----
    const val BIKE_WET = 212.0            // vollgetankt gewogen (MOTORRAD)
    const val FRONT_SHARE = 0.51          // Gewichtsanteil vorn ohne Fahrer (Annahme)
    const val H_BIKE = 0.55               // Schwerpunkthöhe Maschine (Annahme)
    const val FUEL_FULL = 17.5 * 0.745    // 17,5 l Benzin ≈ 13,0 kg
    val TANK_POS = doubleArrayOf(0.78, 0.82)   // Tank-Schwerpunkt x ab Hinterachse, Höhe (Annahme)
    const val M_UNSPRUNG_F = 17.0         // Rad, Reifen, Bremsen, Gleitrohre (Annahme)
    const val M_WHEEL_R = 15.0            // Hinterrad mit Reifen, Scheibe, Kettenrad (Annahme)
    const val M_SWINGARM = 9.0            // Schwinge mit Sattel (Annahme)
    const val K_GYR_BIKE = 0.40           // Trägheitsradius Nicken, gefederte Maschine (Annahme)
    const val RIDER_X_SHARE = 0.40        // Fahrerschwerpunkt: 40 % des Radstands vor der Hinterachse (Annahme)
    const val RIDER_H = 0.92
    const val K_GYR_RIDER = 0.25
    const val PASS_X_SHARE = 0.05
    const val PASS_H = 1.05
    const val LUGGAGE_X = 0.0
    const val LUGGAGE_H = 0.95

    // ---- Federn ----
    const val K_F_STOCK = 9.57            // N/mm je Holm
    const val K_R_STOCK = 99.0            // N/mm
    const val RING_MM = 1.0               // 1 Ring Vorspannung ≈ 1 mm (Annahme)
    const val RINGS_STOCK = 4.0
    const val LR0 = 2.25                  // Hebelübersetzung Rad:Feder bei ausgefedert (Annahme)
    const val LR_PROG = 0.20              // Progression: am Ende 2,25/(1+0,2) ≈ 1,88 (Annahme)
    const val REF_RIDER = 80.0
    const val REF_SAG_RIDER_F = 36.0      // Kalibrierziel Serie mit 80 kg (Forenangaben 35–40 mm)
    const val REF_SAG_RIDER_R = 32.0
    // Luftpolster Gabel (je Holm)
    const val AIR_PISTON = 14.5e-4        // m² wirksame Fläche
    const val AIR_V0 = 260e-6             // m³ Luftvolumen bei Serien-Ölstand (Annahme)
    const val OIL_AREA = 16.6e-4          // m² Querschnitt für Ölstand (Annahme)

    // ---- Dämpfer (Annahmen) ----
    const val ZETA_F_REB = 0.55
    const val ZETA_F_COMP = 0.28
    const val ZETA_R_REB = 0.60
    const val ZETA_R_COMP = 0.30
    const val CLICK_ALPHA = 0.075         // exp(-0,075): 1 Klick auf = −7,2 %, 1 Klick zu = +7,8 %
    const val HS_BETA = 0.30              // je HS-Umdrehung −26 % bzw. +35 % High-Speed-Steigung
    const val HS_RATIO = 0.45             // High-Speed-Steigung / Low-Speed-Steigung in Serie
    const val REB_HS_RATIO = 0.55
    const val VK_F = 0.12                 // Kniegeschwindigkeit Druck vorn (m/s, axial)
    const val VK_R = 0.18                 // Kniegeschwindigkeit Druck hinten (m/s, am Rad)
    const val VK_REB = 0.25

    // ---- Anschläge ----
    const val TOPOUT_K = 300.0            // N/mm Ausfederfeder (gedämpft, Review-Punkt 4)
    const val TOPOUT_C = 2500.0           // Ns/m
    const val BUMP_ZONE_MM = 10.0
    const val BUMP_PROG = 30.0            // N/mm² Ölpolster/Gummi
    const val HARD_STOP = 5000.0          // N/mm

    // ---- Reifen (Annahmen) ----
    const val KT_F = 175.0                // N/mm bei 2,50 bar
    const val KT_R = 210.0                // N/mm bei 2,90 bar
    const val KT_EXP = 0.9
    const val CT_F = 1200.0               // Ns/m Reifendämpfung (Review-Punkt 4: vorher 250)
    const val CT_R = 1600.0
    const val PAC_B = 16.0                // Magic Formula Längskraft
    const val PAC_C = 1.4
    const val PAC_E = 0.1
    const val MU_LOAD_SENS = 0.08         // Reibwertabfall je Lastverdopplung
    const val FZ_NOM = 1500.0
    const val RELAX_LEN = 0.15            // m Einlauflänge
    const val ROLL_RES = 0.015
    const val I_WHEEL_F = 0.65            // kg m²
    const val I_WHEEL_R = 1.0
    const val I_ENGINE = 0.015            // kg m² Kurbeltrieb/Kupplung auf Kurbelwelle (Annahme)

    // ---- Antrieb ----
    val GEARS = doubleArrayOf(2.562, 2.052, 1.714, 1.500, 1.360, 1.269)   // moto-data / motorcyclespecs
    const val PRIMARY = 1.553             // Primärübersetzung (Annahme, nicht doppelt geprüft)
    const val SPROCKET_F = 17             // Ritzel (Annahme)
    const val SPROCKET_R = 43             // Kettenrad (Annahme)
    const val CHAIN_PITCH = 0.015875      // 530er Kette
    const val DRIVE_EFF = 0.92
    const val RPM_MAX = 13500.0
    const val RPM_SHIFT = 12500.0
    val COUNTERSHAFT_REL = doubleArrayOf(0.075, 0.005)   // m vor/über dem Schwingendrehpunkt (Annahme)

    // ---- Luft ----
    const val CDA = 0.35                  // m² (Annahme, halb geduckt)
    const val RHO = 1.2
    val AERO_CP = doubleArrayOf(0.70, 0.85)

    // ---- abgeleitet ----
    val rake = RAKE_DEG * DEG
    val offsetMm = RF_MM * sin(rake) - TRAIL_MM * cos(rake)
    val finalRatio = SPROCKET_R.toDouble() / SPROCKET_F
    fun sprocketRadius(teeth: Int) = CHAIN_PITCH / (2 * sin(PI / teeth))

    /** Motordrehmoment an der Kurbelwelle (vereinfachte Volllastkurve, Annahme). */
    fun engineTorque(rpm: Double): Double {
        if (rpm > RPM_MAX) return 0.0
        val r = max(rpm, 3000.0)
        val t = 117.0 * (1 - ((r - 10000.0) / 8200.0).pow(2))
        return t.coerceAtLeast(55.0)
    }

    fun overallRatio(gear: Int) = PRIMARY * GEARS[gear] * finalRatio

    /** Größte Antriebskraft am Hinterrad bei Geschwindigkeit v (m/s), bester Gang. */
    fun maxDrive(v: Double, rollRadius: Double): Pair<Double, Int> {
        var best = 0.0
        var bestG = 0
        for (g in GEARS.indices) {
            val rpm = max(v, 0.5) / rollRadius * overallRatio(g) * 60 / (2 * PI)
            if (rpm > RPM_SHIFT && g < GEARS.lastIndex) continue
            val f = engineTorque(rpm) * overallRatio(g) * DRIVE_EFF / rollRadius
            if (f > best) { best = f; bestG = g }
        }
        return best to bestG
    }

    data class Assumption(val name: String, val value: String, val why: String)

    val assumptions = listOf(
        Assumption("Masse vollgetankt", "212 kg, 51 % vorn", "MOTORRAD-Messung; Verteilung geschätzt"),
        Assumption("Ungefederte Massen", "17 kg vorn · 15 kg Hinterrad + 9 kg Schwinge", "Rad, Reifen, Bremse, Gleitrohre bzw. Schwinge"),
        Assumption("Fahrer", "Schwerpunkt bei 40 % Radstand vor der Hinterachse, 0,92 m hoch", "Sportliche Sitzposition, Fahrer starr mit der Maschine verbunden"),
        Assumption("Nickträgheit", "Trägheitsradius 0,40 m (Maschine), 0,25 m (Fahrer)", "Typische Werte für Supersportler"),
        Assumption("Bezugslage", "Gabel 23 mm, Heck 12 mm eingefedert ohne Fahrer", "In dieser Lage gilt die Werksgeometrie 23,75° / 98 mm / 1415 mm"),
        Assumption("Hebelübersetzung hinten", "2,25 : 1 ausgefedert bis 1,88 : 1 eingefedert", "Progressive Umlenkung, typisch für diese Klasse"),
        Assumption("Kalibrierung Vorspannung", "80 kg Fahrer ergeben in Serie 36 mm vorn / 32 mm hinten", "Forenangaben zum Serien-Durchhang der K7"),
        Assumption("Vorspannring Gabel", "1 Ring ≈ 1 mm", "Ringabstand am Einsteller mit Messschieber prüfen"),
        Assumption("Klickbereich", "0–20 Klicks, High-Speed 0–4 Umdrehungen", "Gesamtzahl je Einsteller an deiner Maschine zählen"),
        Assumption("Klickwirkung", "1 Klick auf −7,2 %, 1 Klick zu +7,8 % Low-Speed-Kraft; HS-Umdrehung −26 % / +35 % HS-Steigung", "Relativ zur Serie. Nahe „ganz zu“ wirken Klicks in Wirklichkeit oft stärker"),
        Assumption("Dämpfer-Kennlinie", "Low-Speed linear bis Knie, darüber High-Speed-Steigung; LS verschiebt das Knie, HS ändert die Steigung", "Vereinfachtes Ventilmodell; echte Kennlinie nur auf dem Dämpferprüfstand messbar"),
        Assumption("Dämpfungsmaß Serie", "Zug vorn ζ 0,55 · Druck vorn ζ 0,28 · Zug hinten ζ 0,60 · Druck hinten ζ 0,30", "Übliche Werte für Straßen-Supersportler"),
        Assumption("Luftpolster Gabel", "260 cm³ je Holm, 14,5 cm², Exponent 1,4", "Ölstand +1 mm ≈ −1,7 cm³ Luft"),
        Assumption("Ausfederanschlag", "300 N/mm mit 2500 Ns/m Dämpfung", "Gedämpft, damit das Rad beim Ausfedern nicht ungedämpft schwingt"),
        Assumption("Reifen senkrecht", "175 N/mm vorn bei 2,5 bar · 210 N/mm hinten bei 2,9 bar, Dämpfung 1200 / 1600 Ns/m", "Steifigkeit skaliert mit Luftdruck^0,9"),
        Assumption("Reifen längs", "Magic Formula B 16 · C 1,4 · E 0,1: Haftmaximum bei rund 13 % Schlupf, rutschend etwa 80 % davon, Einlauflänge 0,15 m", "Reibwert je Profil: trocken 1,05–1,25, nass 0,70"),
        Assumption("Antrieb", "Primär 1,553 · Ritzel 17 / Kettenrad 43 · 530er Kette · 92 % Wirkungsgrad", "Getriebestufen aus Datenblatt, Primär und Kettenräder nicht doppelt geprüft"),
        Assumption("Motor", "Volllastkurve mit 117 Nm bei 10.000 /min, Begrenzer 13.500 /min", "Vereinfachte Kurve, keine Elektronik"),
        Assumption("Ritzelposition", "75 mm vor und 5 mm über dem Schwingendrehpunkt", "Bestimmt den Kettenzug-Effekt (Anti-Squat)"),
        Assumption("Luftwiderstand", "cw·A 0,35 m², Druckpunkt 0,85 m hoch", "Halb geduckt; Auftrieb vernachlässigt"),
        Assumption("Fahrer-Regler", "Bremse 85 % vorn / 15 % hinten, kein ABS, keine Traktionskontrolle", "Die K7 hat weder ABS noch Traktionskontrolle"),
        Assumption("Kurve", "Ebenes Modell in der Fahrzeugebene mit g/cos(Schräglage)", "Seitenkräfte und Lenkung werden nicht simuliert")
    )
}

/** Luftfederkraft der Gabel (beide Holme) in N bei axialem Weg s (mm). */
fun forkAirForce(sMm: Double, oilMm: Double): Double {
    val v0 = K7.AIR_V0 - oilMm * 1e-3 * K7.OIL_AREA
    val v = max(v0 - K7.AIR_PISTON * max(0.0, sMm) * 1e-3, v0 * 0.08)
    return 2 * K7.AIR_PISTON * 101325.0 * ((v0 / v).pow(1.4) - 1)
}

/** Federweg des Federbeins (mm) bei Radweg x (mm), progressive Umlenkung. */
fun shockStroke(xMm: Double): Double =
    (xMm + K7.LR_PROG * xMm * xMm / (2 * K7.TRAVEL_R_MM)) / K7.LR0

/** Ableitung dy/dx = 1 / Hebelübersetzung. */
fun shockRatio(xMm: Double): Double =
    (1 + K7.LR_PROG * xMm.coerceIn(0.0, K7.TRAVEL_R_MM + 10) / K7.TRAVEL_R_MM) / K7.LR0

/** Reifensteifigkeit in N/m für einen Luftdruck. */
fun tyreStiffness(front: Boolean, bar: Double): Double =
    if (front) K7.KT_F * (bar / 2.5).pow(K7.KT_EXP) * 1000 else K7.KT_R * (bar / 2.9).pow(K7.KT_EXP) * 1000
