package de.k7labor.fahrwerk.data

import de.k7labor.fahrwerk.physics.K7
import de.k7labor.fahrwerk.physics.ScenarioId
import de.k7labor.fahrwerk.physics.fmt

/** Prüfstatus einer Angabe. */
enum class Status(val label: String, val meaning: String) {
    OK2("2 Quellen", "belastbar"),
    OK1("1 Quelle", "plausibel"),
    CONF("unsicher", "Quellen widersprechen sich"),
    OPEN("offen", "am Fahrzeug prüfen"),
    MODEL("Modell", "berechnet oder angenommen")
}

data class Source(val title: String, val url: String)

data class DataRow(val cat: String, val label: String, val value: String, val status: Status, val sources: List<String>, val note: String) {
    /** Suche nur in sichtbaren Texten, nicht in Quellenkürzeln (Review-Punkt 13). */
    fun matches(q: String): Boolean {
        if (q.isBlank()) return true
        val n = q.trim().lowercase()
        val srcTitles = sources.mapNotNull { Content.SOURCES[it]?.title }
        return listOf(cat, label, value, note, status.label).plus(srcTitles).any { it.lowercase().contains(n) }
    }
}

object Content {
    val SOURCES = mapOf(
        "CW" to Source("Cycle World, First Ride 2007", "https://issues.cycleworld.com/article/2007/5/1/2007-suzuki-4gsx-r1000"),
        "MD" to Source("moto-data.net GSX-R 1000 (2007)", "https://www.moto-data.net/suzuki/gsx-r-1000-2007"),
        "TK" to Source("Teknik Motorsport, Serien-Federraten 2007", "https://www.teknikmotorsport.com/bike-suspension-parts/suzuki/gsx-r1000/2007/?pgnum=3"),
        "GX" to Source("gixxer.com: Suspension Setting K7-1000", "https://www.gixxer.com/threads/suspension-setting-k7-1000.200643/"),
        "GX2" to Source("gixxer.com: 2007 GSXR1000 Suspension Settings", "https://www.gixxer.com/threads/2007-gsxr1000-suspension-settings.170551/"),
        "DE" to Source("gsx-r1000.de: Fahrwerksabstimmung K7 org. Dämpfer", "https://www.gsx-r1000.de/themen/fahrwerksabstimmung-k7-org-d%C3%A4mpfer.7783/"),
        "SZ" to Source("Suzuki GSX-R1000 Fahrerhandbuch (Folgegeneration)", "https://suzukimotorcycles.com.au/wp-content/uploads/2024/07/GSX-R1000-Owners-Manual-99011-17K57-01A.pdf"),
        "AM" to Source("AMCN (998,6 cm³)", "https://amcn.com.au/?p=23468"),
        "KT" to Source("K-Tech Federn K7/K8 (M-Sliders)", "https://msliders.com/products/rear-shock-spring-gsx-r-1000-k7-k8"),
        "SF" to Source("suzuki-forums.net: HS/LS Compression K7/K8", "https://www.suzuki-forums.net/threads/high-and-low-compression-on-gsxr1000-k7-k8.1283/"),
        "MO" to Source("MOTORRAD Top-Test GSX-R 1000 (2007)", "https://www.motorradonline.de/supersportler/top-test-suzuki-gsx-r-1000-der-siebte-streich/"),
        "MS" to Source("motorcyclespecs.co.za GSX-R1000 2007", "https://www.motorcyclespecs.co.za/model/suzu/suzuki_gsxr1000%2007.htm"),
        "SC" to Source("suzukicycles.org Modelljahr 2007", "https://www.suzukicycles.org/2000-2009/2007a.shtml")
    )

