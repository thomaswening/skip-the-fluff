# Changelog

## 0.1.0 (2026-11-12)


### Features

* **permissions:** pass the microphone to the website and play audio without a tap ([#20](https://github.com/thomaswening/skip-the-fluff/issues/20)) ([1d926a5](https://github.com/thomaswening/skip-the-fluff/commit/1d926a523682e8a95c3a51586d3923c5d2ed27cb))
* **permissions:** say where to switch the microphone on after two refusals ([#21](https://github.com/thomaswening/skip-the-fluff/issues/21)) ([de4360b](https://github.com/thomaswening/skip-the-fluff/commit/de4360b85f40b4c6bedbd21d9be7ce317064cf11))
* **shell:** recover when Android ends the renderer process or the app ([#23](https://github.com/thomaswening/skip-the-fluff/issues/23)) ([730fd00](https://github.com/thomaswening/skip-the-fluff/commit/730fd008cb6bf8993dc228c62f6a0641c718e781))
* **shell:** show the app's own screen when duolingo.com cannot be reached ([#24](https://github.com/thomaswening/skip-the-fluff/issues/24)) ([8994315](https://github.com/thomaswening/skip-the-fluff/commit/8994315df80f0064edaaf0cc155127d0e0f3fc5a))
* **shell:** show the website in a WebView that survives configuration changes ([#17](https://github.com/thomaswening/skip-the-fluff/issues/17)) ([f326fa8](https://github.com/thomaswening/skip-the-fluff/commit/f326fa84f9a6458f6f9a65d961a70512e1091c89))


### Bug Fixes

* **docs:** exempt Android Authority from the weekly link check ([#16](https://github.com/thomaswening/skip-the-fluff/issues/16)) ([707274e](https://github.com/thomaswening/skip-the-fluff/commit/707274e2cd8f5622abb171e251d8f8f3d4417fdb))


### Documentation

* **adr:** accept ADR-0007 ([#28](https://github.com/thomaswening/skip-the-fluff/issues/28)) ([0d76a9a](https://github.com/thomaswening/skip-the-fluff/commit/0d76a9a26cdb282c127c6a1b88bcf8b129329689))
* **adr:** propose ADR-0001, the website in Android's own WebView ([#5](https://github.com/thomaswening/skip-the-fluff/issues/5)) ([0436a04](https://github.com/thomaswening/skip-the-fluff/commit/0436a045196325c9cefac9a4609b5d7c249fa005))
* **adr:** propose ADR-0002, fixes as data applied by one engine ([#6](https://github.com/thomaswening/skip-the-fluff/issues/6)) ([976c5a3](https://github.com/thomaswening/skip-the-fluff/commit/976c5a3f7240849816ce4ceb300e962797bc7432))
* **adr:** propose ADR-0003, keeping the lesson across an app switch ([#7](https://github.com/thomaswening/skip-the-fluff/issues/7)) ([96d4a09](https://github.com/thomaswening/skip-the-fluff/commit/96d4a09c7901b895f08598452f0b8cb73eef9cdb))
* **adr:** propose ADR-0004, minimum Android 10 (resolves OPEN-D) ([#8](https://github.com/thomaswening/skip-the-fluff/issues/8)) ([d31b165](https://github.com/thomaswening/skip-the-fluff/commit/d31b1650098ec472c22914a262f4525736fe8c24))
* **adr:** propose ADR-0005, keeping the signing key safe ([#9](https://github.com/thomaswening/skip-the-fluff/issues/9)) ([e2d6ec7](https://github.com/thomaswening/skip-the-fluff/commit/e2d6ec794fa776dd0115323e8ea42e7b6709b127))
* **adr:** propose ADR-0006, the app's structure and build ([#13](https://github.com/thomaswening/skip-the-fluff/issues/13)) ([2173937](https://github.com/thomaswening/skip-the-fluff/commit/2173937226b1663e1653cf5b6a12d497aa9b63e3))
* **adr:** propose ADR-0007 on testing the engine and the fixes ([#27](https://github.com/thomaswening/skip-the-fluff/issues/27)) ([c9bdc9d](https://github.com/thomaswening/skip-the-fluff/commit/c9bdc9d033a999329eb048321f81aafd0c9392d6))
* **architecture:** add the architecture overview ([#10](https://github.com/thomaswening/skip-the-fluff/issues/10)) ([5d5498a](https://github.com/thomaswening/skip-the-fluff/commit/5d5498a40e08b7222a9908b7553ea45f724ddf04))
* **development:** add the emulator recipe that checks the microphone ([#22](https://github.com/thomaswening/skip-the-fluff/issues/22)) ([2c909e5](https://github.com/thomaswening/skip-the-fluff/commit/2c909e520ff1d38513e35ff93fbcb9004147b48c))
* **requirements:** address the first review of v0.2 ([2f6a3cc](https://github.com/thomaswening/skip-the-fluff/commit/2f6a3ccc53e7c9a77bfbd0474f9286792ea24aa8))
* **requirements:** v0.2, first elicitation pass ([19549ec](https://github.com/thomaswening/skip-the-fluff/commit/19549ece9960042d91fdd1607e2c454cc74d4efe))
* **requirements:** v0.2, first elicitation pass ([738e19d](https://github.com/thomaswening/skip-the-fluff/commit/738e19d785838fc067c23a0020342a43d9f8f6ff))
* **requirements:** v0.3, second elicitation pass ([#4](https://github.com/thomaswening/skip-the-fluff/issues/4)) ([ec21cfd](https://github.com/thomaswening/skip-the-fluff/commit/ec21cfd3845d0f5e5ec0e83bd47dc3f208ffa51a))
* **requirements:** warn about personal data in report screenshots ([7566a6c](https://github.com/thomaswening/skip-the-fluff/commit/7566a6c0e2a9ad009e876778128fc5eac7c3a215))
* **roadmap:** add the roadmap and place each open decision in a phase ([#11](https://github.com/thomaswening/skip-the-fluff/issues/11)) ([ddb7ff8](https://github.com/thomaswening/skip-the-fluff/commit/ddb7ff813ee6e20f251ec3bcfe86579eeb4c69cd))
* **roadmap:** build only hide in phase 2 and phase 3 fixes from seen screens ([#29](https://github.com/thomaswening/skip-the-fluff/issues/29)) ([9d08de4](https://github.com/thomaswening/skip-the-fluff/commit/9d08de4e1a005bd8b8e94dd5d51edcd49f934125))
* **verification:** record REQ-014's full measurement and the landscape check ([#26](https://github.com/thomaswening/skip-the-fluff/issues/26)) ([98098fa](https://github.com/thomaswening/skip-the-fluff/commit/98098fa9e19ddfb48456e39ead59bdfff666d8a9))
* **verification:** record the phone checks of REQ-016, REQ-039 and REQ-044 ([#25](https://github.com/thomaswening/skip-the-fluff/issues/25)) ([47f3287](https://github.com/thomaswening/skip-the-fluff/commit/47f3287c258fae2220f8737b8eed3b3803d30639))
