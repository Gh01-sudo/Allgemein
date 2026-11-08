# S25 Ultra (One UI 9 / Android 17, Germany, Nov 2026): extra security tricks and fact-check of index.html

Scope: the guide is /home/user/Allgemein/s25-ultra-sicherheit/index.html (11 chapters in `CH`, 14 tricks in `TRICKS`, a 4-message quiz `Q`, and a 6-step emergency plan in `#notfall`). Some primary pages (SamMobile, Android Authority, Sammy Fans) returned HTTP 403, so a few findings rest on search snippets. Those are marked as such.

## Q1: Which lesser-known built-in features are worth adding?

### Takeaway
Several legitimate features have solid sources and are missing from the guide: Quick Share "Contacts only" and Private Sharing, the Remote Lock security question, scheduled restarts (the correct Samsung name is "Restart on schedule"), unknown SmartTag detection in SmartThings Find, Secure Wi-Fi, the "permissions used in the last 24 hours" view, and Maximum restrictions' link and attachment blocking in messages (also missing). Many other candidates in the brief (Private DNS, MAC randomization, clipboard alerts, Modes & Routines, Good Lock, USB "charging only", Samsung Health encryption, Sensors off, Emergency SOS, medical info) are real Android/One UI features, but I could not get a source for their exact One UI 9 paths in this session.

