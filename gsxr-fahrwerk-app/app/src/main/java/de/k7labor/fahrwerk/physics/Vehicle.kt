package de.k7labor.fahrwerk.physics

import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

/** Grundvorspannung aus der Kalibrierung: Gabel je Feder (mm), Federbein (mm). */
data class Calibration(val p0F: Double, val p0R: Double)

/**
 * Dämpferkennlinie. v > 0 = Einfedern (Druckstufe). Rückgabe: Kraft, die der Bewegung entgegenwirkt
 * (positiv beim Einfedern). Low-Speed linear bis zur Kniegeschwindigkeit, darüber High-Speed-Steigung.
 */
class Damper(
    val cLs: Double, val vk: Double, val cHs: Double,
    val cReb: Double, val vkReb: Double, val cRebHs: Double
) {
    fun force(v: Double): Double =
        if (v >= 0) {
            if (v < vk) cLs * v else cLs * vk + cHs * (v - vk)
        } else {
            val a = -v
            -(if (a < vkReb) cReb * a else cReb * vkReb + cRebHs * (a - vkReb))
        }
}

/**
 * Masse, Schwerpunkt und Bauteil-Lagen eines konkreten Setups.
 * Körperfestes Koordinatensystem: Ursprung im Schwingendrehpunkt, x nach vorn, z nach oben,
 * in der Bezugslage (Werksgeometrie) achsparallel zum Boden.
 */
class Vehicle(val st: Setup, val cal: Calibration) {
    val wb = K7.WHEELBASE_MM / 1000
    val rf = K7.RF_MM / 1000
    val rr = K7.RR_MM / 1000
    val ls = K7.SWINGARM_MM / 1000
    val phiRef = K7.SWINGARM_DEG * DEG
    val eps = K7.rake
    val u0x = -sin(eps)
    val u0z = cos(eps)

    /** Schwingendrehpunkt in Weltkoordinaten der Bezugslage. */
    val pivotX = ls * cos(phiRef)
    val pivotZ = rr + ls * sin(phiRef)

    /** Vorderachse körperfest bei Gabelweg s = Bezugslage, inkl. Durchstecken. */
    val afX: Double
    val afZ: Double

    // Massen
    val mUf = K7.M_UNSPRUNG_F
    val mWr = K7.M_WHEEL_R
    val mSa = K7.M_SWINGARM
    val mS: Double        // gefederte Masse inkl. Fahrer, Sozius, Gepäck, Kraftstoff
    val cgX: Double       // Schwerpunkt der gefederten Masse, körperfest
    val cgZ: Double
    val iS: Double        // Nickträgheit um den eigenen Schwerpunkt
    val iSa = K7.M_SWINGARM * ls * ls / 12
    val mTotal: Double

    // Weitere körperfeste Punkte
    val seatX = K7.SEAT_PT_MM[0] / 1000 - pivotX
    val seatZ = K7.SEAT_PT_MM[1] / 1000 - pivotZ
    val csX = K7.COUNTERSHAFT_REL[0]
    val csZ = K7.COUNTERSHAFT_REL[1]
    val aeroX = K7.AERO_CP[0] - pivotX
    val aeroZ = K7.AERO_CP[1] - pivotZ
    val rSprF = K7.sprocketRadius(K7.SPROCKET_F)
    val rSprR = K7.sprocketRadius(K7.SPROCKET_R)

    // Federn, Dämpfer, Reifen
    val preF = cal.p0F + (K7.RINGS_STOCK - st.fPre) * K7.RING_MM   // mm je Feder
    val preR = cal.p0R + st.rPre                                  // mm Federbein
    val ktF = tyreStiffness(true, st.pF)
    val ktR = tyreStiffness(false, st.pR)
    val damperF: Damper
    val damperR: Damper   // auf die Kolbenstange bezogen

