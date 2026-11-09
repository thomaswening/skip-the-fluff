// The fix engine ([ADR-0002](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0002-fix-engine.md)): injected into every page of the website before its own scripts.
// Phase 0 ships only this stub, so that the type check and the copy into the APK work from the start.
// The type check reads the JSDoc comments: https://github.com/thomaswening/skip-the-fluff/blob/main/docs/development.md#engine-type-check.
// @ts-check
"use strict";

/**
 * One marker of a fix: an element selector, optionally with the text the element must contain.
 * @typedef {object} Marker
 * @property {string} selector
 * @property {string} [text]
 */

/**
 * One fix file in fixes/ ([ADR-0002](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0002-fix-engine.md), point 3).
 * @typedef {object} Fix
 * @property {string} requirement the ID of the requirement the fix implements
 * @property {boolean} disputed
 * @property {Marker[]} markers all of them must match before the fix acts
 * @property {"hide" | "press"} action
 * @property {number} possiblyBrokenAfterLessons
 */

(function startFixEngine() {
  /** @type {Fix[]} */
  const switchedOnFixes = [];
  // Phase 2 adds the exercise guard, the fixes' actions and the four messages to the app.
  void switchedOnFixes;
})();
