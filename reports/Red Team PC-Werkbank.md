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

## Red Team: Premium-Teile (9.11.2026)

Geprüft: die neu aufgenommenen ids `msi-x870e-carbon`, `asus-x870e-hero`, `gs-2x16-6000c28`, `ar-lf3-420`, `sn8100-2`, `cor-rm1000x`, `bq-dp14-1000` und `fd-define7`, die Dämmstufe von `bq-sb802`/`fd-define7` sowie `betterOptions`/`betterMetric`.
Methode: Live-Daten von hardwaredealz.com (Geizhals-Daten, `updated_at` 09.11.2026 14:36 Uhr) für Mainboards, Netzteile und RAM-Vergleichskits. Herstellerseite von Fractal Design. Geizhals-, heise- und Galaxus-Daten nur über Such-Snippets, weil direkte Abrufe HTTP 403 bzw. 503 lieferten. `betterOptions` wurde in Node mit dem unveränderten Skript der Datei für 17 Szenarien durchgerechnet.

### Zusammenfassung

- **Echter Logikfehler durch neue Daten:** `fd-define7` hat `rads:[240,280,360]`. Laut Fractal Design nimmt das Define 7 oben aber einen 420-mm-Radiator auf. Das Tool meldet deshalb bei `ar-lf3-420` im Define 7 einen falschen **Konflikt** („nimmt Radiatoren mit 240 / 280 / 360 mm auf, nicht 420 mm“), und `betterOptions` blendet den 420er als Upgrade aus.
- **Preise:** Dark Power 14 1000 W ist 14 % zu teuer eingetragen (227,89 € statt 260 €). Für die SN8100 2 TB liegt das günstigste Live-Angebot bei 400 € (Tool 359 €, −10 %; Quelle ist ein Marktplatz-Vergleich, deshalb mittlere Sicherheit). Carbon (443,37 €), Hero (525,22 €), RM1000x (171,19 €) und LF III Pro 420 (77,05 €) liegen innerhalb von 10 %.
- **Das CL28-Premium-Kit bringt gegenüber dem bestehenden Kit keine Latenz.** Hinter `gs-2x16-6000` steht das Patriot Viper Venom VVR532G60C28AK. Es hat **CL28** (Tool: `cl:30`) und kostet 524,90 €. Das G.Skill-CL28-Kit für 886 € bringt also bei gleichem Takt und gleicher CL nur RGB. Der Preis von 886 € ist widersprüchlich belegt und deshalb nicht prüfbar.
- **`betterOptions` ist bei den geprüften Regeln fachlich korrekt:** Gen5-SSDs zählen auf Boards ohne PCIe-5.0-M.2 nicht als Upgrade, RAM über DDR5-6000 zählt bei Ryzen nicht als schneller, und bei Intel wird DDR5-6400 vorgeschlagen. Schwächen: Bei Ryzen wird ausgehend von DDR5-6400 CL32 das 6000-CL30-Kit mit dem widersprüchlichen Text „langsamer: DDR5-6000“ als „Besser“ angezeigt. Board-Upgrades richten sich fast nur nach dem Chipsatzrang (B650 → X870E Carbon für +327 € ohne Bedarfsprüfung). Netzteil-Upgrades gelten schon bei „+150 W Reserve“, auch wenn die Reserve gar nicht gebraucht wird.
- **Dämmstufe:** Silent Base 802 und Define 7 sind beide ab Werk mit Dämmmatten und geschlossener Front ausgestattet. Die gleiche Einstufung `silent:3` ist sinnvoll.

### Befunde

| Schwere | id | Feld | Wert im Tool | korrekter Wert | Quelle-URL |
|---|---|---|---|---|---|
| hoch | fd-define7 | rads | [240,280,360] | [240,280,360,420] (Deckel bis 420 mm, Front bis 360 mm, Boden bis 280 mm) | https://www.fractal-design.com/de/products/cases/define/define-7/schwarz/ |
| mittel | bq-dp14-1000 | price | 260 | 227,89 (−12 % bzw. Tool +14 %) | https://www.hardwaredealz.com/hardware/psu/be-quiet!-dark-power-14-1000w-atx-31/preisvergleich.31261 |
| mittel | gs-2x16-6000 (Bestandsteil, beeinflusst Premium-Vergleich) | cl | 30 | 28 (VVR532G60C28AK, CL28-40-40-110, 524,90 €) | https://hardwaredealz.com/hardware/ram/patriot-viper-venom-udimm-32gb-kit/preisvergleich.66347 |
| mittel | sn8100-2 | price | 359 | ca. 400 (günstigstes von 11 Neuangeboten, live abgerufen; Geizhals-Snippet undatiert 398,90 €) | https://dropreference.com/de/product/0619659220860 |
| gering | fd-define7 | gpu | 440 | 470 (Open Layout), laut Datenblatt 445 mm mit Frontlüfter. 440 ist konservativ und nicht falsch | https://www.fractal-design.com/de/products/cases/define/define-7/schwarz/ |
| gering | fd-define7 | form | ATX | E-ATX/ATX/mATX/ITX (das Tool kennt kein E-ATX, ATX ist korrekt) | https://www.fractal-design.com/de/products/cases/define/define-7/schwarz/ |
| gering | gs-2x16-6000c28 | Nutzen | CL28 als Premium | gleiche CL wie das Patriot-Kit für 524,90 €; ähnliche CL28-Kits: Klevv Fit V 558,78–606,36 € | https://www.hardwaredealz.com/hardware/ram |
| gering | Logik ram | benefitText | „Besser“ + „langsamer: DDR5-6000“ (Ryzen, von 6400 CL32) | Text für AMD auf „gleich schnell, niedrigere Latenz“ ändern oder den Vorschlag unterdrücken | Simulation (Node) |
| gering | Logik board/psu | betterMetric | Chipsatzrang ×100 bzw. Watt/2 ohne Bedarf | Board: Bedarf (M.2, USB4) gegen Aufpreis abwägen. Netzteil: Watt nur zählen, wenn unter `rec`+Reserve | Simulation (Node) |

### Bestätigt

- **msi-x870e-carbon:** X870E, ATX, 4 DIMM, max. 256 GB, 4× M.2 (davon 2× PCIe 5.0 x4, 2× PCIe 4.0 x4), WLAN (WiFi 7). Preis ab 443,37 € (Tool 458, +3 %). https://www.hardwaredealz.com/hardware/mainboard/msi-mpg-x870e-carbon-wifi/preisvergleich.22469
- **asus-x870e-hero:** X870E, ATX, 4 DIMM, max. 256 GB, 5× M.2 (3× PCIe 5.0, 2× PCIe 4.0), WiFi 7. Preis ab 525,22 € (Tool 560, +7 %). https://www.hardwaredealz.com/hardware/mainboard/asus-rog-crosshair-x870e-hero/preisvergleich.22457
- **cor-rm1000x:** 1000 W, 80 PLUS Gold (Cybenetics sogar ETA-Platinum), ATX 3.1, 12V-2x6 (600 W), ATX. Preis ab 171,19 € (Tool 165, −4 %). https://www.hardwaredealz.com/hardware/psu/corsair-rmx-series-2024-rm1000x-1000w-atx-31/preisvergleich.22070
- **bq-dp14-1000:** 1000 W, 80 PLUS Titanium / Cybenetics ETA-Titanium, ATX 3.1, 12V-2x6 (600 W), ATX. Nur der Preis weicht ab (siehe oben). Quelle wie oben.
- **ar-lf3-420:** rad 420, Preis ab 77,05 € (Geizhals 23.10.2026, Tool 82, +6 %). https://geizhals.de/arctic-liquid-freezer-iii-pro-420-acfre00181a-a3525739.html. `cap:330` (+10 % gegenüber dem 360er mit 300) ist als Schätzung plausibel, weil Tests den 420er nur wenige Kelvin vor dem 360er sehen.
- **sn8100-2:** PCIe 5.0, 14.900 MB/s lesen. https://www.testsieger.de/ssd-festplatten/6CQBCS1EZP632-wd-black-sn8100-2-tb-pcie-5-0x4-nvme-m-2-ssd-lesegeschwindigkeiten-von-bis-zu-14-900-mb-s-schreibgeschwindigkeiten-von-bis-zu-14-000-mb-s-tlc-3d-cba-nand.html
- **gs-2x16-6000c28:** cap 32, 2 Module, DDR5-6000, CL28 (CL28-36-36-96). Das geht aus dem Produkttitel bei Geizhals hervor (F5-6000J2836G16GX2-TZ5NR).
- **fd-define7:** Kühlerhöhe 185 mm, ATX-Netzteil (max. 250 mm), gedämmt („industrielle Hochdichte-Schalldämmung“).
- **silent-Einstufung:** Silent Base 802 (Dämmmatten, wechselbare Front/Deckel geschlossen/Mesh) und Define 7 (Dämmung, geschlossene Front, ModuVent-Deckel) sind gleichwertig gedämmte Silent-Gehäuse. `silent:3` für beide passt. Bei gleichem Rang ergibt `betterMetric` (Differenz nur 0,08 durch die GPU-Länge) korrekt kein gegenseitiges Upgrade; vom Fractal North aus wird das günstigere SB802 vorgeschlagen.
- **betterOptions-Stichproben (Node-Simulation, Nutzerkonfiguration mit 9900X/B650 Gaming Plus):**
  - SSD auf dem Gen4-Board: nur Lexar 4 TB (Kapazität), kein Gen5-Laufwerk. Auf dem X870E Carbon erscheint die SN8100 als „PCIe 5.0“-Upgrade.
  - RAM AMD (6000 CL30): nur Kapazitäts-Upgrades. DDR5-6400 wird nicht als schneller gewertet.
  - RAM Intel (Z890): DDR5-6400 wird vorgeschlagen.
  - Netzteil: Upgrades nur mit gleicher oder höherer Wattzahl und passendem Formfaktor.
  - Wegen der Pareto-Logik erscheint das CL28-Kit für 886 € nie, weil das 48-GB-Kit (880 €) günstiger und „besser“ ist.

### Nicht prüfbar

- **gs-2x16-6000c28, Preis (886 €):** Die Quellen widersprechen sich. heise nennt für die weiße Variante 885–888 €, ein Geizhals.at-Snippet für dieselbe weiße Variante „ab 549,90 €“, Newegg ca. 699 US-$ für die schwarze. Für die schwarze TZ5NR gibt es keinen datierten deutschen Preis.
- **ar-lf3-420, `cap` 330 W:** Das ist eine Schätzung ohne Herstellerangabe. Plausibel, aber nicht belegbar.
- **fd-define7, Preis (175 €):** Kein aktueller deutscher Preis gefunden. Die UVP lag 2020 bei 164,99/174,99 €, Galaxus nennt undatiert 170,96 € (Solid weiß). https://www.computerbase.de/2020-02/fractal-design-define-7-test/
- **bq-sb802, Preis (155 €):** Ein undatiertes Geizhals-Snippet nennt 139,89 € (−10 %). Ohne Datum gibt es keine Korrektur.
- **sn8100-2, Preis:** Der Wert von 400 € stammt von einem Marktplatz-Vergleich (dropreference), nicht von Geizhals. Die Datierung des Geizhals-Werts (398,90 €) ist unklar.

### Umsetzung Premium-Teile (9.11.2026)
Alle Punkte übernommen: Define 7 mit 420er-Radiator und 445 mm GPU-Länge; Dark Power 14 auf 228 €, SN8100 2 TB auf 400 €; X870E Carbon 443 €, Crosshair X870E Hero 525 €, RM1000x 171 €, Liquid Freezer III Pro 420 77 € (als geprüft markiert); Patriot Viper Venom als CL28-Kit (VVR532G60C28AK) für 525 € korrigiert, auch in der Einkaufsliste. Logik: RAM-Upgrades dürfen nicht langsamer sein (bei Ryzen bis DDR5-6000 gerechnet), Mainboard-Wertung stärker nach Ausstattung (M.2, PCIe 5.0) statt Chipsatz, Netzteil-Mehrleistung zählt nur, wenn sie gebraucht wird.

## Red Team: Gesamtprüfung (9.11.2026, Runde 3)

