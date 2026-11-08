# Red Team: S25 Ultra Sicherheitskompass

Stand: 8. November 2026 · Gegenstand: `s25-ultra-sicherheit/index.html` (Version vor diesem Bericht)

Die Anleitung wurde aus drei Blickwinkeln angegriffen:

1. **Angreifer:** Welche realistischen Angriffe funktionieren trotz vollständiger Umsetzung?
2. **Schadensprüfer:** Welcher Ratschlag kann selbst Schaden anrichten?
3. **Angriffsfläche Seite:** Was verrät oder gefährdet die Webseite selbst?

Alle Befunde mit Status „behoben“ sind in der aktuellen Fassung umgesetzt.

## Zusammenfassung

| # | Schwere | Befund | Status |
|---|---|---|---|
| RT-01 | **Kritisch** | Die Antwort „Ich werde überwacht“ im Bedrohungs-Check wurde im Browser gespeichert. Ein Täter mit Zugriff aufs Handy hätte sie sehen können. | behoben |
| RT-02 | **Hoch** | Die häufigste Diebstahl-Kette (PIN ausspähen → Handy stehlen → Flugmodus → Konten übernehmen) war in Stufe „Basis“ nur teilweise abgedeckt. | behoben |
| RT-03 | **Hoch** | Smart Lock / „Entsperrung verlängern“ wurde nicht erwähnt. Das Handy bleibt entsperrt, solange ein vertrautes Gerät in der Nähe ist. | behoben |
| RT-04 | **Hoch** | Die Stalkerware-Prüfung konnte zu Fehlalarmen führen: Beispielnamen ähnelten echten Systemdiensten, und Googles Find Hub fehlte unter den legitimen Geräteadministratoren. | behoben |
| RT-05 | Mittel | Der Profi-Tipp „SMS als zweiten Faktor entfernen“ konnte zum Selbstaussperren führen. | behoben |
| RT-06 | Mittel | Restrisiken wurden nicht benannt. Dadurch entstand ein trügerisches Gefühl vollständiger Sicherheit. | behoben (neuer Abschnitt „Grenzen des Schutzes“) |
| RT-07 | Mittel | Die Repo-Fassung lädt Google Fonts und überträgt so beim Öffnen die IP-Adresse an Google. Für eine Datenschutz-Anleitung ist das widersprüchlich. Das LG München I hat das dynamische Einbinden 2022 als DSGVO-Verstoß gewertet (Az. 3 O 17493/20). | behoben in der Repo-Fassung (Systemschriften) |
| RT-08 | Mittel | Die SIM-PIN stand nur in Stufe „Profi“, gehört aber zur Diebstahl-Kette jeder Stufe. | behoben (Verweis in Kapitel 4) |
| RT-09 | Mittel | Gesperrte Handys zeigten SMS-Codes auf dem Sperrbildschirm. Der Schutz dagegen (Trick 07) war nur ein optionaler Trick. | behoben (Verweis in Kapitel 4) |
| RT-10 | Gering | Viele deutsche Menünamen sind nur für One UI 7 belegt; neue Pfade (Messenger, Tracking, „Netzwerk und Sicherheit sperren“) sind ungeprüft. | offen: Abgleich am echten Gerät nötig |
| RT-11 | Gering | Die Handy-Modelle zeigen Beispielzustände („Ein“/„Aus“), die nicht dem Werkszustand entsprechen müssen. | offen (akzeptiert, Bildunterschriften erklären die Aktion) |
| RT-12 | Gering | Die Telefonnummern 116 116, 116 016 und 116 006 wurden nicht gegen eine Quelle geprüft. | offen: vor Weitergabe prüfen |
| RT-13 | Info | Die Seite nutzt `innerHTML` nur mit eigenen, statischen Daten und ohne Nutzereingaben. Es gibt kein XSS-Risiko, keine Tracker und keine Formulare. | kein Handlungsbedarf |
| RT-14 | Info | Fortschritt und Schutzstufe liegen in `localStorage`. Daraus lässt sich ablesen, dass jemand sein Handy absichert. Das ist unkritisch, außer bei RT-01. | akzeptiert |

