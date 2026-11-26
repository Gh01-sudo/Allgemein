# NAS-Werkbank

Interaktiver NAS-Konfigurator nach dem Vorbild der PC-Werkbank: Du wählst Gerät, Festplatten oder Zubehör, das dir wichtig ist, und die Werkbank ergänzt den Rest passend dazu.

**Öffnen:** `index.html` im Browser öffnen. Keine Installation, keine externen Verbindungen (außer den „Preis prüfen“-Links zu Geizhals und Alternate).

## Funktionen
- **Schwerpunkt Synology:** Hersteller-Filter (Standard: Synology, umschaltbar auf alle). 23 Synology-Modelle von DS124 bis DS2422+ inklusive neo+-Reihe, Rackstations und Auslaufmodellen, dazu Synology-RAM, -SSDs, -Festplatten, -Netzwerkkarten und Erweiterungseinheiten mit den offiziellen Freigaben je Modell.
- **Schächte wählen:** beliebig, 1, 2, 4, 5, 6, 8 oder 12+; Erweiterungseinheiten (DX517, DX525, DX1222, RX418) erhöhen die Schachtzahl.
- **9 Positionen:** NAS-System, Festplatten, Erweiterungseinheit, SSD-Cache, Arbeitsspeicher (mit Stückzahl), Netzwerk/PCIe-Karte, USV, Backup – Stückzahlen per − / +.
- **Einsatz:** Dateien & Backup, Medien & Fotos (Plex/Jellyfin), Homelab & VMs.
- **Beste Kombination nach Preis, Strom und deinen Vorgaben:** Die Automatik spielt alle Geräte, Plattenmodelle und Stückzahlen durch, bewertet die besten Kandidaten mit der vollständigen Kompatibilitätsprüfung und schließt Kombinationen mit Konflikten aus (Budget, Kapazität, Ausfallsicherheit, Transcoding, VMs, Netzwerk, Lautstärke, Strom über 5 Jahre). Die Gewichtung ist ein Modell, keine objektive Wahrheit; geschätzte Preise können die Wahl verschieben.
- **Prüfung:** Schächte inkl. Erweiterung, RAID-Mindestanzahl, SMR/Desktop-Platten, Synology-Laufwerksrichtlinie 2025 (Festplatten frei, M.2 nur Synology), freigegebene RAM-Module je Modell, identische Module, ECC/Nicht-ECC bei neo+, Karten-Freigaben je Modell, M.2-Plätze, Transcoding, Docker/VMs, USV, Backup (3-2-1), Füllstand, Lautstärke, Daten heute größer als Kapazitätsziel.
- **RAID-Rechner (eigener Reiter):** Platten auch unterschiedlich groß; SHR, SHR-2, Basic, JBOD, RAID 0/1/5/6/10, RAIDZ1/2/3 mit Nutzkapazität, TiB, Ausfalltoleranz, Redundanz, ungenutztem Platz, Effizienz und Tempo; Aufteilung je Platte und Übernahme der Ausfallsicherheit in die Konfiguration.

## Wie bei der PC-Werkbank
- **Steckbrief**, **„Besser“-Tipp** und **„Bessere Wahl“** im Auswahlfenster mit Live-Vorschau (Preis, Nutzkapazität, Strom, Lautstärke)
- **Dashboard (Cockpit):** Instrumente, Kennzahlen, Aufgaben und Dienste, Leistungsprofil (Netzdiagramm), zwei Konfigurationen nebeneinander, andere Geräte für deine Platten, Vergleich mit Cloud-Speicher über 5 Jahre, Sparen ohne Nachteile, Regler mit „Rückgängig“, Kapazität je Ausfallsicherheit, Kostenverteilung, Preis pro TB aller Platten, Stufe rauf/runter, Strom/Lautstärke/Prüfung. Abschnitte einklappbar.
- **Übersicht:** klickbares Schema mit Schächten (Daten/Parität), M.2, RAM, LAN, USV und Backup; Stärken, Schwachstellen, Upgrade-Pfad, Teileliste
- **Vergleich:** alle Teile einer Kategorie als sortierbare Tabelle mit Bestwerten und Preis-Leistungs-Diagramm
- **Speichern / Laden, Konfigurations-Code** (beginnt mit `NAS1:`), JSON-Datei, **Druckansicht** als A4-Datenblatt

## Preise und Daten
Teile mit „Preis geprüft 25.11.“ wurden am 25. November 2026 per Preisvergleich recherchiert (überwiegend Geizhals-Daten); alle anderen Preise sind daraus abgeleitete Schätzungen. Festplatten (ca. 30–55 €/TB), Arbeitsspeicher und SSDs sind seit 2025 wegen der KI-Nachfrage stark gestiegen und schwanken. Leistungs-, Strom- und Lautstärkewerte sind aus Datenblättern abgeleitete Schätzungen. Vor dem Kauf Tagespreise und Kompatibilitätslisten prüfen.
Die Teiledatenbank steht oben im `<script>` von `index.html` (Objekt `DB`) und lässt sich dort anpassen.

Red-Team-Prüfung der Preise, Datenblätter und Regeln (zwei Runden): `reports/Red Team NAS-Werkbank.md` (Korrekturen sind eingearbeitet).
