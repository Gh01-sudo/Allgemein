# K7 Fahrwerkslabor (Android)

Fahrwerksprüfstand für die **Suzuki GSX-R 1000 K7 (2007–2008)** als native Android-App (Kotlin, Jetpack Compose). Läuft ab Android 12, gebaut für Android 17 (API 37). Keine Internetverbindung, keine Werbung, kein Konto.

## Installation

Die fertige APK liegt unter [`release/K7-Fahrwerkslabor-1.1.0.apk`](release/) und als GitHub-Release `fahrwerk-app-v1.1.0`. Auf dem Handy öffnen und die Installation aus dieser Quelle erlauben.

## Was die App kann

| Bereich | Inhalt |
|---|---|
| **Setup** | Beladung, alle Einsteller an Gabel und Federbein, Reifendruck, Federtausch, Einsatzprofil. Gesamtnote und Teilnoten, Hinweise, Empfehlung mit Vorschau. |
| **Prüfstand** | Fahrten (Kante, Schlagloch, Wellen, Bremsen 0,8 g, Vollbremsung, Beschleunigen, Kurve 45°, Landstraße nach ISO 8608), Rüttelprüfstand (Frequenzgang 0,5–25 Hz), Dämpferprüfstand (Kraft-Geschwindigkeit, Schleifen), Federprüfstand. |
| **Werkstatt** | Durchhang messen und auswerten, Diagnose nach Fahrgefühl mit konkreten Änderungen. |
| **Setups** | Speichern, vergleichen, als Datei oder Text exportieren und importieren. |
| **Daten** | Datenblatt mit Quellen und Status, Modellannahmen, Selbstprüfungen, Grenzen des Modells. |

## Darstellung und Bedienung

- **Seitenansicht außen und Technik:** maßstäblich nach Radstand, Rädern und Lenkkopf. Technik zeigt Federn, Ölstand, Kette, Schwerpunkte und Radlast-Pfeile (1 kN = 12 cm).
- **Antippen:** Bauteil oder weißen Einsteller im Bild antippen → Erklärung, Live-Werte, Drehknöpfe (im Uhrzeigersinn = zu bzw. mehr Vorspannung, wie am Motorrad).
- **Gesten:** zwei Finger zoomen, waagrecht wischen = Zeitleiste, Doppeltippen = Zoom zurück.
- **Geisterbild:** Serie gestrichelt im selben Augenblick.
- **Ruhige Darstellung:** keine selbstlaufende Animation (folgt anfangs „Animationen entfernen“ des Systems).

## Physikmodell

Ebenes Mehrkörpermodell mit Zeitschritt 0,2 ms:

- gefederte Masse mit Fahrer (Heben, Nicken, Längsbewegung)
- Vorderrad an der schrägen Gabel (Luftfeder, Ölstand, Endanschläge)
- Hinterrad an der Schwinge mit progressiver Umlenkung
- Reifen mit Einfederung, Dämpfung und Haftgrenze (Magic Formula, Schlupf mit Einlauflänge)
- Kettenzug entlang der Kettenlinie (Anti-Squat), Bremsmomente, Motorkennlinie mit Getriebe, Luftwiderstand

**Grenzen:** Das Modell kennt keine Seitenkräfte, kein Lenken, kein Pendeln, keine Reibung in Gabel und Federbein, und der Fahrer ist starr. Viele Massen und alle Dämpferkennlinien sind Annahmen. Sechs Serienwerte sind als „unsicher“ markiert, weil sich die Quellen widersprechen. Ohne eigene Messungen am Motorrad ist das Ergebnis eine fundierte Schätzung.

## Selbst bauen

Voraussetzungen: JDK 21 und Android SDK (Platform 37).

```bash
./gradlew testDebugUnitTest   # 27 Physik-, Daten- und Geometrietests, Bildschirm-Test mit Robolectric
./gradlew assembleDebug       # Debug-APK
```

Für eine signierte Release-APK `keystore.properties` anlegen (nicht im Repository) oder `K7_KEYSTORE_PROPS` auf die Datei setzen:

```properties
storeFile=/pfad/zu/k7-release.jks
storePassword=…
keyAlias=k7labor
keyPassword=…
```

Danach `./gradlew assembleRelease`.

## Sicherheit

Fahrwerksänderungen verändern das Fahrverhalten. Immer in kleinen Schritten ändern, beide Gabelholme gleich einstellen und auf bekannter Strecke vorsichtig probefahren. Ölstand und Federn sind Werkstattarbeit. Die App ersetzt keine Fachwerkstatt.

Suzuki und GSX-R sind Marken der Suzuki Motor Corporation. Dies ist ein privates Projekt ohne Verbindung zu Suzuki.
