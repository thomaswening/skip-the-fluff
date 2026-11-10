# Skip the Fluff: Manual verification
Living record | 11 November 2026

This document records the checks that no automated test can make. Each one is done by hand on the [reference device](glossary.md#reference-device): sign-in, a password manager, behaviour without a network. The [roadmap](roadmap.md#principles) requires these checks before each release. The unit tests and CI cover everything else ([development.md](development.md)).

## How a check is recorded

Each check gets a row in the record below when it is first done. A later run on another build adds a row of its own, so the record shows when a requirement was last seen working. A failed check also gets a row, with the issue or pull request that resolves it.

- **Build:** the commit of `main` the installed debug build was made from, or the pull request's commit if it was not merged yet.
- **Steps:** the steps and expected results, in short. The pull request that built the requirement has them in full.
- **Result:** pass or fail, with what was seen where it differed from the expected result.

## Record

| Requirement | Steps | Build | Date | By | Result |
|---|---|---|---|---|---|
| [REQ-044](requirements.md#req-044), no connection | 1. Airplane mode on, start the app: the app's own screen "duolingo.com cannot be reached" shows. 2. Tap "Reload page", still offline: the spinner shows for about a second, then the button can be tapped again. 3. Airplane mode off, wait half a minute: nothing reloads by itself. 4. Tap "Reload page": [the website](glossary.md#the-website) opens. Steps in [PR 24](https://github.com/thomaswening/skip-the-fluff/pull/24). | `3b38d19` ([PR 24](https://github.com/thomaswening/skip-the-fluff/pull/24)) | 10 November 2026 | Thomas | Pass |
| [REQ-039](requirements.md#req-039), password managers | Signed out, with the Duolingo password saved in Google Password Manager and Google as Android's [autofill](glossary.md#autofill) service. 1. Tap "I already have an account", then the field "Email or username": Android offers the saved account. 2. Choose it: email and password are filled in. 3. Tap "Log in": signed in, the learning path shows. | `3b38d19` (code as on `main` at `8994315`) | 11 November 2026 | Thomas | Pass |
| [REQ-016](requirements.md#req-016), email and password sign-in | Signed in with email and password, as in the row above. 1. Close the app from recent apps and open it again: still signed in. 2. Restart the phone and open the app: still signed in. | `3b38d19` (code as on `main` at `8994315`) | 11 November 2026 | Thomas | Pass |
