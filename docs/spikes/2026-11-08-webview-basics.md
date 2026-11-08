# Spike: Android's WebView can show the website with sign-in, microphone, audio and autofill working

Date: 8 November 2026 | For: [ADR-0001](../adr/0001-android-webview.md)

## Question

On the [reference device](../glossary.md#reference-device), with [the website](../glossary.md#the-website) shown in Android's [WebView](../glossary.md#webview) and no [fixes](../glossary.md#fix) applied:

1. Does email and password sign-in work, and does it survive closing and reopening the app ([REQ-016](../requirements.md#req-016))?
2. Does a speaking [exercise](../glossary.md#exercise) record the learner's voice once Android's microphone permission is granted ([REQ-038](../requirements.md#req-038))?
3. Does a listening exercise play its audio without a tap ([REQ-038](../requirements.md#req-038))?
4. Does a password manager fill the sign-in form through Android's [autofill](../glossary.md#autofill) ([REQ-039](../requirements.md#req-039))?
5. Does the website show the same lesson screens as in Chrome?

## Method

For this [spike](../glossary.md#spike), Claude wrote a throwaway app, kept in [2026-11-08-webview-basics/](2026-11-08-webview-basics/app/src/main/java/io/github/thomaswening/skipthefluff/spike/MainActivity.kt). Its one screen is a WebView that opens `https://www.duolingo.com/` with the settings [ADR-0001](../adr/0001-android-webview.md) names:

- JavaScript and the website's local storage on;
- `mediaPlaybackRequiresUserGesture` off, so audio plays without a tap;
- cookies accepted and written to disk when the app goes to the background;
- navigation to any host other than duolingo.com and its subdomains handed to the default browser;
- the website's microphone request passed on once Android's `RECORD_AUDIO` permission is granted;
- the back gesture going back one page.

It was built as a debug [APK](../glossary.md#apk) with Gradle 8.14.3, Android Gradle Plugin 8.13.0, Kotlin 2.2.20 and Microsoft's OpenJDK 21, for Android 8.0 (API 26) and up, targeting API 36. To build it again:

```
cd docs/spikes/2026-11-08-webview-basics
./gradlew assembleDebug
```

Thomas installed it on the Samsung Galaxy S25 FE by copying the APK to the phone and did each check by hand on 8 November 2026. The phone's keyboard is SwiftKey and its default browser is Firefox.

## Result

| Question | Answer | Observed |
|---|---|---|
| 1. Sign-in survives a restart | yes | Signed in with email and password, closed the app from the recent-apps screen, reopened it: still signed in. |
| 2. Microphone | yes | Android's permission dialog appeared on the first speaking exercise. After it was granted, speaking exercises worked as in Chrome. |
| 3. Audio without a tap | yes | Listening exercises played their audio by themselves. |
| 4. Autofill | yes, with Google; no, with Avira | The long-press menu of the email field offers "Autofill", so the WebView takes part in Android's autofill. With Avira selected as the autofill service, Avira answered that the field cannot be autofilled. With Google selected and a login with its password saved for `duolingo.com` in Google Password Manager, Google offered the login in the sign-in form and suggested its password. |
| 5. Same lesson screens as in Chrome | yes, with one difference caused by the spike | The screens matched Chrome. The page reached under Android's status and navigation bars, so the website's top and bottom rows of buttons were hard to hit. |

Two findings explain the differences:

- **Avira does not fill a website inside another app.** Android passed the request to Avira, which then found nothing to fill. Avira probably matches its logins to apps, not to the website a WebView shows. This is Avira's limitation and cannot be fixed by Skip the Fluff.
- **Google Password Manager can be told never to save passwords for an app or a site.** Thomas had excluded both duolingo.com and the spike app in an earlier answer to Google's save prompt, so at first Google neither saved nor offered a password. After he removed both from the exclusion list in Google Password Manager's settings, saving and filling worked. A learner who reports that autofill does nothing should check that list first.
- **The overlap with the system bars comes from the spike, not the WebView.** Android 15 and later let an app that targets API 35 or higher draw behind the status and navigation bars, and the spike reserves no room for them.

## Not tested

- Other password managers, such as Bitwarden, Samsung Pass or 1Password.
- The Android and WebView versions on the phone were not written down.
- A lesson kept across a switch to another app ([REQ-014](../requirements.md#req-014)). That is the subject of its own [ADR](../glossary.md#adr).
- Behaviour with [fixes](../glossary.md#fix) applied, and Google sign-in, which [REQ-015](../requirements.md#req-015) sends to the default browser.

## Conclusion

Android's WebView meets [REQ-016](../requirements.md#req-016) and [REQ-038](../requirements.md#req-038) on the reference device with documented settings alone. It takes part in Android's autofill, and Google Password Manager saves and fills a password for duolingo.com, which is what [REQ-039](../requirements.md#req-039)'s acceptance line names. The first build must leave room for the system bars outside lessons ([REQ-013](../requirements.md#req-013)). Adopted in [ADR-0001](../adr/0001-android-webview.md), which is accepted on this evidence.
