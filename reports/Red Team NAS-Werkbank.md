# Red Team: NAS-Werkbank (Prüfstand 25.11.2026, Markt Deutschland)

Geprüfte Datei: `nas-konfigurator/index.html` (DB, CLOUD, checkAll, PRESETS).
Methode: Websuche mit Geizhals-, heise-Preisvergleich-, billiger.de- und Händler-Snapshots sowie Hersteller-Datenblättern und Fachpresse. Live-Seiten konnten nicht direkt geöffnet werden; viele Geizhals-Snapshots sind einige Wochen alt. Werte ohne belastbaren Beleg sind als Schätzung markiert (kein „Preis geprüft“).

## Zusammenfassung

- **Festplatten waren um den Faktor 1,5 bis 2,3 zu billig angesetzt.** Das Tool rechnete mit rund 23 €/TB, der Markt liegt bei etwa 30–55 €/TB. Beispiele: WD Red Plus 8 TB 373 € statt 205 €, IronWolf Pro 24 TB ab 1.008–1.049 € statt 530 €, Exos X20 20 TB ab 1.083 € statt 390 €. US-Tracker melden einen Anstieg des Median-Preises für NAS-Platten um ca. 144 % seit Mitte 2024 (KI-Rechenzentren).
- **Enterprise-Platten sind nicht mehr die günstigsten pro TB.** Exos X20 (ca. 54 €/TB) und MG10 (ca. 45 €/TB) liegen teils über NAS-Platten wie Toshiba N300 8 TB (ca. 31 €/TB). Die Texte „günstigster Preis pro TB“ wurden entfernt.
- **Arbeitsspeicher war 2–4× zu billig.** DDR5-SO-DIMM 32 GB kostet ca. 548–555 € statt 165 €. Die Schätzungen für alle RAM-Module wurden angehoben.
- **Consumer-SSDs** wurden an die bereits geprüften Preise der PC-Werkbank angeglichen (NV3 1 TB 147 €, 990 EVO Plus 1 TB 189 €).
- **Datenblattfehler:** DS1525+ hat keinen PCIe-Slot, sondern einen Synology-Steckplatz für das E10G22-T1-Mini. DS225+ hat 2× M.2 (im Tool 0). QNAP TS-264 hat 2 SO-DIMM-Plätze und lässt sich auf 16 GB erweitern (im Tool verlötet). Asustor AS6804T hat 2× 10 GbE **und** 2× 5 GbE.
- **Synology DSM 7.3:** Fremd-Festplatten und 2,5″-SATA-SSDs sind auf der 2025er Plus-Serie wieder für Speicherpools erlaubt. M.2-SSDs anderer Hersteller sind laut mehreren Berichten weiter eingeschränkt, und die Kompatibilitätsliste nennt weiterhin vor allem Synology-Platten (heise). Hinweistexte und eine neue Warnung für Fremd-M.2 auf diesen Modellen wurden ergänzt.
- **Modellpflege:** UGREEN DXP4800 (N100) wird nicht mehr verkauft (jetzt Auslaufmodell). Neu aufgenommen: UGREEN DXP4800 Pro (Core i3-1315U). DS923+ ist als Auslaufmodell markiert, Nachfolger ist die DS925+.
- **Vorlagen-Budgets** wurden an die neuen Preise angepasst (z. B. Familien-NAS 2.000 € statt 1.400 €).

## Befunde

