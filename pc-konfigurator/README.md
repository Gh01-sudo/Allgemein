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

## Mehr auf einen Blick
- **Steckbrief** an jedem Bauteil (aufklappbar): alle technischen Daten, Stärken, Schwächen, Hinweise und Preisstand.
- **Reiter „Übersicht“:** Kennzahlen, Schema des PCs (inkl. Radiator-Position), gesammelte Stärken und Schwachstellen, Upgrade-Pfad nach Nutzen pro Euro, Teileliste mit Preisstand.
- **Reiter „Vergleich“:** alle Teile einer Kategorie als sortierbare Tabelle mit Bestwerten, Passt-Prüfung und Übernahme per Klick; Preis-Leistungs-Diagramm für Grafikkarten, Prozessoren, RAM und SSDs.
- Auslaufmodelle sind markiert und werden nicht mehr automatisch gewählt.

## Bessere Komponenten
- **„Besser“-Tipp an jedem Bauteil:** das günstigste passende Upgrade mit Aufpreis und Vorteil (z. B. „+9 FPS in 4K“, „80 PLUS Titanium“, „+32 GB“), per Klick übernehmbar.
- **„Bessere Wahl“ im Auswahlfenster:** bis zu drei Stufen nach Aufpreis, jede besser als die günstigere davor; nur Teile, die ohne Konflikt zum eigenen System passen.
- „Besser“ je Bauteil: Grafikkarte/Prozessor mehr Leistung, Kühler mehr Reserve, Gehäuse leiser/mehr Platz, Netzteil effizienter/mehr Reserve, Mainboard Ausstattung, RAM/SSD Kapazität und Tempo.
- Neue Premium-Teile: MSI MPG X870E Carbon WiFi, ASUS ROG Crosshair X870E Hero, Trident Z5 Neo 6000 CL28, Liquid Freezer III Pro 420, WD_Black SN8100 2 TB, Corsair RM1000x, be quiet! Dark Power 14 1000 W, Fractal Define 7.

## Dashboard (Cockpit)
- **Instrumente:** Rundinstrumente für Bildrate gegen Ziel, Netzteil-Last, Kühlreserve, Budget, Lautstärke und Preis pro FPS, darunter Kennzahlen (Preis, Leistungsklasse, Multi-Core, Leistungsaufnahme, Stromkosten pro Jahr, Kompatibilität).
- **8 Schieberegler** für Budget, Gaming/Kreativ, Auflösung, Ziel-FPS, RAM, SSD, Lautstärke und Gehäusegröße; die betroffenen Bauteile werden live neu gewählt, mit „Rückgängig“.
- **Leistungsprofil (Netzdiagramm):** Gaming, Kreativ, Leise, Effizienz, Zukunft, Preis-Leistung, mit Vergleichs-Konfiguration als zweiter Fläche.
- **Spiele und Anwendungen:** geschätzte FPS für eSports, AAA, Raytracing, Upscaling und Frame Generation in vier Auflösungen; Leistungsindizes für Blender, Video-Export, Kompilieren, Foto und lokale KI (Modellgröße nach Grafikspeicher).
- **Zwei Konfigurationen nebeneinander:** gegen jede Vorlage oder gespeicherte Varianten A/B, mit allen Kennzahlen, Profilwerten und Bauteilen.
- **Gegen Fertig-PCs** (HP Omen 35L, Lenovo Legion T7, Dubaro, Alienware Area-51) und **gegen Laptops**, jeweils mit Preis je FPS.
- Bildrate je Auflösung, Kostenverteilung, Upgrade-Stufen und Strom.

## Preise
Teile mit „Preis geprüft 9.11.“ wurden am 9. November 2026 recherchiert, alle anderen Preise sind daraus hochgerechnete Schätzungen. RAM und SSDs sind seit
Ende 2025 wegen knapper Speicherproduktion deutlich teurer und schwanken stark. Vor dem Kauf aktuelle Preise prüfen.
Die Teiledatenbank steht oben im `<script>` von `index.html` (Objekt `DB`) und lässt sich dort anpassen.

## Einkaufsliste
`einkaufsliste.html`: abhakbarer Bestellzettel für die Vorlage „Deine Konfiguration“ mit Modellnummern, Bestpreisen und Shop-Links (Stand 9. November 2026).

Red-Team-Prüfung aller Preise, Specs und Regeln: `reports/Red Team PC-Werkbank.md` (Korrekturen sind eingearbeitet).
