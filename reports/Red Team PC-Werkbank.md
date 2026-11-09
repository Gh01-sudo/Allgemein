# Red Team: PC-Werkbank (Prüfstand 09.11.2026, Markt Deutschland)

Geprüfte Datei: `pc-werkbank.html` (DB, LAPTOPS, checkAll, estLoad/recPsu, PRESETS "mine").
Methode: Live-Preise über hardwaredealz.com (zeigt Geizhals-Daten mit Zeitstempel 09.11.2026, 13:20–14:21 Uhr). Geizhals, heise-Preisvergleich, Caseking, MSI und Notebookcheck-Vergleichsseiten blockierten direkte Abrufe (HTTP 403). Dazu kamen Tests und Meldungen von PCGH, ComputerBase, Notebookcheck, hwcooling, Macwelt und igor'sLAB. Werte ohne Beleg sind unter „Nicht prüfbar“ aufgeführt und nicht erfunden.

## Zusammenfassung

- **Nutzerkonfiguration ist kompatibel.** Sockel, Formfaktor, Kühlerhöhe (155 mm bei max. 185 mm), GPU-Länge (9070 XT 288–360 mm bei max. 432 mm) und Netzteil (RM850e: ATX 3.1, 1× 12V-2x6, 3× PCIe 8-Pin) passen. Die Preise von CPU (302,59 €), GPU (896,44 €), Netzteil (105,46 €), RAM und SSD liegen innerhalb von 10 % des Tools.
- **BIOS:** Das B650 Gaming Plus WiFi unterstützt Ryzen 9000 erst ab neueren BIOS-Ständen (AGESA 1.2.x). Alte Lagerware könnte also nicht booten. Das Board hat aber laut Händler-Q&A einen Flash-BIOS-Button, damit lässt sich das BIOS ohne CPU aktualisieren. Das Tool warnt bei AM5 überhaupt nicht vor dem BIOS (nur bei LGA1700).
- **Preisabweichungen über 10 %** gibt es vor allem bei Intel-CPUs. Arrow Lake ist deutlich billiger als im Tool: 245K −19 %, 225 −13 %, 245KF −14 %, 265KF −12 %. Teurer sind 14400F (+20 %) und 8600G (+13 %), der 7800X3D ist günstiger (−13 %). Bei den GPUs liegen RTX 5050 (+11 %) und RX 9060 XT 16 GB (−11 %) daneben. Mehrere davon sind fälschlich als `chk:true` markiert.
- **Der Multi-Core-Index `m` ist systematisch zu hoch für CPUs mit 6 bis 12 Kernen**: um 10–50 % (z. B. 7800X3D 62 statt ca. 42, 9800X3D 72 statt ca. 55). `value()` rechnet mit `score^3`, deshalb verzerrt das die Empfehlungen im Kreativprofil spürbar.
- **Laptops sind zu optimistisch eingeordnet.** Laut Notebookcheck ist die RTX 5090 Laptop „nur knapp schneller als eine Desktop-RTX 5070“ (5070 = 75), sie liegt also bei ca. 80. Das Tool setzt 88 (ca. 4 % unter der 9070 XT); tatsächlich sind es eher 13 % darunter. Faktische Fehler: Das G14 2026 hat 32 GB statt 16 GB RAM. Das MacBook Pro M5 Max mit 36 GB hat eine 32-Kern-GPU, die 40-Kern-GPU gibt es erst ab 48 GB. Der Scar 18 (2026) nutzt den Core Ultra 9 290HX Plus, nicht den 275HX.
- **Fehlende Neuheiten:** Ryzen 9 9950X3D2 Dual Edition (ab 779 €), Core Ultra 5 250K Plus (ab 216 €), Radeon RX 9070 GRE 12 GB (ab 639 €), Ryzen 5 7500X3D (ab 221,90 €) und das MacBook Pro „PROLED XDR“ (OLED, 28.10.2026, 16" M5 Max ab 5.599 €). RTX 50 Super und Intel Nova Lake gibt es bis heute nicht. Sie fehlen also zu Recht.

## Befunde

| Schwere | Teil-id | Feld | Wert im Tool | korrekter Wert | Quelle-URL |
|---|---|---|---|---|---|
| mittel | i5-14400f | price | 125 | 149,90 (+20 %) | https://hardwaredealz.com/hardware/cpu/intel-core-i5-14400f-boxed-fan/preisvergleich.2215 |
| mittel | r5-8600g | price (chk:true) | 150 | 169,00 (+13 %) | https://hardwaredealz.com/hardware/cpu/amd-ryzen-5-8600g-boxed-fan/preisvergleich.2353 |
| mittel | r7-7800x3d | price (chk:true) | 343 | 298,00 boxed / 283,99 tray (−13 %) | https://hardwaredealz.com/hardware/cpu/amd-ryzen-7-7800x3d-boxed/preisvergleich.1880 |
| mittel | u5-245k | price | 235 | 189,90 (−19 %) | https://www.hardwaredealz.com/hardware/cpu/intel-core-ultra-5-245k-boxed/preisvergleich.22738 |
| mittel | u5-245kf | price (chk:true) | 200 | 172,68 (−14 %) | https://www.hardwaredealz.com/hardware/cpu/intel-core-ultra-5-245kf-boxed/preisvergleich.22739 |
| mittel | u7-265kf | price (chk:true) | 296 | 261,90 (−12 %) | https://www.hardwaredealz.com/hardware/cpu/intel-core-ultra-7-265k-boxed/preisvergleich.22741 (ID 22741 zeigt den 265KF; ID 22740 den 265K für 309 €) |
| mittel | u5-225 | price | 205 | 177,40 (−13 %) | https://www.hardwaredealz.com/hardware/cpu/intel-core-ultra-5-225-boxed-fan/preisvergleich.24595 |
| mittel | rtx5050 | price (chk:true) | 346 | 383,90 (+11 %) | https://hardwaredealz.com/hardware/gpu/nvidia-geforce-rtx-5050-8gb |
| mittel | rx9060xt-16 | price (chk:true) | 679 | 605,90 (−11 %) | https://hardwaredealz.com/hardware/gpu/amd-radeon-rx-9060-xt-16gb |
| mittel | arc-b580 | g | 52 | ca. 39 (CB 1080p Raster: B580 69,3 fps vs. RTX 5060 89,1 fps; 5060 = 50) | https://www.computerbase.de/artikel/grafikkarten/rtx-5060-rtx-4060-rtx-3060-rx-7600-b580-rx-9060-xt-test.97078/seite-2 |
| gering | arc-b570 | g | 45 | ca. 35 (abgeleitet aus B580 ca. 39; B570-Abstand nicht separat belegt) | wie oben |
| gering | rtx5050 | g | 42 | ca. 39 (CB: 69,0 fps vs. 5060 89,1) | wie oben |
| mittel | r9-9900x | m | 85 | ca. 77 (CB R20 12.739 vs. 9950X 16.525) | https://www.hwcooling.net/wp-content/uploads/2024/11/intel-core-ultra-5-245k-g118.html |
| mittel | r7-9800x3d | m | 72 | ca. 55 (9.021) | wie oben |
| mittel | r7-9850x3d | m | 75 | ca. 56 (9800X3D +2 % Multi laut ComputerBase) | https://www.computerbase.de/artikel/prozessoren/amd-ryzen-7-9850x3d-vs-9800x3d-vs-285k-test.95759/seite-4 |
| mittel | r7-9700x | m | 66 | ca. 49 (8.140) | hwcooling (s. o.) |
| mittel | r7-7800x3d | m | 62 | ca. 42 (6.866) | hwcooling (s. o.) |
| mittel | r5-9600x | m | 50 | ca. 40 (6.624) | hwcooling (s. o.) |
| mittel | i5-14400f | m | 50 | ca. 38 (6.211) | hwcooling (s. o.) |
| mittel | r5-7600 | m | 45 | ca. 34 (5.633) | hwcooling (s. o.) |
| mittel | u5-245k / u5-245kf | m | 68 | ca. 59 (9.716) | hwcooling (s. o.) |
| gering | u7-265k / u7-265kf | m | 88 | ca. 86 (14.147) | hwcooling (s. o.) |
| gering | u9-285k | m | 100 | ca. 102 (16.840) | hwcooling (s. o.) |
| gering | u7-270kp | m | 100 | ≥ 102 (schlägt 285K in CB R23: 43.070 vs. 40.157) | https://borncity.com/news/intel-core-ultra-7-270k-plus-mittelklasse-prozessor-uebertrumpft-flaggschiff/ |
| mittel | g14 (Laptop) | ram | 16 GB | 32 GB LPDDR5x-8533 (GU405AR, 3.299 €) | https://www.notebookcheck.com/Asus-ROG-Zephyrus-G14-GU405AR.1307330.0.html |
| mittel | scar18 (Laptop) | g | 88 | ca. 80 („RTX 5090 Laptop nur knapp schneller als Desktop-RTX 5070“, +5–10 %) | https://www.notebookcheck.net/Latest-GeForce-RTX-5090-laptops-are-just-barely-faster-than-a-desktop-RTX-5070-but-there-is-one-key-differentiator.994073.0.html |
| mittel | aorus16 (Laptop) | g | 80 | ca. 72 (abgeleitet: 5080 Laptop ca. 10–15 % unter 5090 Laptop) | https://www.techspot.com/news/107398-laptop-rtx-5080-comes-within-15-rtx-5090.html |
| mittel | g14 (Laptop) | g | 72 | ca. 62–65 (abgeleitet; 115-W-TGP; Abstand 5090L/5070TiL laut CheckFPS +14 % WQHD, +40 % 4K) | https://checkfps.io/en/compare/rtx-5090-laptop-vs-rtx-5070-ti |
| mittel | scar18 (Laptop) | cpu / display | Core Ultra 9 275HX; 18" Mini-LED 240 Hz | Scar 18 (2026): Core Ultra 9 290HX Plus, 18" 4K Mini-LED 240 Hz | https://guru3d.com/story/asus-launches-2026-rog-strix-scar-18-with-rtx-5090-laptop-gpu-and-4k-miniled-display/ |
| mittel | scar18 (Laptop) | price (chk:false) | 4.700 | Spanien „ab 5.299 €“; DE-Straßenpreis nicht belegt | https://www.geeknetic.es/Noticia/40034/El-ASUS-ROG-Strix-SCAR-18-2026-llega-a-Espana-con-RTX-5090-y-pantalla-4K-Mini-LED-de-240-Hz-desde-5299-euros.html |
| mittel | mbp16 (Laptop) | gpu | 40-Kern-GPU bei 36 GB | Bei 36 GB / 2 TB: 18-Core-CPU + **32-Core-GPU**; 40-Core-GPU erst ab 48 GB (ab 4.899 €) | https://www.lambda-tek.com/Apple-MGE74D-A-shUS/B52080561 ; https://preisvergleich.heise.de/apple-macbook-pro-16-2-mgee4d-a-a3750098.html |
| gering | mbp16 (Laptop) | price | 4.499 (UVP) | günstigster Geizhals-Preis ca. 4.162,87 € (36 GB/2 TB) – Feld soll Straßenpreis sein | https://geizhals.de/3750095 (per Suchergebnis, Direktabruf 403) |
| gering | mbp16 (Laptop) | Neuheit | – | MacBook Pro PROLED XDR (OLED, Touch) seit 28.10.2026; 16" M5 Max ab 5.599 € | https://www.macwelt.de/article/3271454/apple-stellt-neues-macbook-pro-mit-touch-vor-fur-ab-3299-euro.html |
| gering | wd-sn7100-1 / -2 | name | WD Black SN7100 | seit CES 2026 als „SanDisk Optimus GX 7100“ verkauft (identische Hardware, Altware noch im Handel) | https://www.igorslab.de/sandisk-beendet-wd-marken-und-fuehrt-ssd-reihe-optimus-ein/ |
| gering | lexar-nm790-4 | price (chk:true) | 500 | 445 (Stand 14.09.2026; aktueller Preis nicht abrufbar) | https://www.pcgameshardware.de/SSD-Hardware-255552/Specials/SSD-Preise-2026-Preis-pro-Terabyte-Kaufberatung-1553743/ |
| gering | s9100-2 | price (chk:true) | 450 | 379–419 (Stand 14.09.2026; aktuell nicht abrufbar) | wie oben |
| gering | gs-2x16-6000 | id/name | id "gs-…" (G.Skill), Name Patriot Viper Venom | Benennung inkonsistent; Preis 520 plausibel (Viper Venom 6000 CL36 500,90 €, CL28 524,90 €; Okt. CL30 ab 489 €) | https://hardwaredealz.com/hardware/ram/patriot-viper-venom-udimm-32gb-kit/preisvergleich.66347 |
| gering | k-2x8-5600 | price | 270 | 16-GB-DDR5-Kits aktuell ab ca. 303 € (anderes Modell; Kingston-Kit selbst nicht geprüft) | https://hardwaredealz.com/hardware/ram |
| gering | bq-sb802 | gpu | 432 | 432 mm nur ohne HDD-Käfig im Weg; mit HDD-Käfig 287 mm | https://www.caseking.de/en/be-quiet-silent-base-802-window-mid-tower-tempered-glass-black/GEBQ-061.html (per Suchergebnis) |
| gering | bq-sb802 | price (chk:true) | 118 | nicht belegbar; frühere Listungen 155–165 €, 118 € wirkt niedrig | https://www.computerbase.de/2020-09/be-quiet-silent-base-802/ |
| mittel | checkAll | Regel fehlt | nur LGA1700-BIOS-Hinweis | Hinweis für AM5-600er-Boards (A620/B650/X670) + Ryzen 9000: BIOS mit AGESA 1.2.x nötig, ggf. Flash-BIOS-Button | https://flashmyboard.com/mb/MSI_B650_GAMING_PLUS_WIFI |
| gering | checkAll | Regel fehlt | – | Keine Prüfung der Stromstecker-Anzahl (3× 8-Pin-Karten wie 9070 XT Red Devil/TUF; RM850e hat 3× 8-Pin, davon 2 an einem Strang) | https://www.igorslab.de/corsair-rm850e-atx-v3-1-netzteil-im-test-wenn-der-grosse-bruder-zum-kannibalen-wird/ |
| gering | checkAll | Logik | `gpu.conn==="12V-2x6"&&!psu.atx3` | `atx3` dient als Ersatz für „nativer 12V-2x6-Anschluss“; nicht gleichbedeutend (ATX 3.0 hatte teils 12VHPWR, nicht jedes ATX-3-NT hat 12V-2x6) | – |
| gering | Katalog | fehlt | – | Ryzen 9 9950X3D2 (16C/32T, 192 MB L3, 200 W TDP, ab 779 €) | https://hardwaredealz.com/hardware/cpu/amd-ryzen-9-9950x3d2-dual-edition-boxed-ohne-kuehler/preisvergleich.33577 |
| gering | Katalog | fehlt | – | Core Ultra 5 250K Plus (ab 216 €) | https://hardwaredealz.com/hardware/cpu/intel-core-ulta-5-250k-plus/preisvergleich.33576 |
| gering | Katalog | fehlt | – | Radeon RX 9070 GRE 12 GB (ab 639 €) | https://hardwaredealz.com/hardware/gpu/amd-radeon-rx-9070-gre-12gb/gigabyte-radeon-rx-9070-gre-gaming-oc-12g/preisvergleich.33593 |
| gering | Katalog | fehlt | – | Ryzen 5 7500X3D (ab 221,90 €) | https://hardwaredealz.com/hardware/cpu/amd-ryzen-5-7500x3d-boxed/preisvergleich.31579 |

Anmerkung zu `m`: Die Werte sind auf 9950X = 100 normiert und aus einer einzigen Messreihe abgeleitet (Cinebench R20, hwcooling). Andere Anwendungen können ±5 Punkte abweichen. Die Richtung ist aber eindeutig: Achtkerner (auch X3D) und Sechskerner sind im Tool zu hoch angesetzt. Weil `value()` die CPU-Wertung mit `score^3` gewichtet, schiebt das im Profil „Kreativ & Workstation“ die Auswahl zu kleinen CPUs.

## Bestätigt

**Nutzerkonfiguration „mine“**
- r9-9900x: 12C/24T, Boost 5,6 GHz, AM5, iGPU, ohne Kühler. Preis 302,59 € (Tool 311, −3 %). https://hardwaredealz.com/hardware/cpu/amd-ryzen-9-9900x-boxed/preisvergleich.21772
- tr-pa120se: Höhe 155 mm (Galaxus); passt ins Silent Base 802 (max. 185 mm).
- msi-b650-gp: ATX, B650, 4 DIMM, 2× M.2 PCIe 4.0 (kein Gen5-M.2), WLAN 6E, PCIe-4.0-x16-Slot; offiziell Ryzen 9000/8000/7000. Flash-BIOS-Button laut Händler-Q&A vorhanden (https://www.digitec.ch/en/s1/questionandanswer/hello-is-there-a-button-on-the-card-to-flash-the-biosthanks-833310). Aktuelles BIOS 7E26v1O4 (09.09.2026). Preis: Angebote ca. 128–159 €, Tool 131 plausibel.
- gs-2x16-6000 (Patriot Viper Venom 32 GB DDR5-6000): Preis 520 € plausibel (s. o.).
- rx9070xt: günstigstes Modell 896,44 € (Tool 877, +2 %). Längen 288–360 mm; Tool-Wert 320 mm ist typisch. Alle Modelle passen ins SB802 (432 mm). https://hardwaredealz.com/hardware/gpu/amd-radeon-rx-9070-xt-16gb
- wd-sn7100-1: Händler 160–172 € (Tool 160).
- cor-rm850e: CP-9020296-EU, ATX 3.1, 1× 12V-2x6 (600 W), 3× PCIe 6+2-Pin, Geizhals-Tiefstpreis 105,46 € am 09.11.26 (Tool 105). https://www.igorslab.de/corsair-rm850e-atx-v3-1-netzteil-im-test-wenn-der-grosse-bruder-zum-kannibalen-wird/
- bq-sb802: GPU 432 mm, Kühler 185 mm, Front-Radiator bis 420 mm, Deckel 360 mm, ATX-Netzteil. Das stimmt.
- Netzteil-Rechnung: 162 + 304 + 75 = 541 W, × 1,3 = 703 W, auf 50 gerundet 750 W. Das entspricht AMDs Empfehlung von 750 W, das RM850e reicht mit Reserve. Auch OC-Karten mit ca. 340 W (577 W × 1,3 = 750 W) ändern daran nichts. Der Faktor 1,3 ist für ATX-3.1-Netzteile (Transienten in der Norm) konservativ und vertretbar.

**Preise innerhalb von ±10 % (hardwaredealz/Geizhals, 09.11.2026)**
- CPU: 8500G 128 € (135), 7600 175,30 € (172), 9600X 163,90 € (177, −7 %), 9700X 230 € tray / 253,90 € boxed (238), 9800X3D 378,90 € (398), 9850X3D 398,90 € (435, −8 %), 9950X 440,59 € tray / 489 € boxed (449), 9950X3D 552,43 € (575), 265K 309 € (313), 270K Plus 295,81 € (298), 285K 528,99 € (530).
- GPU: 9060 XT 8 GB 446,46 € (450), 5060 459 € (459), 5060 Ti 8 GB 514,90 € (514), 5060 Ti 16 GB 799 € (799), 5070 834,46 € (858), RX 9070 819 € (759, +8 %), 5070 Ti 1.299 € (1.279), 5080 1.861 € (1.889), 5090 7.731,76 € (7.800).
- RAM: Corsair Vengeance 64 GB DDR5-6000 CL30: PCGH 1.189 € im Aug. 2026 (Tool 1.190).
- SSD: Kingston NV3 1 TB: Galaxus 124,90 € (Tool 125); PCGH sah im Aug./Sep. 145 €.
- Laptops: G14 2026 3.299 € (Notebookcheck/Galaxus); Aorus Master 16 (9955HX3D/RTX 5080/32 GB/2 TB) laut Notebookcheck-Deal Nov. 2026 3.299–3.669 €.

**Specs und Existenz**
- Es gibt den Ryzen 7 9850X3D (Boost 5,6 GHz vs. 5,2; Spiele +2–5 % vs. 9800X3D, damit ist g = 104 plausibel), den Core Ultra 7 270K Plus (24C/24T, 8P+16E), das Zephyrus G14 2026 mit Core Ultra 9 386H (16C/16T, RTX 5070 Ti Laptop 12 GB, 115 W, 14" 2880×1800 OLED 120 Hz, 1,568 kg) und das MacBook Pro 16 M5 Max (18-Core-CPU).
- Kerne, Threads, Boost und Sockel aller übrigen CPUs stimmen mit dem bekannten Stand überein (14400F 10C/16T 4,7 GHz; 225 10C/10T 4,9 GHz; 245K 14C 5,2; 265K 20C 5,5; 285K 24C 5,7; 9600X 5,4; 9700X 5,5; 9950X/X3D 5,7; 7800X3D 5,0). Bestätigt wurden außerdem: 245KF hat 14C/14T und 5,2 GHz (hardwaredealz), 9900X hat TDP 120 W.
- GPU-Gaming-Index: 5080/9070 XT = 1,14 (Tests: +10–25 %, 4K ca. +16 %); 5090 = 150 (4K ca. +48–50 % vs. 5080, ca. +60–70 % vs. 9070 XT); 9070 ≈ 9070 XT −9 %; 5060 vs. 9060 XT 8 GB fast gleich (CB). Alles plausibel.
- RTX 50 Super: nicht erschienen (laut Berichten auf Eis gelegt). Intel Nova Lake (Core Ultra 400): Launch frühestens Ende 2026/Q1 2027, nicht im Handel. Dass beides fehlt, ist richtig.
- Logik: Prüfungen von Sockel, RAM-Typ/-Slots/-Kapazität, Formfaktor, GPU-Länge, Kühlerhöhe, Radiatorgröße, SFX/ATX, Boxed-Kühler, iGPU-Pflicht ohne GPU und PCIe-5.0-SSD auf Gen4-Board sind korrekt umgesetzt. In der Nutzerkonfiguration löst nichts davon einen Fehler aus.

## Nicht prüfbar

- **Geizhals, heise-Preisvergleich, Caseking, MSI.com und Notebookcheck-Vergleichsseiten** lieferten HTTP 403. Für Teile, die hardwaredealz nicht führt (Mainboards, Kühler, Netzteile außer RM850e, Gehäuse), gibt es deshalb keine tagesaktuellen Preise.
- **Mainboard-Preise** (alle außer der MSI-Spanne 128–159 €), **Kühler** (PA120 SE: Marktplatz-Angebote ca. 28–50 €, nicht datiert; Tool 34 nicht widerlegt), **Netzteile** (Pure Power 12, SF750/SF1000, Straight Power 12, Vertex GX-1200, HX1500i, MWE Gold) und **Gehäuse** (inkl. Silent Base 802 und Fractal North): nicht belegt.
- **Arc B570/B580-Preise:** Die Quellen widersprechen sich. gpuprix zeigt am 22.10.2026 einen Tiefstpreis von 404 € für die B580, eine andere Version derselben Seite 298 €, tech-insider nennt 310 € im September. Damit lässt sich nicht entscheiden, ob 320 € richtig ist.
- **RAM:** 16-GB-Kingston-, 48-GB-Corsair-, 96-GB-G.Skill-, 128-GB-Crucial- und G.Skill-6400-Kits sind nicht einzeln geprüft. Die Größenordnung passt zur Speicherkrise (32 GB DDR5-6000 CL30 ca. 475–540 €, 64 GB ca. 1.036–1.189 €).
- **SSD:** 990 EVO Plus 1/2 TB, 990 Pro 4 TB, 9100 Pro 4 TB und SN7100 2 TB haben keine aktuellen Preise. Die Septemberwerte von PCGH sind oben genannt.
- **RAM-Höhe vs. Kühler:** Die Modulhöhe der Viper Venom und die RAM-Freiheit des PA120 SE ließen sich nicht belegen. Bei diesem Kühlertyp lässt sich der Frontlüfter meist versetzen, das Tool modelliert es aber gar nicht.
- **Mainboard-Specs der übrigen Boards** (M.2-Anzahl, maxRam, z. B. ob B650 per BIOS inzwischen 256 GB kann): nicht geprüft.
- **Laptop-Multi-Core (`m`)** für 386H, 9955HX3D, 275HX/290HX Plus und M5 Max: keine vergleichbare Messreihe gefunden. Gewicht des Aorus Master 16 AMD und Watt-Angaben: nicht belegt.
- **Netzteil-Empfehlungen (`rec`)** von NVIDIA für 5050/5060/5060 Ti und **`power`** der 9060 XT 16 GB (Tool 180 W; AMD-Angabe vermutlich 160 W, nicht belegt): nicht geprüft.
- **Exakter Boost des 270K Plus** (Tool 5,5 GHz): nicht belegt.

## Umsetzung (9.11.2026)

Alle Punkte der Korrekturliste wurden in `pc-konfigurator/index.html` übernommen:
- Preise korrigiert (14400F, 8600G, 7800X3D, 245K/KF, 265KF, 225, RTX 5050, RX 9060 XT 16 GB, 9900X 303 €, RX 9070 XT 896 €, Lexar NM790, 9100 Pro 2 TB, 16-GB-Kit). Silent Base 802 auf 155 € gesetzt und nicht mehr als geprüft markiert.
- Multi-Core-Index `m` aller genannten CPUs und Gaming-Index der Arc B580/B570 und RTX 5050 korrigiert.
- Laptops: G14 32 GB, Leistungswerte 63/72/80, Scar 18 (2026) mit Core Ultra 9 290HX Plus und 4K-Mini-LED zu 5.299 € (nicht als geprüft markiert), MacBook Pro M5 Max mit 32-Kern-GPU zu 4.163 €.
- Neu aufgenommen: Ryzen 9 9950X3D2, Ryzen 5 7500X3D, Core Ultra 5 250K Plus, RX 9070 GRE.
- Neue Regel: Hinweis auf BIOS mit AGESA 1.2 bei Ryzen 9000 auf A620/B650. SN7100 als „SanDisk Optimus GX 7100 (ex WD Black SN7100)“ benannt.
- Nicht umgesetzt: Prüfung der Zahl der GPU-Stromstecker (Netzteile im Katalog haben genug Anschlüsse), MacBook Pro „PROLED XDR“ (zu neu, kein deutscher Preis).

## Plausibilitäts-Audit der automatischen Zusammenstellung (9.11.2026)

108 Regler-Kombinationen (Gaming/Kreativ 0/50/100 × Full HD/WQHD/4K × 60/144 FPS × ohne Budget/1.500/2.500 € × ITX/ATX) automatisch durchgespielt. Gefundene und behobene Fehler:
- ITX-Regler ergab ATX-Gehäuse, weil der Kühler (360er-AiO) vor dem Gehäuse gewählt wurde. Reihenfolge jetzt: Gehäuse vor Kühler.
- Budget wurde ignoriert, wenn keine Grafikkarte ins Teilbudget passte (Folge: RTX 5090 bei 1.500 € Budget). Jetzt wird dann die günstigste passende Karte genommen.
- Prozessor ohne Rücksicht auf Plattformkosten (z. B. Z890-ITX-Board für 390 €) und ohne Blick auf die Grafikkarte (z. B. 9850X3D mit Arc B580, 7500X3D mit RTX 5090). Jetzt: günstigster Prozessor inkl. Mainboard, der die Ziel-Bildrate bzw. die Grafikkarte mit 30 % Reserve bedient; zweiter Durchgang nach der GPU-Wahl.
- Ein zuerst gewählter, schwächerer Prozessor blockierte über die Flaschenhals-Warnung stärkere Grafikkarten. GPU-Wahl ignoriert jetzt den automatisch gewählten Prozessor.
- Ab WQHD werden Karten mit mindestens 12 GB, in 4K mit 16 GB Grafikspeicher bevorzugt; Kreativ-Schwerpunkt bevorzugt NVIDIA (CUDA).
- Core Ultra (LGA1851) bekommt DDR5-6400, Ryzen DDR5-6000 CL30. APUs (8500G/8600G) werden mit dedizierter Grafikkarte nicht mehr gewählt. LGA1700 erhält einen kleinen Malus (kein Aufrüstpfad).
- Ergebnis: keine Konflikte mehr; Warnungen nur noch bei RTX 5090 in ITX-Gehäusen (größtes SFX-Netzteil 1.000 W statt empfohlener 1.050–1.200 W), was korrekt angezeigt wird.