| Schwere | Teil-id | Feld | Wert im Tool | korrigiert | Beleg |
|---|---|---|---|---|---|
| hoch | wd-rp8 | price | 205 | 373 (geprüft) | heise-Preisvergleich WD80EFPX ab 372,99–385,50 € |
| hoch | wd-rp12 | price | 285 | 517 (geprüft) | Geizhals WD120EFGX ab 517 € |
| hoch | sg-iwp24 | price | 530 | 1.030 (geprüft) | Geizhals ST24000NT002 ab 1.008–1.049 € |
| hoch | sg-x20 | price | 390 | 1.083 (geprüft) | Geizhals ST20000NM007D ab 1.083 € (14.10.2026) |
| hoch | tosh-mg10-20 | price | 395 | 899 (geprüft) | Geizhals MG10ACA20TE ab 899 € |
| mittel | tosh-n300-8 | price | 185 | 250 (geprüft) | Geizhals HDWG480 ab 249,99 € |
| mittel | sg-iw4 | price | 105 | 178 (geprüft) | billiger.de ST4000VN006 ab 177,52 € |
| hoch | sg-iwp16 | price | 365 | 560 (Schätzung) | Coolblue 489–545 €, heise 699 € (widersprüchlich) |
| hoch | wd-rpro24 | price | 560 | 990 (Schätzung) | Coolblue 979 € (nicht mehr lieferbar), Galaxus ca. 985 € |
| hoch | cr-d5-32 | price | 165 | 550 (geprüft) | Geizhals CT32G56C46S5 ab 547,89–554,74 € |
| mittel | cr-d4-16 | price | 75 | 140 (Schätzung) | Geizhals CT16G4SFRA32A ab 139 € (undatiert) |
| mittel | kin-nv3-1, sam-990ep-1 | price | 85 / 115 | 147 / 189 (geprüft) | Preise der PC-Werkbank (Red Team 11/2026) |
| mittel | syn-ds925p | price | 650 | 774 (geprüft) | Geizhals ab 774 € |
| mittel | syn-ds1825p | price | 1.080 | 1.355 (geprüft) | Geizhals ab 1.351,70–1.355,61 € |
| niedrig | syn-ds425p | price | 490 | 519 (geprüft) | Geizhals ab 519 € (23.11.2026) |
| niedrig | syn-ds225p | price, m2 | 340, 0 | 355, 2 (geprüft) | Geizhals ab 354,68 €; Synology-Datenblatt 2× M.2 |
| hoch | syn-ds1525p | pcie, mini | PCIe-Slot | nur Netzwerk-Upgrade-Steckplatz (E10G22-T1-Mini) | Synology-Spezifikation „Gen3 x2 network upgrade slot“ |
| mittel | syn-ds1525p | price | 870 | 980 (Schätzung) | Deal 849,94 € (08/2026), Geizhals.at ab 1.057,84 € |
| mittel | qnap-ts464 | price | 560 | 641 (geprüft) | Geizhals ab 641,30 € |
| mittel | qnap-ts264 | RAM | 8 GB verlötet | 2× SO-DIMM, max. 16 GB | QNAP-Spezifikation |
| mittel | ug-dxp4800p | price | 640 | 514 (geprüft) | Geizhals ab 514 € (24.11.2026) |
| niedrig | ug-dxp2800 | price | 340 | 323 (geprüft) | Geizhals ab 322,98 € (24.11.2026) |
| mittel | ug-dxp4800 | eol | – | Auslaufmodell | TechRadar: UGREEN verkauft das Modell nicht mehr |
| mittel | asu-as6804t | lan, price | 2× 10 GbE, 950 | 2× 10 + 2× 5 GbE, 1.100 (Schätzung) | Asustor-Datenblatt, Listenpreis 1.299 $ |
| niedrig | mf-n5pro | price | 1.150 | 1.000 (Schätzung) | Minisforum-Store 899 $ (Sale), früher 1.019–1.299 $ |
| niedrig | hz-bx11…bx41 | mo | 3,90 / 13,10 / 24,30 / 46,40 | 3,81 / 12,97 / 24,75 / 48,31 (geprüft) | Hetzner-Preisseite inkl. 19 % MwSt. |
| mittel | Synology-Regel | Text | „Sperre gelockert“ | Festplatten frei, M.2 weiter eingeschränkt | Tom's Hardware, heise, Android Central |

## Nicht prüfbar (als Schätzung belassen)