Geprüfte Datei: `pc-werkbank.html` (Kopie in `scratchpad/redteam2/`, Original unverändert).
Methode: Live-Preise von hardwaredealz.com (Geizhals-Daten, `updated_at` 09.11.2026 15:35 Uhr) und vom PCGH-Preisvergleich (Geizhals-Basis, abgerufen 09.11.2026 ca. 16–17 Uhr; „ab“-Preis ohne Versand). Geizhals, heise, idealo und billiger.de lieferten HTTP 403. Alle 7 Vorlagen wurden mit Playwright/Chromium und dem unveränderten Skript durchgerechnet, danach ein zweites Mal mit korrigierten Preisen (Kopie `pw2.html`). Wo es nur 1–4 Angebote gibt, ist der Preis als unsicher markiert.

### Zusammenfassung

- **Die Nutzerkonfiguration „mine“ ist kompatibel, kostet aber real ca. 3.140 € statt 2.934 € (+7 %).** Zwei Preise sind deutlich zu niedrig, beide als `chk:true` markiert: SanDisk Optimus GX 7100 2 TB 445,99 € statt 280 € (+59 %) und MSI B850 Tomahawk Max WiFi 264,93 € statt 225 € (+18 %).
- **Die Montage „420er-Radiator oben“ geht im Silent Base 802 nicht.** Laut be quiet! nimmt der Deckel höchstens 360 mm auf, 420 mm passen nur in die Front. Front-Montage ist möglich: Der Radiator misst 458 × 138 × 38 mm, mit Lüftern ca. 63 mm. Dann bleiben ca. 369 mm für die GPU, das reicht für jede RTX 5070 Ti. Der HDD-Käfig muss allerdings raus, denn mit Käfig sind nur 287 mm frei, und praktisch alle RTX 5070 Ti sind länger als 287 mm. Das Tool unterscheidet weder die Radiator-Position noch den HDD-Käfig.
- **Das restliche „mine“ ist bestätigt.** B850 unterstützt Ryzen 9000 ab Werk, eine Flash-BIOS-Taste ist trotzdem vorhanden. Das RM850e hat einen nativen 12V-2x6 (600 W). Die geschätzte Last liegt bei 537 W, empfohlen sind 750 W, also passen 850 W. Das Patriot-RAM (43 mm hoch) kollidiert mit der AiO nicht. DDR5-6000 CL28 ist für AM5 optimal. Ein Datenfehler: Das Tomahawk Max hat 4 M.2-Plätze (2× PCIe 5.0), nicht 3.
- **Speicherkrise:** SSD- und RAM-Preise sind teils stark gestiegen. Betroffen sind SN7100/GX 7100 1 TB (+47 %) und 2 TB (+59 %), Crucial Pro 32 GB DDR5-5600 (+31 %), Trident Z5 RGB 6400 (+22 %) und Lexar NM790 4 TB (+21 %). Die Liste ist nicht vollständig.
- **Die RX 9060 XT 8 GB steht mit 345 € im Tool, real kostet sie 446,46 € (−23 %, `chk:true`).** Dadurch empfiehlt das Tool in der Vorlage „Einstieg Full HD“ fälschlich die 9060 XT als „besser und 114 € günstiger“ als die RTX 5060.
- **Nicht mehr erhältliche Teile im Katalog** (0–2 Angebote in DE): MSI B650 Gaming Plus WiFi (wird in „fhd“ automatisch gewählt!), Cooler Master NR200P V2 (Vorlage „itx“), MWE Gold 650 V2, O11 Dynamic EVO ohne RGB, MSI PRO B760M-A WiFi, Gigabyte B760 Gaming X AX und MSI PRO B860M-A WiFi.
- Die Vorlagen ergeben keine Konflikte, wirken aber teils unausgewogen. „itx“ kombiniert ein 298-€-Board mit einer 172-€-CPU. „4k“ wählt ein 1500-W-Netzteil, weil der Ruhe-Aufschlag das 1200-W-Gerät um 10 W ausschließt. „ws“ setzt eine 12-GB-Karte in eine 96-GB-Workstation.

### Befunde

| Schwere | id | Feld | Wert im Tool | korrekter Wert | Quelle |
|---|---|---|---|---|---|
| hoch | wd-sn7100-2 | price (chk) | 280 | 445,99 (35 Angebote) | https://preisvergleich.pcgameshardware.de/sandisk-optimus-gx-7100-2tb-sdsp71200tan-000e0-a3700970.html |
| hoch | wd-sn7100-1 | price (chk) | 160 | 235,99 | https://preisvergleich.pcgameshardware.de/sandisk-optimus-gx-7100-1tb-sdsp71100tan-000e0-a3700968.html |
| hoch | msi-b850-tomahawk | price (chk) | 225 | 264,93 | https://www.hardwaredealz.com/hardware/mainboard/msi-mag-b850-tomahawk-max-wifi/preisvergleich.24546 |
| hoch | rx9060xt-8 | price (chk) | 345 | 446,46 | https://www.hardwaredealz.com/hardware/gpu/amd-radeon-rx-9060-xt-8gb |
| hoch | bq-sb802 / Logik | rads | [240,280,360,420] ohne Position | Deckel max. 360, Front max. 420; GPU 287 mm mit HDD-Käfig, 432 ohne | https://www.bequiet.com/de/case/2049 |
| mittel | msi-b850-tomahawk | m2 | 3 | 4 (2× PCIe 5.0 x4, 1× 4.0 x4, 1× 4.0 x2) | https://www.msi.com/Motherboard/MAG-B850-TOMAHAWK-MAX-WIFI/Specification |
| mittel | msi-b650-gp | Verfügbarkeit (chk) | 131 | 0 Angebote in DE, ausgelaufen | https://preisvergleich.pcgameshardware.de/msi-b650-gaming-plus-wifi-7e26-001r-a2975546.html |
| mittel | cm-nr200p | Verfügbarkeit | 110 | NR200P V2 schwarz/weiß: 0 Angebote | https://preisvergleich.pcgameshardware.de/cooler-master-masterbox-nr200p-v2-nr200pv2-kcnn-s00-a3139399.html |
| mittel | c-2x16-5600 | price | 480 | 628,99 | https://preisvergleich.pcgameshardware.de/crucial-pro-dimm-kit-32gb-cp2k16g56c46u5-a2950984.html |
| mittel | gs-2x16-6400 | price | 600 | 729,00 | https://preisvergleich.pcgameshardware.de/?fs=G.Skill+Trident+Z5+RGB+32GB+DDR5-6400+CL32 (F5-6400J3239G16GX2-TZ5RK) |
| mittel | lexar-nm790-4 | price (chk) | 445 | 538,95 | https://preisvergleich.pcgameshardware.de/lexar-nm790-4tb-lnm790x004t-rnnn-a2980705.html |
| mittel | gb-b850-elite | price (chk) | 232 | 188,80 | https://www.hardwaredealz.com/hardware/mainboard/gigabyte-b850-aorus-elite-wifi7/preisvergleich.24371 |
| mittel | asus-z890e | price | 480 | 329,00 | https://www.hardwaredealz.com/hardware/mainboard/asus-rog-strix-z890-e-gaming-wifi/preisvergleich.28607 |
| mittel | arc-b580 | price | 320 | 396,90 (nur 4 Angebote) | https://www.hardwaredealz.com/hardware/gpu/intel-arc-b580-graphics-12gb |
| mittel | ar-lf3-240 | price | 85 | 57,44 | https://preisvergleich.pcgameshardware.de/arctic-liquid-freezer-iii-pro-240-acfre00178a-a3501832.html |
| mittel | ar-lf3-360 | price (chk) | 80 | 70,25 | https://preisvergleich.pcgameshardware.de/arctic-liquid-freezer-iii-pro-360-acfre00180a-a3455404.html |
| mittel | bq-pp12-750 | price (chk) | 80 | 96,02 | https://preisvergleich.pcgameshardware.de/be-quiet-pure-power-12-750w-atx-3-1-bp003eu-a3346945.html |
| mittel | bq-pp12-650 | price | 85 | 69,88 | https://preisvergleich.pcgameshardware.de/be-quiet-pure-power-12-650w-atx-3-1-bp002eu-a3346957.html |
| mittel | bq-pp12-550 | price | 75 | 64,88 | https://preisvergleich.pcgameshardware.de/be-quiet-pure-power-12-550w-atx-3-1-bp001eu-a3347083.html |
| mittel | cor-hx1500i | price | 330 | 278,14 (HX1500i 2025) | https://www.hardwaredealz.com/hardware/psu/corsair-hxi-series-2025-hx1500i-1500w-atx-31/preisvergleich.26876 |
| mittel | ll-a3 | price | 85 | 68,70 | https://www.hardwaredealz.com/hardware/case/lian-li-dan-cases-a3-matx-black/preisvergleich.14999 |
| mittel | bq-drp5 | price | 95 | 79,98 | https://preisvergleich.pcgameshardware.de/be-quiet-dark-rock-pro-5-bk036-a3038718.html |
| mittel | Logik betterOptions (mine) | board | X870 Tomahawk „+1 M.2-Platz“ (+75 €) | kein M.2-Vorteil (beide 4 M.2), nur USB4; Vorschlag fachlich schwach | MSI-Datenblatt (s. o.) |
| mittel | Logik betterOptions (mine) | ssd | SN8100 als Upgrade „+120 €“ | mit Realpreisen ist die SN8100 2 TB (419,90 €) **26 € günstiger** als die GX 7100 2 TB | https://preisvergleich.pcgameshardware.de/?fs=WD_BLACK+SN8100+2TB |
| gering | ll-lancool217 | price (chk) | 115 | 99,90 | https://preisvergleich.pcgameshardware.de/lian-li-lancool-217-lan217x-a3483902.html |
| gering | cor-sf1000 | price | 220 | 193,90 | https://www.hardwaredealz.com/hardware/psu/corsair-sf-series-platinum-sf1000-1000w-sfx,-atx-31/preisvergleich.7760 |
| gering | ar-f36 | price | 30 | 22,21 | https://preisvergleich.pcgameshardware.de/arctic-freezer-36-acfre00121a-a3144218.html |
| gering | nc-l12s | price | 60 | 66,90 | https://preisvergleich.pcgameshardware.de/noctua-nh-l12s-a1698589.html |
| gering | s990evo-1 | price | 170 | 189,00 | https://preisvergleich.pcgameshardware.de/samsung-ssd-990-evo-plus-1tb-mz-v9s1t0bw-mz-v9s1t0b-a3316828.html |
| gering | asr-a620m | price | 80 | 94,90 (2 Angebote) | https://preisvergleich.pcgameshardware.de/asrock-a620m-hdv-m-2-a2924859.html |
| gering | cm-mwe-650 | price/Verfügbarkeit | 70 | 87,50 (1 Angebot), ausgelaufen | https://preisvergleich.pcgameshardware.de/cooler-master-mwe-gold-v2-650w-atx-2-52-mpe-6501-acaag-a2387406.html |
| gering | asus-b860-tuf | price | 190 | 170,11 | https://www.hardwaredealz.com/hardware/mainboard/asus-tuf-gaming-b860-plus-wifi/preisvergleich.28612 |
| gering | asus-z890i | price | 390 | 464,13 (2 Angebote) | https://www.hardwaredealz.com/hardware/mainboard/asus-rog-strix-z890-i-gaming-wifi/preisvergleich.28610 |
| gering | msi-b860m-a | price | 145 | 227,37 (2 Angebote) → Ersatz MSI B860M Gaming Plus WiFi 159,57 | https://www.hardwaredealz.com/hardware/mainboard/msi-b860m-gaming-plus-wifi/preisvergleich.28673 |
| gering | msi-b760m-a / gb-b760-gx | price | 125 / 150 | 200,24 / 421,37 (je 1 Angebot) – auslaufend | https://preisvergleich.pcgameshardware.de/msi-pro-b760m-a-wifi-7d99-007r-a2952732.html |
| gering | montech-903 | price | 95 | 54,17 (nur 4 Angebote, unsicher) | https://preisvergleich.pcgameshardware.de/montech-air-903-max-black-a2974784.html |
| gering | fd-terra | price | 190 | 169,90 (4 Angebote) | https://preisvergleich.pcgameshardware.de/fractal-design-terra-fd-c-ter1n-02-a2957135.html |
| gering | ll-o11-evo | name | O11 Dynamic EVO | Nicht-RGB-Version ohne Angebote; EVO RGB 166,95 € | https://preisvergleich.pcgameshardware.de/lian-li-o11-dynamic-evo-rgb-o11dergbx-a3094518.html |
| gering | c-2x64-5600 | price | 2700 | 3499 (1 Angebot, unsicher) | https://preisvergleich.pcgameshardware.de/crucial-pro-dimm-kit-128gb-cp2k64g56c46u5-a3423953.html |
| gering | r7-9800x3d | price (chk) | 398 | 361,87 boxed (−9 %, Grenzfall) | https://www.hardwaredealz.com/hardware/cpu/amd-ryzen-7-9800x3d-boxed/preisvergleich.23412 |
| mittel | scar18 (Laptop) | price | 5299 (ES) | DE ab 6.598 € (G835LXG, 64 GB/4 TB) | https://preisvergleich.pcgameshardware.de/?fs=ROG+Strix+Scar+18+2026+RTX+5090 |
| gering | Logik pickPsu | want | rec × (1 + quiet × 0,2) | „4k“: 1210 W gefordert → HX1500i; Seasonic GX-1200 fällt um 10 W heraus. Aufschlag deckeln (z. B. max. +100 W) | Simulation |
| gering | Logik checkAll | fehlt | – | Hinweis „HDD-Käfig entfernen“, wenn gpu.len > 287 im SB802 (allgemein: Feld `gpuCage`) | be quiet! (s. o.) |