    val DATA: List<DataRow> = listOf(
        DataRow("Motor", "Bauart", "Reihenvierzylinder, flüssigkeitsgekühlt, DOHC, 4 Ventile je Zylinder", Status.OK1, listOf("MD"), ""),
        DataRow("Motor", "Hubraum", "999 cm³ (998,6 cm³)", Status.OK2, listOf("AM", "MD"), "Gegenprobe: 73,4² × π/4 × 59,0 × 4 = 998,6 cm³"),
        DataRow("Motor", "Bohrung × Hub", "73,4 × 59,0 mm", Status.OK2, listOf("MD", "AM"), "Rechnerisch konsistent mit 998,6 cm³"),
        DataRow("Motor", "Verdichtung", "12,5 : 1", Status.OK2, listOf("MD", "MO"), ""),
        DataRow("Motor", "Nennleistung", "185 PS (136 kW) bei 12.000 /min", Status.OK2, listOf("MO", "SC"), "MOTORRAD maß rund 180 PS. Fahrzeugschein Feld P.2 zeigt den Wert deiner Maschine."),
        DataRow("Motor", "Drehmoment", "117 Nm bei 10.000 /min", Status.OK2, listOf("MO", "MD"), "suzukicycles.org nennt 116,7 Nm"),
        DataRow("Antrieb", "Getriebe", "6 Gänge: 2,562 · 2,052 · 1,714 · 1,500 · 1,360 · 1,269", Status.OK2, listOf("MD", "MS"), "Beide Quellen identisch"),
        DataRow("Antrieb", "Kette", "530er Kette", Status.OK2, listOf("MD", "MS"), ""),
        DataRow("Antrieb", "Primärübersetzung / Kettenräder", "1,553 · 17 / 43 Zähne", Status.OPEN, emptyList(), "Im Modell als Annahme verwendet. Zähne am eigenen Ritzel und Kettenrad zählen."),
        DataRow("Antrieb", "Kupplung", "Anti-Hopping-Kupplung", Status.OK1, listOf("CW"), "Cycle World: überarbeitete Anti-Hopping-Kupplung"),
        DataRow("Elektronik", "Fahrmodi S-DMS", "Drei Motorkennfelder A / B / C", Status.OPEN, emptyList(), "Beeinflusst nur die Leistungsentfaltung, nicht das Fahrwerk"),
        DataRow("Elektronik", "Lenkungsdämpfer", "Hydraulisch, elektronisch geregelt (geschwindigkeitsabhängig), nicht von Hand einstellbar", Status.OK1, listOf("CW"), ""),
        DataRow("Elektronik", "ABS / Traktionskontrolle", "Keins von beiden", Status.OPEN, emptyList(), "Nach meinem Wissen bekam die GSX-R 1000 ABS erst ab Modelljahr 2017, hier nicht gegengeprüft. Darum bremst und beschleunigt der Modell-Fahrer ohne Regelsysteme"),
        DataRow("Fahrwerk", "Gabel", "KYB Upside-Down, 43 mm, voll einstellbar: Vorspannung, Zugstufe, Druckstufe Low- und High-Speed", Status.OK2, listOf("GX", "SF"), ""),
        DataRow("Fahrwerk", "Federweg vorn", "125 mm", Status.OK2, listOf("MD", "MS"), ""),
        DataRow("Fahrwerk", "Federbein", "Zentralfederbein mit Umlenkung, voll einstellbar", Status.OK2, listOf("GX", "DE"), "Höhenverstellung am Federbein: im Werkstatthandbuch prüfen"),
        DataRow("Fahrwerk", "Federweg hinten", "130 mm", Status.CONF, listOf("MD", "MS", "MO"), "moto-data und motorcyclespecs: 130 mm; MOTORRAD-Datenblatt: 135 mm. Modell rechnet mit 130 mm Radweg"),
        DataRow("Fahrwerk", "Federrate Gabel (Serie)", "0,976 kg/mm je Holm ≈ 9,57 N/mm", Status.OK1, listOf("TK"), "Einheit abgeleitet; Traxxion-Ersatz 0,975 kg/mm passt dazu"),
        DataRow("Fahrwerk", "Federrate Federbein (Serie)", "10,1 kg/mm ≈ 99 N/mm", Status.OK1, listOf("TK", "KT"), "K-Tech bietet 95 / 105 / 110 N/mm an"),
        DataRow("Geometrie", "Lenkkopfwinkel", "23,75° (23° 45′)", Status.OK2, listOf("CW", "MO"), "moto-data rundet auf 24°"),
        DataRow("Geometrie", "Nachlauf", "98 mm", Status.OK2, listOf("MO", "MD"), "Cycle World nennt 3,92 in ≈ 99,6 mm"),
        DataRow("Geometrie", "Radstand", "1415 mm", Status.OK2, listOf("MO", "MD"), ""),
        DataRow("Geometrie", "Gabel-Offset (abgeleitet)", "${fmt(K7.offsetMm, 1)} mm", Status.MODEL, emptyList(), "Aus Lenkkopfwinkel, Nachlauf und Reifenradius 299,9 mm berechnet"),
        DataRow("Abmessungen", "Sitzhöhe", "810 mm", Status.OK2, listOf("MO", "SC", "MD"), "Ohne Fahrer"),
        DataRow("Abmessungen", "Trockengewicht", "172 kg", Status.OK2, listOf("SC", "MD"), ""),
        DataRow("Abmessungen", "Gewicht vollgetankt", "212 kg (gewogen)", Status.OK1, listOf("MO"), "moto-data nennt 192 kg „wet“ ohne Tankangabe. Modell rechnet mit 212 kg"),
        DataRow("Abmessungen", "Tankinhalt", "17,5 l", Status.OK2, listOf("MO", "MD"), ""),
        DataRow("Räder & Reifen", "Reifen vorn", "120/70 ZR17 auf 3.50 × 17", Status.OK2, listOf("MO", "MD"), "Zulassungsbescheinigung Feld 15.1"),
        DataRow("Räder & Reifen", "Reifen hinten", "190/50 ZR17 auf 6.00 × 17", Status.OK2, listOf("MO", "MD"), "Zulassungsbescheinigung Feld 15.2"),
        DataRow("Räder & Reifen", "Luftdruck vorn (kalt)", "2,50 bar (36 psi)", Status.OK1, listOf("SZ"), "Aufkleber an der Schwinge vergleichen"),
        DataRow("Räder & Reifen", "Luftdruck hinten (kalt)", "2,90 bar (42 psi)", Status.OK1, listOf("SZ"), "wie oben"),
        DataRow("Bremsen", "Vorn", "2 Scheiben 310 mm, radial verschraubte Vierkolben-Festsättel", Status.OK2, listOf("MO", "MS"), ""),
        DataRow("Bremsen", "Hinten", "1 Scheibe 220 mm, Einkolben-Schwimmsattel", Status.OK2, listOf("MO", "MS"), ""),
        // Die sechs widersprüchlichen bzw. unsicheren Werkseinstellungen
        DataRow("Werkseinstellung", "Gabel Vorspannung", "4 Ringe sichtbar", Status.CONF, listOf("GX", "DE"), "gixxer.com: 4 Ringe; gsx-r1000.de: 3,5 Ringe"),
        DataRow("Werkseinstellung", "Gabel Zugstufe", "6 Klicks offen ab ganz zu", Status.OK2, listOf("GX", "DE"), ""),
        DataRow("Werkseinstellung", "Gabel Druckstufe Low-Speed", "14 Klicks offen", Status.CONF, listOf("GX", "DE"), "Zwei Quellen 14, ein Beitrag nennt 10"),
        DataRow("Werkseinstellung", "Gabel Druckstufe High-Speed", "3 Umdrehungen offen", Status.CONF, listOf("DE", "GX"), "Ein Beitrag nennt 2,5 Umdrehungen"),
        DataRow("Werkseinstellung", "Federbein Zugstufe", "11 Klicks offen", Status.CONF, listOf("DE", "GX2"), "Angaben 9 bis 12 Klicks"),
        DataRow("Werkseinstellung", "Federbein Druckstufe Low-Speed", "14 Klicks offen", Status.CONF, listOf("DE", "GX"), "Ein Beitrag nennt 13"),
        DataRow("Werkseinstellung", "Federbein Druckstufe High-Speed", "3 Umdrehungen offen", Status.OK2, listOf("DE", "GX2"), ""),
        DataRow("Werkseinstellung", "Einstellregel Suzuki", "In kleinen Schritten ändern, beide Gabelholme immer gleich einstellen", Status.OK1, listOf("SZ"), "")
    )