### Cited Findings
- **Quick Share, "Who can share with you":** the options are "Contacts only" and "Anyone nearby". "Contacts only" limits incoming shares to saved contacts who use a Samsung account. Devices signed in to your own Samsung account can always send. — [Samsung US: Share content with your contacts](https://www.samsung.com/us/support/answer/ANS10002890/)
- **Quick Share Private Sharing:** a separate mode in the three-dot menu, limited to 20 files / 200 MB at a time. — [Samsung US: Private Share guide](https://www.samsung.com/us/support/answer/ANS10007269/%20); [SamMobile: Quick Share private sharing](https://sammobile.com/news/samsung-quick-share-private-sharing-samsung-cloud-encryption) (snippet only)
- **Remote Lock (android.com/lock):** you enter the phone number, pass a reCAPTCHA, and add the answer to the security question if you set one. Setting up the question needs biometric authentication, and the question is optional. Requirements: a screen lock, an active SIM, a verified phone number, Find Hub turned on, and an internet connection. An offline phone locks once it comes back online. You can use it twice in 24 hours, and only the screen lock on the phone undoes it. — [Google Android Help: Protect your personal data against theft](https://support.google.com/android/answer/15146908?hl=en); [android.com/lock](https://android.com/lock)
- **Tip worth adding:** set the Remote Lock security question in advance. Without it, anyone who knows your number can lock your phone, though only you can unlock it with your screen lock. — [Google Android Help](https://support.google.com/android/answer/15146908?hl=en); [Android Authority: Remote Lock security question](https://www.androidauthority.com/google-remote-lock-security-question-3556828) (snippet only)
- **Identity Check protects more than biometrics:** saved passwords, screen lock and biometric changes, factory reset, turning off Find Hub or theft protection, and adding or removing Google accounts. Outside trusted places, "Verify it's you" prompts accept only biometrics, not the PIN. — [Google Android Help](https://support.google.com/android/answer/15146908?hl=en)
- **Theft Detection Lock limits:** it does not run while the screen is already locked. It may not trigger when a stable Wi-Fi or Bluetooth connection is present, or after repeated locks. Offline Device Lock can lock the screen at most twice in 24 hours. — [Google Android Help](https://support.google.com/android/answer/15146908?hl=en)
- **Scheduled restart:** Settings › Device care (or Battery and device care) › Auto optimisation › "Restart on schedule" (choose days and time) and "Restart when needed". The restart runs only when the phone is idle, the screen is off and the battery is at least 30 %. Afterwards the phone needs the PIN. — [Samsung UK: How to automatically optimise your Galaxy device](https://www.samsung.com/uk/support/mobile-devices/how-to-automatically-optimise-your-galaxy-device); [Samsung US: Device Care](https://www.samsung.com/us/support/answer/ANS10001953)
- **72-hour inactivity restart:** Google Play services 25.14 (April 2025) restarts a phone that has stayed locked for three days, which puts it back in the "Before First Unlock" state. — [9to5Google](https://9to5google.com/2025/04/14/android-auto-restart-security/); [heise](https://heise.de/-10353005); [SamMobile](https://www.sammobile.com/news/your-galaxy-phone-will-now-restart-automatically-if-locked-for-three-days/) (snippet only)
- **Samsung DE names for the security menu:** "Sicherheits- und Datenschutz-Dashboard", "Schutz für verlorene Geräte", "App-Sicherheit", "Weitere Sicherheitseinstellungen", "Sicherer Ordner", "Sicheres WLAN", "Samsung Pass", "In den letzten 24 Stunden verwendete Berechtigungen", "Berechtigungsverwaltung", "Zusätzliche Datenschutzkontrollen", "Weitere Datenschutzeinstellungen", "Unbekannte SmartTags erkennen" › "Scannen". — [Samsung DE: Galaxy Smartphone Sicherheit und Datenschutz](https://www.samsung.com/de/support/mobile-devices/galaxy-smartphone-sicherheit-und-datenschutz/)
  - Possible tricks: Secure Wi-Fi (Samsung's VPN for public Wi-Fi), the 24-hour permission log, and scanning for unknown SmartTags.
- **Auto Blocker / Samsung Message Guard:** Auto Blocker blocks apps from unauthorised sources, commands over USB and unauthorised system software over USB. Message Guard blocks malicious payloads disguised as images in messaging apps. — [Samsung Knox Docs: Samsung Auto Blocker](https://docs.samsungknox.com/admin/fundamentals/whitepaper/samsung-knox-mobile-security/system-security/samsung-auto-blocker/)
- **Maximum restrictions also do things the guide does not mention:**
  - block automatic attachment downloads in messages
  - block link previews and tapping links in messages (for trusted senders you have to copy or type the URL)
  - disable device admin apps and **work profiles**
  - turn on "App protection"
  — [Samsung Knox Docs](https://docs.samsungknox.com/admin/fundamentals/whitepaper/samsung-knox-mobile-security/system-security/samsung-auto-blocker/); [Samsung UAE: Auto Blocker](https://www.samsung.com/ae/support/mobile-devices/protect-your-galaxy-device-with-the-new-auto-blocker-feature/)
- **New in One UI 9:**
  - A blue indicator appears when an app accesses your location; tap it in Quick Settings to see which app.
  - Apps classed as malicious are blocked from running until you decide whether to delete them.
  — [Droid Life, 28 Sep 2026](https://www.droid-life.com/2026/09/28/galaxy-s25-ultra-one-ui-9-updates-the-beginning/)
- **Privacy Alerts (on-device AI flags unnecessary background permission access):** officially supported on the Galaxy S25. — [Samsung Newsroom UK: One UI 9 rollout](https://news.samsung.com/uk/samsung-begins-official-rollout-of-one-ui-9-bringing-the-latest-galaxy-experiences-to-more-devices)
- **The S Pen has no Bluetooth,** so there is no remote-control attack surface. "Warn if S Pen is left behind" still works. — [SamMobile, 22 Jan 2025](https://www.sammobile.com/news/it-is-true-galaxy-s25-ultra-s-pen-does-not-have-bluetooth-features/) (snippet); [Android Authority](https://androidauthority.com/galaxy-s25-ultra-s-pen-no-bluetooth-3517872) (snippet)

### Inferences
- The best additions are Quick Share "Contacts only", the Remote Lock security question, "Restart on schedule", Secure Wi-Fi, the 24-hour permission log and SmartTag scanning. All are official, have sources, and are unknown to most users.
- A standalone 2G toggle: Android 12+ has had "Allow 2G" in the SIM settings. On Samsung, 2G blocking sits inside Maximum restrictions (One UI 7+). I found no source for a separate Samsung toggle in One UI 9, so the guide should not promise one.

### Gaps
- **No sourced One UI 9 menu paths for:**
  - Private DNS (e.g. dns.quad9.net)
  - Wi-Fi MAC randomization ("Randomisierte MAC")
  - clipboard access alerts
  - Modes & Routines security automations
  - Good Lock privacy modules
  - USB default "Nur Laden"
  - Samsung Health encryption
  - the "Sensoren aus" developer tile
  - Emergency SOS (5x side button)
  - medical info on the lock screen
  - Bixby/voice wake on the lock screen
  - Samsung Pass passkeys
  - Find My Mobile "Remote unlock" off
  - hidden Gallery albums

  These are real features from earlier One UI versions, but they were not verified for One UI 9 in this session. If they are added, they should be labelled "Pfad kann abweichen".
- **No source for official German UI strings** of the One UI 9 additions (Security Brief, lockdown via buttons, blue dot). The guide's own disclaimer about this is justified.

## Q2: Is each claim in the guide correct for the S25 Ultra in Germany?

### Takeaway
Most claims match the available sources: the Auto Blocker and Theft Protection paths, the 13-attempt lockout policy, Security Brief, the blue location dot, the One UI 9 rollout in Germany, the S Pen without Bluetooth, android.com/lock and smartthingsfind. The biggest problems:
1. **The "KI-Stimmen-Warnung" (AI voice detection) has no source at all.** No source describes an AI-cloned-voice detector for Germany. Scam Detection is not available in Europe, and Call Screening is documented for the Galaxy S26.
2. **Two claims overstate what a feature does.** "Erweiterter Datenschutz" does not encrypt all Samsung Cloud data, and the Secure Folder is not "geschützt durch den Knox-Vault-Chip". The Enhanced Data Protection path is also wrong or incomplete.
3. **The Maximum restrictions side effects are incomplete.** Most importantly, it disables work profiles and device admin apps, and blocks links and attachments in messages.
4. **The button grip is ambiguous.** A short press of Side + Volume Down takes a screenshot. The guide's "etwa eine Sekunde drücken" (press for about one second) may produce a screenshot instead of the lock.

### Cited Findings: claim by claim

**Device box / header**
- *"SM-S938B (Europa)"*: not verified in this session (see Gaps). Droid Life cites the Korean build S938N…, which fits the S938x naming scheme. — [Droid Life](https://www.droid-life.com/2026/09/28/galaxy-s25-ultra-one-ui-9-updates-the-beginning/)
- *"Februar 2025 mit One UI 7"*: plausible, but the S25 Ultra launch was not sourced in this session. Samsung pages refer to S25 features of early 2025.
- *"7 Jahre, bis ca. 2032"*: not verified in this session (see Gaps).
- *"S Pen ohne Bluetooth, keine Fernsteuerung"*: correct. — [SamMobile](https://www.sammobile.com/news/it-is-true-galaxy-s25-ultra-s-pen-does-not-have-bluetooth-features/); [Android Authority](https://androidauthority.com/galaxy-s25-ultra-s-pen-no-bluetooth-3517872)
- *"Knox Vault (eigener Sicherheitschip)"*: no source found in this session (see Gaps).
- *Callout "Samsung verteilt das Update seit Herbst 2026 schrittweise"*: correct.
  - Global rollout began 16 Sept 2026 with the S26 series. — [Samsung Newsroom](https://news.samsung.com/us/samsung-begins-official-rollout-one-ui-9-latest-galaxy-experiences-devices/)
  - The S25 series in Germany got it in October 2026, about a week after the US. — [inside-digital](https://www.inside-digital.de/news/endlich-mega-update-fuer-weitere-galaxy-handys-von-samsung); [PCWelt](https://www.pcwelt.de/article/3237661/samsung-one-ui-9-0-mit-android-17-steht-jetzt-fuer-galaxy-s25-bereit.html)
  - The download size is reported inconsistently: about 500 MB according to [GIGA](https://www.giga.de/tech/besitzer-eines-samsung-galaxy-s25-plus-oder-ultra-erhalten-bald-riesiges-software-update--01M3KV69ANARZWBWREKEESHATK), 3.8 GB according to [GameStar](https://www.gamestar.de/artikel/one-ui-9-galaxy-s25,3460670.html). The guide gives no size, which is fine.

**Ch. 2 Sperre: lockdown grip, lockout policy**
- *"Leiser-Taste und Seitentaste gleichzeitig gedrückt halten … Alternativ in den Schnelleinstellungen auf das Ein/Aus-Symbol"*: supported. Hold both buttons, or tap Power in Quick Settings; biometrics are disabled afterwards. — [Droid Life](https://www.droid-life.com/2026/09/28/galaxy-s25-ultra-one-ui-9-updates-the-beginning/)
  - **Caveat:** Samsung's support page says Side + Volume Down **short press = screenshot** and **long press = power-off menu or Emergency SOS**, depending on the side-button settings. — [Samsung UK: Customise the Side button](https://www.samsung.com/uk/support/mobile-devices/how-to-customise-the-side-button-with-new-features-on-your-galaxy-phone-and-tablet/) (search snippet)
  - During the beta, Samsung changed the lockdown behaviour several times. In beta 4, a toggle was added that controls auto-lockdown for the Quick Settings power button, and by default it does not auto-lock. — [Help Net Security](https://www.helpnetsecurity.com/2026/06/08/samsung-lockdown-mode-power-menu/); [SamMobile](https://www.sammobile.com/news/one-ui-9-lockdown-mode-removed-power-menu/) (snippets only)
  - **So:** Trick 01's "etwa eine Sekunde drücken" risks a screenshot. Say "gedrückt halten, bis das Menü bzw. die Sperre erscheint" (hold until the menu or the lock appears). Also tell readers to test whether a fingerprint is refused afterwards, and keep "Sperrmodus-Option anzeigen" as a fallback.
- *Lockout policy (rising waits up to 24 h, 13th attempt → factory reset only, applies to updated devices only after a factory reset)*: correct. Samsung's table:

  | Failed attempts | Wait |
  |---|---|
  | 5 | 1 min |
  | 6 | 5 min |
  | 7 | 15 min |
  | 8 | 30 min |
  | 9 | 90 min |
  | 10 | 4 h |
  | 11 | 12 h |
  | 12 | 24 h |

  On the 13th failed attempt the device is permanently locked. Updated devices keep their old settings unless fully factory reset after the update. — [Samsung UK: One UI 9.0 lockscreen security policy changes](https://www.samsung.com/uk/support/mobile-devices/one-ui-9-lockscreen-security-policy/); [Notebookcheck](https://www.notebookcheck.net/Samsung-tightens-One-UI-9-0-lockscreen-security-permanent-lockout-after-13-failed-attempts.1351645.0.html)
  - Samsung's page names no way to switch the policy off.
  - After the reset, the Samsung and Google accounts previously on the device must still be verified (FRP). — [Samsung Levant](https://www.samsung.com/levant/support/mobile-devices/one-ui-9-lockscreen-security-policy/) (search snippet)
- Android also asks for the PIN, pattern or password at least every 72 hours, even with fingerprint unlock. — [Notebookcheck One UI 9 security features](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)

**Ch. 3 Automatische Sperre**
- *Path and on by default*: correct. Auto Blocker is on by default on devices released with One UI 6.1.1 or later, and the S25 Ultra launched with One UI 7. — [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)
- *"Maximale Einschränkungen: 2G, ungesicherte WLANs, geteilte Alben, Foto-Standort"*: correct as far as it goes, but **incomplete**. It also:
  - blocks automatic attachment downloads in messages
  - blocks link previews and tapping links in messages
  - turns on App protection
  - **disables device admin apps and work profiles**

  The 2G and Wi-Fi items are One UI 7+ additions. — [Samsung Knox Docs](https://docs.samsungknox.com/admin/fundamentals/whitepaper/samsung-knox-mobile-security/system-security/samsung-auto-blocker/); [Samsung UAE](https://www.samsung.com/ae/support/mobile-devices/protect-your-galaxy-device-with-the-new-auto-blocker-feature/)
- *Note: "Anrufe und SMS … funktionieren dann nicht"*: Notebookcheck says calls, messages **and mobile data** may stop working in 2G-only areas. — [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)
- *Phone mock-up screen 3 ("Die vier Zusatzsperren sind aktiv")*: misleading, since there are more than four.

**Ch. 4 Diebstahlschutz**
- *Path Sicherheit und Datenschutz › Schutz bei Geräteverlust › Diebstahlschutz; Identity Check, Security Delay (1 h), Theft Detection Lock, Offline Device Lock, Remote Lock; needs a Samsung account and Find My Mobile*: correct. — [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)
  - The Samsung DE wording is "Schutz für verlorene Geräte". — [Samsung DE](https://www.samsung.com/de/support/mobile-devices/galaxy-smartphone-sicherheit-und-datenschutz/)
- *"Mein Gerät finden von Google"*: Google's help now calls it **Find Hub** (the German UI string may still differ). — [Google Android Help](https://support.google.com/android/answer/15146908?hl=en)
- *"Wer Ihre PIN ausgespäht hat, muss eine Stunde warten …"*: correct in substance, but incomplete. The delay and Identity Check apply outside trusted places, so at home (a trusted place) the delay does not stop someone. — [Google Android Help](https://support.google.com/android/answer/15146908?hl=en); [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)

**Ch. 6 Security Brief**
- Correct: Now Brief › settings › "Security and privacy" toggle. It needs a Samsung account; sideloaded APKs can trigger false positives; supported back to the S23. — [Engadget](https://www.engadget.com/2279220/samsung-galaxy-one-ui-9-features-you-should-use/); [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)
  - Now Brief also needs a network connection. — [Samsung Newsroom UK](https://news.samsung.com/uk/samsung-begins-official-rollout-of-one-ui-9-bringing-the-latest-galaxy-experiences-to-more-devices)
  - The exact sub-path "⋮ › Einstellungen" is unverified. Engadget calls it "Now Brief content settings".

**Ch. 7 Blue location dot**
- Correct. — [Droid Life](https://www.droid-life.com/2026/09/28/galaxy-s25-ultra-one-ui-9-updates-the-beginning/)
- The guide's path "Datenschutz › Berechtigungsmanager": Samsung DE calls it "Berechtigungsverwaltung". — [Samsung DE](https://www.samsung.com/de/support/mobile-devices/galaxy-smartphone-sicherheit-und-datenschutz/)

**Ch. 8 Anrufprüfung and KI-Stimmen**
- *Call screening path (Telefon › ⋮ › Einstellungen › Anrufprüfung), works in German*: Notebookcheck documents this **for the Galaxy S26** (13 languages incl. German). It does not confirm that the S25 Ultra on One UI 9 gets it. — [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)
- *"One UI 9 analysiert … ob die Stimme künstlich erzeugt ist, und warnt Sie" and the mock-up "Mögliche KI-Stimme erkannt"*: **no source found.**
  - None of Notebookcheck, Engadget, Droid Life or the Samsung Newsroom mentions AI-voice or deepfake detection.
  - Samsung's only call-fraud feature is Google **Scam Detection**. On the S26 it is US-English only, with expansion to Japan, Canada and India. Germany is not listed.
  - [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html); [Samsung Newsroom UK](https://news.samsung.com/uk/samsung-begins-official-rollout-of-one-ui-9-bringing-the-latest-galaxy-experiences-to-more-devices)
  - **Treat as wrong for Germany:** remove the claim, or reword it as "no AI-voice detection in Germany".
  - The chapter's "n:1" (new) badge and the Ch. 9 note ("Security Brief, KI-Stimmen-Warnung laufen ohnehin auf dem Gerät") inherit this error.
- *Scam Detection initially only for Japan, Canada and India*: correct. — [Samsung Newsroom UK](https://news.samsung.com/uk/samsung-begins-official-rollout-of-one-ui-9-bringing-the-latest-galaxy-experiences-to-more-devices)

**Ch. 10 Sicherer Ordner**
- *Path via "Weitere Sicherheitseinstellungen › Sicherer Ordner"*: consistent with the Samsung DE menu names. — [Samsung DE](https://www.samsung.com/de/support/mobile-devices/galaxy-smartphone-sicherheit-und-datenschutz/)
- *"geschützt durch den Knox-Vault-Chip"*: no source found (see Gaps). Probably an overstatement.

**Trick 02 Neustart als Tresor**
- *Path "Automatische Optimierung › Automatischer Neustart"*: the correct item names are **"Restart on schedule" / "Restart when needed"** (the German strings are probably "Neustart nach Zeitplan" / "Neustart bei Bedarf", unverified). It only runs when the phone is idle, the screen is off and the battery is at least 30 %. — [Samsung UK](https://www.samsung.com/uk/support/mobile-devices/how-to-automatically-optimise-your-galaxy-device)
- *"Android startet ein gesperrtes Gerät nach drei Tagen ohne Entsperren ohnehin neu"*: correct (Play services 25.14, April 2025). — [9to5Google](https://9to5google.com/2025/04/14/android-auto-restart-security/)

**Trick 04 Erweiterter Datenschutz**
- *Path "[Name] › Samsung Cloud › Erweiterter Datenschutz"*: Samsung documents **Samsung Cloud app › ⋮ › Einstellungen › Erweiterter Datenschutz › "Sicherungsdaten verschlüsseln"**, or Settings › Security and privacy › More security settings. The path varies by version. — [Samsung US: What is Enhanced data protection](https://www.samsung.com/us/support/answer/ANS10003637); [Samsung UK](https://www.samsung.com/uk/support/mobile-devices/what-is-the-enhanced-data-protection-feature-on-the-galaxy-z-fold5-flip5/)
- *"verschlüsselt Ihre Samsung-Cloud-Daten Ende-zu-Ende"*: **overclaim.**
  - It covers call logs, messages, clock, settings and app backups. Some large data such as message attachments may not be end-to-end encrypted.
  - Backups made before activation can't be restored.
  - Turning it off deletes the E2E backups.
  - The recovery code has 28 characters.
  - It may not be available on some carrier devices or in some regions.
  - [Samsung US](https://www.samsung.com/us/support/answer/ANS10003637) (search snippet)

**Trick 05 Tracker**
- *Path "Sicherheit und Notfall › Unbekannte Tracker-Warnungen" + manual scan*: correct in substance. The menu wording varies ("Benachrichtigungen über unbekannte Bluetooth-Tracker" on One UI 6). — [nextpit](https://www.nextpit.de/android-fuehrt-anti-tracking-funktionen-ein-erkennung-deaktivierung/); [techbone](https://www.techbone.de/samsung/galaxy/bluetooth-benachrichtigung-ueber-unbekannte-tracker-aktivieren-oder-deaktivieren) (snippets)
- Samsung SmartTags are additionally detected via SmartThings Find ("Unbekannte SmartTags erkennen"). — [Samsung DE](https://www.samsung.com/de/support/mobile-devices/galaxy-smartphone-sicherheit-und-datenschutz/)

**Trick 06 App-Fixierung**
- Path consistent with Samsung DE "Weitere Sicherheitseinstellungen". — [Samsung DE](https://www.samsung.com/de/support/mobile-devices/galaxy-smartphone-sicherheit-und-datenschutz/)

**Trick 09 USB**
- The title "Fremdes USB-Ladekabel ist kein Risiko mehr" overstates it.
- Auto Blocker blocks USB commands and USB software installs whether or not Developer options are on. Turning off Developer options is a separate step, not what makes Auto Blocker work. — [Samsung Knox Docs](https://docs.samsungknox.com/admin/fundamentals/whitepaper/samsung-knox-mobile-security/system-security/samsung-auto-blocker/)

**Trick 10 IMSI-Catcher**
- 2G blocking via Maximum restrictions is correct. — [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)
- The tip "Fällt Ihr Handy auf E oder G zurück, ist das ein Warnzeichen" conflicts with it: with 2G blocked, the phone should not connect to 2G at all. (This is my own reasoning, no source.)

**Emergency plan**
- *smartthingsfind.samsung.com, android.com/lock*: valid. — [android.com/lock](https://android.com/lock)
- *"android.com/lock: nur mit Telefonnummer und Sicherheitsfrage"*: incomplete. The security question is optional, and the lock needs an **active SIM**, a verified number, Find Hub on and an internet connection. A thief who removes the SIM can defeat it until the phone is back online with a SIM. — [Google Android Help](https://support.google.com/android/answer/15146908?hl=en)
- *"Offline-Suche über andere Galaxy- und Android-Geräte"*: plausible (Samsung's offline finding plus Google's Find Hub network). Not separately sourced here.
- *"Aktivierungssperre bleibt bestehen" after remote wipe*: FRP verification after a reset is confirmed in a related Samsung context. — [Samsung Levant](https://www.samsung.com/levant/support/mobile-devices/one-ui-9-lockscreen-security-policy/) (snippet)
- *116 116*: not verified in this session (see Gaps).

**Quiz**
- The cross-references are internally consistent: "Trick 11" = Code-Wort (TRICKS index 10 → displayed as 11), "Kapitel 2" = Sperre, "Kapitel 3" = Automatische Sperre.
- The fake APK-update message is consistent with Auto Blocker blocking apps from unauthorised sources. — [Samsung Knox Docs](https://docs.samsungknox.com/admin/fundamentals/whitepaper/samsung-knox-mobile-security/system-security/samsung-auto-blocker/)
- The +44 7700 900xxx number is the UK's reserved fictional range, a safe choice (general knowledge, no source).

**Footer sources**
- The Notebookcheck, Engadget and Droid Life URLs all load and support the One UI 9 claims, except the AI-voice claim.
- The BSI link is only the homepage; a specific smartphone guidance page would be better.

### Inferences
- Must fix:
  1. Remove or rewrite the AI-voice-detection claims (Ch. 8 why-text, mock-up screen 3, Ch. 9 note).
  2. Correct the Enhanced Data Protection scope and path (Trick 04 and the Ch. 5 note).
  3. List all Maximum restrictions effects, especially work profiles and links in messages.
  4. Rephrase the button grip as "hold", not "press about one second".
  5. Fix the Auto restart naming.
  6. Remove the "E/G" warning-sign hint, or reconcile it with the 2G block.
  7. Fix the Remote Lock prerequisites.
- Should fix: mark Call screening on the S25 Ultra as "if available"; soften the Secure Folder Knox Vault wording; update "Mein Gerät finden" to Find Hub; note that Security Delay only applies outside trusted places.

### Gaps
- No German UI strings were verified for "Sicherheitsverzögerung", "Identitätsprüfung", "Maximale Einschränkungen", "Anrufprüfung" or "Daten nur auf dem Gerät verarbeiten". The Samsung DE page I read does not list them.
- The "Galaxy AI › Process data only on device" toggle and the Wartungsmodus path were not checked in this session (I believe both are correct from earlier One UI versions).
- SM-S938B, the 7-year update policy and Knox Vault on the S25 Ultra were not confirmed by a source in this session. They match Samsung's S25 launch announcements as I know them, but are not cited here.
- 116 116 (the central German card-blocking number, Sperr-Notruf) is correct from general knowledge but was not fetched in this session. A citable source is sperr-notruf.de (not fetched).
- Whether the S25 Ultra on One UI 9 gets Samsung's Call screening in Germany: no source.

## Q3: Could any tips in the guide cause harm?

### Takeaway
The guide is mostly cautious (it shows "Automatisch auf Werkseinstellungen zurücksetzen" as off), but four tips can cause real harm:
1. Maximum restrictions can silently break a company work profile or MDM, and links in messages stop opening.
2. The 13-attempt lockout is irreversible, and the guide presents a factory reset as a way to "activate" it.
3. Readers may rely on a non-existent AI-voice warning.
4. Enhanced Data Protection can cause permanent data loss if the recovery code is lost, or if it is turned off.

### Cited Findings
- **Maximum restrictions** disables device admin apps and work profiles, and blocks tapping links and auto-downloading attachments in messages. For anyone with a work profile (Android Enterprise/Intune) or banking or parcel links in SMS, this breaks things without an obvious cause. — [Samsung Knox Docs](https://docs.samsungknox.com/admin/fundamentals/whitepaper/samsung-knox-mobile-security/system-security/samsung-auto-blocker/)
  - In 2G-only areas, calls, SMS and mobile data may fail, which could include emergency situations. — [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)
- **The 13th failed attempt permanently locks the device; only a factory reset unlocks it,** and Samsung lists no option to turn this off. The Wartung chapter's note ("Planen Sie ohnehin ein Zurücksetzen, aktiviert das zugleich die strengere One-UI-9-Sperrregel") frames this as an advantage. A child or a stranger entering wrong PINs could then trigger a full wipe, and the guide should warn about that and insist on backups. — [Samsung UK](https://www.samsung.com/uk/support/mobile-devices/one-ui-9-lockscreen-security-policy/)
- **Enhanced Data Protection:** if every device and the 28-character recovery code are lost, the data is gone; turning EDP off deletes the encrypted backups; older backups can't be restored. — [Samsung US](https://www.samsung.com/us/support/answer/ANS10003637)
- **Lockdown grip:** a short press of Side + Volume Down takes a screenshot, and a long press may open Emergency SOS depending on the side-button settings. Practising the grip while unsure of the setting could start an SOS call. — [Samsung UK side button](https://www.samsung.com/uk/support/mobile-devices/how-to-customise-the-side-button-with-new-features-on-your-galaxy-phone-and-tablet/) (snippet)
- **Remote Lock without a security question:** anyone who knows your number can lock your phone (a nuisance, not data access). You can use it twice in 24 hours. — [Google Android Help](https://support.google.com/android/answer/15146908?hl=en)

### Inferences
- Add warning boxes to:
  - Ch. 3: work profile, links and attachments, 2G areas
  - Ch. 2 / Ch. 11: the lockout is irreversible, so keep backups; children
  - Trick 04: recovery code, older backups, turning it off
  - Trick 01: test the grip first; SOS may trigger depending on the side-button settings
- The guide's choice to keep "Automatisch auf Werkseinstellungen zurücksetzen" off is correct and should stay. On One UI 9 devices that have been reset, the 13-attempt rule enforces a similar outcome anyway.
- The note "Gehen Sie nicht selbst hin, sondern geben Sie den Standort an die Polizei" is sensible safety advice.

### Gaps
- I could not confirm whether One UI 9 on an updated S25 Ultra still offers the older "Automatisch auf Werkseinstellungen zurücksetzen" (wipe after 20 attempts) toggle, or how it interacts with the new policy.
- I could not confirm the default side-button long-press assignment on the S25 Ultra with One UI 9 (power menu vs. Bixby vs. lockdown).