### Fehlende Teile (belegte Preise, 09.11.2026)

- **cpu `r9-9900x3d`**: Ryzen 9 9900X3D, 12C/24T, 5,5 GHz, 456,51 €. https://www.hardwaredealz.com/hardware/cpu/amd-ryzen-9-9900x3d-boxed/preisvergleich.25511
- **board `msi-b850-gp6e`**: MSI B850 Gaming Plus WiFi6E (ATX, 4 DIMM, 2× M.2, davon 1× Gen5), 151,89 €. Das ist der Nachfolger des ausgelaufenen B650 Gaming Plus. https://www.hardwaredealz.com/hardware/mainboard/msi-b850-gaming-plus-wifi6e/preisvergleich.29505
- **board `asr-b850i-lightning`**: ASRock B850I Lightning WiFi (ITX, 2 DIMM, 2× M.2, davon 1× Gen5, WiFi 6E), 186,90 €. Spart in „itx“ 111 € gegenüber dem ROG B850-I. https://www.hardwaredealz.com/hardware/mainboard/asrock-phantom-gaming-b850i-lightning-wifi/preisvergleich.24689
- **board `gb-x870e-elite`**: Gigabyte X870E Aorus Elite WiFi7 (4 DIMM, 4× M.2, davon 3× Gen5, USB4), 270,44 €. Günstiger als das X870 Tomahawk und besser ausgestattet. https://www.hardwaredealz.com/hardware/mainboard/gigabyte-x870e-aorus-elite-wifi7/preisvergleich.21933
- **psu `sea-coregx-850`**: Seasonic Core GX-850 (ATX 3.1, 12V-2x6 600 W, Gold/Cybenetics Platinum, 7 J. Garantie), 99,90 €. https://www.hardwaredealz.com/hardware/psu/seasonic-core-gx-atx-3-2024-gx-850-850w-atx-31/preisvergleich.24234
- **cooler `bq-drp6`**: be quiet! Dark Rock Pro 6, 169 mm, laut Hersteller 300 W TDP, 84,42 €. Nachfolger des DRP5. https://www.hardwaredealz.com/hardware/cooler/be-quiet!-dark-rock-pro-6/preisvergleich.34291 und https://www.bequiet.com/de/cpucooler/7322
- **case `bq-pb501`**: be quiet! Pure Base 501 (gedämmt; GPU 368 mm, Kühler 178 mm, Front-Radiator bis 360, Deckel bis 240), 59,75 €. https://www.hardwaredealz.com/hardware/case/be-quiet!-pure-base-501-black,-schallgedämmt/preisvergleich.23458 und https://www.igorslab.de/bequiet-pure-base-501-im-test/1/
- **case `ll-a3`-Ersatz für ITX `cm-nr200p`**: nicht ergänzt, weil kein gleichwertiges, lieferbares ITX-Gehäuse mit belegten Maßen geprüft werden konnte. Das NR200P V2 ist ausgelaufen.
- **Nicht erschienen, also bewusst nicht aufgenommen:** RTX 50 Super, Radeon RX 9080, Arc B770, Core Ultra 400 (Nova Lake). Alle vier sind weder bei hardwaredealz noch im PCGH-Preisvergleich gelistet.

### Kombinationen (Playwright, alle 7 Vorlagen)

| Vorlage | Ergebnis Tool | Bewertung |
|---|---|---|
| mine | 2.934 € (real ca. 3.140 €), keine Konflikte | Kompatibel. Fachlich: Der 420er gehört in die Front, der HDD-Käfig muss raus. Ausgewogen für 4K/Kreativ (9900X ↔ 5070 Ti). Netzteil passend (537 W Last / 850 W). |
| office | 8600G, A620M, 16 GB, NV3, A3, Boxed, MWE 650 | Sinnvoll. Das 650-W-Netzteil ist bei 163 W überdimensioniert und ausgelaufen; mit Realpreisen wählt das Tool korrekt das PP12 550 (64,88 €). 16 GB kosten 305 € und damit mehr als CPU und Board zusammen. Das ist Speicherkrise, kein Logikfehler. |
| fhd | 1.617 € bei 1.600 € Budget | Budget um 17 € überschritten. Gewählt wird das **ausgelaufene** B650 Gaming Plus. Der Upgrade-Tipp „RX 9060 XT 8 GB, 114 € günstiger“ ist falsch (real 446 €). R5 7600 ↔ RTX 5060 ist ausgewogen. |
| wqhd | 9800X3D + 9070 XT, B850 Tomahawk, PA120 SE, RM850e | Ausgewogen. Mit Realpreisen wählt das Tool das B850 Aorus Elite (189 €). Gut. |
| 4k | 9850X3D + 5090, X870 Tomahawk, HX1500i | Das 1500-W-Netzteil (330 € statt real 278 €) ist überdimensioniert; NVIDIA nennt 1000 W. Ein 120-mm-Doppelturm (PA120 SE) reicht für den 9850X3D. 32 GB passen. |
| ws | 9950X + 96 GB, RTX 5070 12 GB, X870 Tomahawk, LF III 360 | Die GPU ist für eine Workstation mit 96 GB knapp (12 GB VRAM). Sinnvoller wäre bei Kreativ-Schwerpunkt eine 16-GB-Mindestgrenze. Sonst stimmig. |
| itx | R5 7600 + RTX 5070, ROG B850-I (298 €), SF750 | Kompatibel, aber das Board kostet 1,7× so viel wie die CPU. Mit dem B850I Lightning (186,90 €) wären es −111 €. Das NR200P V2 ist nicht mehr lieferbar. |

**betterOptions für „mine“:** Die CPU-Vorschläge (9950X +146 €, +30 % Multi-Core) und die GPU-Vorschläge (5080 +610 € für +14 % in 4K) sind korrekt. RAM: 48 GB werden als Upgrade gezeigt, ohne den Wechsel von CL28 auf CL30 zu erwähnen (gering). Board: X870 Tomahawk mit „+1 M.2“ ist durch den Datenfehler (`m2:3`) entstanden und bringt real nichts außer USB4. SSD: Mit Realpreisen ist die SN8100 günstiger **und** schneller, und das Tool würde sie korrekt als „günstiger“ anzeigen. Netzteil: Der Hinweis „+150 W Reserve“ beim Straight Power 12 1000 W ist bei 537 W Last unnötig.

### Bestätigt (±10 %)

- **CPU:** 8500G 128 €, 8600G 169 €, 14400F 149,90 €, 7600 175,30 €, 9600X 163,90 €, 7500X3D 221,90 €, 225 177,40 €, 245K 185,90 €, 245KF 172,68 €, 250K Plus 216 €, 9700X 230/253,90 €, 7800X3D 279,07 €, 265K 309 €, 265KF 261,90 €, 270K Plus 295,81 €, 9900X 302,59 €, 9850X3D 398,90 €, 9950X 440,59 €, 9950X3D 552,43 €, 9950X3D2 779 €, 285K 528,99 € (hardwaredealz).
- **GPU (Serien-Tiefstpreis hardwaredealz):** 5050 383,90 €, 5060 459 €, 5060 Ti 8 GB 499,34 €, 5060 Ti 16 GB 782,37 €, 9060 XT 16 GB 624,21 €, 9070 GRE 639 €, 5070 834,46 €, 9070 819 €, 9070 XT 886,84 €, 5070 Ti 1.299 €, 5080 1.861 €, 5090 7.731,76 €, Arc B570 280,76 €.
- **Mainboards:** B850M Gaming X 161,66 €, X870E-E 439 €, X870 Tomahawk 289 € (1 Angebot), B850-I 277,73 €, Z890 Tomahawk 345,16 €, X870E Carbon 443,36 €, Crosshair Hero 526,97 €.
- **RAM:** Patriot Viper Venom CL28 524,90 €, Kingston Beast 16 GB 299 €, G.Skill CL28 861,98 €, Corsair 64 GB 1.235,22 €, Corsair 48 GB 948,99 € (1 Angebot).
- **SSD:** NV3 1 TB 134,90 €, 990 EVO Plus 2 TB 328,67 €, SN8100 2 TB 419,90 €, 9100 Pro 2 TB 415,99 €, 990 Pro 4 TB 669,99 €, 9100 Pro 4 TB 859,95 €.
- **Kühler:** Assassin X 120 Refined SE 18,90 €, PA120 SE 34,29 €, AXP90-X47 37,39 €, LF III Pro 420 76,05 €, NH-D15 G2 143,90 €, Kraken Elite 360 235,90 €.
- **Netzteile:** RM850e 104,60 €, SF750 156,84 €, Straight Power 12 1000 W 199,89 €, RM1000x 165,90 €, Dark Power 14 1000 W 227,88 €, Vertex GX-1200 243,75 €.
- **Gehäuse:** Pop Air 76,64 €, North 134,89 €, SB802 136,89 €, Define 7 169,90 €.
- **Laptops:** G14 2026 (GU405AR, 32 GB) 3.299 €. Aorus Master 16 Gen 2 (9955HX3D/5080/32 GB/2 TB) 3.299 €. MacBook Pro 16 M5 Max 36 GB/2 TB (32-Kern-GPU) 4.099 € (Tool 4.163 €, −1,5 %). Dazu neu: MacBook Pro PROLED XDR 16,3" M5 Max ab 5.098,28 €.
- **„mine“-Technik:** B850-Support für Ryzen 9000, Flash-BIOS-Taste, RM850e mit 12V-2x6 und Silent Base 802 mit 185 mm Kühlerhöhe sind belegt.

### Nicht prüfbar

- G.Skill Trident Z5 Neo 96 GB DDR5-6000 (1.750 €): kein passendes Angebot gefunden.
- ASRock B650M Pro RS **WiFi** (176 €): nur die Variante ohne WLAN gefunden (116,05 €).
- Gigabyte B760 Gaming X AX und MSI PRO B760M-A WiFi: nur Einzelangebote. LGA1700-Boards laufen aus.
- Leistungsangaben `cap` (Kühler) und `g`/`m` neuer Teile: Schätzungen, nicht gemessen.
- Exakte GPU-Länge der jeweils gekauften RTX-5070-Ti-Variante: Das Tool rechnet pauschal mit 305 mm.

## Red Team: Fertig-PCs und Schätzformeln (10.11.2026)

Geprüft wurden `PREBUILT`, `appEstimates` und das Fazit in `renderPrebuilt`. Preisquelle ist vor allem der PCGH-Preisvergleich (Geizhals-Daten, Stand 10.11.2026, 04:22). Preise inkl. MwSt., ohne Versand.

### 1. Fertig-PCs im Tool

