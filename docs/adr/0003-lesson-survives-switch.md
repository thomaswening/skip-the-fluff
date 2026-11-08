# ADR-0003: The lesson survives an app switch because the app keeps its WebView and renderer process alive

- **Status:** Accepted
- **Date:** 2026-11-08
- **Decided by:** Thomas, 8 November 2026, in conversation, on Claude's brief and the [spike](../spikes/2026-11-08-lesson-survival.md) he ran

## Context

[REQ-014](../requirements.md#req-014) requires that a lesson continues at the [exercise](../glossary.md#exercise) the learner left after up to 5 minutes in another app. Its measurement is 5 switches of 4 minutes on the [reference device](../glossary.md#reference-device), every one passing. Its note accepts that Android may end a background app under memory pressure, but only beyond ordinary short switches. In Chrome, Thomas loses the lesson when he switches to ChatGPT.

A lesson's state lives only in [the website](../glossary.md#the-website)'s JavaScript memory. Neither Skip the Fluff nor Android can save and restore it: `WebView.saveState` keeps the page history, not the page. Restoring it some other way would mean reading or changing the website's state, which [REQ-003](../requirements.md#req-003) and [REQ-012](../requirements.md#req-012) rule out. So the lesson survives exactly as long as the [WebView](../glossary.md#webview) and its page stay alive. Three things can end them:

1. **The app rebuilding its window.** By default Android destroys and rebuilds an app's window when the screen rotates, the window changes size or dark mode is switched, and the WebView goes with it. In WPF terms, it is as if every theme change closed and reopened the `Window`.
2. **Android ending the WebView's [renderer process](../glossary.md#renderer-process)** to free memory. The page runs in that separate process. By default Android lowers its priority as soon as the app is in the background, so it is among the first things ended.
3. **Android ending the whole app** to free memory.

The [spike](../spikes/2026-11-08-lesson-survival.md) of 8 November 2026 measured this on the S25 FE:

- With only point 1 handled, the lesson survived a clock app, the home screen, Spotify and 10 minutes away. It was lost after 4 minutes of autoplaying Instagram stories.
- With the renderer process also kept as important as the app, the Instagram case passed 5 times out of 5.
- The website does not end a hidden lesson by itself.

## Decision

**Skip the Fluff keeps the lesson by keeping the WebView and its renderer process alive in the background. It does not save or restore the lesson, and it runs no [foreground service](../glossary.md#foreground-service).**

1. **No window rebuilds.** The app's one window declares the configuration changes it handles itself (`android:configChanges`): orientation, screen size and layout, smallest screen size, keyboard, dark mode (`uiMode`), font size, density and language. Android then never rebuilds it, and the WebView survives. The app redraws its own views for the new configuration, such as dark mode for the menu button of [REQ-035](../requirements.md#req-035).
2. **The renderer process keeps the app's priority.** The app calls `setRendererPriorityPolicy(RENDERER_PRIORITY_IMPORTANT, false)` on the WebView. The `false` means the priority is not lowered when the app is not visible.
3. **Paused, not frozen.** In the background the app calls `webView.onPause()`, which stops media and animations, and `onResume()` on return. It does not call `pauseTimers()`, which would freeze every WebView's JavaScript.
4. **Recovering without crashing.** IF Android ends the renderer process, THEN the app replaces the WebView and loads the website at its last address (`onRenderProcessGone`). IF Android ends the whole app, THEN it restores the WebView's page history from its saved state. In both cases the lesson is lost, as [REQ-014](../requirements.md#req-014)'s note accepts, but the app does not crash and the learner is signed in ([REQ-016](../requirements.md#req-016)).
5. **No foreground service.** A foreground service with a "Lesson kept open" notification during lessons would make Android less likely to end the app. The [spike](../glossary.md#spike) showed it is not needed. IF reports ([REQ-033](../requirements.md#req-033)) later show lessons lost in ordinary use, THEN a new [ADR](../glossary.md#adr) reconsiders it.

## Alternatives considered

- **A foreground service during lessons:** the strongest alternative. It is how a music player tells Android "I'm busy". It was set aside because the spike passed without it. It would put a notification on screen in every lesson and add a service to build and test.
- **Saving and restoring the lesson:** not possible without reading or changing the website's state, which [REQ-003](../requirements.md#req-003) and [REQ-012](../requirements.md#req-012) rule out.
- **Freezing the page in the background (`pauseTimers()`):** saves battery but would freeze the lesson's JavaScript. Nothing measured required it.
- **Letting Android rebuild the window and restoring the WebView:** the page and the lesson would be lost on every rotation or dark-mode switch.

## Consequences

- Easier: no notification, no service and no extra permission. The decision is a few lines of the app's setup.
- Harder: the app must handle configuration changes itself. A view of its own that does not redraw for dark mode or a new size shows stale colours or layout.
- Harder: the renderer process keeps the app's priority in the background, so it uses memory that Android would otherwise free sooner. It is still ended when the whole app is.
- Not covered: phones with less memory than the S25 FE, heavier apps than Instagram, and absences longer than 10 minutes were not measured ([spike](../spikes/2026-11-08-lesson-survival.md), Not tested).
- Follow-up: the first build repeats [REQ-014](../requirements.md#req-014)'s full measurement, including a rotation and a dark-mode switch during the lesson.
