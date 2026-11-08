# Skip the Fluff: Verifiable Product Requirements
Version: 0.2 draft | 8 November 2026

**Status:**

- Second draft, after the first elicitation pass (8 November 2026). Not yet accepted as good enough to design against.
- The first release is a sideloaded Android app that shows the Duolingo website with the distractions in [section 2](#2-distraction-removal) removed.
- Still open: [OPEN-A](#open-a), [OPEN-B](#open-b), [OPEN-D](#open-d) and [OPEN-G](#open-g). Open decisions are collected in [section 10](#open-decisions); how the document got here is in the [revision history](#revision-history).

## Requirement conventions
**SHALL** = mandatory for the stated release; **SHOULD** = preferred, with an acceptable documented exception; **COULD** = optional or future capability. Each requirement has a stable ID and is never renumbered. Conditions are expressed as **WHEN … THEN …**, **IF … THEN …**, **BEFORE …**, **WHILE …** or **AFTER …**. An implementation technique is not a product requirement unless explicitly stated. Items marked **OPEN** require a decision before they can be treated as acceptance criteria. **OUT OF SCOPE** marks a capability explicitly not required for the first release.

**The website** = Duolingo's web application at duolingo.com, as served to a mobile browser. Skip the Fluff displays it; it does not own, copy or modify it.

**Fix** (used by [section 2](#2-distraction-removal), [REQ-018](#req-018) and [section 8](#8-maintainability)): one change Skip the Fluff applies to the website on the user's device, such as hiding an element or pressing a skip control, that implements one requirement. Each fix can be switched off on its own.

**Disputed fix** (used by [REQ-001](#req-001) and [REQ-018](#req-018)): a fix that removes a screen which other learners may reasonably find useful, even though Thomas does not. Whether a fix is disputed is stated in its requirement when the requirement is written.

**Exercise** = a screen of a lesson on which the learner answers something. **Non-exercise screen** = any other screen in or around a lesson: intros, vocabulary previews, celebrations, animations, prompts and promotions. The distinction is what [REQ-012](#req-012) relies on.

**Reference device** (used by [REQ-009](#req-009), [REQ-013](#req-013), [REQ-014](#req-014) and [REQ-022](#req-022)): Thomas's Samsung Galaxy S25 FE, in portrait orientation, with Chrome's current WebView.

## 1. Purpose and users

<a id="req-001"></a>**REQ-001 — Purpose (SHALL).** Skip the Fluff SHALL let a learner do Duolingo lessons on an Android phone through the website, with the screens that do not serve learning removed, so that a session takes little more time than its exercises do. It serves (a) Thomas, doing a short session before work, and (b) other Duolingo learners who install it from GitHub. Thomas's use decides which fixes exist. WHEN a fix serves Thomas but other learners may want to keep the screen it removes, THEN it is a [disputed fix](#requirement-conventions) and ships switched off ([REQ-018](#req-018)). Decided by Thomas, 8 November 2026. Rationale: the official Android app shows many motivational and gamification screens around each lesson; they break the flow and cost time, and Duolingo has not acted on repeated feedback asking for a setting to skip them.

<a id="req-002"></a>**REQ-002 — Installing without developer tools (SHALL).** A learner SHALL be able to install and use Skip the Fluff on an unrooted Android phone by downloading one APK, without a computer, ADB, root or developer options.

<a id="req-003"></a>**REQ-003 — Out of scope (OUT OF SCOPE).** The following are out of scope for every release, not only the first: completing, answering or skipping exercises; earning or changing XP, gems, streaks or other rewards; unlocking paid (Super or Max) features; modifying, decompiling or patching Duolingo's Android app; distribution through the Google Play Store ([REQ-028](#req-028)); iOS. Rationale: each would turn a display tool into one that cheats, circumvents payment or breaks Duolingo's terms of service, and so puts users' accounts and the project at risk.

<a id="req-004"></a>**REQ-004 — The website, not the app (SHALL).** Skip the Fluff SHALL work by displaying the website and SHALL NOT read, modify or depend on Duolingo's Android app. Rationale: Duolingo's terms forbid reverse engineering its apps, and a modified app can be detected by signature checks and the Play Integrity API; the website needs neither.

## 2. Distraction removal

None of the fixes in this section is a [disputed fix](#requirement-conventions) unless its requirement says so.

<a id="req-005"></a>**REQ-005 — "Use the app" prompts (SHALL).** WHEN the website shows a prompt to install or switch to Duolingo's app, THEN Skip the Fluff SHALL NOT display it. Rationale: in a mobile browser the prompt appears after every session and every time the user returns to the browser (observed by Thomas, 8 November 2026).

<a id="req-006"></a>**REQ-006 — Leaderboard and league popups (SHALL).** WHEN the website shows a leaderboard or league popup, THEN Skip the Fluff SHALL NOT display it.

<a id="req-007"></a>**REQ-007 — Streak and reminder nags (SHALL).** WHEN the website shows a screen or popup whose purpose is to encourage keeping a streak or to set up reminders, THEN Skip the Fluff SHALL NOT display it.

<a id="req-008"></a>**REQ-008 — Super and Max upsells (SHALL).** WHEN the website shows an advertisement or promotion for Super or Max, THEN Skip the Fluff SHALL NOT display it. It SHALL NOT unlock or imitate any paid feature ([REQ-003](#req-003)).

<a id="req-009"></a>**REQ-009 — Unit-completion animation (SHALL).** WHEN the learner completes a unit, THEN the learner SHALL be able to continue within 1 second of the completion animation appearing. Measurement: on the reference device, from the first frame of the animation to the next screen being shown or a continue control being usable, over 5 unit completions; every one within 1 second. Threshold decided by Thomas, 8 November 2026 ([OPEN-F](#open-f)).

<a id="req-010"></a>**REQ-010 — Listening-lesson intro (SHALL).** WHEN a listening lesson starts, THEN the first screen the learner sees SHALL be its first exercise. Neither the intro (which the website already lets the learner skip) nor the "important words" preview SHALL be shown.

<a id="req-011"></a>**REQ-011 — Further distractions (SHOULD).** Further non-exercise screens that Thomas identifies from screenshots taken during use SHOULD be removed. Each one becomes its own requirement in this section before it is implemented, stating whether it is a [disputed fix](#requirement-conventions). **[OPEN-A](#open-a):** which screens.

<a id="req-012"></a>**REQ-012 — Acting only on non-exercise screens (SHALL).** To remove a non-exercise screen, Skip the Fluff MAY hide it or press the website's own skip or continue control on it. It SHALL NOT interact with an exercise in any way. IF a screen cannot be identified with confidence as a non-exercise screen, THEN Skip the Fluff SHALL leave it displayed and SHALL NOT act on it. Rationale: pressing a control on the wrong screen could answer or skip an exercise, which [REQ-003](#req-003) excludes; showing a distraction is the cheaper failure. Decided by Thomas in the intake conversation and confirmed in the first elicitation pass, 8 November 2026.

## 3. Display and lesson flow

<a id="req-013"></a>**REQ-013 — Using the whole screen (SHALL).** Skip the Fluff SHALL show the website without any browser interface (address bar, tabs, toolbars). WHILE a lesson is shown, Android's status and navigation bars SHALL be hidden; outside a lesson they SHALL be shown. WHILE a lesson is shown on the reference device, all of an exercise, including its check or continue button, SHALL be visible without scrolling. This behaviour SHALL NOT have a switch in settings ([REQ-018](#req-018)). Rationale: in Chrome on the reference device, lesson content does not fit vertically (observed by Thomas, 8 November 2026); outside lessons the clock and normal navigation are more useful than the extra height. System bars and the missing switch decided by Thomas, 8 November 2026 ([OPEN-C](#open-c)).

<a id="req-014"></a>**REQ-014 — The lesson survives an app switch (SHALL).** WHEN the learner leaves Skip the Fluff during a lesson and returns within 5 minutes, THEN the lesson SHALL continue at the exercise the learner left. Measurement: on the reference device, start a lesson, answer two exercises, switch to another app for 4 minutes, return; repeated 5 times, the lesson continues every time. This behaviour SHALL NOT have a switch in settings ([REQ-018](#req-018)). Note: Android may end a background app under memory pressure; this requirement covers ordinary short switches. Rationale: in Chrome, switching to ChatGPT to ask why an answer was wrong cancels the lesson (observed by Thomas, 8 November 2026). The 5-minute window and the missing switch were confirmed by Thomas, 8 November 2026 ([OPEN-C](#open-c)).

<a id="req-015"></a>**REQ-015 — Links outside the website (SHOULD).** WHEN the learner follows a link that leads outside duolingo.com, THEN it SHOULD open in the phone's default browser, not inside Skip the Fluff.

## 4. Account and sign-in

<a id="req-016"></a>**REQ-016 — Email and password sign-in (SHALL).** The learner SHALL be able to sign in with an email address and password, and SHALL stay signed in across restarts of the app and the phone until they sign out. Rationale: Google blocks its sign-in inside embedded browsers, so email and password is the sign-in that works in the first release. Decided by Thomas, 8 November 2026.

<a id="req-017"></a>**REQ-017 — Google sign-in (COULD).** A later release COULD let the learner sign in with Google. Thomas wants this after the first release (8 November 2026). **[OPEN-B](#open-b):** how.

## 5. Settings and information

<a id="req-018"></a>**REQ-018 — Switching fixes on and off (SHALL).** Skip the Fluff SHALL have a settings screen on which the learner can switch each fix of [REQ-005](#req-005) to [REQ-011](#req-011) on or off individually. Every fix SHALL be on by default, except a [disputed fix](#requirement-conventions), which SHALL be off by default ([REQ-001](#req-001)). A change SHALL take effect at the latest when the next page of the website loads. Full screen ([REQ-013](#req-013)) and keeping the lesson across an app switch ([REQ-014](#req-014)) SHALL NOT have switches. Decided by Thomas, 8 November 2026. Rationale for the missing switches: those two are why the app exists, not optional fixes, and each switch adds a state to test.

<a id="req-019"></a>**REQ-019 — About and disclaimer (SHALL).** Skip the Fluff SHALL have an about screen stating that it is unofficial, not affiliated with or endorsed by Duolingo, and used at the user's own risk, with its version, licence and a link to its repository.

## 6. Privacy and safety

<a id="req-020"></a>**REQ-020 — No data collection (SHALL).** Skip the Fluff SHALL NOT make any network request other than those the website itself makes. It SHALL NOT contain analytics, tracking, advertising or crash reporting.

<a id="req-021"></a>**REQ-021 — No access to credentials (SHALL).** Sign-in SHALL happen in the website's own form. Skip the Fluff SHALL NOT read, store or transmit the learner's password.

## 7. Execution environment

<a id="req-022"></a>**REQ-022 — Supported devices (SHALL).** Skip the Fluff SHALL run on Android phones from a minimum Android version upwards, and SHALL be accepted on the reference device. **[OPEN-D](#open-d):** the minimum version.

<a id="req-029"></a>**REQ-029 — Language (SHALL).** The app's interface, the README and all documentation SHALL be in English. Decided by Thomas, 8 November 2026.

## 8. Maintainability

<a id="req-023"></a>**REQ-023 — Fixes kept apart from the app (SHALL).** Each fix SHALL be defined separately from the rest of the app and SHALL name the requirement it implements. Acceptance: when the website changes, repairing a fix changes only that fix's definition. Rationale: Duolingo can change the website at any time, so fixes are the part that will need repair most often.

<a id="req-024"></a>**REQ-024 — Fixes ship with the app (SHALL).** Fixes SHALL be delivered only inside the installed app, and an updated fix only through a new release. Skip the Fluff SHALL NOT download fixes, rules or code at run time. Rationale: code downloaded at run time could be altered in transit or at its source without review. Decided by Thomas, 8 November 2026.

<a id="req-025"></a>**REQ-025 — A broken fix fails safe (SHALL).** IF a fix no longer matches the website, THEN Skip the Fluff SHALL show the website unchanged for that fix, SHALL NOT crash, and SHALL NOT block the lesson.

## 9. Licence, naming and distribution

<a id="req-026"></a>**REQ-026 — Licence (SHALL).** Skip the Fluff SHALL be released under the GNU General Public License v3.0 only. Rationale: changes others make stay open; the existing unofficial Duolingo desktop wrapper, "DL: language lessons" by hmlendea on Flathub ([hmlendea/dl-desktop](https://github.com/hmlendea/dl-desktop)), uses the same licence and appears to be tolerated by Duolingo. Decided by Thomas, 8 November 2026.

<a id="req-027"></a>**REQ-027 — Name and branding (SHALL).** The product SHALL be named Skip the Fluff. Its name, icon and assets SHALL NOT use Duolingo's name, logo, mascot (including the name "Duo"), owl imagery or brand colours. Duolingo SHALL be mentioned only to describe what the app works with ("an unofficial, distraction-free wrapper for duolingo.com"). The repository is [thomaswening/skip-the-fluff](https://github.com/thomaswening/skip-the-fluff), public from its creation. Rationale: trademark is where a complaint from Duolingo is most likely. Name chosen by Thomas, 8 November 2026; name clearance and visibility in [OPEN-E](#open-e).

<a id="req-028"></a>**REQ-028 — Distribution (SHALL).** The first release SHALL be distributed as a signed APK on the repository's GitHub Releases. Distribution through F-Droid COULD follow ([OPEN-G](#open-g)). Distribution through the Google Play Store is OUT OF SCOPE: Play does not allow apps whose main purpose is to show a website without its owner's permission. Decided by Thomas, 8 November 2026.

<a id="req-030"></a>**REQ-030 — README (SHALL).** The repository SHALL have a README that states what Skip the Fluff does and does not do ([REQ-003](#req-003)), how to install it, the disclaimer of [REQ-019](#req-019), and how to report a fix that has stopped working.

<a id="open-decisions"></a>
## 10. Deferred capabilities and unresolved decisions

An open decision is resolved immediately before the work that depends on it, not in advance: several are better settled with evidence from a spike than by argument, and one settled early on a guess is worse than one settled late on a measurement. The roadmap names, per phase, which decisions that phase needs, and a phase is not complete until they are recorded here. Each decision below therefore carries a **Needed by** note saying what it blocks. A decision stays in this section once settled, marked **Resolved**, so that the reasoning survives the decision.

<a id="open-a"></a>**OPEN-A — Which further distractions to remove.** [REQ-011](#req-011). Thomas will send screenshots of screens to remove as they come up in lessons. Each becomes a requirement in [section 2](#2-distraction-removal). **Needed by:** nothing yet; collected continuously.

<a id="open-b"></a>**OPEN-B — How to support Google sign-in.** [REQ-017](#req-017), [REQ-021](#req-021). Google rejects sign-in from embedded browsers. Candidates: sign in through a Custom Tab and hand the session over to the app (works, but the hand-over is fragile and must not violate [REQ-021](#req-021)); ask Google users to add a password to their Duolingo account (no code, but a manual step for users). **Needed by:** the first release after 1.0.

<a id="open-c"></a>**OPEN-C — Full-screen details and further switches.** [REQ-013](#req-013), [REQ-014](#req-014), [REQ-018](#req-018). Whether Android's status and navigation bars are hidden during lessons or always, and whether [REQ-013](#req-013) and [REQ-014](#req-014) can be switched off. **Needed by:** the settings design. **Resolved:** the bars are hidden only while a lesson is shown, so that the clock and normal navigation stay available elsewhere. Neither full screen nor keeping the lesson gets a switch: they are why the app exists, and each switch adds a state to test. Accepted by Thomas as proposed, 8 November 2026.

<a id="open-d"></a>**OPEN-D — Minimum Android version.** [REQ-022](#req-022). Lower reaches more phones; higher allows simpler code and newer WebView behaviour. **Needed by:** the ADR on the app's structure and build.

<a id="open-e"></a>**OPEN-E — Name clearance.** [REQ-027](#req-027). A web check by Claude on 8 November 2026 found no app, F-Droid entry or GitHub project named Skip the Fluff, but a YouTube summary channel and a consulting agency use the phrase, and the EUIPO and DPMA registers were not searched. **Needed by:** making the repository public. **Resolved:** a search by Claude in [TMview](https://www.tmdn.org/tmview/) on 8 November 2026, which covers the EUIPO, DPMA, USPTO, UKIPO and most national offices, found no trade mark containing "skip the fluff", "skipthefluff" or "skip fluff" (a control search for "duolingo" returned 101 marks, so the search worked). The closest mark is the single word "Fluff", filed in France on 16 September 2026 for classes 9, 41 and 42 (INPI 5298706); it does not cover the phrase. This is not legal advice. On this evidence Thomas decided, 8 November 2026, to keep the name and create the repository public.

<a id="open-f"></a>**OPEN-F — How fast "without waiting" is.** [REQ-009](#req-009), [REQ-010](#req-010). A measurable threshold for the time from a non-exercise screen appearing to the learner being able to continue. **Needed by:** acceptance of [REQ-009](#req-009). **Resolved:** within 1 second, measured on the reference device as [REQ-009](#req-009) states. One second feels immediate yet leaves time to identify the screen with confidence before acting on it ([REQ-012](#req-012)). [REQ-010](#req-010) needs no threshold: its intro screens are not shown at all. Accepted by Thomas as proposed, 8 November 2026.

<a id="open-g"></a>**OPEN-G — Distribution through F-Droid.** [REQ-028](#req-028). F-Droid builds apps from source and reviews them, which reaches users who avoid unknown APKs, but it requires reproducible builds and an F-Droid metadata entry. **Needed by:** the first release after 1.0. Thomas decided, 8 November 2026, that F-Droid stays a COULD until then.

<a id="revision-history"></a>
## 11. Revision history

Each decision is recorded, with who took it and when, in the requirement or open decision it changed; this table only says which pass changed what.

| Version | Date | Pass | Changes |
|---|---|---|---|
| 0.1 | 8 Nov 2026 | Intake | First draft from the intake conversation, in the project-inception format. Replaces an informal draft written before the format was found. Records Thomas's decisions on [REQ-012](#req-012), [REQ-016](#req-016), [REQ-018](#req-018), [REQ-024](#req-024), [REQ-026](#req-026) to [REQ-029](#req-029); opens [OPEN-A](#open-a) to [OPEN-F](#open-f). |
| 0.2 | 8 Nov 2026 | 1 | Adds the term [disputed fix](#requirement-conventions) and ships such fixes switched off ([REQ-001](#req-001), [REQ-011](#req-011), [REQ-018](#req-018)). Resolves [OPEN-C](#open-c) (system bars hidden only in lessons, no switches for [REQ-013](#req-013) and [REQ-014](#req-014)), [OPEN-E](#open-e) (TMview search clear; repository public) and [OPEN-F](#open-f) (1 second in [REQ-009](#req-009)). Confirms [REQ-012](#req-012) and the 5-minute window of [REQ-014](#req-014). Opens [OPEN-G](#open-g) for F-Droid ([REQ-028](#req-028)). Names the prior art, "DL: language lessons", in [REQ-026](#req-026); there is no separate prior-art document. Commits follow Conventional Commits with release-please and SemVer from 0.x, as in CrateEdit; this is to be recorded in `docs/development.md` when it is created. |
