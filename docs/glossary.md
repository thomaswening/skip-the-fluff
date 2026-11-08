# Skip the Fluff: Glossary

This glossary explains the terms a reader of Skip the Fluff's documents may not know. Each entry is tagged by where the term comes from:

- *domain*: Android and Duolingo, found in their documentation;
- *engineering*: software engineering, found in programming references;
- *project*: coined by Skip the Fluff, or used by Skip the Fluff in a sense of its own.

A document links a term on its first use, e.g. `[fix](glossary.md#fix)`. Anchors never change: a renamed term keeps its old anchor. Where [requirements.md](requirements.md) defines a term bindingly, the entry here is a one-line gloss and the requirements' wording governs.

Fixed labels after an entry relate it to others: **Kind of** names the broader term, **See also** related terms, **Not to be confused with** a look-alike with another meaning, **Defined in** the document whose wording governs.

<a id="adr"></a>**ADR** (architecture decision record) *(engineering)*: a short document that records one design decision, the options considered and why one was chosen. Example: the planned ADR on whether Skip the Fluff uses a [WebView](#webview) or a [Custom Tab](#custom-tab).

<a id="apk"></a>**APK** (Android package) *(domain)*: the file in which an Android app is distributed and installed, comparable to an `.msi` installer on Windows. Example: `skip-the-fluff-0.1.0.apk`, downloaded from GitHub Releases.

- See also: [sideloading](#sideloading)

<a id="conventional-commits"></a>**Conventional Commits** *(engineering)*: a convention for commit messages, `type(scope): summary`, that a program can read to choose the next version and write the changelog. Example: `feat(fixes): hide the "use the app" prompt`.

- See also: [release-please](#release-please), [SemVer](#semver)

<a id="custom-tab"></a>**Custom Tab** *(domain)*: a browser tab that an Android app opens with the phone's own browser, styled like part of the app. Unlike a [WebView](#webview), the app cannot change the page in it. Example: Google sign-in works in a Custom Tab but not in a WebView ([OPEN-B](requirements.md#open-b)).

- Not to be confused with: [WebView](#webview)

<a id="disputed-fix"></a>**disputed fix** *(project)*: a [fix](#fix) that removes a screen other learners may reasonably want to keep. It ships switched off. Example: none so far.

- Kind of: [fix](#fix)
- Defined in: [requirements.md, Requirement conventions](requirements.md#requirement-conventions)

<a id="exercise"></a>**exercise** *(project)*: a screen of a lesson on which the learner answers something. Skip the Fluff never acts on one. Example: the screen on which the learner matches audio snippets to their translations.

- Not to be confused with: [non-exercise screen](#non-exercise-screen)
- Defined in: [requirements.md, Requirement conventions](requirements.md#requirement-conventions)

<a id="fix"></a>**fix** *(project)*: one change Skip the Fluff applies to [the website](#the-website) on the learner's device, implementing one requirement, which can be switched off on its own. Example: hiding the "use the app" prompt ([REQ-005](requirements.md#req-005)).

- See also: [disputed fix](#disputed-fix)
- Defined in: [requirements.md, Requirement conventions](requirements.md#requirement-conventions)

<a id="important-words-preview"></a>**"important words" preview** *(project)*: the screen before the first exercise of a listening lesson that lists the lesson's new words and plays them as audio. Example: a list of five French words, each with a speaker button.

- Kind of: [non-exercise screen](#non-exercise-screen)
- Defined in: [REQ-010](requirements.md#req-010)

<a id="non-exercise-screen"></a>**non-exercise screen** *(project)*: any screen in or around a lesson that is not an [exercise](#exercise): intros, vocabulary previews, celebrations, animations, prompts and promotions. Example: the unit-completion animation ([REQ-009](requirements.md#req-009)).

- Not to be confused with: [exercise](#exercise)
- Defined in: [requirements.md, Requirement conventions](requirements.md#requirement-conventions)

<a id="reference-device"></a>**reference device** *(project)*: the phone every measured requirement is checked on: Thomas's Samsung Galaxy S25 FE, in portrait orientation, with Chrome's current [WebView](#webview). Example: [REQ-009](requirements.md#req-009)'s 1-second threshold is measured on it.

- Defined in: [requirements.md, Requirement conventions](requirements.md#requirement-conventions)

<a id="release-please"></a>**release-please** *(engineering)*: a tool by Google, run as a GitHub workflow, that reads [Conventional Commits](#conventional-commits) on `main` and keeps a [release PR](#release-pr) with the next version and changelog. Example: after `feat(fixes): hide the leaderboard popup` is merged, the release PR proposes 0.2.0.

- See also: [SemVer](#semver)

<a id="release-pr"></a>**release PR** *(engineering)*: the pull request [release-please](#release-please) keeps open and updates after every merge. Merging it publishes a release. Example: `chore(main): release 0.1.0`, which adds the 0.1.0 section to `CHANGELOG.md`.

<a id="semver"></a>**SemVer** (Semantic Versioning) *(engineering)*: version numbers `major.minor.patch`, raised by what a release changes. Before 1.0 the major number stays 0. Example: 0.2.0 → 0.2.1 for a bug fix, 0.2.1 → 0.3.0 for a new feature.

<a id="sideloading"></a>**sideloading** *(domain)*: installing an Android app from an [APK](#apk) file instead of from the Google Play Store. Android warns before it allows it. Example: downloading Skip the Fluff's APK in the phone's browser and opening it ([REQ-032](requirements.md#req-032)).

<a id="squash-merge"></a>**squash merge** *(engineering)*: merging a pull request as one new commit on `main` that holds all its changes, with the pull request's title as the commit message. The branch's own commits are not kept on `main`. Example: a branch with five commits becomes `docs(requirements): v0.3, second elicitation pass (#3)`.

<a id="the-website"></a>**the website** *(project)*: Duolingo's web application at duolingo.com, as served to a mobile browser. Skip the Fluff displays it; it does not own, copy or modify it. Example: the lesson page that Skip the Fluff shows full screen.

- Defined in: [requirements.md, Requirement conventions](requirements.md#requirement-conventions)

<a id="webview"></a>**WebView** *(domain)*: an Android component that shows web pages inside an app, comparable to WebView2 in a WPF window. The app can run its own CSS and JavaScript in the page. Example: Skip the Fluff shows [the website](#the-website) in a WebView and hides its prompts with injected CSS.

- Not to be confused with: [Custom Tab](#custom-tab)
