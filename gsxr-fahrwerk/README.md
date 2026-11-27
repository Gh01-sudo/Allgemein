# K7 Fahrwerkslabor

Interaktives Fahrwerks-Dashboard für die Suzuki GSX-R 1000 K7 (2007–2008) mit Serienfahrwerk.

`index.html` im Browser öffnen. Enthält:

- Simulation: Halbfahrzeug-Modell mit Nicken (Front und Heck gekoppelt) für 14 Einzelsituationen: Bodenwelle, Schlagloch, Kante hoch, Absatz runter, Dehnfugen, Rippen, lange Wellen, Kuppe, Senke, Bremsung, Beschleunigung, Kurve, Anbremsen in die Kurve, Wechselkurve. Alle Hindernisse und Manöver in Größe einstellbar.
- Testzyklen: vier Rundkurse mit eigenem Starttempo und Intensität (Landstraße, Rennstrecke, Stadt mit Kopfsteinpflaster und Gleisen, Autobahn mit Brückenfugen und Spurwechsel). Bremsen, Schräglage und Hindernisse greifen ineinander, Hindernisse liegen dort, wo das Motorrad zum geplanten Zeitpunkt wirklich ist
- Zwei-Stempel-Prüfstand: Sinus-Sweep Hub und Nicken (logarithmisch, Stempelgeschwindigkeit 0,25 m/s wie echte Anlagen) und Falltest. Daraus ein Frequenzgang mit Aufbau-Eigenfrequenz, Überhöhung und Radspringen, jeweils gegen den Vergleich. Prüfstandsversuche zählen nicht zum Setup-Index
- Prüfstand-Ansicht: 21 Prüfsituationen als Kacheln in fünf Gruppen mit Note, Notenbalken und Änderung; feste Kopfleiste mit Index-Ring und Gruppennoten; Federbein links, Gabel rechts, Maschine und Messschrieb in der Mitte. Jede Änderung rechnet sofort neu; die Zeile „Letzte Änderung“ zeigt die Wirkung, die gestrichelte Kontur den Zustand vorher. Vergleich mit Vorher, Serie oder gemerktem Setup
- Einstell-Landkarte: Farbband unter jedem Fahrwerks-Einsteller zeigt die Note der gewählten Prüfsituation über den ganzen Regelbereich, ▲ markiert die beste Stellung und stellt sie per Klick ein
- Kraftschlussgrenze: Blockiert das Vorderrad oder dreht das Hinterrad durch, überträgt der Reifen nur Gleitreibung; Tempo, Weg und Nicken folgen der echten Verzögerung (Anzeige Soll/Ist). Reifensteifigkeit folgt dem aktuellen Tempo
- Auswertung: Zerlegung, wo der Setup-Index Punkte verliert (Kriterium × Achse, summiert exakt auf 100 − Index, mit den verursachenden Situationen), Wirkung jedes Einstellers auf den Gesamtindex als Tornado-Diagramm mit Übernehmen-Knopf, Testparcours als Farbtabelle
- Reifen-Lastempfindlichkeit (µ sinkt mit der Radlast) und 2 ms Verzögerung der Dämpferkraft (Hysterese durch Ölkompressibilität und Shim-Trägheit)
- Erklärungen: „Was zeigt das?“ in jeder Karte, Wirkung des zuletzt verstellten Einstellers im Änderungsband, offene Liste, was das Modell rechnet und was nicht (keine Lenk- und Querdynamik)
- Bewertung ohne harte Null-Sättigung (Teilnoten laufen weich gegen 0), Tornado wahlweise nur Fahrdynamik
- Schnelle Rechnung: Federkennlinien, Reifen-Progression, Reibung und Fahrmanöver sind tabelliert (Abweichung unter 0,0001 mm), alle 21 Läufe für Setup und Vergleich in rund 0,2 s, verteilt auf ruckelfreie Häppchen
- Live-Seitenansicht mit echter Kinematik: Lenkkopfwinkel, Nachlauf, Radstand, Nickwinkel
- Durchhang-Messassistent mit konkreter Vorspann-Empfehlung
- Fahrverhalten-Diagnose mit Testknöpfen
- Technische Daten mit Prüfstatus und Quellen (doppelte Prüfung), Modellannahmen, Rechenprüfungen
- Setups speichern, vergleichen, exportieren

Werte mit Status „abweichend“, „offen“ oder „Modell“ sind nicht doppelt bestätigt und am Fahrzeug oder im Werkstatthandbuch zu prüfen.