## Details

### RT-01: Sensible Antwort gespeichert (kritisch)

- **Angriff:** Eine überwachende Person (häusliche Gewalt, Kontrolle in der Partnerschaft) öffnet die Seite auf dem Handy des Opfers und sieht das gesetzte Häkchen. Das kann eine Eskalation auslösen.
- **Fix:**
  - Die Antwort wird nie gespeichert. Ein bereits gespeicherter Wert wird beim Laden gelöscht.
  - Der Hilfetext weist auf Browserverlauf, privaten Tab und ein anderes Gerät hin.
- **Prüfung:** Nach dem Neuladen ist das Häkchen leer, `localStorage` enthält nur `{"bank":true}`.

### RT-02 / RT-08 / RT-09: Diebstahl-Kette (hoch)

- **Angriff (dokumentiertes Muster):**
  1. Die PIN wird in Bar oder Bahn ausgespäht.
  2. Das Handy wird gestohlen.
  3. Auf dem Sperrbildschirm wird der Flugmodus eingeschaltet, damit die Ortung stoppt.
  4. Die SIM kommt in ein anderes Gerät, um SMS-Codes zu empfangen.
  5. Mit der PIN werden Konten übernommen.
- **Lücken der Anleitung:**
  - Die Sicherheitsverzögerung greift erst nach dem Entsperren und nur außerhalb sicherer Orte.
  - Der Flugmodus vom Sperrbildschirm war nicht adressiert.
  - Die SIM-PIN stand nur im Profi-Kapitel.
- **Fix:** Kapitel 4 (Basis) hat drei neue Schritte:
  - „Netzwerk und Sicherheit sperren“ (Menüname ungeprüft, mit „falls vorhanden“ gekennzeichnet)
  - Smart Lock / „Entsperrung verlängern“ beschränken
  - PIN verdeckt eingeben, mit Verweis auf SIM-PIN und auf ausgeblendete Sperrbildschirm-Inhalte

### RT-04: Fehlalarme bei der Stalkerware-Prüfung (hoch)

- **Risiko:** Nutzer deaktivieren legitime Systemdienste oder Googles Find Hub als Geräteadministrator. Danach funktioniert die Ortung nicht mehr, und das schwächt genau den Schutz, den die Anleitung aufbaut.
- **Fix:**
  - Die Beispielnamen in den Handy-Modellen sind jetzt eindeutig fiktiv („Handy Monitor Pro“, „Phone Tracker Plus“).
  - Find Hub ist als legitim markiert.
  - Neuer Warnhinweis: Nichts nur wegen eines technisch klingenden Namens deaktivieren, sondern die Herkunft prüfen.

### RT-06: Grenzen des Schutzes (mittel)

Neuer Abschnitt mit sechs Restrisiken und Gegenmaßnahmen:

- staatliche Spionage-Software (Zero-Click)
- Forensik am entsperrten Gerät
- Social Engineering beim Anbieter
- andere Geräte im selben Konto
- Menschen, die Ihre PIN kennen
- veraltete Anleitung

Für Spionage-Software stützt sich der Abschnitt auf die NSA-Empfehlung, Geräte regelmäßig neu zu starten (*Mobile Device Best Practices*, 2020), und verweist auf Fachstellen (Amnesty Security Lab, Access Now).

## Empfehlungen vor der Weitergabe

1. Menünamen an einem deutschen S25 Ultra mit One UI 9 abgleichen (RT-10), besonders „Netzwerk und Sicherheit sperren“, „Entsperrung verlängern“ und die Pfade der Profi-Kapitel.
2. Die Hilfe-Telefonnummern gegen offizielle Quellen prüfen (RT-12).
3. Die Anleitung bei jedem großen One-UI-Update erneut prüfen. Der Fakten-Check liegt in `reports/S25 Ultra One UI 9 Sicherheit.md`.