| id | Tool | Befund 10.11.2026 | Quelle | Schwere |
|---|---|---|---|---|
| omen35l | 2.549 € (Juli) | **ab 3.799 €** (2 Angebote, Lieferstatus nicht angezeigt). Die 2.549 € sind ein abgelaufenes Angebot; die eingebetteten Geizhals-Preise im Notebookcheck-Artikel vom 26.09.2026 zeigen ebenfalls 3.799 €. Ausstattung stimmt (9800X3D, RTX 5070 Ti, 32 GB DDR5-6000, 1 TB). | https://preisvergleich.pcgameshardware.de/?fs=Omen+35L+GT16-1472ng · https://www.notebookcheck.com/Ryzen-7-X3D-GeForce-RTX-5070-Ti-32-GB-RAM-1-TB-SSD-HP-Omen-Gaming-PC-im-Angebot.1409230.0.html | hoch |
| legiont7 | 2.999 €, 265KF | Die Variante aus dem Artikel (90Y600AEGF) hat einen **Core Ultra 7 265K**, nicht 265KF. Laut Preisvergleich gibt es **keine Angebote mehr**, sie ist also nicht lieferbar. Am 12.09.2026 lag der Geizhals-Bestpreis schon bei 3.181,82 €, die 2.999 € galten nur bei NBB. Die günstigste lieferbare T7 mit RTX 5080 ist 90Y6001PGF (265KF, 32 GB, 2 TB) **ab 3.698,77 €** (3 Angebote). | https://preisvergleich.pcgameshardware.de/?fs=Legion+T7+34IAS10+5080 · https://www.notebookcheck.com/GeForce-RTX-5080-Core-Ultra-7-32-GB-RAM-1-TB-SSD-Lenovo-Legion-Gaming-PC-im-Angebot.1397802.0.html | hoch |
| dubaro | 3.499 €, 1 TB | Den Preis von 3.499 € bestätigt die Herstellerseite. Die SSD hat aber **2 TB** (PCIe 4.0), nicht 1 TB. Lieferzeit nicht angegeben. Ob Windows im Preis enthalten ist, ist nicht belegt; das Tool rechnet „inkl. Windows“. Auf derselben Seite steht ein Dubaro-PC mit Ryzen 9 9900X und RTX 5070 Ti (also CPU und GPU wie im Selbstbau) für 3.599 €. | https://dubaro.de/Specials/NVIDIA-RTX-5070Ti-Gaming-PCs/ | mittel |
| area51 | 3.599 € (März 2025) | **Nicht belegbar.** Beim Desktop (AAT2250: 265K, 64 GB, 2 TB; AAT2265: 9850X3D) listet Geizhals **keine Angebote**. Die Konfiguration im Tool (265F, 32 GB) ließ sich nicht finden. Empfehlung: Eintrag entfernen oder als „nicht lieferbar“ kennzeichnen. | https://preisvergleich.pcgameshardware.de/?fs=Alienware+Area-51+5080 | hoch |

### Neue Kandidaten (lieferbar, belegt)

1. **Lenovo Legion T7 34IAS10 (90Y600AFGF)**: Core Ultra 7 265K, RTX 5070 Ti 16 GB, 32 GB DDR5-5600, 1 TB PCIe 4.0, 850 W, Windows 11 Home. **2.599 €** bei notebooksbilliger.de, „sofort ab Lager“ (Stand 10.11.2026). https://preisvergleich.pcgameshardware.de/lenovo-legion-t7-34ias10-90y600afgf-a3775439.html
2. **Lenovo Legion Tower 5i Gen10 (90YA001XGF)**: Core Ultra 7 265KF, RTX 5070 Ti 16 GB, 32 GB DDR5-5600, 1 TB, 850 W, Windows 11 Home. **2.599 €** bei notebooksbilliger.de, „auf Lager“. https://preisvergleich.pcgameshardware.de/lenovo-legion-tower-5i-gen10-90ya001xgf-a3510245.html
3. **Captiva Highend Gaming R10-0257**: Ryzen 7 9800X3D, RX 9070 XT, 32 GB, **ab 2.445,03 €** (6 Angebote). SSD, Windows und Lieferstatus konnten nicht geprüft werden (HTTP 429). Die Variante R10-4339 hat Windows 11 Home, aber nur 16 GB RAM, und kostet ab 2.335,72 €. https://preisvergleich.pcgameshardware.de/captiva-highend-gaming-r10-0257-100257-a3868979.html

Die beiden Lenovo-Seiten zeigen dieselben drei Händlerpreise (2.599 / 2.714,45 / 2.729 €). Das ist plausibel, weil es Schwestermodelle sind, sollte aber vor der Übernahme einmal im Browser geprüft werden.

### 2. Schätzformeln

| Formel | Tool | Befund | Korrektur | Quelle | Schwere |
|---|---|---|---|---|---|
| Frame Generation NVIDIA | ×1,9 (Text „2×“) | ComputerBase misst bei DLSS-4-FG 2× 78 → 123,3 FPS, also ×1,58. MFG 3× ergibt ×2,22, MFG 4× ×2,73. | `fg` NVIDIA = 1.6 (2×). Wenn MFG 4× gezeigt werden soll: 2.7, mit eigenem Text. | https://www.computerbase.de/artikel/grafikkarten/nvidia-geforce-rtx-5060-test.92811/seite-6 | mittel |
| Frame Generation AMD/Intel | ×1,7 | Nicht gemessen. Dass AMD/Intel höher liegen soll als NVIDIA 2×, ist nicht belegt. | Vorläufig 1.6, wie NVIDIA. | – | niedrig |
| Lokale KI, `llmFast` | VRAM/0,62 → 16 GB ≈ 25 Mrd. | 0,62 GB je Mrd. Parameter gilt nur für die Gewichte. gemma3:27b in q4_K_M ist 17 GB groß und passt nicht in 16 GB; Kontext (KV-Cache) und Desktop kommen noch dazu. | `Math.floor((vram-1.5)/0.62)` ergibt 23 Mrd. bei 16 GB. Für `llmSlow` dasselbe: `(vram-1.5+ramGb*0.7)/0.62`. | https://ollama.com/library/gemma3/tags | mittel |
| Raytracing-Faktor AMD/NVIDIA | 0,95 / 1,15 | Damit liegt die RTX 5070 Ti im RT-Wert 22 % vor der RX 9070 XT. Laut PCGH baut die 5070 Ti ihren Vorsprung bei RT deutlich aus; laut Hardware Unboxed liegt die 9070 XT mit RT eher auf Höhe der RTX 5070. Das passt größenordnungsmäßig. | keine | https://www.pcgameshardware.de/Radeon-RX-9070-XT-Grafikkarte-281023/Specials/GPU-Duell-vs-Geforce-RTX-5070-Ti-1471688/ · https://tech4gamers.com/?p=305781 | – |
| GPU-Index 5080/5070 Ti | 105/93 = +13 % | GamersNexus misst in 4K +12 bis 16 %. | keine | https://gamersnexus.net/gpus/do-not-buy-nvidia-rtx-5070-ti-gpu-absurdity-benchmarks-review | – |
| GPU-Index RX 9070 XT | 92 (−1 %) | TechSpot misst über 55 Spiele −5 % zur 5070 Ti, Hardware Unboxed −1 % in 4K. | optional 90 | https://www.techspot.com/review/2970-amd-radeon-rx-9070-xt-vs-nvidia-geforce-rtx-5070-ti/ | niedrig |
| Absolute 4K-FPS, eSports-Faktor, Upscaling ×1,55, Blender-Index | – | In dieser Runde keine belastbare Messreihe gefunden (TechPowerUp antwortet mit 403). | nicht bewertet | – | – |

### 3. Aussage „HP Omen 35L ca. 805 € günstiger“

Die Rechnung im Code stimmt mit den Zahlen im Tool: 3.094 € + 260 € = 3.354 €, minus 2.549 € = 805 €. In 4K erreichen beide 65 FPS (Index 93 × 0,7), also 100 %. Die Aussage ist aber **heute falsch**. Der Omen kostet laut Preisvergleich ab 3.799 € und ist damit rund 445 € **teurer** als der Selbstbau. Mit aktuellen Preisen gilt:

- Legion T7 (RTX 5080, 1 TB): nicht lieferbar.
- Area-51: nicht belegbar.
- Dubaro: 3.499 €, ohne geklärtes Windows. Damit wäre der Selbstbau etwa 145 € günstiger.

Der neue Kandidat Legion T7 90Y600AFGF (2.599 €, 4K-Schätzung ebenfalls 65 FPS) wäre etwa **755 € günstiger**. Allerdings hat er nur DDR5-5600, 1 TB SSD und ein Lenovo-eigenes Mainboard und Netzteil.

Der Fehler steckt im Verfahren: `renderPrebuilt` wählt „cheapest“ ohne Rücksicht auf `chk`, das Preisdatum oder die Lieferbarkeit. Empfehlung: Nur Einträge mit `chk:true` und einem Preisdatum jünger als 30 Tage berücksichtigen und das Datum im Fazit nennen. Auch die Hardware-Summe von 3.094 € wurde in dieser Runde nicht neu geprüft.

### Umsetzung Fertig-PCs und Schätzformeln (10.11.2026)
Übernommen: HP Omen 35L auf 3.799 € (2.549 € war ein abgelaufenes Angebot); Legion T7 mit RTX 5080 als 90Y6001PGF für 3.699 € mit 2 TB; Dubaro mit 2 TB und Hinweis zu Windows; Alienware Area-51 entfernt (keine Angebote); neu: Legion T7 90Y600AFGF und Legion Tower 5i Gen10 mit RTX 5070 Ti für je 2.599 €, Captiva R10-0257 mit RX 9070 XT (2.445 €, unbelegt). Das Fazit nutzt nur noch belegte Preise und nennt die Quelle. Frame Generation auf Faktor 1,6 (2×), Hinweis auf MFG 4× bis ca. 2,7; lokale KI rechnet 1,5 GB Reserve ab (16 GB ≈ 23 Mrd. Parameter); RX 9070 XT Gaming-Index 90.

## Red Team: Runde 5 – Datenbank und Auswahl (10.11.2026)

Preisstand: **11.11.2026**, ca. 03:00–04:15 Uhr (Erstabruf 10.11., 17:35 Uhr; alle Werte am 11.11. neu abgerufen). Quellen: hardwaredealz.com (Geizhals-Daten, `updated_at` 11.11.2026 02:35) und PCGH-Preisvergleich (Geizhals-Basis). Preise inkl. MwSt., ohne Versand, günstigstes deutsches Angebot. CPU-Preise sind Boxed-Preise. Geprüft wurde die unveränderte Datei über eine Kopie mit Lese-Hook (`redteam5/pw5.html`) in Playwright/Chromium. msi.com, bequiet.com und igorslab.de lieferten HTTP 403; Aussagen dazu stammen aus Suchtreffern und sind so gekennzeichnet.

### Zusammenfassung

- **Nutzerkonfiguration:** alle 8 Teile lieferbar (17–69 Angebote je Teil), Summe heute **3.119 €** (Tool 3.087 €, +1 %). Keine Inkompatibilität gefunden. Die Zusammenstellung ist für 4K-Gaming plus Blender/KI/Foto stimmig; ein klar besseres Teil zum gleichen Geld gibt es nicht. Nicht belegbar bleibt der Platz eines 63 mm dicken 360er-Radiators (mit Lüftern) im Deckel des Silent Base 802.
- **Datenbank:** 114 Einträge geprüft (alle 19 ohne `chk:true`, davon einer nicht auffindbar, dazu 95 mit `chk:true`). 9 Preise weichen um 10 % oder mehr ab, 6 Teile sind praktisch nicht mehr lieferbar (0–2 Angebote) und noch nicht als `eol` markiert. RTX 50 Super, Arc B770 und RX 9080 sind in keinem der beiden Preisvergleiche gelistet und werden nicht vorgeschlagen.
- **Automatische Auswahl:** Die Vorlagen sind technisch konfliktfrei. Die Budgetlogik hält das Budget aber nicht ein (bis +45 %) und lässt es an anderer Stelle ungenutzt (543 € Rest bei verfehltem FPS-Ziel). Der Kühler-Rückfall wählt bei „sehr leise“ den billigsten statt den stärksten Kühler.
- **Upgrade- und Sparvorschläge:** 3 von 7 „Besser“-Tipps für „mine“ sind fachlich fragwürdig (420er-AiO, 4-TB-SSD mit PCIe 4.0, 48-GB-Kit mit nur 1 Angebot).
- **Fertig-PCs/Laptops:** 9 von 11 Preisen bestätigt. Legion Pro 7i (2.599 €) ist zu diesem Preis nicht erhältlich; Dubaro kostet 3.299 € statt 3.499 €.

