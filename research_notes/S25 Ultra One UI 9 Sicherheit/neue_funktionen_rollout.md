# One UI 9 (Android 17) on the Galaxy S25 Ultra: new security and privacy features and the rollout in Germany/Europe (as of 7 Nov 2026)

Source note: the main official sources are Samsung Newsroom DE (16.09.2026, written for the S26 launch) and the Samsung support page on the lock-screen policy (sa_en, updated 03.10.2026). The global Samsung Newsroom rollout article (news.samsung.com/global/samsung-begins-official-rollout-of-one-ui-9-...) timed out twice and could not be read. SamMobile and Sammy Fans articles returned 403, so for those I only have search snippets. The feature descriptions for the S25 Ultra come mostly from Samsung's Korean S25 Ultra changelog as translated by Droid Life. That is official Samsung text, but it comes through the press and from the Korean build.

## 1. Quick "secure lock" (Volume Down + Side key, or Power button in Quick Settings)

### Takeaway
This is in the official One UI 9 changelog for the S25 Ultra (Korean build). Holding Volume Down and the Side key together, or tapping Power in Quick Settings, puts the phone in a secure lock state with biometric unlock turned off. It works like the earlier "Lockdown" mode. German press reports that the old Lockdown entry in the power menu is gone.

### Cited Findings
- The S25 Ultra changelog (Korea, build S938NKSUCDZIF) says you can lock the phone by holding Volume Down and Side together or by tapping Power in Quick Settings, and that biometric unlock is disabled while the device is in this secure lock state. — [Droid Life, 28.09.2026](https://www.droid-life.com/2026/09/28/galaxy-s25-ultra-one-ui-9-updates-the-beginning/)
- Same description in summary form ("Quick and secure phone locking … when you're in a hurry"). — [Notebookcheck / search summary](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html), [Geeky Gadgets](https://www.geeky-gadgets.com/samsung-one-ui-9-features-6/)
- German blogs report, from S26 beta builds, that the manual Lockdown button is gone from the power menu, biometrics stay blocked until you enter a PIN, pattern or password, and the message "For your security, your phone is locked" appears. — [schmidtisblog.de](https://www.schmidtisblog.de/samsung-one-ui-9-sperrt-langfinger-automatisch-aus-der-lockdown-button-fliegt-aus-dem-menue-1800764/), [it-boltwise.de](https://www.it-boltwise.de/samsung-one-ui-9-sperrbildschirm-pflicht-beim-ausschalten-von-galaxy-geraeten.html)

### Inferences
- Since the feature is in the S25 Ultra's own changelog, it is very likely in the European build (S938BXXUCDZIF) as well. It is a system function with no AI or regional dependency.

### Gaps
- I did not find a German or EU changelog for the S25 Ultra that confirms the exact German wording.
- Not clarified: whether notifications are also hidden on the lock screen (as with the old Lockdown), and whether the key combination has to be switched on first.

## 2. Security Delay (Sicherheitsverzögerung) and Identity Check

### Takeaway
Both are not new in One UI 9. They arrived with One UI 7 under Theft protection and are still there in One UI 9, including on the S25 Ultra. Identity Check is opt-in according to Samsung. Security Delay is a one-hour wait for sensitive changes made outside safe places. I found no official confirmation that either is on by default on the S25 Ultra.

### Cited Findings
- In One UI 9 these sit under Settings > Security and privacy > Lost device protection > Theft protection, alongside Theft Detection Lock, Offline Device Lock and Remote Lock. They require a Samsung account and Find My Mobile. Introduced with One UI 7. — [Notebookcheck, 07.10.2026](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)
- Identity Check requires biometric authentication for sensitive changes made outside trusted locations. Security Delay adds a one-hour wait, for example before new biometrics can be added. — [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)
- Samsung's support page lists the delayed actions (outside safe places): turning off Identity Check, changing biometrics, changing other lock-screen settings, turning off Find My Mobile, and editing safe places. — [Samsung UK Support](https://www.samsung.com/uk/support/mobile-devices/how-to-use-security-settings/)
- Samsung's One UI 7 announcement describes Identity Check as an opt-in feature. — [Samsung Newsroom UK (One UI 7)](https://news.samsung.com/uk/samsung-one-ui-7-enhances-security-and-privacy-in-the-age-of-ai-giving-users-greater-transparency-and-choice)
- Notebookcheck gives no default state for Theft protection and notes that Samsung urged users in 2025 to turn these features on. — [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html), [SamMobile 2025](https://www.sammobile.com/news/samsung-reaches-out-to-40-million-users-to-help-them-secure-their-phones/)
- Possible mix-up: a search summary attributes the "on by default only on devices released with One UI 6.1.1" rule to Identity Check. The Notebookcheck article itself attributes it to Auto Blocker (see section 9). — [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)

### Inferences
- On an S25 Ultra updated from One UI 8/8.5, the previous Theft protection settings almost certainly carry over. You should check them manually and turn them on if needed.

### Gaps
- No official statement on the default state of Identity Check or Security Delay on the S25 Ultra under One UI 9.
- No confirmation that One UI 9 changes anything functionally in Identity Check or Security Delay.

## 3. Security Brief and privacy alerts (Now Brief / Now Bar)

### Takeaway
Security Brief is new in One UI 9 and available on the S25 Ultra. Samsung lists support back to the S23. You turn it on in the Now Brief settings ("Security and privacy" toggle). It requires a Samsung account. According to the changelog, apps classified as malicious stay blocked until the user decides what to do with them. The AI privacy alerts officially list the "Galaxy S25". The S25 Ultra is not named separately in that footnote.

### Cited Findings
- Samsung Newsroom DE: "Security Brief bringt wichtige Hinweise zu Sicherheit und Datenschutz in Now Brief", including warnings about malware, apps from unknown sources and unnecessary permissions. Now Brief requires a Samsung account, and features vary by country, region, language, device and apps (footnote 3). — [Samsung Newsroom DE, 16.09.2026](https://news.samsung.com/de/samsung-startet-den-offiziellen-rollout-von-one-ui-9-fur-die-galaxy-s26-serie)
- Datenschutzwarnungen (privacy alerts) use on-device AI to flag unnecessary background permission access. Supported devices (footnote 10): S26, S26 Ultra, S25, S25 FE, S24, S24 FE, S23, S23 FE, Z Fold8 Ultra/Fold8/Flip8, Fold7/Flip7, Fold6/Flip6, Fold5/Flip5, Tab S11/S10/S9 series. Disclaimer: "KI-Datenschutzwarnungen berücksichtigen möglicherweise nicht alle relevanten Faktoren." — [Samsung Newsroom DE](https://news.samsung.com/de/samsung-startet-den-offiziellen-rollout-von-one-ui-9-fur-die-galaxy-s26-serie)
- S25 Ultra changelog: alerts about malicious apps, apps from unknown sources or excessive permissions. Apps classified as malicious or vulnerable are blocked from running until you decide whether to delete them. — [Droid Life](https://www.droid-life.com/2026/09/28/galaxy-s25-ultra-one-ui-9-updates-the-beginning/)
- To turn it on: Now Brief settings > "Security and privacy" toggle. Alerts then appear on the Now Bar. Samsung lists support back to the Galaxy S23. Tested on the S25 Ultra and S26 Ultra. Sideloaded apps (an unsigned app in the test) can trigger alerts, but Engadget saw no alert in normal use. — [Engadget, 10.10.2026](https://www.engadget.com/2279220/samsung-galaxy-one-ui-9-features-you-should-use/)
- Not visible if Now Brief is turned off or no Samsung account is used. Personalised content requires your consent. — [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)
- German press confirms Security Brief and privacy alerts in the German rollout. Samsung explicitly names the privacy alerts for the S24 series, Z Fold6 and Z Flip6. — [inside-digital.de, 09.10.2026](https://www.inside-digital.de/news/endlich-mega-update-fuer-weitere-galaxy-handys-von-samsung)

### Inferences
- "Galaxy S25" in Samsung's footnote most likely means the whole series (S25/S25+/S25 Ultra), because the S26 Ultra is listed but the S26+ is not. The footnotes are inconsistent. Engadget's hands-on on the S25 Ultra supports availability.
- The feature is not on by default. It is opt-in through the Now Brief toggle and depends on a Samsung account.

### Gaps
- Not clarified whether the blocking of malicious apps also works without the Now Brief toggle or Samsung account (that is, whether it is an Auto Blocker / Play Protect function).

## 4. Blue location-access indicator

### Takeaway
Confirmed in the S25 Ultra changelog and in German press: a blue indicator appears at the top of the screen when an app uses location. Tapping it in the Quick Panel shows which app it was.

### Cited Findings
- "A blue indicator appears at the top of the screen when an app uses location. Tapping it in Quick Settings shows which app accessed it." — [Droid Life (S25 Ultra changelog)](https://www.droid-life.com/2026/09/28/galaxy-s25-ultra-one-ui-9-updates-the-beginning/)
- German: a location indicator appears when an app uses location, and the Quick Panel shows which app. — [inside-digital.de](https://www.inside-digital.de/news/endlich-mega-update-fuer-weitere-galaxy-handys-von-samsung)

### Gaps
- None of importance.

## 5. AI-synthesized voice detection during calls ("safer calling")

### Takeaway
It is in the Korean S25 Ultra changelog: when a caller uses known voice-phishing patterns, the system checks the audio for AI-synthesized voices and warns the user. It builds on Samsung's on-device voice-phishing detection, which started in Korea with data from the Korean National Police Agency. I found no evidence that it is available in Germany or in German.

### Cited Findings
- "When a caller mentions content matching known voice-phishing tactics, the system analyzes the audio for AI-synthesized voices and alerts you." It depends on such phishing cues appearing in the call, and no accuracy figures are given. — [Droid Life](https://www.droid-life.com/2026/09/28/galaxy-s25-ultra-one-ui-9-updates-the-beginning/)
- Samsung's on-device voice-phishing detection started in Korea (National Police Agency data) and "isn't available globally just yet" (One UI 8 era). — [Android Police](https://www.androidpolice.com/samsung-ai-voice-phishing-scam-detection/)
- Sammy Fans: Samsung is expanding on-device Scam Detection with stable One UI 9. Samsung warns that it is not always accurate, so some scam calls may be missed and genuine calls may be flagged. No regions named (snippet only, page returned 403). — [Sammy Fans, 29.09.2026](https://www.sammyfans.com/2026/09/29/samsung-expands-on-device-ai-scam-detection-with-one-ui-9-stable-update/)

### Inferences
- Since the feature was in the Korean changelog and is tied to Korean anti-phishing data, it is very likely Korea-only or region-limited.

### Gaps
- No German or EU source for availability or German-language support. The European changelog was not found.

## 6. Call screening (Samsung Call Assistant)

### Takeaway
Call screening (the assistant answers, asks the caller who they are and why they are calling, and shows the conversation as text) supports 13 languages including German, according to Notebookcheck. Notebookcheck lists it for the Galaxy S26 series. I found no confirmation that it comes to the S25 Ultra with One UI 9.

### Cited Findings
- Phone app > More options > Settings > Call screening. 13 languages including English, German and Spanish. Listed for the Galaxy S26 series as the "S26 alternative" to Google Scam Detection. — [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)
- SamMobile's Galaxy AI compatibility table for One UI 9 lists Call Brief for the S25 series and does not mention scam or voice detection (snippet only). — [SamMobile](https://www.sammobile.com/news/samsung-one-ui-9-galaxy-ai-features-compatible-devices/)

### Gaps
- Not clarified whether call screening is available on the S25 Ultra under One UI 9, or only on S26/Fold8/Flip8. Treat it as open, since Notebookcheck mentions only the S26.

## 7. Google Scam Detection (Gemini Nano) on Samsung in Germany/EU

### Takeaway
As of the available sources, it is not available on Galaxy phones in Germany. On the S26 it started in the US only, in English. One UI 9 adds Japan, Canada and India. In Germany it works only on Pixel phones.

### Cited Findings
- Google Scam Detection on the S26 since Feb 2026, US/English. One UI 9 adds Japan, Canada and India. No European country is listed, and Samsung's German press release does not mention the feature. Germany, the UK, France and Spain are covered only through the Pixel Phone app (Pixel 9 and newer). It has to be turned on in Settings. — [Notebookcheck, 07.10.2026](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)
- At the S26 launch: limited to English speakers in the US, and German buyers have to go without it. — [GIGA](https://www.giga.de/tech/samsung-galaxy-s26-auf-dieses-feature-muessen-kaeufer-in-deutschland-verzichten--01KFX3YMVY71CMTPMFFVPCY0W2), [Android Authority](https://www.androidauthority.com/google-scam-detection-samsung-galaxy-s26-3643942/)
- A search summary of the Samsung newsroom says scam detection is being "expanded to more countries and languages". I could not verify this, because the global article timed out and the German article does not mention Scam Detection on direct reading. — [Samsung Newsroom DE](https://news.samsung.com/de/samsung-startet-den-offiziellen-rollout-von-one-ui-9-fur-die-galaxy-s26-serie)

### Inferences
- For an S25 Ultra in Germany, Google Scam Detection is very likely not available. It is not even confirmed for the S25 series in the US.

### Gaps
- No official Google or Samsung statement on a German launch for Galaxy phones.

## 8. New lock-screen failed-attempt policy (13th attempt = permanent lock)

### Takeaway
Officially from Samsung: escalating wait times from the 5th failed attempt, 24 hours after the 12th, and a permanent lock after the 13th that can only be cleared by a factory reset. It applies to devices that ship with One UI 9.0. On updated devices such as the S25 Ultra, the previous settings stay in place. The new policy applies there only after a full factory reset performed after the update.

### Cited Findings
- Schedule: 5 → 1 min, 6 → 5 min, 7 → 15 min, 8 → 30 min, 9 → 90 min, 10 → 4 h, 11 → 12 h, 12 → 24 h, 13 → "the Galaxy device will be permanently locked". — [Samsung Support (sa_en), updated 03.10.2026](https://www.samsung.com/sa_en/support/mobile-devices/one-ui-9-lockscreen-security-policy/)
- Devices that ship with One UI 9.0 get the policy from the start. Devices updated to One UI 9.0 keep their previous settings, and the new policy applies only after a full factory reset. Samsung cannot reset a PIN remotely. After a reset, the Samsung and Google accounts are needed to clear FRP. — [Samsung Support](https://www.samsung.com/sa_en/support/mobile-devices/one-ui-9-lockscreen-security-policy/)
- Android rule: the PIN, pattern or password is required at least every 72 hours even when biometrics are used. — [Samsung Support](https://www.samsung.com/sa_en/support/mobile-devices/one-ui-9-lockscreen-security-policy/)
- Currently affects mainly Fold8/Flip8 (plus S26 FE, Fold8 Ultra). — [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)
- connect.de (31.07.2026, before release): same schedule. Identical wrong entries made immediately one after another count as only one failed attempt. — [connect.de](https://www.connect.de/news/samsung-one-ui-9-schaerfere-displaysperre-weniger-fehlversuche-3213010.html)
- Partly conflicting German blog accounts (for example "13th failed attempt leads to a reset only from the next major update"). These are superseded by the Samsung support page. — [schmidtisblog.de](https://www.schmidtisblog.de/samsung-one-ui-9-pin-werksreset-sicherheit-1812038/)

### Inferences
- For an S25 Ultra updated from One UI 8/8.5, the policy does not apply until the device is factory reset. After a reset (for example during troubleshooting), it does apply, and that should be stated clearly in the report.

### Gaps
- Only the Saudi (sa_en) version of the support page was read. A DE version was not checked, but the policy is very likely global.

## 9. Other new or relevant One UI 9 / Android 17 security features

### Takeaway
On the platform side, Android 17 brings an expanded Advanced Protection mode (accessibility restrictions, device-to-device unlock disabled, WebGPU off in Chrome, Intrusion Logging opt-in), OTP protection and Encrypted Client Hello. Whether Samsung offers Google's Advanced Protection mode on the S25 Ultra is not confirmed. Auto Blocker remains, with a Maximum restrictions mode.

### Cited Findings
- Android 17 Advanced Protection: accessibility only for verified accessibility tools, device-to-device unlocking disabled, Chrome WebGPU disabled, scam detection for chat notifications, Intrusion Logging (opt-in), and limited to selected Android 17 devices. — [Android Developers – Android 17 features](https://developer.android.com/17/features), [Help Net Security, 17.06.2026](https://www.helpnetsecurity.com/2026/06/17/android-17-security-and-privacy-features/), [eSecurity Planet](https://www.esecurityplanet.com/cybersecurity/news-android-17-intrusion-logging-spyware-protection/)
- Android 17: temporary precise location for apps, sharing approximate location with websites, apps without priority status get no immediate access to SMS OTPs, and ECH on by default for apps targeting SDK 37. — [Android Developers](https://developer.android.com/17/features), [BleepingComputer](https://www.bleepingcomputer.com/news/security/android-17-to-expand-banking-scam-call-and-privacy-protections/)
- Auto Blocker (One UI 9): blocks apps from unauthorised sources, USB commands, suspected malware images in messaging apps, and unofficial updates. On by default only on devices released with One UI 6.1.1 or later, so an update does not turn it on. Maximum restrictions mode blocks 2G and automatic reconnection to unsecured Wi-Fi, blocks shared albums, and strips location data from shared photos. Note that in areas with only 2G coverage, calls, messages and data may stop working. — [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)
- German blogs also mention a USB lock and proactive threat protection, with few details. — [borncity.com](https://borncity.com/news/one-ui-9-samsung-automatisiert-smartphone-sicherheit-radikal-um/)

### Inferences
- The S25 Ultra launched with One UI 7, which is later than 6.1.1, so Auto Blocker is probably on by default there. This is inferred from Notebookcheck's rule and is not stated explicitly.

### Gaps
- No source on Knox Matrix changes in One UI 9.
- No source on whether Android Advanced Protection mode is available on Samsung or the S25 Ultra. A forum post (MalwareTips) mentions only "reported" items, which is unreliable.

## 10. One UI 9 rollout for the S25 Ultra (Korea, US, Europe, Germany)

### Takeaway
Korea from 28.09.2026 (first for beta testers, build S938NKSUCDZIF). US from about 03.10.2026. Europe and Germany from the week of 09.10.2026 (build S93xBXXUCDZIF, so S938BXXUCDZIF for the Ultra), first as a roughly 550 MB update for beta testers and then wider at about 3 to 4 GB. On 29.10.2026 Samsung confirmed to heise that the S25 Ultra has been updated in Germany. I did not find the security patch level for the S25 Ultra.

### Cited Findings
- 28.09.2026: stable One UI 9 for the S25 series, Korea only, possibly only beta testers at first (about 534 MB). Build for the S25 Ultra in Korea: S938NKSUCDZIF. — [Droid Life](https://www.droid-life.com/2026/09/28/galaxy-s25-ultra-one-ui-9-updates-the-beginning/)
- Samsung had removed its own rollout date. The S25 series went Korea on 28.09 and US from 03.10. — [Tech Times, 30.09.2026](https://www.techtimes.com/articles/328267/20260930/galaxy-s25-gets-stable-one-ui-9-after-samsung-scrubbed-its-own-rollout-date.htm), [Sammy Fans (US)](https://www.sammyfans.com/2026/10/03/samsung-galaxy-s25-fe-edge-plus-ultra-fold-flip-7-one-ui-9-us/)
- Europe: stable One UI 9.0 for S25/S25+/S25 Ultra with firmware S93xBXXUCDZIF, initially about 550 MB for beta participants, with a wider release to follow (snippet, page returned 403). — [SamMobile](https://www.sammobile.com/news/galaxy-s25-stable-one-ui-9-update-released-globally/)
- Germany: "seit dieser Woche" (since this week) for S25/S25+/S25 Ultra, firmware S93xBXXUCDZIF. Former beta testers about 550 MB, everyone else about 3 to 4 GB. — [inside-digital.de, 09.10.2026](https://www.inside-digital.de/news/endlich-mega-update-fuer-weitere-galaxy-handys-von-samsung)
- GameStar: the S25/S25+/S25 Ultra/S25 Edge share a firmware base, about 3.8 GB package. — [GameStar](https://www.gamestar.de/artikel/one-ui-9-galaxy-s25,3460670.html)
- 29.10.2026: Samsung reports 29 models updated in Germany, including S25, S25+, S25 Ultra, S25 Edge and S25 FE. Not every device gets every feature. — [heise online](https://www.heise.de/en/news/One-UI-9-These-Galaxy-phones-got-the-update-in-Germany-11505176.html)
- For comparison, the S26 in Europe got S94xBXXU4BZI6 with the September security patch. The S26 One UI 9 rollout in Germany officially started on 16.09.2026. — [SamMobile on X](https://x.com/SamMobiles/status/2104817938931040378), [Samsung Newsroom DE](https://news.samsung.com/de/samsung-startet-den-offiziellen-rollout-von-one-ui-9-fur-die-galaxy-s26-serie)
- Earlier German reports (Netzwelt, Teltarif) still said Germany was not included yet, which is now out of date. — [teltarif.de](https://www.teltarif.de/en/one-ui-9-android-17-update-galaxy-s25/news/106184.html), [netzwelt.de](https://www.netzwelt.de/news/258977-one-ui-90-galaxy-s25-flaggschiff-sollte-update-suchen.html)

### Inferences
- The patch level of the first S25 build is probably September or October 2026, given the build letter "I" (September) and the S26 comparison. This is not confirmed.
- As of November 2026, the update should be broadly available in Germany, but the rollout is staged, so it may not show up on every device straight away.

### Gaps
- No exact date of the wide (non-beta) release for the S938B in Germany.
- Security patch level of S938BXXUCDZIF not found. Any follow-up builds (November patch) not checked.
- Carrier-branded variants (Telekom, Vodafone, o2) not checked.
