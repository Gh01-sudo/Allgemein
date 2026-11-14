# PC-Werkbank

Interaktiver PC-Konfigurator: Du wählst die Teile, die dir wichtig sind, und die Werkbank ergänzt den Rest passend dazu.

**Öffnen:** `index.html` im Browser öffnen. Keine Installation, keine externen Verbindungen (außer den „Preis prüfen“-Links zu Geizhals).

## Funktionen
- **8 Steckplätze:** Prozessor, CPU-Kühler, Mainboard, Arbeitsspeicher, Grafikkarte, SSD, Netzteil, Gehäuse
- **Zubehör und Software (optional):** Monitor, Zusatzlüfter, Betriebssystem (Windows 11 Home/Pro als System-Builder-Lizenz, Linux) und Tastatur & Maus. Werden nie automatisch ergänzt, zählen aber zu Gesamtpreis und Budget; der Monitor wird gegen Auflösung und Bildrate geprüft.
- **Automatisch ergänzen:** fehlende Teile werden passend zu Einsatz (Gaming, Büro, Kreativ/Workstation),
  Budget und der Leistungsklasse deiner gewählten Teile gewählt. „Behalten“ hält ein ergänztes Teil fest.
- **Kompatibilitätsprüfung:** Sockel, RAM-Typ und Steckplätze, Formfaktor, GPU-Länge, Kühlerhöhe und Radiator,
  Kühlleistung, Netzteil-Leistung und Bauform (SFX/ATX), 12V-2x6, iGPU-Pflicht ohne Grafikkarte,
  CPU-Flaschenhals, VRAM- und RAM-Empfehlungen
- **Leistungsbild** (Gaming-Klasse, Multi-Core) und **Strombilanz** mit empfohlener Netzteilgröße
- **Vorlagen:** Deine Konfiguration (4K-Gaming + Kreativ), Büro, Einstieg Full HD, Gaming WQHD, 4K-Enthusiast, Workstation, Kompakt-ITX
- Teileliste zum Kopieren; die Konfiguration bleibt im Browser gespeichert

## Mehr auf einen Blick
- **Kopfleiste**, die beim Scrollen oben bleibt: Gesamtpreis mit Budgetbalken, Bildrate gegen Ziel, Grafikkarte/Prozessor, Strom und Netzteil-Last, Lautstärke und Prüfstatus. Ein Klick springt in die passende Ansicht.
- **Konkrete Grafikkarten:** 54 Partnerkarten (z. B. MSI Gaming Trio, Gigabyte Windforce SFF, Sapphire Nitro+, PowerColor Reaper) mit eigenem Preis, Länge, Slot-Höhe und Kühler-Lautstärke. Der Chip-Eintrag bleibt als „Modell offen“ wählbar; „Modell wählen“ zeigt die passenden Karten. Bei hohem Leise-Wunsch nimmt die Automatik bis 10 % Aufpreis für einen leiseren Kühler in Kauf.
- **Steckbrief** an jedem Bauteil (aufklappbar): alle technischen Daten, Stärken, Schwächen, Hinweise und Preisstand.
- **Reiter „Übersicht“:** Kennzahlen, Schema des PCs (inkl. Radiator-Position), gesammelte Stärken und Schwachstellen, Upgrade-Pfad nach Nutzen pro Euro, Teileliste mit Preisstand.
- **Reiter „Vergleich“:** alle Teile einer Kategorie als sortierbare Tabelle mit Bestwerten, Passt-Prüfung und Übernahme per Klick; Preis-Leistungs-Diagramm für Grafikkarten, Prozessoren, RAM und SSDs.
  Filter nach Preisspanne, Hersteller (bei Grafikkarten auch Chip) und Suchbegriff; Spalte „P/L“ (100 = bestes passendes Teil) mit Markierung von Preis-Leistungs-Sieger und -Tipps sowie Kacheln „Stärkste“, „Günstigste“ und „Leiseste starke Karte“.
- Auslaufmodelle sind markiert und werden nicht mehr automatisch gewählt.

## Speichern und Drucken
- **Speichern / Laden:** beliebig viele benannte Konfigurationen im Browser speichern, laden, löschen (mit Rückfrage) und im Dashboard vergleichen.
- **Konfigurations-Code** (beginnt mit `PCW1:`) zum Kopieren und auf einem anderen Gerät Einfügen; außerdem als JSON-Datei herunterladen und wieder öffnen.
- **Drucken:** Druckansicht als A4-Datenblatt mit Teileliste zum Abhaken, Preisen, Bildraten, Prüfung, Kauf- und Einbauhinweisen und Code; im Druckdialog auch als PDF speicherbar. Im eingebetteten Artifact-Viewer blockiert der Browser das Drucken und Herunterladen, dort bleibt „Als Text kopieren“.

## Interaktiv
- **Tooltips** an Instrumenten, Balken, Diagrammpunkten und Schema (Maus, Touch und Tastatur).
- **Schema anklickbar:** Bauteil im PC-Schema anklicken, um es zu tauschen; Konflikte sind rot markiert.
- **Diagramme zum Anfassen:** Punkt im Preis-Leistungs-Diagramm übernimmt das Teil, Kosten-Balken öffnen die Auswahl.
- **Live-Vorschau im Auswahlfenster:** jedes Teil zeigt die Wirkung auf Preis, FPS, Multi-Core, Netzteil-Last oder Kühlreserve.

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
- **Gegen Fertig-PCs** (Lenovo Legion T7 und Tower 5i, Captiva, HP Omen 35L, Dubaro; Fazit nur mit belegten Preisen) und **gegen Laptops**, jeweils mit Preis je FPS.
- **Sparen ohne Nachteile:** gleichwertige, günstigere Teile mit Ersparnis, nur ohne Konflikte.
- **Preisentwicklung:** Preisindex seit 2024 für RAM, SSD, Grafikkarte und Prozessor, Tabelle „damals – heute“ und Kauftipp.
- Bildrate je Auflösung, Kostenverteilung, Upgrade-Stufen und Strom.
- Reihenfolge: Wichtigstes zuerst; jeder Abschnitt lässt sich einklappen, der Zustand wird gemerkt.

## Preise
Teile mit „Preis geprüft“ wurden zwischen dem 9. und 14. November 2026 recherchiert (Grafikkarten-Modelle, Zubehör und neue Teile am 14.11.), alle anderen Preise sind daraus hochgerechnete Schätzungen. RAM und SSDs sind seit
Ende 2025 wegen knapper Speicherproduktion deutlich teurer und schwanken stark. Vor dem Kauf aktuelle Preise prüfen.
Die Teiledatenbank steht oben im `<script>` von `index.html` (Objekt `DB`) und lässt sich dort anpassen.


Red-Team-Prüfung aller Preise, Specs und Regeln: `reports/Red Team PC-Werkbank.md` (Korrekturen sind eingearbeitet).
