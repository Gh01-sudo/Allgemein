# NAS-Werkbank

Interaktiver NAS-Konfigurator nach dem Vorbild der PC-Werkbank: Du wählst Gerät, Festplatten oder Zubehör, das dir wichtig ist, und die Werkbank ergänzt den Rest passend dazu.

**Öffnen:** `index.html` im Browser öffnen. Keine Installation, keine externen Verbindungen (außer den „Preis prüfen“-Links zu Geizhals).

## Funktionen
- **7 Positionen:** NAS-System (Fertig-NAS oder Selbstbau), Festplatten mit Stückzahl, SSD-Cache, Arbeitsspeicher, Netzwerkkarte, USV, Backup
- **Einsatz:** Dateien & Backup, Medien & Fotos (Plex/Jellyfin), Homelab & VMs
- **Automatisch ergänzen:** wählt Gerät, Plattenmodell und -anzahl passend zu Ziel-Nutzkapazität, Ausfallsicherheit (0/1/2 Platten), Transcoding, VMs, Netzwerk-Tempo, Lautstärke, Budget und 5-Jahres-Stromkosten. „Behalten“ hält ein ergänztes Teil fest.
- **Prüfung:** Schächte, RAID-Mindestanzahl, SMR- und Desktop-Platten, Plattenfreigabe je Schachtanzahl, Synology-Besonderheiten (Plattenliste der 2025er Plus-Serie, Fremd-RAM, Synology-only-NICs, SSD-Pools), RAM-Typ/ECC/Steckplätze/Maximum, Barebone ohne RAM, M.2-Plätze, PCIe/Mini-Steckplatz, Treiber, Hardware-Transcoding, Docker/VMs, USV-Leistung und Überbrückungszeit, Backup-Größe und 3-2-1-Regel, Füllstand, Lautstärke
- **Kennzahlen:** Nutzkapazität (TB/TiB), RAID-Art (SHR, RAID, RAIDZ), Durchsatz mit Engpass, Rebuild-Dauer, grobes Datenverlust-Risiko, Strom 24/7 mit Ruhezustand, Lautstärke in dB(A), Preis pro TB, laufende Kosten
- **Vorlagen:** Familien-NAS, Einstieg Backup, Medienserver, Homelab, Selbstbau TrueNAS, Leise im Wohnzimmer, Datenarchiv

## Wie bei der PC-Werkbank
- **Steckbrief**, **„Besser“-Tipp** und **„Bessere Wahl“** im Auswahlfenster mit Live-Vorschau (Preis, Nutzkapazität, Strom, Lautstärke)
- **Dashboard (Cockpit):** Instrumente, Kennzahlen, Aufgaben und Dienste, Leistungsprofil (Netzdiagramm), zwei Konfigurationen nebeneinander, andere Geräte für deine Platten, Vergleich mit Cloud-Speicher über 5 Jahre, Sparen ohne Nachteile, Regler mit „Rückgängig“, Kapazität je Ausfallsicherheit, Kostenverteilung, Preis pro TB aller Platten, Stufe rauf/runter, Strom/Lautstärke/Prüfung. Abschnitte einklappbar.
- **Übersicht:** klickbares Schema mit Schächten (Daten/Parität), M.2, RAM, LAN, USV und Backup; Stärken, Schwachstellen, Upgrade-Pfad, Teileliste
- **Vergleich:** alle Teile einer Kategorie als sortierbare Tabelle mit Bestwerten und Preis-Leistungs-Diagramm
- **Speichern / Laden, Konfigurations-Code** (beginnt mit `NAS1:`), JSON-Datei, **Druckansicht** als A4-Datenblatt

## Preise und Daten
Alle Preise sind Richtpreise (Stand November 2026) und **noch nicht** durch eine Red-Team-Prüfung belegt wie bei der PC-Werkbank. Festplatten, SSDs und RAM sind seit 2025 deutlich teurer und schwanken stark. Leistungs-, Strom- und Lautstärkewerte sind aus Datenblättern abgeleitete Schätzungen. Vor dem Kauf Tagespreise und Kompatibilitätslisten prüfen.
Die Teiledatenbank steht oben im `<script>` von `index.html` (Objekt `DB`) und lässt sich dort anpassen.