    /** Welche Einsteller einen unsicheren Serienwert haben (wird in der Oberfläche markiert). */
    val UNSURE_STOCK = setOf("fPre", "fLS", "fHS", "rReb", "rLS")

    data class Action(val key: String, val delta: Double, val label: String)
    data class Symptom(val id: String, val title: String, val causes: List<String>, val actions: List<Action>, val scenario: ScenarioId)

    val DIAG = listOf(
        Symptom("dive", "Gabel taucht beim Bremsen tief ein oder schlägt durch", listOf("Zu wenig Endprogression in der Gabel", "Druckstufe Low-Speed zu offen", "Federn zu weich für dein Gewicht"), listOf(Action("fLS", -2.0, "Druckstufe LS vorn 2 Klicks zu"), Action("fOil", 5.0, "Ölstand +5 mm (Werkstatt)")), ScenarioId.BRAKE),
        Symptom("chatter", "Vorderrad stempelt auf Rippen und Flickstellen", listOf("High-Speed-Druckstufe zu hart", "Luftdruck zu hoch", "Zugstufe zu stark: Gabel verhärtet sich (Packing)"), listOf(Action("fHS", 0.5, "Druckstufe HS vorn ½ Umdr. öffnen"), Action("fReb", 1.0, "Zugstufe vorn 1 Klick öffnen")), ScenarioId.RIPPLES),
        Symptom("rearpump", "Heck pumpt nach Wellen, schaukelt nach", listOf("Zugstufe hinten zu offen", "Zu wenig Vorspannung hinten"), listOf(Action("rReb", -2.0, "Zugstufe hinten 2 Klicks zu")), ScenarioId.BUMP),
        Symptom("traction", "Hinterrad findet am Kurvenausgang keine Traktion", listOf("Druckstufe hinten zu hart", "Zugstufe hinten zu stark: Rad kommt nicht schnell genug zurück", "Zu viel Vorspannung hinten"), listOf(Action("rLS", 2.0, "Druckstufe LS hinten 2 Klicks öffnen"), Action("rReb", 1.0, "Zugstufe hinten 1 Klick öffnen")), ScenarioId.ACCEL),
        Symptom("understeer", "Lenkt träge ein, schiebt über das Vorderrad", listOf("Front steht zu hoch oder Heck zu tief: zu viel Nachlauf", "Zu viel Vorspannung vorn", "Druckstufe vorn zu hart"), listOf(Action("fProt", 2.0, "Gabel 2 mm durchstecken"), Action("rShim", 2.0, "Heck 2 mm anheben")), ScenarioId.CORNER),
        Symptom("nervous", "Unruhig bei hohem Tempo, Lenker schlägt beim Beschleunigen", listOf("Front zu tief oder Heck zu hoch: zu wenig Nachlauf", "Zugstufe vorn zu offen", "Lenkungsdämpfer oder Lenkkopflager prüfen", "Reifendruck und Reifenzustand prüfen"), listOf(Action("fProt", -2.0, "Gabel 2 mm weniger durchstecken"), Action("fReb", -1.0, "Zugstufe vorn 1 Klick zu")), ScenarioId.ACCEL),
        Symptom("harsh", "Hart und unkomfortabel auf schlechtem Asphalt", listOf("High-Speed-Druckstufe zu hart", "Luftdruck über Sollwert"), listOf(Action("fHS", 0.5, "Druckstufe HS vorn ½ Umdr. öffnen"), Action("rHS", 0.5, "Druckstufe HS hinten ½ Umdr. öffnen")), ScenarioId.COUNTRY),
        Symptom("wallow", "Schwammig im Wechsel, Heck sackt durch", listOf("Druckstufe LS hinten zu offen", "Zu wenig Vorspannung hinten", "Zugstufe hinten zu offen"), listOf(Action("rLS", -2.0, "Druckstufe LS hinten 2 Klicks zu"), Action("rPre", 2.0, "Vorspannung hinten +2 mm")), ScenarioId.CORNER),
        Symptom("unused", "Kabelbinder zeigt: Federweg wird nicht genutzt", listOf("Druckstufe zu hart", "Zu viel Vorspannung", "Ölstand zu hoch"), listOf(Action("fLS", 2.0, "Druckstufe LS vorn 2 Klicks öffnen"), Action("fOil", -5.0, "Ölstand −5 mm (Werkstatt)")), ScenarioId.BRAKE),
        Symptom("frontfloat", "Front schwimmt in schnellen Wechselkurven", listOf("Zugstufe vorn zu offen", "Zu wenig Vorspannung vorn"), listOf(Action("fReb", -2.0, "Zugstufe vorn 2 Klicks zu"), Action("fPre", -1.0, "Vorspannung vorn +1 Ring (1 Ring weniger sichtbar)")), ScenarioId.BUMP),
        Symptom("twoup", "Mit Sozius: Heck sackt ab, Front leicht", listOf("Vorspannung hinten nicht an Zuladung angepasst", "Zugstufe hinten zu offen für die höhere Last"), listOf(Action("rPre", 5.0, "Vorspannung hinten +5 mm"), Action("rReb", -2.0, "Zugstufe hinten 2 Klicks zu")), ScenarioId.BUMP),
        Symptom("kick", "Lenker schlägt bei Bodenwellen im Kurvenausgang", listOf("Zugstufe vorn zu stark oder zu schwach", "Heck zu hoch, dadurch wenig Nachlauf", "Lenkungsdämpfer prüfen (elektronisch geregelt, in der Werkstatt prüfen lassen)"), listOf(Action("rShim", -1.0, "Heck 1 mm absenken"), Action("fReb", -1.0, "Zugstufe vorn 1 Klick zu")), ScenarioId.CORNER)
    )
}
