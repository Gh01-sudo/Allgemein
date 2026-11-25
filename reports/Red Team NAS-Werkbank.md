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
