# Faktencheck: S25 Ultra Sicherheitskompass

Stand: 8. November 2026 · Prüfgegenstand: alle Angaben der Anleitung `s25-ultra-sicherheit/index.html`, die **nicht** schon im Recherchebericht `reports/S25 Ultra One UI 9 Sicherheit.md` geprüft wurden. Das sind die Kapitel 12 bis 18, die Profi-Tipps, die Tricks, Telefonnummern, die Rechtsangabe sowie die Abschnitte „Grenzen“ und „Schutz-Schichten“.

Methode: Jede Angabe wurde mit Websuche gegen Herstellerseiten (Samsung, Google, Signal, WhatsApp), Behörden und Fachpresse geprüft. Insgesamt waren es 33 Recherchen.

**Ergebnis:**
- **19 Angaben bestätigt**, davon eine mit kleiner Ergänzung (116 116)
- **15 Angaben korrigiert oder präzisiert**, alle in der Anleitung umgesetzt
- **4 Angaben nicht belegbar.** Sie sind in der Anleitung als unsicher gekennzeichnet oder abgeschwächt.

## Bestätigt

| Angabe | Beleg |
|---|---|
| 116 016 Hilfetelefon Gewalt gegen Frauen, rund um die Uhr | [betanet](https://www.betanet.de/gewaltschutz-frauen.html), [hilfetelefon.de](https://www.hilfetelefon.de/) |
| 116 006 Opfer-Telefon Weisser Ring | [Bundesnetzagentur](https://www.bundesnetzagentur.de/DE/Fachthemen/Telekommunikation/Nummerierung/116xyz/116006.html?nn=697214) |
| 116 116 zentraler Sperr-Notruf (ergänzt: aus dem Ausland +49 116 116) | [sperr-notruf.de](https://www.sperr-notruf.de/media/pages/presse/a92052d091-1772612809/mehr-als-jeder-zweite-kennt-die-116-116.pdf), [test.de](https://www.test.de/EC-Karte-Girocard-Kreditkarte-So-sperren-Sie-die-Karte-schnell-und-sicher-4571575-0/) |
| LG München I, 20.01.2022, Az. 3 O 17493/20: Google Fonts dynamisch eingebunden = DSGVO-Verstoß | [IHK](https://www.ihk.de/bergische/recht-und-steuern/wettbewerbsrecht/google-fonts-5646176), [Dr. Datenschutz](https://www.dr-datenschutz.de/schadensersatz-urteil-google-fonts-und-die-dsgvo/) |
| NSA empfiehlt wöchentlichen Neustart (Mobile Device Best Practices, 2020) | [Zimperium](https://www.zimperium.com/blog/mobile-security-alert-nsa-recommends-weekly-smartphone-reboots), [Panda Security](https://www.pandasecurity.com/en/mediacenter/nsa-recommends-restart-mobile-devices-weekly/) |
| Privates DNS mit `dns.quad9.net`, Prüfseite `on.quad9.net` | [Quad9 Docs](https://docs.quad9.net/Setup_Guides/Android/Android_9%2B_%28Encrypted%29/), [Quad9 FAQ](https://docs.quad9.net/FAQs/) |
| WhatsApp: Datenschutz › Erweitert › IP-Adresse in Anrufen schützen / Link-Vorschau | [WhatsApp FAQ](https://whatsapp.com/faq/en/android/23225461), [Trusted Reviews](https://www.trustedreviews.com/how-to/how-to-protect-your-ip-address-when-making-whatsapp-calls-4388868) |
| WhatsApp: Konto › Passkeys | [WhatsApp Blog](https://blog.whatsapp.com/one-billion-people-are-now-protected-with-passkeys-on-whatsapp-plus-more-account-security-features) |
| Signal: Benutzernamen, Telefonnummer verbergen | [Signal Support](https://support.signal.org/hc/en-us/articles/6712070553754) |
| Google Erweitertes Schutzprogramm, inzwischen auch mit Passkeys; schränkt Drittanbieter-Apps ein | [Google Workspace Hilfe](https://knowledge.workspace.google.com/admin/security/protect-users-with-the-advanced-protection-program), [Yahoo/Engadget](https://news.yahoo.com/google-expands-passkey-support-advanced-151020118.html) |
| Google Inaktivitätsmanager (bis zu 10 Vertrauenspersonen) | [Google Hilfe](https://support.google.com/accounts/answer/3036546) |
| My Ad Center wirkt auf allen angemeldeten Geräten | [Google Hilfe](https://support.google.com/My-Ad-Center-Help/answer/12155656) |
| Android entzieht ungenutzten Apps Berechtigungen („App-Aktivität pausieren, wenn nicht verwendet“) | [Google Android-Hilfe](https://support.google.com/android/answer/13627979) |
| Samsung-Sicherheitsbulletins mit Schweregrad „Critical“ | [Samsung Risk Classification](https://security.samsungmobile.com/securityReportingRiskClassification.smsb) |
| Knox Vault: eigener Prozessor und Speicher, bremst Rateversuche, auf der S25-Serie | [Samsung Knox Docs](https://docs.samsungknox.com/admin/fundamentals/whitepaper/samsung-knox-for-android/core-platform-security/knox-vault), [Samsung S25](https://www.samsung.com/sg/business/insights/samsung-galaxy-s25-the-smart-choice-for-data-security/) |
| eSIM-Abfrage beim Zurücksetzen (Standard: behalten) | [Verizon](https://www.verizon.com/support/knowledge-base-300713/), [Samsung Community](https://eu.community.samsung.com/t5/galaxy-s24-series/does-hard-reset-remove-the-esim/m-p/12422612) |
| Samsung Internet Smart Anti-Tracking | [Samsung Newsroom](https://news.samsung.com/global/samsung-internet-17-0-puts-privacy-and-security-front-and-center) |
| Kamera-Einstellung „Standort-Tags“ | [Samsung Support](https://www.samsung.com/ae/support/mobile-devices/how-can-i-activate-location-tags-on-my-galaxy-device/) |
| IMSI-Catcher erzwingen 2G-Downgrade | [Google Security Blog](https://security.googleblog.com/2023/08/android-14-introduces-first-of-its-kind.html), [EFF](https://www.eff.org/deeplinks/2023/09/apple-and-google-are-introducing-new-ways-defeat-cell-site-simulators-it-enough) |

## Korrigiert (in der Anleitung umgesetzt)

| # | Stelle | Vorher | Jetzt | Beleg |
|---|---|---|---|---|
| 1 | Kap. 4 | „Netzwerk und Sicherheit sperren“ verhindert sicher den Flugmodus | Pfad präzisiert. Die Wirkung ist laut Nutzerberichten uneinheitlich, deshalb mit Testanleitung. | [Samsung IE](https://www.samsung.com/ie/support/mobile-devices/how-to-use-security-settings/), [Samsung Community](https://eu.community.samsung.com/t5/galaxy-s25-series/request-pin-to-activate-flight-mode/td-p/11689417) |
| 2 | Kap. 4 | Extend Unlock „unter den sicheren Sperreinstellungen“ | Eigener Menüpunkt unter Sperrbildschirm und AOD. Dazu Samsungs Warnung, dass die Erkennung am Körper Personen nicht unterscheidet. | [Samsung AE](https://www.samsung.com/ae/support/mobile-devices/what-is-extend-unlock-feature-and-how-do-i-set-it-on/) |
| 3 | Kap. 6 | Google-Sicherung unter „Google › Sicherung“ | Samsung-Pfad „Konten und Sicherung › Google Drive“, Alternative genannt, Auto-Bedingungen und 57-Tage-Löschung ergänzt | [Samsung UK](https://www.samsung.com/uk/support/apps-services/how-to-use-google-one-to-back-up-your-galaxy-phones-data/) |
| 4 | Kap. 11 | WhatsApp: sechsstellige PIN | PIN oder (neu) längeres Passwort | [PCWorld](https://www.pcworld.com/article/3224374/keep-hackers-out-of-your-whatsapp-account-with-these-3-steps.html), [WABetaInfo](https://wabetainfo.com/whatsapp-launches-three-new-account-security-updates/) |
| 5 | Kap. 11 | Signal-Registrierungssperre ohne Risiko | Warnung: Wer die PIN vergisst, ist bis zu 7 Tage ausgesperrt | [Signal Support](https://support.signal.org/hc/en-us/articles/360007059792) |
| 6 | Kap. 11 Tipp | Nur „Wer kann mich finden“ | Reihenfolge: zuerst „Wer kann meine Nummer sehen“ | [Signal Support](https://support.signal.org/hc/en-us/articles/6829998083994) |
| 7 | Kap. 12 Tipp | Jedes KI-bearbeitete Foto hat ein Wasserzeichen | Nur bei größeren Änderungen. Zusätzlich C2PA-Metadaten, das Wasserzeichen ist leicht entfernbar. | [Tom's Guide](https://www.tomsguide.com/news/samsung-galaxy-s24s-big-ai-safety-feature-has-a-glaring-flaw), [C2PA Viewer](https://c2paviewer.com/articles/samsung-galaxy-s25-c2pa) |
| 8 | Kap. 13 Tipp | Ausgeblendeter Ordner „vollständig gesperrt“ | Daten verschlüsselt und Apps gestoppt, einzelne Benachrichtigungen möglich | [Samsung Knox Docs](https://docs.samsungknox.com/secure-folder/get-started/show-and-hide-secure-folder) |
| 9 | Kap. 15 | Find Hub sicher als Geräteadministrator gelistet | „gegebenenfalls“, weil es nicht belegt ist | – (kein Beleg gefunden) |
| 10 | Kap. 16 | WLAN-/Bluetooth-Suche aus ohne Nebenwirkung | Nebenwirkung auf Routinen ergänzt | [Samsung Community](https://r2.community.samsung.com/t5/Galaxy-S/Location-WiFi-based-Routines-issue/td-p/19340769) |
| 11 | Kap. 16 | Smart Anti-Tracking „einschalten“ | „prüfen“, weil es in Europa meist schon an ist | [Samsung Newsroom](https://news.samsung.com/global/samsung-internet-17-0-puts-privacy-and-security-front-and-center) |
| 12 | Kap. 17 | Pfad „SIM-Kartensperre einrichten“ | Neuer Pfad „SIM-Kartensicherheit“, alter Name als Alternative | [Verizon S25 FE](https://www.verizon.com/support/knowledge-base-310817/), [EE S25](https://deviceguides.ee.co.uk/samsung/galaxy-s25-android-15/basic-functions/turning-use-of-pin-on-or-off) |
| 13 | Trick 09 | „Ladesäulen können Daten abgreifen“ | Theoretisch möglich, kaum belegte Fälle, geringes Risiko | [Dark Reading](https://www.darkreading.com/ics-ot-security/fbi-fcc-warn-juice-jacking-public-chargers-risk), [Sophos](https://www.sophos.com/en-us/blog/fbi-and-fcc-warn-about-juicejacking-but-just-how-useful-is-their-advice) |
| 14 | Trick 10 | 2G komplett blockiert | Notrufe weiterhin über 2G möglich | [Google Security Blog](https://security.googleblog.com/2023/08/android-14-introduces-first-of-its-kind.html) |
| 15 | Grenzen | Amnesty Security Lab für alle | Hilft vor allem der Zivilgesellschaft (Journalismus, Aktivismus, Menschenrechtsarbeit), dazu Access Now Helpline | [Amnesty Security Lab](https://securitylab.amnesty.org/latest/2024/05/support-against-digital-attacks-how-the-security-lab-can-help/) |

## Nicht belegbar (gekennzeichnet oder abgeschwächt)

- Deutsche Menünamen der One-UI-9-Oberfläche: Samsung DE veröffentlicht sie bisher nur für One UI 7.
- Find Hub als Geräteadministrator-App auf Samsung.
- WhatsApp-Pfad zum Ende-zu-Ende-verschlüsselten Backup, nur von PCWorld belegt.
- Verhalten von „Inhalt ausblenden“ auf dem Sperrbildschirm unter One UI 7/9. Nutzer berichten von Abweichungen ([SamMobile](https://www.sammobile.com/news/where-did-my-lock-screen-notifications-go-in-one-ui-7)).

## Empfehlung

Vor der breiten Weitergabe sollte jemand die markierten Pfade einmal an einem deutschen S25 Ultra mit One UI 9 durchtippen. Besonders zu prüfen sind „Netzwerk und Sicherheit sperren“ (mit Flugmodus-Test), „Entsperrung verlängern“, „SIM-Kartensicherheit“ und die Google-Sicherung.
