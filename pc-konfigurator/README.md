# PC-Werkbank

Interaktiver PC-Konfigurator: Du wählst die Teile, die dir wichtig sind, und die Werkbank ergänzt den Rest passend dazu.

**Öffnen:** `index.html` im Browser öffnen. Keine Installation, keine externen Verbindungen (außer den „Preis prüfen“-Links zu Geizhals).

## Funktionen
- **8 Steckplätze:** Prozessor, CPU-Kühler, Mainboard, Arbeitsspeicher, Grafikkarte, SSD, Netzteil, Gehäuse
- **Automatisch ergänzen:** fehlende Teile werden passend zu Einsatz (Gaming, Büro, Kreativ/Workstation),
  Budget und der Leistungsklasse deiner gewählten Teile gewählt. „Behalten“ hält ein ergänztes Teil fest.
- **Kompatibilitätsprüfung:** Sockel, RAM-Typ und Steckplätze, Formfaktor, GPU-Länge, Kühlerhöhe und Radiator,
  Kühlleistung, Netzteil-Leistung und Bauform (SFX/ATX), 12V-2x6, iGPU-Pflicht ohne Grafikkarte,
  CPU-Flaschenhals, VRAM- und RAM-Empfehlungen
- **Leistungsbild** (Gaming-Klasse, Multi-Core) und **Strombilanz** mit empfohlener Netzteilgröße
- **Vorlagen:** Deine Konfiguration (4K-Gaming + Kreativ), Büro, Einstieg Full HD, Gaming WQHD, 4K-Enthusiast, Workstation, Kompakt-ITX
- Teileliste zum Kopieren; die Konfiguration bleibt im Browser gespeichert

## Bessere Komponenten
- **„Besser“-Tipp an jedem Bauteil:** das günstigste passende Upgrade mit Aufpreis und Vorteil (z. B. „+9 FPS in 4K“, „80 PLUS Titanium“, „+32 GB“), per Klick übernehmbar.
- **„Bessere Wahl“ im Auswahlfenster:** bis zu drei Stufen nach Aufpreis, jede besser als die günstigere davor; nur Teile, die ohne Konflikt zum eigenen System passen.
- „Besser“ je Bauteil: Grafikkarte/Prozessor mehr Leistung, Kühler mehr Reserve, Gehäuse leiser/mehr Platz, Netzteil effizienter/mehr Reserve, Mainboard Ausstattung, RAM/SSD Kapazität und Tempo.
- Neue Premium-Teile: MSI MPG X870E Carbon WiFi, ASUS ROG Crosshair X870E Hero, Trident Z5 Neo 6000 CL28, Liquid Freezer III Pro 420, WD_Black SN8100 2 TB, Corsair RM1000x, be quiet! Dark Power 14 1000 W, Fractal Define 7.

## Dashboard
Zweiter Reiter „Dashboard“ mit allen Auswertungen auf einen Blick: Kennzahlen (Preis, Bildrate, Multi-Core, Strom, Lautstärke, Kompatibilität),
**Schieberegler** für Budget, Gaming/Kreativ, Auflösung, Ziel-FPS, RAM, SSD, Lautstärke und Gehäusegröße (die betroffenen Bauteile werden
automatisch neu gewählt, mit „Rückgängig“), Bildrate je Auflösung mit CPU-Grenze, Kostenverteilung, Upgrade-Stufen für GPU/CPU,
Stromkosten pro Jahr und ein **Laptop-Vergleich** (14", 16", 18", MacBook Pro) mit dem Gerät, das der Desktop-Leistung am nächsten kommt.

## Preise
Teile mit „Preis geprüft 9.11.“ wurden am 9. November 2026 recherchiert, alle anderen Preise sind daraus hochgerechnete Schätzungen. RAM und SSDs sind seit
Ende 2025 wegen knapper Speicherproduktion deutlich teurer und schwanken stark. Vor dem Kauf aktuelle Preise prüfen.
Die Teiledatenbank steht oben im `<script>` von `index.html` (Objekt `DB`) und lässt sich dort anpassen.

## Einkaufsliste
`einkaufsliste.html`: abhakbarer Bestellzettel für die Vorlage „Deine Konfiguration“ mit Modellnummern, Bestpreisen und Shop-Links (Stand 9. November 2026).

Red-Team-Prüfung aller Preise, Specs und Regeln: `reports/Red Team PC-Werkbank.md` (Korrekturen sind eingearbeitet).