    init {
        val prot = st.fProt / 1000
        afX = (wb - pivotX) + u0x * prot
        afZ = (rf - pivotZ) + u0z * prot

        // Schwerpunkt der gefederten Maschine (vollgetankt) aus Gesamtschwerpunkt minus ungefederte Teile
        val saWx = pivotX / 2
        val saWz = (pivotZ + rr) / 2
        val mSprBike = K7.BIKE_WET - mUf - mWr - mSa
        var sx = (K7.BIKE_WET * K7.FRONT_SHARE * wb - mUf * wb - mWr * 0.0 - mSa * saWx) / mSprBike
        var sz = (K7.BIKE_WET * K7.H_BIKE - mUf * rf - mWr * rr - mSa * saWz) / mSprBike
        val fuelMiss = (1 - st.fuel / 100) * K7.FUEL_FULL

        data class Part(val m: Double, val x: Double, val z: Double, val k: Double)
        val parts = mutableListOf<Part>()
        parts += Part(mSprBike, sx, sz, K7.K_GYR_BIKE)
        if (fuelMiss > 0) parts += Part(-fuelMiss, K7.TANK_POS[0], K7.TANK_POS[1], 0.0)
        if (st.rider > 0) parts += Part(st.rider, K7.RIDER_X_SHARE * wb, K7.RIDER_H, K7.K_GYR_RIDER)
        if (st.passenger > 0) parts += Part(st.passenger, K7.PASS_X_SHARE * wb, K7.PASS_H, K7.K_GYR_RIDER)
        if (st.luggage > 0) parts += Part(st.luggage, K7.LUGGAGE_X, K7.LUGGAGE_H, 0.1)
        val m = parts.sumOf { it.m }
        sx = parts.sumOf { it.m * it.x } / m
        sz = parts.sumOf { it.m * it.z } / m
        var inertia = 0.0
        for (p in parts) {
            val d2 = (p.x - sx) * (p.x - sx) + (p.z - sz) * (p.z - sz)
            inertia += p.m * (p.k * p.k + d2)
        }
        mS = m
        cgX = sx - pivotX
        cgZ = sz - pivotZ
        iS = inertia
        mTotal = mS + mUf + mWr + mSa

        // Dämpfer: Dämpfungsmaße beziehen sich auf eine feste Referenz (Serie, 80 kg),
        // denn der Dämpfer weiß nichts vom Fahrergewicht.
        val ref = REF_LOADS
        val cos2 = cos(eps) * cos(eps)
        val kvF = 2 * K7.K_F_STOCK * 1000 / cos2
        val ccF = 2 * sqrt(kvF * ref.first) * cos2              // axial
        val ratioRef = shockRatio(K7.REF_SAG_RIDER_R)
        val kwR = K7.K_R_STOCK * 1000 * ratioRef * ratioRef
        val ccR = 2 * sqrt(kwR * ref.second) / (ratioRef * ratioRef) // an der Kolbenstange
        val a = K7.CLICK_ALPHA
        val b = K7.HS_BETA
        val stock = Setup.STOCK
        damperF = Damper(
            cLs = K7.ZETA_F_COMP * ccF * exp(-a * (st.fLS - stock.fLS)),
            vk = K7.VK_F,
            cHs = K7.HS_RATIO * K7.ZETA_F_COMP * ccF * exp(-b * (st.fHS - stock.fHS)),
            cReb = K7.ZETA_F_REB * ccF * exp(-a * (st.fReb - stock.fReb)),
            vkReb = K7.VK_REB,
            cRebHs = K7.REB_HS_RATIO * K7.ZETA_F_REB * ccF * exp(-0.3 * a * (st.fReb - stock.fReb))
        )
        damperR = Damper(
            cLs = K7.ZETA_R_COMP * ccR * exp(-a * (st.rLS - stock.rLS)),
            vk = K7.VK_R * ratioRef,
            cHs = K7.HS_RATIO * K7.ZETA_R_COMP * ccR * exp(-b * (st.rHS - stock.rHS)),
            cReb = K7.ZETA_R_REB * ccR * exp(-a * (st.rReb - stock.rReb)),
            vkReb = K7.VK_REB * ratioRef,
            cRebHs = K7.REB_HS_RATIO * K7.ZETA_R_REB * ccR * exp(-0.3 * a * (st.rReb - stock.rReb))
        )
    }

    /** Axiale Gabelkraft beider Holme (N) bei Einfederung s (mm) und Geschwindigkeit sDot (m/s). */
    fun forkSpring(sMm: Double): Double {
        val k = 2 * st.kF
        if (sMm < 0) return k * preF + K7.TOPOUT_K * sMm
        var f = k * (preF + sMm) + forkAirForce(sMm, st.fOil)
        val b = K7.TRAVEL_F_MM - K7.BUMP_ZONE_MM
        if (sMm > b) f += K7.BUMP_PROG * (sMm - b) * (sMm - b)
        if (sMm > K7.TRAVEL_F_MM) f += K7.HARD_STOP * (sMm - K7.TRAVEL_F_MM)
        return f
    }

    fun forkForce(sMm: Double, sDot: Double): Double {
        var f = forkSpring(sMm) + damperF.force(sDot)
        if (sMm < 0) f += K7.TOPOUT_C * sDot
        return f
    }

    /** Federkraft hinten, auf das Rad bezogen (N) bei innerem Radweg x (mm). */
    fun rearSpring(xMm: Double): Double {
        if (xMm < 0) return st.kR * preR / K7.LR0 + K7.TOPOUT_K * xMm
        var f = st.kR * (preR + shockStroke(xMm)) * shockRatio(xMm)
        val b = K7.TRAVEL_R_MM - K7.BUMP_ZONE_MM
        if (xMm > b) f += K7.BUMP_PROG * (xMm - b) * (xMm - b)
        if (xMm > K7.TRAVEL_R_MM) f += K7.HARD_STOP * (xMm - K7.TRAVEL_R_MM)
        return f
    }

    fun rearForce(xMm: Double, xDot: Double): Double {
        val r = shockRatio(xMm)
        var f = rearSpring(xMm) + damperR.force(xDot * r) * r
        if (xMm < 0) f += K7.TOPOUT_C * xDot
        return f
    }

    /** Geometrischer Radweg hinten (mm) aus Schwingenwinkel phi. */
    fun rearGeo(phi: Double) = K7.REF_SAG_R_MM + ls * (sin(phiRef) - sin(phi)) * 1000

    /** Innerer Radweg des Federbeins (mm): ein längeres Federbein (rShim > 0) federt weniger ein. */
    fun rearInternal(phi: Double) = rearGeo(phi) - st.rShim * K7.LR0

    companion object {
        /** Gefederte Achslasten (kg) der Referenz Serie 80 kg, nur für die Dämpferauslegung. */
        val REF_LOADS: Pair<Double, Double> by lazy {
            val wb = K7.WHEELBASE_MM / 1000
            val m = K7.BIKE_WET + K7.REF_RIDER
            val x = (K7.BIKE_WET * K7.FRONT_SHARE * wb + K7.REF_RIDER * K7.RIDER_X_SHARE * wb) / m
            val front = m * x / wb - K7.M_UNSPRUNG_F
            val rear = m * (1 - x / wb) - K7.M_WHEEL_R - K7.M_SWINGARM / 2
            max(front, 50.0) to max(rear, 50.0)
        }
    }
}
