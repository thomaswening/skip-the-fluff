# Spike: a lesson in the WebView survives 4 minutes in another app

Date: 8 November 2026 | For: [ADR-0003](../adr/0003-lesson-survives-switch.md), [REQ-014](../requirements.md#req-014)

## Question

On the [reference device](../glossary.md#reference-device), with [the website](../glossary.md#the-website) shown in Android's [WebView](../glossary.md#webview):

1. Does a lesson continue at the [exercise](../glossary.md#exercise) the learner left after 4 minutes in another app, as [REQ-014](../requirements.md#req-014)'s measurement asks?
2. Does the website itself end a lesson whose page is hidden?
3. IF the lesson is lost, THEN which process did Android end: the WebView's [renderer process](../glossary.md#renderer-process) or the whole app?
4. Does keeping the renderer process as important as the app in the background make the lesson survive where it did not before?

## Method

This [spike](../glossary.md#spike) ran in two rounds on 8 November 2026. Thomas did each run by hand on the Samsung Galaxy S25 FE:

1. Start a lesson and answer two exercises.
2. Switch to another app and set a timer.
3. Return when the timer runs out, and note whether the lesson is at the same exercise.

**Round 1** used the app of the [WebView spike](2026-11-08-webview-basics.md) unchanged. It keeps its window across rotation and similar changes (`android:configChanges`). It does nothing else for the background.

**Round 2** used a copy of that app with three changes, kept in [2026-11-08-lesson-survival/](2026-11-08-lesson-survival/app/src/main/java/io/github/thomaswening/skipthefluff/spike/MainActivity.kt):

- `setRendererPriorityPolicy(RENDERER_PRIORITY_IMPORTANT, false)`, so that the renderer process stays as important as the app while the app is not visible. Android's default lowers it as soon as the app is in the background.
- `webView.onPause()` and `onResume()` with the app, which pause media and animations but leave the page's JavaScript running.
- A short message on screen if Android ended the renderer process (`onRenderProcessGone`) or restarted the whole app (a saved state at start).

Round 2 repeated only the run that had failed in round 1. It was built and installed as in the WebView spike. To build it again:

```
cd docs/spikes/2026-11-08-lesson-survival
./gradlew assembleDebug
```

## Result

| Round | Away in | Time away | Lesson continued |
|---|---|---|---|
| 1 | Google's clock app, timer running | 4 minutes | yes |
| 1 | Instagram, stories playing by themselves | 4 minutes | no, the page reloaded |
| 1 | home screen | 4 minutes | yes |
| 1 | Spotify, a song on repeat | 4 minutes | yes |
| 1 | Google's clock app | 10 minutes | yes |
| 2 | Instagram, stories playing by themselves | 4 minutes, 5 times | yes, 5 of 5; no message appeared |

| Question | Answer |
|---|---|
| 1. Lesson survives 4 minutes | Not reliably in round 1 (3 of 4). Yes in round 2 (5 of 5 in the hardest case). |
| 2. The website ends a hidden lesson | No: the lesson survived 10 minutes in round 1. Thomas's lost lessons in Chrome therefore came from the browser or Android, not from the website. |
| 3. Which process Android ended | Not known for round 1, which had no messages. The page reloaded instead of the app showing an error, which fits Android ending a process to free memory. Round 2 lost nothing, so it shows nothing. |
| 4. Renderer priority helps | Yes: the case that failed in round 1 passed 5 times in round 2. |

## Not tested

- Thomas recalled four runs of 4 minutes in round 1, not five.
- Round 2 did not repeat the clock, home-screen and Spotify runs. Its changes only raise the renderer process's priority, so they are not expected to break them.
- Other heavy apps, such as the camera or a game, longer times away, and phones with less memory than the S25 FE.
- Which process Android ended in round 1.
- A lesson kept while the screen is rotated or dark mode is switched during the lesson.

## Conclusion

A lesson survives a 4-minute switch on the reference device, even to a video app, when the app keeps its window across configuration changes and keeps the renderer process as important as itself. The website does not end hidden lessons. No [foreground service](../glossary.md#foreground-service) is needed. Adopted in [ADR-0003](../adr/0003-lesson-survives-switch.md).
