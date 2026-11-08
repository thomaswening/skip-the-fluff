# ADR-0004: The minimum is Android 10 and follows Chrome's support (resolves OPEN-D)

- **Status:** Proposed
- **Date:** 2026-11-08
- **Decided by:** Thomas, 8 November 2026, in conversation, on Claude's brief

## Context

[REQ-022](../requirements.md#req-022) leaves the minimum Android version to [OPEN-D](../requirements.md#open-d). Android checks it when the app is installed: it is the [APK](../glossary.md#apk)'s `minSdk`, comparable to the minimum Windows version of a .NET app. [REQ-047](../requirements.md#req-047) requires it to be stated before installing, together with a minimum [WebView](../glossary.md#webview) version.

What sets the lower bound:

- **Android 8.0 is the hard floor.** [Autofill](../glossary.md#autofill) in the WebView ([REQ-039](../requirements.md#req-039)) and the two calls of [ADR-0003](0003-lesson-survives-switch.md), `setRendererPriorityPolicy` and `onRenderProcessGone`, need Android 8.0 (API level 26) or later.
- **Security sets the real bound.** Chrome 138, released in mid-2025, was the last version for Android 8 and 9. Chrome 139 and later need Android 10 ([Android Authority](https://www.androidauthority.com/chrome-dropping-support-android-8-9-3571442), [heise](https://heise.de/-10457380)). The WebView is built from Chrome's code and released with it. On Android 8 or 9 it is therefore frozen without security updates, while it shows a page where learners type their Duolingo password. These sources speak of Chrome, so this record assumes that the WebView follows the same cut-off.
- **The features of [ADR-0002](0002-fix-engine.md) are checked while the app runs**, not by version number (`WebViewFeature.isFeatureSupported`, [WebViewCompat reference](https://developer.android.com/reference/androidx/webkit/WebViewCompat)). [REQ-048](../requirements.md#req-048)'s screen checks them. A WebView that still receives updates has them.

The [reference device](../glossary.md#reference-device) runs a far newer Android, so the choice affects other learners, not Thomas.

## Decision

**Skip the Fluff runs on Android 10 (API level 29) and later. Its minimum follows the oldest Android version that Chrome, and with it the WebView, still updates.**

1. **Minimum Android 10.** The app's `minSdk` is 29.
2. **The minimum follows Chrome.** WHEN Google stops WebView updates for an Android version, THEN the next release of Skip the Fluff raises its minimum to match, and its release notes say so. Phones already running the app keep working but receive no further release, because Android does not install an APK whose minimum is above the phone's version.
3. **Learners whose phone is left behind are told.** Each release carries a small machine-readable file stating its minimum Android version. WHEN the update check of [REQ-040](../requirements.md#req-040), automatic or started by the learner, finds a newer release that the phone cannot install, THEN Skip the Fluff says that this phone gets no more updates and names the last version for it. A learner who never checks learns it from the README and the release pages ([REQ-047](../requirements.md#req-047)).
4. **No fixed minimum WebView version of our own.** The README and each release page state "an Android System WebView that still receives updates", and the WebView version the release was tested with, read on the reference device. For example: "Version 0.1.0 was tested with Android System WebView 154.0.7712.45 on a Samsung Galaxy S25 FE" (the number is made up). The tested version records what is known to work. It is not a cut-off. What protects learners is [REQ-048](../requirements.md#req-048)'s feature check.

The requirements change with this record (version 0.5): [REQ-022](../requirements.md#req-022) names Android 10. [REQ-040](../requirements.md#req-040) gets the notice of point 3 and a button to check for updates by hand. [REQ-020](../requirements.md#req-020) allows that check. [REQ-047](../requirements.md#req-047) takes the wording of point 4. The manual button was asked for by Thomas while deciding this record.

## Alternatives considered

- **Android 8.0:** the strongest alternative, since it reaches the most phones. It was set aside because phones on Android 8 and 9 run a WebView that no longer receives security updates. Every one of them would pass [REQ-048](../requirements.md#req-048)'s feature check, so the app could not even warn them.
- **Android 12 or later:** reaches fewer phones for hardly simpler code. Nothing the app needs arrived in Android 11 or 12.
- **A fixed minimum WebView version of our own:** it would be a guess. A WebView older than the guess but still updated would be refused for no reason, and one newer than it but missing a feature would pass.

## Consequences

- Easier: every supported phone runs a WebView that still receives security updates, at least until Google's next cut-off.
- Easier: the app's code can rely on Android 10's behaviour, such as the WebView being updated through the Play Store as "Android System WebView" on every supported phone.
- Harder: learners on Android 8 and 9 cannot install Skip the Fluff.
- Harder: each Chrome cut-off is a task for the maintainer. Someone must notice it, raise the minimum and say so in the release notes.
- Follow-up: the release workflow attaches the machine-readable file of point 3 to every release.
- Follow-up: the first release reads the WebView version on the reference device for the README and the release page.