### 1. Nutzerkonfiguration „mine“

| Teil | Tool | 11.11.2026 | Angebote | Quelle |
|---|---|---|---|---|
| Ryzen 9 9900X (boxed) | 303 | 301,09 | 37 | https://www.hardwaredealz.com/hardware/cpu/amd-ryzen-9-9900x-boxed/preisvergleich.21772 |
| Arctic Liquid Freezer III Pro 360 | 70 | 70,25 | 42 | https://preisvergleich.pcgameshardware.de/?fs=Arctic+Liquid+Freezer+III+Pro+360 |
| MSI MAG B850 Tomahawk Max WiFi | 265 | 254,90 (hardwaredealz: 264,93) | 30 | https://preisvergleich.pcgameshardware.de/?fs=Tomahawk+Max+WIFI+B850 |
| Patriot Viper Venom 32 GB DDR5-6000 CL28 (VV532G60C28AK) | 525 | 524,90 (RGB-Variante 544,90) | 17 | https://preisvergleich.pcgameshardware.de/?fs=Patriot+Viper+Venom+32GB+Kit+DDR5-6000+CL28 |
| RTX 5070 Ti (günstigste: Gigabyte Eagle OC SFF) | 1.279 | 1.311,33 | 17 | https://www.hardwaredealz.com/hardware/gpu/nvidia-geforce-rtx-5070-ti-16gb |
| Samsung 9100 Pro 2 TB | 400 | 415,99 | 69 | https://preisvergleich.pcgameshardware.de/?fs=Samsung+9100+PRO+2TB |
| Corsair RM850e (2025) | 105 | 103,90 | 24 | https://www.hardwaredealz.com/hardware/psu (ID 24601) |
| be quiet! Silent Base 802 | 140 | 136,89 (mit Fenster 138,25) | 30 | https://www.hardwaredealz.com/hardware/case (ID 14085/14084) |
| **Summe** | **3.087** | **3.119,25** | | |

Kompatibilität:

- **M.2 / Lane-Sharing:** Laut MSI-Übersicht hängen M2_1 und M2_2 beide mit PCIe 5.0 x4 direkt an der CPU, ohne Sharing; PCI_E1 bleibt x16. Nur M2_3 (PCIe 4.0 x2) teilt sich Lanes mit PCI_E3. Die 9100 Pro gehört in M2_1 und kostet der Grafikkarte keine Lanes. Quelle: https://www.msi.com/blog/overview-of-pcie-lane-sharing-on-msi-b850-motherboards (nur über Suchtreffer gelesen, Direktabruf 403); hwcooling nennt 2× PCIe 5.0 x4 und 2× PCIe 4.0 x2: https://hwcooling.net/en/si-mag-b850-tomahawk-max-wifi-cheaper-chipset-but-better-features-review. Das Tool kennt nur `m2:4`, nicht die x2-Anbindung der Plätze 3 und 4.
- **Radiator oben:** be quiet! nennt für den Deckel 120/240/360 mm, vorn bis 420 mm (https://www.bequiet.com/en/case/2045, Suchtreffer; https://sts-tutorial.com/review/be-quiet-silent-base-802). Die Tool-Regel (`radFrontOnly:[420]`) passt dazu. **Nicht belegt** ist die Bauhöhe: Der Liquid Freezer III Pro hat einen 38-mm-Radiator, mit Lüftern 63 mm. Ein Forenbeitrag nennt ca. 70 mm Abstand im Deckel, ein anderer eine Kollision der Lüfter mit hohem RAM (https://forums.tomshardware.com/threads/arctic-lfii-280-fitting-in-top-mount-of-silent-base-801-802-case.3746328). Vor dem Kauf nachmessen. KitGuru weist darauf hin, dass der geschlossene Dämmdeckel einen Deckel-Radiator ausbremst; dann den Mesh-Deckel verwenden oder vorn montieren.
- **Grafikkarte:** 305 mm im Tool; Platz 432 mm ohne, 287 mm mit voll bestücktem HDD-Käfig (bestätigt, sts-tutorial). Der Tool-Hinweis „HDD-Käfig ausbauen“ ist richtig; laut Test stören nur die unteren beiden Käfigplätze.
- **Netzteil:** Last 537 W, Empfehlung 750 W, gewählt 850 W. In Ordnung.

Fachliche Bewertung: RTX 5070 Ti ist wegen CUDA/OptiX und 16 GB die richtige Wahl für Blender und KI; die RX 9070 XT (875,60 €) spart 436 €, verliert aber genau diese Stärken. Die RTX 5080 kostet 489 € mehr für ca. 13 %. Der 9900X ist für GPU-Rendering und 4K ausreichend; der 9800X3D (360 €) brächte in 4K kaum etwas und verliert Multi-Core-Leistung. Der Core Ultra 7 270K Plus (296 €) wäre im CPU-Rendering schneller, braucht aber ein anderes Board und im Tool DDR5-6400 (+204 €) und sitzt auf einem Sockel ohne Aufrüstpfad. Einziger echter Sparpunkt außerhalb der Tool-Liste: Die PCIe-5.0-SSD bringt bei Spielen und Fotoarbeit wenig; eine 990 EVO Plus 2 TB kostet 328,67 € (−87 €). 64 GB RAM wären für Blender/KI wünschenswert, kosten derzeit aber +710 €.

### 2. Befunde Datenbank

| Schwere | id | Feld | Wert im Tool | korrekter Wert | Quelle |
|---|---|---|---|---|---|
| hoch | legionpro7i (LAPTOPS) | price / Lieferbarkeit | 2.599 (Edu-Angebot März) | 32-GB-Varianten ohne Angebot; günstigste gelistete RTX-5080-Variante 4.839,46 € (64 GB / 2 TB, 3 Angebote) | https://preisvergleich.pcgameshardware.de/?fs=Legion+Pro+7+16IAX10H+RTX+5080+32GB |
| hoch | c-2x24-6000 | price / Lieferbarkeit | 880 | 948,99 (+8 %), nur 1 Angebot; alle anderen 48-GB-Vengeance-6000-CL30-Varianten ohne Angebot | https://preisvergleich.pcgameshardware.de/?fs=Corsair+Vengeance+48GB+Kit+DDR5-6000+CL30 |
| hoch | msi-b860m-a | eol | lieferbar, 227 | 227,37 mit nur 2 Angeboten; wird trotzdem automatisch gewählt (Szenario S6). Als `eol:true` markieren | https://preisvergleich.pcgameshardware.de/?fs=MSI+PRO+B860M-A+WIFI |
| mittel | kingston-nv3-1 | price (chk) | 125 | 146,99 (+18 %); steckt in 5 automatischen Zusammenstellungen | https://preisvergleich.pcgameshardware.de/?fs=Kingston+NV3+1TB |
| mittel | k-2x8-5600 | cl / price | CL40, 305 | CL40-Kit (KF556C40BBK2-16) 352,77 (+16 %); CL36-Kit (KF556C36BBEK2-16) 299,00. Vorschlag: `cl:36, price:299` | https://preisvergleich.pcgameshardware.de/?fs=Kingston+FURY+Beast+16GB+Kit+DDR5-5600 |
| mittel | r7-9850x3d | price (chk) | 435 | 383,48 (−12 %) | https://www.hardwaredealz.com/hardware/cpu/amd-ryzen-7-9850x3d-boxed/preisvergleich.33161 |
| mittel | r7-9800x3d | price (chk) | 398 | 360,08 (−10 %) | https://www.hardwaredealz.com/hardware/cpu/amd-ryzen-7-9800x3d-boxed/preisvergleich.23412 |
| mittel | fd-north | price (chk) | 142 | 122,90 weiß TG / 128,45 schwarz TG (−13 % / −10 %) | https://www.hardwaredealz.com/hardware/case (ID 14512, 14514) |
| mittel | asr-b650m-pro | price / eol | 176, lieferbar | 206,47 (+17 %), nur 1 Angebot → `eol:true` | https://preisvergleich.pcgameshardware.de/?fs=ASRock+B650M+Pro+RS+WiFi |
| mittel | msi-z890-tomahawk | price / eol | 328, lieferbar | 330,67, nur 2 Angebote; Nachfolger „WIFI II“ gelistet (noch ohne Angebot) → `eol:true` | https://preisvergleich.pcgameshardware.de/?fs=MSI+MAG+Z890+Tomahawk+WIFI |
| mittel | asr-a620m | Lieferbarkeit | lieferbar, 95 | 94,90, nur 2 Angebote; ist das Board der Vorlage „Büro & Zuhause“ | https://preisvergleich.pcgameshardware.de/?fs=ASRock+A620M-HDV%2FM.2%2B |
| mittel | msi-b760m-a (eol) | price | 125 | 200,24, 1 Angebot | https://preisvergleich.pcgameshardware.de/?fs=MSI+PRO+B760M-A+WIFI |
| mittel | gb-b760-gx (eol) | price | 150 | 421,37, 1 Angebot | https://preisvergleich.pcgameshardware.de/?fs=Gigabyte+B760+Gaming+X+AX |
| mittel | sea-gx1200 | atx3-Text / Lieferbarkeit | „ATX 3.1 mit 12V-2x6“ | Vertex GX-1200 ist ATX 3.0; 244,05, nur 3 Angebote; wird für die RTX 5090 automatisch gewählt | https://www.hardwaredealz.com/hardware/psu/seasonic-vertex-gx-1200-1200w-atx-30/preisvergleich.4188 |
| niedrig | r9-9950x | price (chk) | 449 | boxed 479,09 (+7 %), tray 441,79 | https://www.hardwaredealz.com/hardware/cpu/amd-ryzen-9-9950x-boxed/preisvergleich.21773 |
| niedrig | nzxt-elite-360 | price | 260 | 235,90 (−9 %, Modell 2024 ohne RGB) | https://preisvergleich.pcgameshardware.de/?fs=NZXT+Kraken+Elite+360 |
| niedrig | r5-9600x | price (chk) | 177 | 163,00 (−8 %) | https://www.hardwaredealz.com/hardware/cpu/amd-ryzen-5-9600x-boxed/preisvergleich.21762 |
| niedrig | r7-7800x3d | price (chk) | 298 | 277,70 (−7 %) | https://www.hardwaredealz.com/hardware/cpu/amd-ryzen-7-7800x3d-boxed/preisvergleich.1880 |
| niedrig | asus-b850i | price (chk) | 298 | 277,73 (−7 %) | https://www.hardwaredealz.com/hardware/mainboard/asus-rog-strix-b850-i-gaming-wifi/preisvergleich.24493 |
| niedrig | asus-x870e-e | price | 470 | 437,41 (−7 %) | https://www.hardwaredealz.com/hardware/mainboard (ID 22461) |
| niedrig | tr-axp90 | price | 35 | 37,39 (+7 %) | https://preisvergleich.pcgameshardware.de/?fs=Thermalright+AXP90-X47+Full+Copper |
| niedrig | arc-b570 | price (chk) | 270 | 286,93 (+6 %) | https://www.hardwaredealz.com/hardware/gpu/intel-arc-b570-graphics-10gb |
| niedrig | dubaro (PREBUILT) | price | 3.499 | 3.299 (−6 %); 7800X3D, RTX 5070 Ti, 32 GB, 2 TB | https://dubaro.de/Specials/NVIDIA-RTX-5070Ti-Gaming-PCs/ |
| niedrig | rx9070 | price (chk) | 759 | 721,64 (−5 %; am 10.11. noch 819) | https://www.hardwaredealz.com/hardware/gpu/amd-radeon-rx-9070-16gb |
| niedrig | rtx5080 | price (chk) | 1.889 | 1.799,99 (−5 %) | https://www.hardwaredealz.com/hardware/gpu/nvidia-geforce-rtx-5080-16gb |
| niedrig | c-2x16-5600 | price (chk) | 629 | 660,09 (+5 %) | https://preisvergleich.pcgameshardware.de/?fs=Crucial+Pro+32GB+Kit+DDR5-5600 |
| niedrig | r5-8500g | price | 135 | 128,00 (−5 %) | https://www.hardwaredealz.com/hardware/cpu/amd-ryzen-5-8500g-boxed-fan/preisvergleich.2352 |
| niedrig | s9100-2 / s990pro-4 / c-2x32-6000 / r7-9700x / msi-b850-tomahawk | price | 400 / 700 / 1.190 / 238 / 265 | 415,99 / 669,99 / 1.235,22 / 247,19 / 254,90 (je ±4 %) | PCGH-Preisvergleich, hardwaredealz (wie oben) |
| niedrig | rtx5070ti / rx9060xt-16 / rx9070xt / rtx5090 / gs-2x16-6000c28 / nc-d15g2 / aorus16 | price | 1.279 / 606 / 896 / 7.800 / 886 / 150 / 3.299 | 1.311,33 / 624,95 / 875,60 / 7.637,38 / 861,98 / 143,90 / 3.399 (je ±2–4 %) | hardwaredealz-GPU-Seiten, PCGH-Preisvergleich |
| niedrig | captiva-9070xt (PREBUILT) | ssd | „k. A.“ | 1 TB laut Listung; 2.445,03 €, 6 Angebote | https://preisvergleich.pcgameshardware.de/?fs=Captiva+R10-0257 |
| niedrig | legiont7-5080 (PREBUILT) | Hinweis | – | Listung trägt den Zusatz „EN/ML“ (Tastatur/Sprachversion prüfen); 3.698,77 €, 3 Angebote | https://preisvergleich.pcgameshardware.de/?fs=Legion+T7+34IAS10+90Y6001PGF |
| niedrig | `priceLink`, `TREND`, `CHANGES` | Text | „Preis geprüft 9.11.“; 525 / 446 / 1.279 / 7.800 / 896 fest im Code | Datum und Werte aus der DB ableiten statt fest eintragen | Code Zeile ca. 1040, 1818–1830 |

Bereits als `eol` markiert und weiterhin ohne Angebot: msi-b650-gp (0 Angebote), cm-nr200p (0), cm-mwe-650 (1 Angebot, 87,50 €).

### 3. Kombinationen (automatische Auswahl)

Alle 7 Vorlagen und 8 Regler-Szenarien liefen ohne Konflikt oder Warnung aus `checkAll`.

| Lauf | Ergebnis (Kern) | Summe / Budget | Beurteilung |
|---|---|---|---|
| mine | Nutzerteile | 3.087 / – | stimmig, siehe Abschnitt 1 |
| office | 8600G, Boxed, A620M, 16 GB, NV3, 550 W, A3-mATX | 828 / – | passt; Board nur noch 2 Angebote |
| fhd | 7600, B850 Aorus Elite, 32 GB, RTX 5060 (fest) | **1.660 / 1.600** | Budget um 60 € überzogen; 80 statt 120 FPS; 189-€-Board zu teuer für das Budget |
| wqhd | 9800X3D, PA120 SE, RX 9070 XT, 850 W | 2.572 / – | ausgewogen; 108 statt 144 FPS ohne Hinweis |
| 4k | 9850X3D, RTX 5090, Vertex GX-1200 | 9.734 / – | passt; Netzteil ist ATX 3.0 und kaum lieferbar |
| ws | 9950X, LF III Pro 360, X870E, 96 GB, RTX 5070 Ti, 1000 W | 4.628 / – | stimmig |
| itx | 7600, AXP90-X47, B850I, RTX 5070, SF750, Terra | 2.442 / – | läuft; 90 statt 120 FPS; CPU für die Karte knapp, aber durch 77 mm Kühlerhöhe begründet |
| S1 Full HD, 16 GB | 7600, RTX 5060 Ti 8 GB | **1.458 / 1.200** | +21 % über Budget |
| S2 WQHD | 7600, RX 9070 16 GB, 750 W | 1.986 / 2.000 | gut |
| S3 4K Kreativ, 64 GB, 4 TB, sehr leise | 265KF, **PA120 SE**, B860, RTX 5070 Ti, Pure Base 501 | 3.705 / – | 34-€-Luftkühler mit 220 W für eine 250-W-CPU trotz „sehr leise“; KF-CPU ohne iGPU (kein Quick Sync) |
| S4 ITX, flüsterleise | 7600, **AXP90-X47**, RX 9070 GRE, Terra | 2.223 / – | lautester Kühler der Liste trotz Regler auf Maximum; NH-L12S (110 W, 70 mm) passt und wäre leiser |
| S5 3.000 €, 4K, 60 % Kreativ | 250K Plus, DDR5-6400 für 729 €, **RTX 5060 Ti 16 GB** | **2.457 / 3.000** | 543 € ungenutzt, 42 statt 60 FPS; RTX 5070 Ti hätte ins Budget gepasst (2.937 €) |
| S6 2.500 €, Kreativ, mATX, 64 GB | 270K Plus, LF III 360, B860M-A, 64 GB | **3.083 / 2.500** | +23 % über Budget; Board mit 2 Angeboten |
| S7 800 €, Full HD | 7600, Arc B570, 16 GB | **1.163 / 800** | +45 %; kein Hinweis, dass 800 € nicht erreichbar sind |
| S8 4K, 144 FPS, ohne Budget | 7800X3D, Assassin X 120, RTX 5090 | 9.583 / – | technisch in Ordnung |

Logik-Befunde:

1. **Budget wird nicht erzwungen** (hoch): `fillMissing` verteilt Anteile, prüft die Summe am Ende aber nicht. Überschreitung in 4 von 5 Budget-Läufen (+4 % bis +45 %).
2. **Budget bleibt ungenutzt** (hoch): Der GPU-Anteil (`alloc*1.15`) deckelt die Grafikkarte, auch wenn danach Geld übrig bleibt (S5).
3. **DDR5-6400-Zwang für LGA1851** (mittel): `prefer("ram")` filtert hart auf ≥ 6400; das kostet 204 € Aufpreis (729 statt 525 €) für wenige Prozent. `platformCost` rechnet diesen Aufpreis nicht mit, deshalb gewinnt Intel den Plattformvergleich zu Unrecht.
4. **Kühler-Rückfall** (mittel): Erfüllt kein Kühler die Leise-Reserve, nimmt `pickCooler` den billigsten mit `cap ≥ need` (S3, S4). Zusätzlich wird das Gehäuse vor dem Kühler gewählt; das Pure Base 501 sperrt dann die 360er-AiO (Front-Radiator und 305-mm-Karte).
5. **Kein Hinweis bei verfehltem FPS-Ziel** (niedrig): wqhd 108/144, itx 90/120, fhd 80/120.

### 4. Upgrade- und Sparvorschläge für „mine“

| Vorschlag im Tool | Beurteilung |
|---|---|
| Ryzen 9 9950X, +146 € | fachlich richtig; heute +178 € (boxed) |
| Liquid Freezer III Pro 420, +7 € | **fragwürdig**: 420 mm passt im Silent Base 802 nur vorn (HDD-Käfig raus, GPU max. 369 mm); der Tipp nennt das nicht. Nutzen gering, die 360er hat bereits 185 % der nötigen Kühlleistung |
| Gigabyte X870E Aorus Elite WiFi7, +5 € | vertretbar (heute +16 € gegenüber 254,90 €); Lane-Sharing dieses Boards nicht geprüft |
| Corsair Vengeance 48 GB, +355 € | **nicht umsetzbar**: 1 Angebot zu 948,99 € (+424 €) |
| RTX 5080, +610 € | richtig, heute +489 € für +14 % |
| Lexar NM790 4 TB, +139 € | **irreführend**: als „Besser“ markiert, ist aber PCIe 4.0 (7.400 statt 14.700 MB/s); der Text nennt nur „+2 TB“ |
| Straight Power 12 1000 W, +95 € | schwach: „+150 W Reserve“ bei 537 W Last und 850 W Bestand; Platinum allein rechtfertigt 95 € kaum |
| Sparen: Board → B850 Aorus Elite WiFi7, −76 € | richtig (heute −66 €); 3 statt 4 M.2-Plätze |
| Sparen: RM850e → Seasonic Core GX-850, −5 € | nur mit der weißen Variante (97,90 €); die schwarze kostet 107,72 € und ist teurer als das RM850e (103,90 €) |

Der Upgrade-Pfad (`#oPath`) enthält dieselben Einträge in der Reihenfolge CPU, GPU, Board, Kühler, Netzteil.

### 5. Fertig-PCs und Laptops

Bestätigt: Legion T7 90Y600AFGF 2.599,00 € (11 Angebote), Legion Tower 5i 90YA001XGF 2.599,00 € (12), Captiva R10-0257 2.445,03 € (6), HP Omen 35L GT16-1472ng 3.799,00 € (2), Legion T7 90Y6001PGF 3.698,77 € (3); ROG Zephyrus G14 (2026) 3.255,99–3.299,00 € (29–33), ROG Strix Scar 18 (2026) 6.598,00 € (32), MacBook Pro 16 M5 Max 36 GB / 2 TB 4.099,00 € (48). Abweichend: Aorus Master 16 Gen 2 (9955HX3D, 32 GB, 2 TB) 3.399,00 € (7), Dubaro 3.299 €, Legion Pro 7i nicht zum Tool-Preis erhältlich (siehe Tabelle).

### Bestätigt

Preise innerhalb ±3 % (11.11.2026): r5-8600g 169, i5-14400f 146,90, r5-7600 171,99, u5-225 176,94, r5-7500x3d 219, u5-250kp 216, u5-245k 185,90, u5-245kf 172,68, u7-270kp 296,17, r9-9900x 301,09, u7-265k 309, u7-265kf 261,90, r9-9900x3d 455,59, r9-9950x3d2 779, u9-285k 528,98; rtx5050 383,90, arc-b580 387,90, rx9060xt-8 446,46, rtx5060 459, rtx5060ti-8 521,90, rtx5060ti-16 799, rtx5070 860,12, rx9070gre 638; gs-2x16-6000 524,90, gs-2x16-6400 729, c-2x64-5600 3.499 (1 Angebot); wd-sn7100-1 235,99, wd-sn7100-2 445,99, s990evo-1 189, s990evo-2 328,67, sn8100-2 411,92, lexar-nm790-4 538,95, s9100-4 859,95; gb-b850m-gx 161,72 (+4 %), msi-b850-gp6e 152,21, asr-b850i-lightning 187,35, gb-x870e-elite 270,76, gb-b850-elite 188,80, msi-x870e-carbon 443,29, asus-x870e-hero 526,97, asus-b860-tuf 169,27, asus-z890i 464,13 (2 Angebote), asus-z890e 329; tr-ax120 18,90, ar-f36 22,14, tr-pa120se 34,29, nc-l12s 66,90, ar-lf3-240 57,44, ar-lf3-360 70,25, ar-lf3-420 76,05, bq-drp5 79,90, bq-drp6 84,42; bq-pp12-550 64,88, bq-pp12-650 69,88, bq-pp12-750 96,02, sea-coregx-850 97,90 (weiß), cor-rm850e 103,90, cor-sf750 156,85 (−5 %), bq-sp12-1000 199,89, cor-sf1000 194,90, cor-rm1000x 165,90, bq-dp14-1000 227,88, cor-hx1500i 278,14; fd-pop-air 77,78, ll-a3 68,69, montech-903 54,17 (4 Angebote), ll-lancool217 99,90, bq-pb501 59,62, ll-o11-evo 166,95, bq-sb802 136,89, fd-define7 169,90, fd-terra 169,90.

### Nicht prüfbar

- **gs-2x48-6000** (G.Skill Trident Z5 Neo 96 GB, 1.750 €): in zwei Suchen kein 96-GB-Kit von G.Skill gefunden. Preis und Lieferbarkeit offen; das Teil ist in der Vorlage „Workstation“ fest gesetzt.
- **msi-x870-tomahawk**: hardwaredealz zeigt 289 € mit nur 1 Angebot; im PCGH-Preisvergleich nicht gegengeprüft.
- **Bauhöhe 360er-Radiator im Deckel des Silent Base 802** und RAM-Abstand: keine Herstellerangabe erreichbar.
- **Lane-Sharing des Gigabyte X870E Aorus Elite WiFi7** (Upgrade-Tipp): nicht geprüft.
- **Dubaro:** Lieferzeit und Windows-Lizenz weiterhin nicht belegt.
- **RTX 50 Super:** kein Händlerangebot in beiden Preisvergleichen; Berichte nennen nur Verschiebungen. Nicht als erschienen behandelt.
- GPU-Längen, Leistungsindizes und Verbrauchswerte wurden in dieser Runde nicht neu vermessen.

### Umsetzung Runde 5 (11.11.2026)
- **Preise:** alle Korrekturen der Liste übernommen (u. a. 9800X3D 360 €, 9850X3D 383 €, RTX 5070 Ti 1.311 €, RTX 5080 1.800 €, RTX 5090 7.637 €, Kingston NV3 147 €, Fractal North 123 €). Die Preisentwicklung im Dashboard liest ihre aktuellen Werte jetzt aus der Datenbank.
- **Lieferbarkeit:** MSI PRO B860M-A, ASRock B650M Pro RS, MSI Z890 Tomahawk, ASRock A620M-HDV, Corsair 48-GB-Kit und Seasonic Vertex GX-1200 als Auslaufmodelle markiert; sie werden weder automatisch gewählt noch als Upgrade vorgeschlagen. Laptop Legion Pro 7i entfernt.
- **Neu:** MSI PRO B860M-P WiFi6E, G.Skill Flare X5 16 GB DDR5-6000 CL30, be quiet! Pure Power 13 M 750 W. Nicht übernommen: Teile mit unbelegten Eckdaten (B850 Gaming Plus MAX, Tomahawk-Varianten, RM850e ThermalProtect).
- **Budget:** Nach dem Füllen wird das Budget erzwungen (automatisch gewählte Teile schrittweise günstiger, Leistungsteile zuletzt); bleibt Geld übrig und das FPS-Ziel ist verfehlt, wird die stärkste passende Grafikkarte gewählt. Ist das Budget nicht erreichbar, erscheint ein Hinweis.
- **Weitere Logik:** DDR5-6400 für Core Ultra nur bei höchstens 10 % Aufpreis; Kühler-Rückfall wählt den stärksten statt den billigsten; SSD-Tipps nie mit langsamerer Schnittstelle; kein Kühler-Tipp bei 50 % Reserve; Spar-Schwelle 15 € bzw. 3 %; Netzteil-Reserve-Text nur bei echtem Bedarf; ATX-3.0-Netzteile korrekt beschriftet; Hinweis bei verfehlter Ziel-Bildrate; ITX-Prozessoren nach Kühlgrenze des kleinsten lieferbaren Gehäuses.
- **Audit danach:** 108 Regler-Kombinationen ohne Konflikt; 6 Warnungen (ITX mit reinem Kreativ-Schwerpunkt: Prozessor bremst die Grafikkarte, weil stärkere Prozessoren dort nicht kühlbar sind). Budget-Überschreitungen nur noch, wo die Vorgaben (32 GB RAM, 2 TB SSD) ein Budget von 1.500 € unerreichbar machen.
- **Offen:** Bauhöhe des 360er-Radiators im Deckel des Silent Base 802 gegenüber dem RAM ist nicht belegt.


---

# Runde 6 (14.11.2026): GPU-Partnermodelle, Zubehör, neue Kernteile

## Red-Team-Prüfung 6 – pc-werkbank.html (Stand 14.11.2026)

Quellen: HWD = hardwaredealz.com (Geizhals-Angebote mit market "de", Attribute Länge/Slots); PCGH = preisvergleich.pcgameshardware.de (Geizhals-Daten, Suche sortiert nach Preis; bei Boards und Gehäusen auch die Produktseite); dazu Herstellerseiten und Testberichte. Toleranz beim Preis: ±5 %.

## 1. GPU_MODELS (geprüft: 54 Modelle; Preis, Länge, Slots über HWD)

| id | Feld | im Code | gefunden | Quelle | Urteil |
|---|---|---|---|---|---|
| asus-prime-rtx-5060-ti-oc | Preis/Länge/Slots | 790/304/3 | 789,99/304/3 | HWD 26841 | OK |
| asus-dual-rtx-5060-ti-oc | Preis/Länge/Slots | 799/229/3 | 799/229/3 | HWD 26839 | OK |
| msi-rtx-5060-ti-ventus-2x-oc-plus | P/L/S | 829/227/2 | 829/227/2 | HWD 26853 | OK |
| gigabyte-rtx-5060-ti-windforce-oc | P/L/S | 867/208/2 | 867/208/2 | HWD 27905 | OK |
| gigabyte-rtx-5060-ti-gaming-oc | P/L/S | 879/281/2 | 879/281/2 | HWD 26849 | OK |
| gigabyte-rtx-5060-ti-windforce-max-oc | P/L/S | 897/208/2 | 896,97/208/2 | HWD 30327 | OK |
| asus-tuf-gaming-rtx-5060-ti-oc | P/L/S | 949/302/3 | 949/302/3 | HWD 26844 | OK |
| msi-rtx-5060-ti-gaming-trio-oc | P/L/S | 1183/300/2 | 1183,43/300/2 | HWD 26852 | OK |
| msi-rtx-5070-ventus-2x-oc | P/L/S | 861/236/3 | 861,49/236/3 | HWD 25496 | OK |
| gigabyte-rtx-5070-windforce-oc-sff | P/L/S | 899/282/3 | 898,52/282/3 | HWD 25611 | OK |
| asus-dual-rtx-5070-oc | P/L/S | 905/249/3 | 905/249/3 | HWD 28032 | OK |
| msi-rtx-5070-gaming-trio-oc | P/L/S | 917/338/3 | 917,24/338/3 | HWD 25363 | OK |
| gigabyte-rtx-5070-gaming-oc | P/L/S | 919/327/3 | 919/327/3 | HWD 25440 | OK |
| asus-t1-rtx-5070-oc-edition | P/L/S | 949/305/3 | 949/305/3 | HWD 33588 | OK |
| asus-tuf-gaming-rtx-5070-oc | P/L/S | 1039/329/4 | 1039/329/4 | HWD 25359 | OK |
| msi-rtx-5070-ti-ventus-3x-oc | P/L/S | 1399/303/3 | 1399/303/3 | HWD 25183 | OK |
| gigabyte-rtx-5070-ti-gaming-oc | P/L/S | 1399/340/4 | 1399/340/4 | HWD 25194 | OK |
| gigabyte-rtx-5070-ti-windforce-oc-sff | P/L/S | 1399/304/3 | 1399/304/3 | HWD 25199 | OK |
| gigabyte-rtx-5070-ti-eagle-oc-sff | P/L/S | 1399/304/3 | 1399/304/3 | HWD 25472 | OK |
| asus-prime-rtx-5070-ti-oc | P/L/S | 1440/304/3 | 1439,90/304/3 | HWD 25223 | OK |
| inno3d-rtx-5070-ti-x3-oc | P/L/S | 1469/300/2 | 1469/300/2 | HWD 25229 | OK |
| msi-rtx-5070-ti-gaming-trio-oc | P/L/S | 1481/338/3 | 1481/338/3 | HWD 26368 | OK |
| asus-tuf-gaming-rtx-5070-ti-oc | P/L/S | 1580/329/4 | 1579,90/329/4 | HWD 25168 | OK |
| pny-rtx-5080-argb-epic-x… | P/L/S | 1949/329/3 | 1949/329/3 | HWD 25110 | OK |
| asus-prime-rtx-5080-oc | P/L/S | 1959/306/3 | 1959/306/3 | HWD 25130 | OK |
| gigabyte-rtx-5080-windforce-oc-sff | P/L/S | 1969/304/3 | 1969/304/3 | HWD 25071 | OK |
| zotac-…-5080-amp-extreme-infini | P/L/S | 1999/332/4 | 1999/332/4 | HWD 25201 | OK |
| gigabyte-rtx-5080-gaming-oc | P/L/S | 2049/340/4 | 2049/340/4 | HWD 24952 | OK |
| gigabyte-rtx-5080-aero-oc-sff | P/L/S | 2049/304/3 | 2049/304/3 | HWD 25342 | OK |
| msi-rtx-5080-gaming-trio-oc | P/L/S | 2169/338/3 | 2169/338/3 | HWD 25131 | OK |
| asus-tuf-gaming-rtx-5080-oc | P/L/S | 2180/348/4 | 2179,90/348/4 | HWD 24951 | OK |
| sapphire-pure-rx-9060-xt | P/L/S | 648/240/3 | 647,68/240/3 | HWD 29143 | OK |
| powercolor-reaper-rx-9060-xt | P/L/S | 649/200/2 | 649 (PCGH) bzw. 689 (HWD)/200/2 | PCGH, HWD 29138 | OK (Geizhals/PCGH 649) |
| sapphire-pulse-rx-9060-xt | P/L/S | 654/244/3 | 653,90/244/3 | HWD 29141 | OK |
| gigabyte-rx-9060-xt-gaming-oc | P/L/S | 662/281/2 | 661,90/281/2 | HWD 29134 | OK |
| sapphire-nitro-rx-9060-xt | P/L/S | 667/300/3 | 666,52/300/3 | HWD 29140 | OK |
| xfx-swift-rx-9060-xt-oc-gaming-edition | P/L/S | 675/270/2 | 674,76/270/2 | HWD 29145 | OK |
| powercolor-hellhound-rx-9060-xt | P/L/S | 729/330/2 | 729,38/330/2 | HWD 29136 | OK |
| asrock-rx-9060-xt-steel-legend-16gb-oc | P/L/S | 819/298/3 | 819/298/3 | HWD 29129 | OK |
| gigabyte-rx-9070-gaming-oc | P/L/S | 812/288/3 | 811,90 (PCGH 806,99)/288/3 | HWD 25381 | OK |
| asrock-rx-9070-challenger | P/L/S | 834/290/3 | 833,90/290/3 | HWD 25289 | OK |
| powercolor-reaper-rx-9070 | P/L/S | 836/289/2 | 836,02/289/2 | HWD 25366 | OK |
| sapphire-pulse-rx-9070 | P/L/S | 860/280/3 | 859,84/280/3 | HWD 25368 | OK |
| sapphire-pure-rx-9070 | P/L/S | 879/320/3 | 879/320/3 | HWD 25387 | OK |
| asus-prime-rx-9070-oc | P/L/S | 879/312/3 | 879/312/3 | HWD 25393 | OK |
| sapphire-nitro-rx-9070 | P/L/S | 899/331/4 | 899/331/4 | HWD 25385 | OK |
| powercolor-reaper-rx-9070-xt | P/L/S | 914/289/2 | 914,11/289/2 | HWD 25400 | OK |
| xfx-swift-rx-9070-xt-triple-fan… | P/L/S | 940/325/4 | 940,37/325/4 | HWD 25429 | OK |
| sapphire-pulse-rx-9070-xt | P/L/S | 960/320/3 | 959,83/320/3 | HWD 25386 | OK |
| gigabyte-rx-9070-xt-gaming-oc | P/L/S | 966/288/3 | 965,71/288/3 | HWD 25382 | OK |
| xfx-mercury-rx-9070-xt… | P/L/S | 975/360/4 | 974,90/360/4 | HWD 25402 | OK |
| powercolor-hellhound-rx-9070-xt | P/L/S | 989/327/3 | 989/327/3 | HWD 25383 | OK |
| asrock-rx-9070-xt-steel-legend-dark | P/L/S | 1015/298/3 | 1015,07/298/3 | HWD 25288 | OK |
| sapphire-nitro-rx-9070-xt | P/L/S | 1019/331/4 | 1019/331/4 | HWD 25384 | OK |
| (Chip rtx5070ti) | ab-Preis (= günstigstes Modell der Liste) | 1399 | INNO3D RTX 5070 Ti X3 (ohne OC) 1349,99 € fehlt in der Liste | HWD 25230 | UNKLAR (−3,5 %, in Toleranz) |
| (Chip rx9070xt) | ab-Preis (= günstigstes Modell der Liste) | 914 | XFX Swift RX 9070 XT White Triple Fan 875,15 € fehlt in der Liste | HWD 25445, PCGH | UNKLAR (−4,3 %, in Toleranz) |

### Lautstärke-Einordnung (Kühlerserie)

| Serie | im Code | Befund | Quelle | Urteil |
|---|---|---|---|---|
| MSI Ventus (2X/3X) | 3 laut | PCGH nennt die RTX 5070 Ti Ventus 3X „laut“ mit schwacher Kühlung; laut ComputerBase ist ihr Kühler mit 300 W überfordert | PCGH-Vergleich RTX 5070 Ti (6 Custom-Modelle), ComputerBase-Test RTX 5070 Ti Custom | OK |
| MSI Gaming Trio | 1 leise | Igor'sLab misst die RTX 5070 Gaming Trio unter Last mit etwa 32 dB(A) | igorslab.de | OK |
| ASUS TUF | 1 leise | ComputerBase: TUF im Quiet-BIOS „flüsterleise“ | ComputerBase (Bildstrecke 116171) | OK |
| Sapphire Nitro+ / PowerColor Hellhound | 1 leise | große Kühler, nach Testberichten durchweg leise; hier keine eigene Messung gesucht | – | OK (plausibel) |
| PowerColor Reaper (9070 / 9070 XT) | 3 laut | Tom's Hardware lobt bei der Reaper die Lautstärke („noch beeindruckender als die Temperaturen“, laut Suchauszug), HotHardware: „Lautstärke kein Problem“. Belege für „laut“ fehlen | Tom's Hardware (RX 9070 XT Review S. 8), HotHardware | UNKLAR, eher 2 |
| ASUS Dual (5060 Ti / 5070) | 3 laut | keine Messung gefunden, die „laut“ belegt (TPU hat das Dual-Modell nicht in der Tabelle) | – | UNKLAR |
| Gigabyte Windforce (SFF) | 3 laut | keine eindeutige Messung gefunden | – | UNKLAR |

## 2. Chip-Einträge ohne Modellliste

| id | Feld | im Code | gefunden | Quelle | Urteil |
|---|---|---|---|---|---|
| rtx5050 | price | 407 | 406,90 (Gigabyte Windforce OC 8G) | HWD 29554 | OK |
| rx9060xt-8 | price | 514 | 513,90 (Sapphire Pulse 8GB) | PCGH, HWD 29142 | OK |
| rtx5060 | price | 479 | 459,00 (Gainward Ghost, Gigabyte Windforce OC 8G) | PCGH, HWD 28425 | Abweichung −4,2 % (in Toleranz, aber nicht der Bestpreis) → 459 empfohlen |
| rtx5060ti-8 | price | 522 | 521,90 (Gainward Ghost 8GB) | HWD 26846 | OK |
| rx9070gre | price | 638 | 638,00 (Gigabyte Gaming OC 12G) | HWD 33593 | OK |
| arc-b570 | price | 287 | 291,00 (ASRock Challenger OC) | PCGH | OK (+1,4 %) |
| arc-b580 | price | 397 | 386,90 (ASRock Challenger OC) | PCGH | OK (−2,6 %) |
| rtx5090 | price | 7637 | 7702,58 (Aorus Stealth ICE), danach 7708,58 (TUF OC) | HWD 29659/25076 | OK (−0,9 %) |

## 3. Neue Kategorien

### monitor
| id | Feld | im Code | gefunden | Quelle | Urteil |
|---|---|---|---|---|---|
| aoc-q27g4xf | Preis; 27", 1440p, 180 Hz, IPS | 149 | 148,90 | PCGH | OK |
| sam-g55c | Preis; 27", 1440p, 165 Hz, VA | 130 | 129,90 (26,9") | PCGH | OK |
| sam-g50sf | Preis; QD-OLED 180 Hz, True Black 400 | 283 | 282,90 | PCGH | OK |
| lg-27gs95qe | Preis; 26,5", 240 Hz, WOLED | 545 | 545,00 | PCGH | OK |
| sam-g55t-34 | Preis; 34", UWQHD, 165 Hz, VA | 214 | 213,90 (2023er-Modell) | PCGH | OK |
| lg-27us550 | Preis; 4K, 60 Hz, IPS, HDR10 | 169 | 168,99 | PCGH | OK |
| dell-s2725qs | Preis; 4K, 120 Hz, IPS | 278 | 277,72 | PCGH | OK |
| gb-m27up | Preis; 4K, 160 Hz | 303 | 302,88 (M27UP ICE: 269) | PCGH | OK |
| asus-xg27ucs | Preis; 4K, 160 Hz, Fast IPS | 312 | 311,99 | PCGH | OK |
| asus-pa279crv | Preis | 399 | 398,90 | PCGH | OK |
| msi-272urx | Preis; 4K, 240 Hz, QD-OLED | 699 | 699,00 (272URXDE); 272URX ohne DE: 907,85 | PCGH | OK |