Netzwerkkarten, USVs, Synology-RAM und -SSDs, Synology HAT-Platten (Modellnamen wechseln zu HAT3310/HAT3320; HAT3320-8T ca. 427–479 €), TerraMaster-Geräte, QNAP TS-233/TS-433/TS-664/TS-473A, Asustor AS3304T v2/AS5404T, Selbstbau-Bundles, externe USB-Platten, iCloud+ 6/12 TB und Google One 30 TB. Für die Selbstbau-Varianten wurde der RAM-Anteil an die neuen Speicherpreise angepasst.

## Quellen

- [Synology DS1525+ Spezifikation](https://www.synology.com/en-us/products/compare/DS1525+/DS925+)
- [Synology DS225+/DS425+ Vergleich](https://www.synology.com/en-us/products/compare/DS225+/DS425+/DS725+)
- [Tom's Hardware: Synology DSM 7.3](https://www.tomshardware.com/pc-components/nas/synology-walks-back-controversial-compatibility-policy-for-2025-nas-units-third-party-hdd-and-ssd-support-returns-with-diskstation-manager-7-3-update)
- [heise: Synology hebt Sperre nur teilweise auf](https://heise.de/-10742895)
- [Asustor AS6804T Review (HWBusters)](https://hwbusters.com/networking/asustor-lockerstor-4-gen-3-as6804t/)
- [QNAP TS-264 Spezifikation](https://www.qnap.com/en/product/ts-264/specs/hardware)
- [TechRadar: UGREEN DXP4800 Pro](https://techradar.com/computing/ugreen-dxp4800-pro-4-bay-nas-review)
- [Hetzner Storage Box](https://www.hetzner.com/de/storage/storage-box)
- [Geizhals DS925+](https://geizhals.at/synology-diskstation-ds925-a3476790.html), [DS1825+](https://geizhals.de/synology-diskstation-ds1825-a3486593.html), [DS425+](https://geizhals.de/synology-diskstation-ds425-a3498620.html), [DS225+](https://geizhals.at/synology-diskstation-ds225-a3498647.html)
- [Geizhals TS-464-8G](https://geizhals.de/qnap-turbo-station-ts-464-8g-a2872932.html), [DXP4800 Plus](https://geizhals.de/ugreen-dxp4800-plus-a3162929.html), [DXP2800](https://geizhals.de/ugreen-dxp2800-a3162972.html)
- [Geizhals IronWolf Pro 24 TB](https://geizhals.de/seagate-ironwolf-pro-nas-hdd-rescue-24tb-st24000nt002-a3120752.html), [Exos X20](https://geizhals.de/seagate-exos-x-x20-20tb-st20000nm007d-a2648746.html), [MG10 20 TB](https://geizhals.de/2851607), [N300 8 TB](https://geizhals.de/toshiba-n300-nas-systems-8tb-hdwg480xzsta-hdwg480ezsta-a2579337.html)
- [heise-Preisvergleich WD80EFPX](https://preisvergleich.heise.de/western-digital-wd-red-plus-8tb-wd80efpx-a3068091.html), [billiger.de IronWolf 4 TB](https://www.billiger.de/pricelist/5698397890-seagate-ironwolf-4-tb-3-6-st4000vnz06-4-st)
- [Geizhals Crucial SO-DIMM 32 GB DDR5](https://geizhals.de/crucial-so-dimm-32gb-ct32g56c46s5-a2988651.html), [Crucial DDR4 16 GB](https://geizhals.de/2326574)
- [Minisforum N5 Pro (Guru3D)](https://www.guru3d.com/story/minisforum-n5-pro-ai-nas-with-ryzen-ai-9-hx-pro-370-specs-price/)
- [HDD-Preisentwicklung November 2026](https://hddhunt.com/blog/best-hard-drive-deals-november-2026/)

---

# Runde 2: Synology-Schwerpunkt, neue Modelle, RAID-Rechner (25.11.2026)

Zwei unabhängige Prüfer haben die gesamte Datenbank und alle Regeln geprüft: einer alles mit Synology-Bezug (Datenblätter, Spec-Tabs, Zubehörseiten mit „Applied Models“, DDR4-Modulseite, Paketlisten), einer alles Übrige einschließlich der Rechenmodelle. Grundlage für die neuen Synology-Modelle war eine vorgeschaltete Recherche der aktuellen Synology-Produktliste. Alle Befunde sind eingearbeitet.

## Neu aufgenommen
- **Synology:** DS124, DS223j, DS223, DS423, DS725+, DS725neo+, DS925neo+, DS1525neo+, DS1825neo+, DS2422+, RS422+, RS1221+; Auslaufmodelle DS224+, DS423+, DS923+, DS1522+, DS1621+, DS1821+ (Phase Out, werden nicht automatisch gewählt). „neo+“ = gleiche Hardware wie „+“, aber 4 GB non-ECC ab Werk (ComputerBase: „non-ECC option“).
- **Synology-Zubehör:** RAM D4NS01-4G, D4ES04-4G, D4ES03-8G/16G, D4ES02-4G/8G, D4ES01-4G/8G/16G, D4ECSO-2666-16G mit Freigabeliste je Modell; SSD SNV5420-400/800/1600; Karten E10G30-T1/-T2, E10M20-T1, M2D20 mit Freigabeliste je Modell; Erweiterungseinheiten DX517, DX525, DX1222, RX418; HAT3300-4T, HAT3320-8T/20T, HAT3310-12T/16T, HAT5310-20T, HAT5320-24T.
- **Andere:** UGREEN DXP2800 GT, DXP4800 GT; TerraMaster F2-425 Plus, F4-425 Plus, F4-425 Pro.

## Befunde und Korrekturen (Auswahl)

| Schwere | Eintrag | Feld | vorher | korrigiert | Beleg |
|---|---|---|---|---|---|
| hoch | syn-hat12, syn-hat16 | warr | 5 | 3 Jahre (ganze Plus-Serie) | synology.com/products/drives/hdd/plus-hat |
| hoch | DS225+ | m2 | 2 | 0 (laut Spec-Tab kein M.2) | synology.com/products/DS225+#specs |
| hoch | 2025er Plus | Regel M.2 | Warnung | Konflikt: Fremd-M.2 nicht für Cache/neuen Pool (KB „Drive compatibility policies“) | kb.synology.com/en-global/DSM/tutorial/Drive_compatibility_policies |
| mittel | synoRam DS923+, DS1522+, DS1621+, DS1821+, DS2422+, DS423+, RS1221+, neo+ | Freigaben | unvollständig | laut DDR4-Seite ergänzt (D4ES03, D4ES04, D4NS01, D4ECSO) | synology.com/products/DDR4 |
| mittel | RAM-Regel | – | fehlte | Synology verlangt identische Module; Automatik wählt jetzt z. B. 2× D4ES03-16G statt zu mischen (RAM mit Stückzahl) | Spec-Tabs, Memory Notes |
| mittel | syn-rs1221p | exp | 0 | 12 mit RX418 | synology.com/products/RS1221+#specs |
| mittel | syn-e10g30t1 | fits | ohne DS2422+ | DS2422+ ergänzt | synology.com/products/E10G30-T1 |
| mittel | syn-d4es04-4 | price | 281 | 165 € (Geizhals 01.10.–16.11.2026) | geizhals.de/synology-d4es04-4g-a3480615.html |
| mittel | tm-f4-424pro | cpu, slots | i3-N300, 2 | i3-N305, 1 Slot | terra-master.com/products/f4-424-pro |
| mittel | qnap-ts473a | ecc | nein | ECC unterstützt | qnap.com/de-de/product/ts-473a/specs/hardware |
| mittel | qnap-ts264/464/664/233 | idle | 11/14/18/6 | 16/19/21/3,4 W | QNAP-Spezifikationen |
| mittel | qnap-ts464, ug-dxp4800p | price/chk | 641/514 „geprüft“ | 760/560 Schätzung (widersprüchliche Belege) | MediaMarkt AT, heise |
| mittel | sg-iw8, wd-rp8, wd-rp12, wd-rp4, sg-iw4, tosh-n300-8, wd-hc580 | Watt, dB | alte Datenblattwerte | aktuelle Datenblätter WD/Seagate/Toshiba | Herstellerdatenblätter |
| mittel | b2 | Preis | 5,6 €/TB | ca. 6,4 €/TB (6,95 $ seit 1.5.2026) | backblaze.com/cloud-storage/pricing |
| mittel | CLOUD Hetzner 10/20 TB | Jahrespreis | 291,60/556,80 | 297,00/579,72 € | hetzner.com/storage/storage-box |
| niedrig | HAT-Platten, SNV-SSDs | MB/s, Watt, TBW | Schätzwerte | Synology-Datenblätter | Synology HDD-/SSD-Seiten |
| niedrig | SNV5420-1600G | price | 1.200 | 1.789 € | Geizhals Okt. 2026 |
| niedrig | ug-dxp4800pro | ramMax | 64 | 96 GB | TechRadar 09.05.2026 |
| niedrig | mf-n5pro | price | 1.000 | 1.119 € (EU-Shop) | minisforumpc.eu |
| niedrig | tp-tx401, qnap-qm2 | price | 95/290 | 68/230 € | Geizhals/heise, office-partner.de |
| niedrig | Crucial-RAM | Verfügbarkeit | – | Marke eingestellt, „Restbestand“ | storagereview.com |
| niedrig | Unraid-Hinweis | Text | „einmalig 50–250 €“ | 49/109/249 $, nur Lifetime ohne Folgekosten | unraid.net |
| niedrig | speedInfo | RAID 0 mit 2 Platten | 1× | ca. 1,8× | eigene Analyse |

## Bestätigt
- SHR-/SHR-2-Schichtmodell (4+4+8+8 TB → 16 bzw. 8 TB wie im Synology RAID Calculator), Mindestanzahl Platten RAID 1/5/6/10 und RAIDZ1/2/3, Ausfalltoleranz, TB→TiB 0,909, 2,5 GbE ≈ 288 MB/s, dB-Addition, kWh-Formel.
- Synology-Hardware: Schächte, CPUs, RAM-Ausstattung (DS1525+/DS1825+: 1× 8 GB, ein Slot frei), LAN, M.2, PCIe/Upgrade-Slot, Erweiterungseinheiten, Leistungsaufnahme im Ruhezustand, VMM/Container Manager, Phase-Out-Status.
- TrueNAS Community Edition kostenlos; Plex mit AMD-iGPU eingeschränkt.

## Bewusst abweichend / nicht prüfbar
- Grundgeräusch der Synology-Geräte liegt bewusst 2–3 dB unter dem Datenblatt, weil das Datenblatt mit eingebauten Platten misst und das Tool das Plattengeräusch separat addiert.
- Gleichzeitige 4K-Transcodes (t4) sind Schätzungen; kein Hersteller nennt Zahlen.
- Preise ohne belastbaren Beleg (als Schätzung markiert): DS725+, DS725neo+, DS1525neo+, DS1825neo+, DS2422+, RS422+, RS1221+, Auslaufmodelle, HAT3320-20T, SNV5420-400/800, DX517, DX1222, RX418, ältere RAM-Module.
- Ob Synology Fremd-Netzwerkkarten offiziell zulässt, ließ sich nicht belegen; das Tool meldet sie als Konflikt (kein Treiber in DSM).
- DX525: Synology nennt kompatible Laufwerke als Pflicht; ob DSM 7.3 das lockert, ist offen (Hinweis im Tool).
