// Loads a test page with the fix engine injected as the app injects it, and records the engine's messages.
// What the engine tests check: https://github.com/thomaswening/skip-the-fluff/blob/main/docs/development.md#engine-tests
"use strict";

const fs = require("node:fs");
const path = require("node:path");
const { pathToFileURL } = require("node:url");

const ENGINE_PATH = path.join(__dirname, "..", "fix-engine.js");
const PAGES_DIRECTORY = path.join(__dirname, "pages");

/** The name the engine looks for its channel under; the same as APP_CHANNEL_NAME in fix-engine.js. */
const APP_CHANNEL_NAME = "skipTheFluffApp";

/** Where the stand-in channel keeps the messages it received, as parsed objects. */
const RECORDED_MESSAGES = "recordedMessagesForTest";

/** The made-up exercise marker of the engine test pages. */
const MADE_UP_EXERCISE_MARKERS = [{ selector: "[data-test^='challenge']" }];

/** The attribute the engine tags hidden elements with; the same as HIDDEN_ATTRIBUTE in fix-engine.js. */
const HIDDEN_ATTRIBUTE = "data-skip-the-fluff-hidden";

/**
 * A switched-on fix that hides elements, with the fields the engine does not use filled in.
 * @param {string} name
 * @param {{ selector: string, text?: string }[]} markers
 */
function hideFix(name, markers) {
  return { name, requirement: "REQ-005", disputed: false, markers, action: "hide", possiblyBrokenAfterLessons: 5 };
}

/**
 * The engine wrapped as the app wraps it: in a function, so nothing becomes a global, called with the configuration.
 * @param {object} configuration
 */
function engineScript(configuration) {
  const engineSource = fs.readFileSync(ENGINE_PATH, "utf8");
  return `(function () {\n${engineSource}\nstartFixEngine(${JSON.stringify(configuration)});\n})();`;
}

/**
 * A stand-in for the app's end of the channel. It runs before the engine, as the WebView's listener does.
 * Runs in the page, so it uses only names passed in.
 */
function installRecordingChannel([channelName, recordName]) {
  const recorded = [];
  globalThis[recordName] = recorded;
  globalThis[channelName] = { postMessage: (message) => recorded.push(JSON.parse(message)) };
}

/**
 * Opens a test page from test/pages with the engine injected before the page's content.
 * @param {import("@playwright/test").Page} page
 * @param {string} pageName file name in test/pages, e.g. `engine-prompt.html`
 * @param {object[]} fixes the switched-on fixes
 */
async function openWithEngine(page, pageName, fixes) {
  const configuration = { exerciseMarkers: MADE_UP_EXERCISE_MARKERS, fixes };
  await page.addInitScript(installRecordingChannel, [APP_CHANNEL_NAME, RECORDED_MESSAGES]);
  await page.addInitScript(engineScript(configuration));
  await page.goto(pathToFileURL(path.join(PAGES_DIRECTORY, pageName)).href);
}

/**
 * The messages the engine sent so far, oldest first.
 * @param {import("@playwright/test").Page} page
 */
function sentMessages(page) {
  return page.evaluate((recordName) => globalThis[recordName], RECORDED_MESSAGES);
}

/**
 * Waits until every change made so far has reached the engine and the screen has been drawn once,
 * so that a test can check that nothing happened.
 * @param {import("@playwright/test").Page} page
 */
function settle(page) {
  return page.evaluate(() => new Promise((resolve) => requestAnimationFrame(() => setTimeout(resolve, 0))));
}

/**
 * Adds an element at the end of the page.
 * @param {import("@playwright/test").Page} page
 * @param {string} dataTest the element's data-test attribute
 * @param {string} text
 */
function addElement(page, dataTest, text) {
  return page.evaluate(
    ([attributeValue, textContent]) => {
      const element = document.createElement("div");
      element.setAttribute("data-test", attributeValue);
      element.textContent = textContent;
      document.body.append(element);
    },
    [dataTest, text],
  );
}

/**
 * Removes every element with the data-test attribute.
 * @param {import("@playwright/test").Page} page
 * @param {string} dataTest
 */
function removeElements(page, dataTest) {
  return page.evaluate((attributeValue) => {
    document.querySelectorAll(`[data-test='${attributeValue}']`).forEach((element) => element.remove());
  }, dataTest);
}

/**
 * The elements with the data-test attribute and text, as a Playwright locator.
 * @param {import("@playwright/test").Page} page
 * @param {string} dataTest
 * @param {string} text
 */
function elementWith(page, dataTest, text) {
  return page.locator(`[data-test='${dataTest}']`, { hasText: text });
}

/**
 * How many elements carry the engine's hiding attribute.
 * @param {import("@playwright/test").Page} page
 */
function hiddenByEngineCount(page) {
  return page.locator(`[${HIDDEN_ATTRIBUTE}]`).count();
}

module.exports = {
  hideFix,
  openWithEngine,
  sentMessages,
  settle,
  addElement,
  removeElements,
  elementWith,
  hiddenByEngineCount,
};
