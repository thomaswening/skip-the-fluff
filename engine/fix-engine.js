// The fix engine ([ADR-0002](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0002-fix-engine.md)): injected into every page of the website before its own scripts.
// Whoever injects it wraps this file in a function and calls startFixEngine with the configuration, so nothing
// here becomes a global of the page: `(function () {` + this file + `startFixEngine(` + JSON + `);})();`.
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
 * Only `hide` is built so far: `press` comes with the first fix that needs it
 * ([roadmap](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/roadmap.md#phase-2-fix-engine-and-first-fix)).
 * @typedef {object} Fix
 * @property {string} requirement the ID of the requirement the fix implements
 * @property {boolean} disputed
 * @property {Marker[]} markers all of them must match before the fix acts
 * @property {"hide"} action
 * @property {number} possiblyBrokenAfterLessons
 */

/**
 * A switched-on fix with the name of its file, without `.json`, e.g. `req-005-app-prompt`.
 * @typedef {Fix & { name: string }} NamedFix
 */

/**
 * What the app passes to the engine when it registers it.
 * @typedef {object} EngineConfiguration
 * @property {Marker[]} exerciseMarkers from fixes/exercise-markers.json; any visible match stops every fix
 * @property {NamedFix[]} fixes the switched-on fixes
 */

/**
 * The app's end of the message channel, which the WebView's message listener puts into the page
 * ([ADR-0002](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0002-fix-engine.md), point 6).
 * @typedef {object} AppChannel
 * @property {(message: string) => void} postMessage
 */

/** The name under which the app's message listener appears in the page. The app registers it under the same name. */
const APP_CHANNEL_NAME = "skipTheFluffApp";

/** The attribute that marks an element hidden by a fix. Its value is the fix's name. */
const HIDDEN_ATTRIBUTE = "data-skip-the-fluff-hidden";

/** The message types the engine sends. Lesson started and lesson ended come with the lesson state. */
const MessageType = Object.freeze({
  FIX_TOOK_EFFECT: "fixTookEffect",
  FIX_FAILED: "fixFailed",
});

/** The kind of error reported for a value thrown that is not an Error. */
const UNKNOWN_ERROR_KIND = "unknown";

/**
 * Starts watching the page and applies the switched-on fixes behind the exercise guard.
 * @param {EngineConfiguration} configuration
 */
function startFixEngine(configuration) {
  const appChannel = findAppChannel();
  const activeFixes = [...configuration.fixes];

  addHidingStyleSheet();
  // Tagging an element is a change too. It starts one more round, which finds nothing new to tag.
  const observer = new MutationObserver(() => applyFixes(configuration.exerciseMarkers, activeFixes, appChannel));
  observer.observe(document, { childList: true, subtree: true, attributes: true, characterData: true });
}

/**
 * Runs every active fix once, unless an exercise is visible. A fix that throws is removed from `activeFixes`.
 * @param {Marker[]} exerciseMarkers
 * @param {NamedFix[]} activeFixes changed: a failed fix is removed
 * @param {AppChannel | undefined} appChannel
 */
function applyFixes(exerciseMarkers, activeFixes, appChannel) {
  if (isExerciseVisibleOrUnknown(exerciseMarkers)) {
    return;
  }
  for (const fix of [...activeFixes]) {
    applyFixSafely(fix, activeFixes, appChannel);
  }
}

/**
 * The exercise guard ([REQ-012](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-012)).
 * IF the guard itself fails, THEN it answers as if an exercise were visible, so no fix acts.
 * @param {Marker[]} exerciseMarkers
 * @returns {boolean}
 */
function isExerciseVisibleOrUnknown(exerciseMarkers) {
  try {
    return exerciseMarkers.some((marker) => matchingElements(marker).some(isVisible));
  } catch {
    return true;
  }
}

/**
 * Applies one fix. IF it throws, THEN it is switched off for the rest of the page and the app is told the kind of
 * error, never page content. A fix throws only on an invalid selector, which throws before anything is hidden
 * ([REQ-025](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-025)).
 * @param {NamedFix} fix
 * @param {NamedFix[]} activeFixes changed: the fix is removed when it fails
 * @param {AppChannel | undefined} appChannel
 */
function applyFixSafely(fix, activeFixes, appChannel) {
  try {
    const newlyHiddenCount = applyHideFix(fix);
    if (newlyHiddenCount > 0) {
      sendToApp(appChannel, { type: MessageType.FIX_TOOK_EFFECT, fix: fix.name });
    }
  } catch (error) {
    activeFixes.splice(activeFixes.indexOf(fix), 1);
    sendToApp(appChannel, { type: MessageType.FIX_FAILED, fix: fix.name, errorKind: errorKindOf(error) });
  }
}

/**
 * Hides the elements of the fix's first marker when all its markers match.
 * @param {NamedFix} fix
 * @returns {number} how many elements were hidden that were not hidden before
 */
function applyHideFix(fix) {
  const matchesPerMarker = fix.markers.map(matchingElements);
  if (matchesPerMarker.some((matches) => matches.length === 0)) {
    return 0;
  }
  const elementsToHide = matchesPerMarker[0].filter((element) => !element.hasAttribute(HIDDEN_ATTRIBUTE));
  for (const element of elementsToHide) {
    element.setAttribute(HIDDEN_ATTRIBUTE, fix.name);
  }
  return elementsToHide.length;
}

/**
 * The elements a marker matches: its selector, and its text if it has one. Throws on an invalid selector.
 * @param {Marker} marker
 * @returns {Element[]}
 */
function matchingElements(marker) {
  const bySelector = [...document.querySelectorAll(marker.selector)];
  const text = marker.text;
  if (text === undefined) {
    return bySelector;
  }
  return bySelector.filter((element) => normalisedText(element).includes(text));
}

/**
 * The element's text with runs of white space collapsed, so a marker text need not repeat the page's line breaks.
 * @param {Element} element
 * @returns {string}
 */
function normalisedText(element) {
  return (element.textContent ?? "").replace(/\s+/g, " ").trim();
}

/**
 * Whether the element takes up space on screen and is not made invisible by CSS.
 * An ancestor with `display: none` leaves the element without a layout box, so its size is zero. `visibility` is
 * inherited, so the element's own computed value covers its ancestors. Opacity is ignored on purpose: an exercise faded out still
 * counts as visible, which errs on the side of [REQ-012](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-012).
 * @param {Element} element
 * @returns {boolean}
 */
function isVisible(element) {
  if (getComputedStyle(element).visibility !== "visible") {
    return false;
  }
  const box = element.getBoundingClientRect();
  return box.width > 0 && box.height > 0;
}

/**
 * Hides every tagged element. A constructed style sheet is not part of the page's elements, so the website's
 * own scripts cannot remove it by rewriting the page.
 */
function addHidingStyleSheet() {
  const styleSheet = new CSSStyleSheet();
  styleSheet.replaceSync(`[${HIDDEN_ATTRIBUTE}] { display: none !important; }`);
  document.adoptedStyleSheets = [...document.adoptedStyleSheets, styleSheet];
}

/**
 * The app's channel, read once at start before the website's own scripts run. Undefined without it.
 * @returns {AppChannel | undefined}
 */
function findAppChannel() {
  const channel = Reflect.get(globalThis, APP_CHANNEL_NAME);
  return typeof channel?.postMessage === "function" ? channel : undefined;
}

/**
 * Sends one message to the app, if there is a channel. Never throws.
 * @param {AppChannel | undefined} appChannel
 * @param {Record<string, string>} message
 */
function sendToApp(appChannel, message) {
  try {
    appChannel?.postMessage(JSON.stringify(message));
  } catch {
    // A message that cannot be sent must not stop the fixes.
  }
}

/**
 * The kind of a thrown value, e.g. `TypeError`. Never its message, which may quote page content.
 * @param {unknown} error
 * @returns {string}
 */
function errorKindOf(error) {
  return error instanceof Error ? error.name : UNKNOWN_ERROR_KIND;
}
