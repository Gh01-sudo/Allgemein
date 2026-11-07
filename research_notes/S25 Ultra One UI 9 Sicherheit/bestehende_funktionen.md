# Established security and privacy features of the Galaxy S25 Ultra (SM-S938B) under One UI 9 / Android 17 (as of Nov 2026)

Context: One UI 9.0 (Android 17) is rolling out to S25 / S25+ / S25 Ultra / S25 Edge / S25 FE / Fold7 / Flip7, with October 2026 patches per US reports — [Notebookcheck, 07.10.2026](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html); [Samsung Newsroom Malaysia: One UI 9 rollout](https://news.samsung.com/my/samsung-begins-official-rollout-of-one-ui-9-bringing-the-latest-galaxy-experiences-to-more-devices); [Droid-Life, 28.09.2026](https://www.droid-life.com/2026/09/28/galaxy-s25-ultra-one-ui-9-updates-the-beginning/). Several sources below cover One UI 6.1.1 to 8.5. Where a statement predates One UI 9, it is marked as such.

## 1. Auto Blocker (Automatische Sperre) incl. Maximum restrictions

### Takeaway
Standard Auto Blocker blocks sideloading, USB commands, malicious images (Message Guard) and unofficial system updates. "Maximum restrictions" adds a longer list: 2G, auto-reconnect to unsecured Wi-Fi, shared albums, location stripping, link previews, device admin and work profile, and App protection. In One UI 9 it also cuts the USB data connection completely. It is on by default on devices released with One UI 6.1.1 or later, so it should be on for the S25 Ultra (launched with One UI 7). Maximum restrictions is off by default.

### Cited Findings
- **Standard Auto Blocker blocks:** (1) installing apps from sources other than Galaxy Store and Play Store; (2) commands over USB cable from malicious chargers, PCs or devices; (3) suspected malware images in messaging apps, including third-party apps (Samsung Message Guard); (4) system software updates not authorised by Samsung — [Samsung UK Support](https://www.samsung.com/uk/support/mobile-devices/protect-your-galaxy-device-with-the-new-auto-blocker-feature/)
- **Maximum restrictions blocks, per the official Samsung list (UK page):**
  - Turns on App protection (scans installed apps for malicious behaviour).
  - Blocks device admin apps and work profile activation.
  - Blocks 2G networks. Calls, SMS and data may fail where only 2G is available.
  - Blocks automatic reconnection to previously used unsecured Wi-Fi networks. Manual connection still works.
  - Blocks hyperlinks and previews in messages that contain web addresses.
  - Blocks shared albums.
  - Removes location data from photos shared via Samsung Messages and Gallery.
  - Source: [Samsung UK Support](https://www.samsung.com/uk/support/mobile-devices/protect-your-galaxy-device-with-the-new-auto-blocker-feature/)
- APKMirror release notes for Auto Blocker 1.2.01.8: from One UI 7.0, Maximum restrictions additionally blocks USB connections, 2G and auto-reconnect to insecure Wi-Fi — [APKMirror](https://www.apkmirror.com/apk/samsung-electronics-co-ltd/auto-blocker/samsung-auto-blocker-1-2-01-8-release/)
- **Default state:** Auto Blocker is on by default on Galaxy devices released with One UI 6.1.1. Devices updated to One UI 6.1.1 keep their previous setting — [Samsung UK Support](https://www.samsung.com/uk/support/mobile-devices/protect-your-galaxy-device-with-the-new-auto-blocker-feature/). Maximum restrictions is off by default — [Samsung Knox Whitepaper: Auto Blocker](https://docs.samsungknox.com/admin/fundamentals/whitepaper/samsung-knox-mobile-security/system-security/samsung-auto-blocker/)
- Notebookcheck (One UI 9 article) confirms: on by default only on devices released with One UI 6.1.1 or later. Path: Settings > Security and privacy > Auto Blocker. In Maximum restrictions, calls, SMS and data may fail in 2G-only areas — [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)
- **One UI 8.5:** Samsung added code that turns Auto Blocker back on automatically 30 minutes after it was switched off. A sub-setting ("Turn on automatically") lets users keep it off — [Android Authority APK teardown](https://www.androidauthority.com/one-ui-8-5-auto-blocker-auto-turn-on-apk-teardown-3599873); [WebProNews](https://www.webpronews.com/samsung-one-ui-8-5-to-auto-reactivate-auto-blocker-for-enhanced-security)
- **One UI 9 USB change:**
  - In One UI 8.5, Maximum restrictions blocked USB commands but left the physical USB connection open. In One UI 9 (first seen in beta 1 on S26), Maximum restrictions cuts off the USB connection entirely, except for charging — [Android Authority](https://www.androidauthority.com/samsung-one-ui-9-auto-blocker-update-3666893/); [SammyFans, 14.05.2026](https://www.sammyfans.com/2026/05/14/samsung-galaxy-s26-one-ui-9-new-auto-blocker-features/); [SammyGuru](https://sammyguru.com/one-ui-9-auto-blocker-security/)
  - Under One UI 7, USB blocking (except charging) was reportedly already present, then removed, and is now back — [SammyGuru](https://sammyguru.com/one-ui-9-auto-blocker-security/)
- **One UI 9 Security Report:** a new section in Auto Blocker logs blocked installs from unknown sources, as a 7-day or monthly history — [Android Authority: Manage unknown apps](https://www.androidauthority.com/samsung-one-ui-9-manage-unknown-apps-3666922/); [SammyGuru](https://sammyguru.com/one-ui-9-auto-blocker-security/)
- **Phishing App Risk Alert:** new in One UI 9. It stops already-installed phishing apps from opening, and is announced for all supported Galaxy devices — [Beebom](https://gadgets.beebom.com/news/one-ui-9-adds-new-security-feature-that-stops-phishing-apps-from-opening-on-samsung-phones); [SamMobile](https://www.sammobile.com/news/one-ui-9-galaxy-z-flip-fold-8-stop-phishing-apps-running/)

### Inferences
- The S25 Ultra launched with One UI 7, which is later than 6.1.1, so Auto Blocker should be on out of the box. If a user turned it off, One UI 8.5 and later may turn it back on automatically.
- Media coverage usually lists only part of the Maximum restrictions items. The Samsung UK list above is the most complete.

### Gaps
- No official Samsung confirmation was found that the full USB block from the One UI 9 beta is in the stable S25 Ultra release, or that it works identically there. The Android Authority page could not be fetched (HTTP 403), so search snippets were used.
- The exact German UI labels under One UI 9 were not verified.

## 2. Theft protection, Find My Mobile / SmartThings Find, Remote Lock

### Takeaway
Since One UI 7, the S25 Ultra has the following components under Settings > Security and privacy > Lost device protection > Theft protection:
- Theft Detection Lock
- Offline Device Lock
- Remote Lock
- Identity Check with Safe Places
- Security Delay (1 hour)

Samsung asks users to turn these on manually, which suggests they are not all on by default.

### Cited Findings
- **Theft Detection Lock:** machine learning detects theft-like motion (such as a snatch) and locks the screen immediately — [Samsung UK Support: security settings](https://www.samsung.com/uk/support/mobile-devices/how-to-use-security-settings/)
- **Offline Device Lock:** locks the screen when the device is kept offline for a long time. Enabling it requires accepting terms — [Samsung UK Support](https://www.samsung.com/uk/support/mobile-devices/how-to-use-security-settings/)
- **Remote Lock:** locks the device using only the phone number and a security challenge, and helps with account recovery. Toggle: "Use remote lock" — [Samsung UK Support](https://www.samsung.com/uk/support/mobile-devices/how-to-use-security-settings/)
- **Identity Check:** requires biometrics to change sensitive security settings outside Safe Places (home, work, other). Requires biometrics set up and a Samsung account login — [Samsung UK Support](https://www.samsung.com/uk/support/mobile-devices/how-to-use-security-settings/)
- **Security Delay:** applies outside Safe Places. After biometric confirmation, a one-hour wait runs before these changes take effect:
  - Turning off Identity Check
  - Changing biometrics
  - Changing other lock screen settings
  - Turning off Find My Mobile
  - Editing Safe Places
  - Source: [Samsung UK Support](https://www.samsung.com/uk/support/mobile-devices/how-to-use-security-settings/)
- Samsung (UK newsroom, June 2025) urges users to activate the anti-theft features. One UI 7 support is listed for S25, S24, Z Fold6/Flip6, Fold5/Flip5 and S23 — [Samsung Newsroom UK](https://news.samsung.com/uk/samsung-urges-customers-to-activate-latest-anti-theft-features-to-help-tackle-phone-theft-crime); [SamMobile, 03.06.2025](https://www.sammobile.com/news/samsung-galaxy-phone-users-turn-on-anti-theft-features-uk/)
- Notebookcheck (October 2026): theft protection requires a Samsung account and Find My Mobile turned on. It does not state a default — [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)
- **Find My Mobile:** Settings > Security and privacy > Lost device protection > "Allow this phone to be found". SmartThings Find must be activated. Google's theft protection and Find My Device are under Settings > Google > All services — [Samsung UK Support](https://www.samsung.com/uk/support/mobile-devices/how-to-use-security-settings/)
- **One UI 9 lock screen policy (new, not a legacy feature):** wait times grow up to 24 hours after the 12th wrong attempt. After the 13th, only a factory reset helps. This applies only to devices launching with One UI 9 (Fold8/Flip8). Updated devices such as the S25 Ultra keep their old settings unless factory reset — [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html); [Tech-ish, 25.07.2026](https://tech-ish.com/2026/07/25/one-ui-9-galaxy-wrong-pin-lockscreen-security/)
- **Lockdown in the power menu (One UI 9):** a PIN or password is then needed to unlock, power off or restart — [Help Net Security](https://www.helpnetsecurity.com/?p=373536); [Eastern Herald](https://easternherald.com/2026/06/07/samsung-one-ui-9-lockdown-mode-power-menu-galaxy-security/)

### Inferences
- Remote Lock on Samsung is the One UI integration of Google's Remote Lock, which is used via android.com/lock. That URL was not confirmed by any source fetched here.

### Gaps
- No primary source was fetched on SmartThings Find offline finding (an offline device being found by other Galaxy devices), the "Remote unlock" option in Find My Mobile, or the android.com/lock flow.
- The exact defaults of each theft-protection toggle on a fresh S25 Ultra are not documented by Samsung.
- Whether the One UI 9 "Identity Check" matches Google's Android 15/16 Identity Check one-to-one was not verified.

## 3. Samsung Cloud Enhanced Data Protection (end-to-end encryption)

### Takeaway
Enhanced Data Protection (EDP) has been available since One UI 5.1.1 and is opt-in. It uses a 28-character recovery code that Samsung does not store. It covers call logs, messages, clock, settings and apps, but not necessarily large attachments.

### Cited Findings
- **Path:** Settings > Security and privacy > More security settings > Enhanced data protection. Toggles: "Encrypt backup data" and "Encrypt synced data". Requires One UI 5.1.1 or later. Not available on some carrier devices or in some regions — [Samsung US Support](https://www.samsung.com/us/support/answer/ANS10003637)
- **Covered data:** call logs, messages, clock, settings, apps. Large files such as message attachments may not be encrypted. Unsupported items are backed up unencrypted — [Samsung US Support](https://www.samsung.com/us/support/answer/ANS10003637)
- **Recovery code:**
  - 28 characters, generated when the feature is turned on.
  - Cannot be viewed again, and Samsung does not store it.
  - If both the device and the code are lost, the data is unrecoverable.
  - With a device still signed in, a new code can be generated, which invalidates the old one.
  - Sources: [Samsung US Support](https://www.samsung.com/us/support/answer/ANS10003637); [Samsung UK Support](https://www.samsung.com/uk/support/mobile-devices/what-is-the-enhanced-data-protection-feature-on-the-galaxy-z-fold5-flip5/)
- Backups made before EDP was turned on cannot be restored through it. The setting syncs across all devices on the Samsung account, and only supporting devices can access the encrypted data — [Samsung US Support](https://www.samsung.com/us/support/answer/ANS10003637)
- Error message when resetting the code in parallel: "The recovery code is being reset on another device" — [Samsung AE Support](https://www.samsung.com/ae/support/mobile-devices/issues-with-the-enhanced-data-protection-feature/)

### Gaps
- No One UI 9-specific change to EDP was found. Whether it covers additional categories (for example Samsung Notes or Gallery) under One UI 9 is unconfirmed.
- Availability in Germany or the EU on the SM-S938B was not explicitly confirmed. Samsung only mentions possible regional limits.

## 4. Unknown tracker alerts

### Takeaway
Two systems run in parallel:
- **Android:** Settings > Safety & emergency > Unknown tracker alerts > "Scan now". Detects Find Hub-compatible tags and AirTags.
- **Samsung SmartThings Find:** "Unknown tag alerts" plus a manual "Unknown Tag Search" in the SmartThings app (Life > Find). Detects SmartTags.

### Cited Findings
- Android: path Settings > Safety & emergency > Unknown tracker alerts > Scan now. Works with Find Hub-network-compatible tags, headphones and Apple AirTags — [Google Android Help](https://support.google.com/android/answer/13658562?hl=en)
- Samsung brought Android's unknown tracker alerts to Galaxy phones (Android 14 era) — [SamMobile](https://sammobile.com/news/android-14-unknown-tracker-alerts-samsung-phones)
- SmartThings Find: in the Life tab > Find, users can toggle "Unknown tag alerts" or run a manual search for nearby unknown SmartTags. The manual scan takes about 10 seconds and returns a list — [Android Central](https://androidcentral.com/accessories/how-to-scan-for-unknown-trackers-that-might-be-following-you); [Engadget](https://www.engadget.com/samsungs-smart-things-app-can-now-detect-if-someone-is-tracking-you-103551450.html); [MacRumors, 2021](https://www.macrumors.com/2021/04/20/samsung-smartthings-prevent-tracker-stalking/)
- Limitation: a community report describes a SmartTag in the car going undetected for about 4 days, and a manual scan also found nothing — [Samsung EU Community](https://eu.community.samsung.com/t5/mobile-apps-services/unknown-smart-tag-not-found/td-p/8199510)

### Gaps
- No One UI 9-specific changes were found.

## 5. Automatic restart / Inactivity restart (72 h) and BFU

### Takeaway
Samsung has its own "Inactivity restart" (introduced with One UI 8.0, according to Samsung support). It reboots a device that has stayed locked for 72 hours, putting it from AFU into BFU. It is on by default per Samsung, and the toggle is under Settings > Security and privacy > More security settings. On the S25 series it arrived with the One UI 8.5 beta / November 2025 patch. Separately, the older "Auto restart" in Device care is a scheduled restart for maintenance, not tied to inactivity.

### Cited Findings
- Samsung support describes "Inactivity restart" as a One UI 8.0 feature: it reboots a locked, unused device after 72 hours and moves it from After First Unlock (AFU) to Before First Unlock (BFU), keeping user data encrypted. Enabled by default. Path: Settings > Security and privacy > More security settings — [Samsung US Support: device keeps restarting](https://www.samsung.com/us/support/troubleshoot/TSG10013116/)
- The countdown starts at lock and resets at every unlock. After the reboot, fingerprint unlock is unavailable until PIN, pattern or password is entered — [SamMobile](https://www.sammobile.com/news/inactivity-restart-security-feature-is-reaching-more-phones-galaxy-s26/); [SamMobile, April 2025](https://www.sammobile.com/news/your-galaxy-phone-will-now-restart-automatically-if-locked-for-three-days/)
- Rollout: Fold7 on One UI 8 with the February 2026 patch; S25 series on One UI 8.5 beta / November 2025 patch; S26 from about March 2026 — [Android Central](https://www.androidcentral.com/phones/samsung-galaxy/samsung-expands-inactivity-restart-a-safety-net-for-galaxy-phones-left-idle); [Android Authority](https://www.androidauthority.com/galaxy-s26-inactivity-restart-3647564); [Tom's Guide](https://www.tomsguide.com/phones/samsung-phones/samsung-galaxys-new-inactivity-restart-locks-out-thieves-and-keeps-you-safe-heres-how-it-works)
- Conflicting claim: one report says Samsung does not enable it by default on the S26 — [CCStartup](https://ccstartup.com/blog/?p=367158). This contradicts the Samsung support page.
- An older WebProNews claim that Samsung's restart is based on uptime is unconfirmed and was written before launch — [WebProNews](https://www.webpronews.com/samsungs-galaxy-s26-will-automatically-restart-every-72-hours-heres-why-that-matters/)
- Google: a Play services update (25.14, April 2025) added an automatic restart after 3 days locked, returning the device to BFU state — [CyberInsider](https://cyberinsider.com/google-adds-auto-restart-to-android-to-block-unauthorized-data-extraction/); [CybersecurityNews](https://cybersecuritynews.com/android-device-will-restart-automatically/). SammyGuru reports that Google called the setting optional and tied it to Advanced Protection — [SammyGuru](https://sammyguru.com/?p=17078)
- **BFU rationale:** in BFU, user data stays encrypted and inaccessible until the PIN, pattern or password is entered. This makes forensic extraction of a stolen device harder — [Samsung Community](https://r2.community.samsung.com/t5/Galaxy-S/Android-s-auto-reboot-feature-is-definitely-a-nifty-little/td-p/18984836); [CyberInsider](https://cyberinsider.com/google-adds-auto-restart-to-android-to-block-unauthorized-data-extraction/)

### Inferences
- On the S25 Ultra under One UI 9, Samsung's own Inactivity restart, which is on by default, should be present, so the question of whether Google's mechanism also applies is practically moot. Device care "Auto restart" (scheduled) remains a separate feature.

### Gaps
- No Samsung source was found on the Device care "Auto restart" under One UI 9 (path, default).
- Whether Google's Play services reboot runs in addition to Samsung's toggle on Samsung devices is unclear.
- The page fetches from SamMobile were blocked (HTTP 403), so snippets were used.

## 6. Galaxy AI "Process data only on device"

### Takeaway
The toggle exists since One UI 6.1 (S24) under Settings > Galaxy AI (some versions: Advanced intelligence). No source found here explicitly confirms that it is still present in One UI 9 or gives an updated list of affected features. Cloud-dependent features stop working or are limited when it is on.

### Cited Findings
- Path: Settings > Galaxy AI. It affects only Samsung's own AI features, not third-party AI. On-device processing may limit features that depend on online processing — [9to5Google, 20.02.2025](https://9to5google.com/2025/02/20/how-to-turn-on-galaxy-ai-on-device-processing/); [Samsung Newsroom AU](https://news.samsung.com/au/your-privacy-secured-how-galaxy-ai-empowers-you-to-take-control-of-your-data)
- SamMobile (September 2025, One UI 7/8) lists which features remain and which are lost. Later firmware can change this — [SamMobile](https://www.sammobile.com/news/use-galaxy-ai-without-sending-data-to-samsung-heres-what-you-lose/). The page could not be fetched (HTTP 403).
- Knox: on-device AI data is not used for training. Cloud-processed data may be. Admins can enforce the setting via the Knox Service Plugin — [Samsung Knox Docs: Data processing for Galaxy AI](https://docs.samsungknox.com/admin/knox-platform-for-enterprise/knox-service-plugin/configure-advanced-policies/data-processing-for-galaxy-ai/)
- One UI 9 has an "AI assistant activity" log showing when Galaxy AI runs tasks, accesses cross-app data or runs background routines such as Now Brief (Samsung support, September 2026) — [Samsung India Support](https://www.samsung.com/in/support/mobile-devices/how-to-use-ai-assistant-activity-feature-in-galaxy-devices/)
- Personal AI data stays on-device, protected by KEEP (Knox Enhanced Encrypted Protection) and Knox Vault (One UI 8) — [Samsung Newsroom US](https://news.samsung.com/us/samsung-introduces-future-ready-mobile-security-for-personalized-ai-experiences/)

### Gaps
- There is no confirmed One UI 9 list of features that fail with the toggle on. From earlier coverage (not verified here, own knowledge), these typically included Live Translate for some languages, cloud-based Note Assist / Browsing Assist, and generative photo editing. This needs checking before use.

## 7. Secure Folder, Maintenance mode, App pinning, Private Share, Samsung Pass / passkeys, Knox Vault

### Takeaway
Knox Vault (an isolated hardware environment for PINs, passwords, biometrics and keys) and Secure Folder are well documented. In One UI 8, Secure Folder gained stronger biometrics and a "Hide Secure Folder" option in Quick settings. Maintenance mode, App pinning, Private Share and Samsung Pass passkeys could not be sourced in this round.

### Cited Findings
- **Knox Vault:** stores passwords, PINs and biometrics in a physically isolated environment, so they stay protected even if the OS is compromised. It also protects KEEP-encrypted personal AI data — [Samsung Newsroom UK](https://news.samsung.com/uk/samsung-introduces-future-ready-mobile-security-for-personalised-ai-experiences); [Samsung Mobile Press](https://samsungmobilepress.com/articles/samsung-introduces-future-ready-mobile-security-for-personalized-ai-experiences)
- **Secure Folder in One UI 8 (beta on S25):** dedicated biometric protection and "Hide Secure Folder" via Quick settings. While hidden, data is fully encrypted until reactivated — [IDTechWire](https://idtechwire.com/samsung-adds-dedicated-fingerprint-security-layer-to-secure-folder-feature/); [SecurityWeek](https://www.securityweek.com/samsung-announces-security-improvements-for-galaxy-smartphones/)
- **Secure Folder recovery:** without "Reset with Samsung account" enabled, a forgotten Secure Folder passcode means no access. Samsung Pass and autofill can be set up inside Secure Folder — [Samsung US Support](https://www.samsung.com/us/support/answer/ANS10001401)
- Community reports of Secure Folder problems after the Android 16 / One UI 8 update on S25 devices — [Samsung EU Community](https://eu.community.samsung.com/t5/galaxy-s25-series/secure-folder-problem-after-upgrade-to-android-16/m-p/13472040/highlight/true)
- New One UI 9 privacy features in Now Brief: Security Brief and Privacy Alerts. Both require a Samsung account and Now Brief — [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)

### Gaps
- No source was found in this round for Maintenance mode (path, behaviour under One UI 9), App pinning, Private Share (status: possibly discontinued or folded into Quick Share; unverified), Samsung Pass passkey support or Samsung Pass merging into Samsung Wallet, or an explicit statement that Knox Vault is in the S25 Ultra. From own knowledge, Knox Vault has been in S-series flagships since the S21, but this needs verification.

## 8. 72-hour requirement for PIN entry with biometrics

### Takeaway
Android requires the primary credential (PIN, pattern or password) at least every 72 hours, even when biometrics are used. Notebookcheck confirms this for One UI 9. The device policy API also defines 72 hours as the maximum strong-auth timeout.

### Cited Findings
- "PIN, pattern, or password entry is required at least every 72 hours, even if you usually use a fingerprint" (One UI 9 context) — [Notebookcheck](https://www.notebookcheck.net/Samsung-One-UI-9-Security-features-you-must-turn-on-yourself.1415703.0.html)
- `DevicePolicyManager.setRequiredStrongAuthTimeout`: platform-defined minimum 1 hour, maximum 72 hours, for when strong auth is required after biometric or trust agent unlocks — [Microsoft Learn: Android API mirror](https://learn.microsoft.com/es-es/dotnet/api/android.app.admin.devicepolicymanager.setrequiredstrongauthtimeout?view=net-android-35.0)

### Inferences
- The 72-hour strong-auth timeout and the 72-hour Inactivity restart are separate mechanisms. The first forces PIN entry; the second forces a reboot into BFU.

## 9. S25 Ultra S Pen without Bluetooth

### Takeaway
The S25 Ultra S Pen has no Bluetooth (BLE), so Air actions and the remote camera shutter are gone. Older BLE S Pens do not bring the features back. Air Command still works. Security-wise, this removes one Bluetooth radio and pairing interface.

### Cited Findings
- Samsung: "Air actions are not available with the Galaxy S25 Ultra S Pen" — [Samsung RU Support](https://www.samsung.com/ru/support/mobile-devices/air-commands-with-s-pen-do-not-work-on-my-galaxy-s25-ultra)
- The S25 Ultra does not support a BLE S Pen, not even the S24 Ultra's. The remote camera shutter is gone. Samsung's reason: fewer than 1% of users used these features. Air Command is unaffected — [Android Authority](https://androidauthority.com/galaxy-s25-ultra-s-pen-no-bluetooth-3517872); [SamMobile, 22.01.2025](https://www.sammobile.com/news/it-is-true-galaxy-s25-ultra-s-pen-does-not-have-bluetooth-features/); [Neowin](https://www.neowin.net/news/samsung-officially-confirms-that-the-galaxy-s25-ultra-does-no-support-bluetooth-s-pen/); [Android Central](https://www.androidcentral.com/phones/does-the-galaxy-s25-ultra-support-the-galaxy-s24-ultra-s-pen)

### Inferences
- Without a Bluetooth link to the S Pen there is one fewer wireless attack surface (pairing, remote triggering). No source makes this security argument explicitly, so it is an inference.

## 10. Update commitment for the S25 Ultra

### Takeaway
Samsung promises 7 OS generations and 7 years of security updates for the S25 series, which launched in February 2025. That means up to Android 22 (One UI 15 / Android 15 + 7). Security support runs to about early 2032. Exact end dates differ between sources.

### Cited Findings
- 7 generations of One UI/Android plus 7 years of security updates. Samsung did not extend the promise compared with the S24 — [9to5Google, 22.01.2025](https://9to5google.com/2025/01/22/samsung-doesnt-increase-update-promise-galaxy-s25/); [SamMobile, 22.01.2025](https://www.sammobile.com/news/samsung-galaxy-s25-plus-ultra-android-one-ui-updates/); [Android Central](https://www.androidcentral.com/phones/how-many-software-updates-will-the-samsung-galaxy-s25-get)
- Enterprise Editions: 8 OS upgrades (up to Android 23) and security updates until January 2032 — [SamMobile, April 2025](https://www.sammobile.com/news/galaxy-s25-enterprise-edition-8-major-android-os-updates)
- Conflicting end dates: some aggregators say security support runs until 2031, others until 2032. These are low-quality sources — [ShopSavvy](https://shopsavvy.com/answers/how-many-years-of-software-updates-will-the-samsung-galaxy-s25-ultra-receive.md)

### Inferences
- Launch was February 2025 and Android 15 / One UI 7 was the starting version. Seven upgrades lead to Android 22. Seven years from launch points to about February 2032 for security updates. One UI 9 / Android 17 is the 2nd of the 7 OS upgrades (One UI 8 / Android 16 was the 1st; One UI 8.5 was a point release).

### Gaps
- Samsung's official security update page (security.samsungmobile.com) with the exact end date was not fetched. The update tier (monthly or quarterly) over the lifecycle has not been verified.