### fans
| id | Feld | im Code | gefunden | Quelle | Urteil |
|---|---|---|---|---|---|
| ar-p12pro-5 | Preis 5er-Pack | 18 | 17,59 (ACFAN00307A, 5er-Pack) | PCGH | OK – der niedrige Preis stimmt |
| ar-p14pro-5 | Preis 5er-Pack | 26 | 25,98 (ACFAN00319A, 5er-Pack) | PCGH | OK – der niedrige Preis stimmt |
| ar-p12pro-argb-3 | Preis 3er-Pack | 21 | 21,01 | PCGH | OK |
| bq-sw4-120 | Preis | 20 | 20,39 | PCGH | OK |
| nc-a12x25g2 | Preis | 33 | 32,89 | PCGH | OK |
| ll-tl-wl-3 | Preis 3er-Pack | 85 | 84,90 schwarz (weiß 69,90) | PCGH | OK |

### os
| id | Feld | im Code | gefunden | Quelle | Urteil |
|---|---|---|---|---|---|
| win11-home | Preis DSP/SB deutsch | 125 | 124,99 | PCGH | OK |
| win11-pro | Preis DSP/SB deutsch | 167 | 166,59 | PCGH | OK |

### kbm
| id | Feld | im Code | gefunden | Quelle | Urteil |
|---|---|---|---|---|---|
| lg-mk295 | Preis | 26 | 25,69 (DE) | PCGH | OK |
| lg-mk540 | Preis | 34 | 34,29 (DE) | PCGH | OK |
| set-kc200-g305 | Preis | 80 (44,95 + 34,99) | 44,95 + 34,99 | PCGH | OK |
| lg-mxkeys-combo | Preis | 136 | 135,94 (DE) | PCGH | OK |
| set-gprox-g502x | Preis | 212 (129,99 + 81,99) | 129,99 schwarz DE + 81,99 | PCGH | OK |

## 4. Neue Kernteile

| id | Feld | im Code | gefunden | Quelle | Urteil |
|---|---|---|---|---|---|
| asr-b850m-prors | price | 130 | 129,90 | PCGH | OK |
| asr-b850m-prors | m2 | 2 | 3 (1× PCIe 5.0 x4, 2× PCIe 4.0 x4) | PCGH-Produktseite, tech4gamers-Test | FALSCH |
| asr-b850m-prors | form / gen5m2 / wifi | mATX / ja / ja | µATX / ja / Wi-Fi 6E | PCGH | OK |
| asus-b850-tufplus | price / form / m2 / gen5 / wifi | 172 / ATX / 3 / ja / ja | 171,90 / ATX / 3 (1× 5.0, 2× 4.0) / ja / Wi-Fi 7 | PCGH | OK |
| gb-b850m-elite-ice | price / form / m2 / gen5 / wifi | 178 / mATX / 2 / ja / ja | 177,75 / µATX / 2 (1× 5.0, 1× 4.0) / ja / Wi-Fi 6E | PCGH | OK |
| msi-b850m-mortar | price / form / m2 / gen5 / wifi | 210 / mATX / 3 / ja / ja | 210,31 / mATX / 3 (2× 5.0 x4, 1× 4.0 x2) / ja / Wi-Fi 7 | PCGH, MSI-Datenblatt | OK |
| c-2x16-6000c30 | price | 620 | 613,61 (CMK32GX5M2B6000C30) | PCGH | OK |
| k-2x16-6000c30 | price | 629 | 629,00 (schwarz, Kit) | PCGH | OK |
| k-2x32-6000c30 | price | 1310 | 1310,00 | PCGH | OK |
| crucial-p310-2 | price / read | 250 / 7100 | 249,99 / 7100 laut Hersteller | PCGH | OK |
| kc3000-2 | price / read | 340 / 7000 | 339,90 / 7000 | PCGH | OK |
| sn850x-2 | price / read | 381 / 7300 | 380,65 / 7300 | PCGH | OK |
| cor-rm750e | price | 105 | 104,89 | PCGH | OK |
| msi-a850gl | price | 107 | 107,09 | PCGH | OK |
| bq-pp13m-850 | price | 119 | 119,00 | PCGH | OK |
| tr-ps120se | price / h | 38 / 154 | 37,59 / 154 mm | PCGH, Thermalright | OK |
| ar-lf3-280 | price | 66 | 65,89 | PCGH | OK |
| nc-u12a | price / h | 125 / 158 | 124,57 / 158 mm | PCGH, Noctua | OK |
| nzxt-h5flow24 | price | 70 | 69,89 (schwarz) | PCGH | OK |
| nzxt-h5flow24 | gpu | 365 | 410 mm | NZXT-Support „H5 Flow 2024 Specs“, Geizhals/PCGH | FALSCH (365 mm gilt für das Modell von 2022) |
| nzxt-h5flow24 | cooler | 165 | 170 mm | NZXT, Geizhals/PCGH | FALSCH (165 mm gilt für das Modell von 2022) |
| ph-xtpro-ultra | price / gpu / cooler | 71 / 415 / 184 | 70,86 / 415 / 184 | PCGH | OK |
| ll-lancool207 | price / gpu / cooler | 83 / 375 / 180 | 83,47 / 375 / 180 | PCGH, Lian Li | OK |
| bq-shb800dx | price / gpu / cooler / rads | 100 / 430 / 180 / bis 420 | 99,90 / 430 / 180 / bis 420; Geizhals führt es als „schallgedämmt“ | PCGH, Overclockers UK | OK |
| fd-meshify3 | price / cooler | 125 / 173 | 124,89 / 173 (Fractal; Geizhals 171,5) | PCGH, Fractal | OK |
| fd-meshify3 | gpu | 400 | 349 mm | Fractal-Produktseite, Geizhals/PCGH | FALSCH |
| fd-meshify3 | rads | 240/280/360 | Front 240/280/360, Deckel 240/280 | Fractal | OK |

## Konkrete Korrekturen

- asr-b850m-prors, m2: 2 → **3**
- nzxt-h5flow24, gpu: 365 → **410**
- nzxt-h5flow24, cooler: 165 → **170**
- fd-meshify3, gpu: 400 → **349**
- rtx5060, price: 479 → **459** (Bestpreis; die alte Zahl liegt noch innerhalb von 5 %)
- GPU_MODELS, Lautstärke PowerColor Reaper (rx9070 / rx9070xt): 3 → **2** (empfohlen; Tom's Hardware und HotHardware beschreiben die Reaper als leise)
- optional GPU_MODELS ergänzen: ["rtx5070ti","inno3d-rtx-5070-ti-x3","INNO3D RTX 5070 Ti X3","INNO3D",1350,300,2,3] und ["rx9070xt","xfx-swift-rx-9070-xt-white-triple-fan-gaming-e","XFX Swift RX 9070 XT White Triple Fan Gaming Edition","XFX",875,325,4,2]. Sonst ist der ab-Preis der Chips 3–4 % zu hoch.

## Umsetzung Runde 6
- **Übernommen:** ASRock B850M Pro RS WiFi 3× M.2; NZXT H5 Flow (2024) GPU 410 mm, Kühler 170 mm; Fractal Meshify 3 GPU 349 mm; RTX 5060 459 €.
- **Lautstärke:** PowerColor Reaper RX 9070 / 9070 XT von „laut“ auf „mittel“.
- **Ergänzt:** INNO3D RTX 5070 Ti X3 (1.350 €) und XFX Swift RX 9070 XT White Triple Fan (875 €); die Ab-Preise der Chips sinken entsprechend.
- **Nicht geändert:** ASUS Dual und Gigabyte Windforce SFF bleiben „laut“ (kein Beleg in beide Richtungen; Einordnung nach Kühlerserie).
- **Tests danach:** 108 Regler-Kombinationen ohne Konflikt oder Warnung, 7 Budget-Szenarien bestanden, alle Oberflächentests ohne JavaScript-Fehler, 400 px ohne horizontales Scrollen.
