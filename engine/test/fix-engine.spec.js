// Tests of the fix engine and its exercise guard, on hand-written test pages
// ([ADR-0007](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0007-testing-fixes.md), point 1).
"use strict";

const { test, expect } = require("@playwright/test");
const {
  hideFix,
  openWithEngine,
  sentMessages,
  settle,
  addElement,
  removeElements,
  elementWith,
  hiddenByEngineCount,
} = require("./engine-harness");

const PROMPT = "made-up-prompt";
const PROMPT_TEXT = "Made-up prompt";
const EXERCISE = "challenge-made-up";
const promptFix = hideFix("made-up-prompt", [{ selector: `[data-test='${PROMPT}']`, text: PROMPT_TEXT }]);

test("REQ-012: a fix whose markers all match hides its element", async ({ page }) => {
  // Act
  await openWithEngine(page, "engine-prompt.html", [promptFix]);

  // Assert
  await expect(elementWith(page, PROMPT, PROMPT_TEXT)).toBeHidden();
});

test("REQ-012: an element that matches the selector but not the text stays visible", async ({ page }) => {
  // Act
  await openWithEngine(page, "engine-prompt.html", [promptFix]);
  await settle(page);

  // Assert
  await expect(elementWith(page, PROMPT, "A decoy with other text")).toBeVisible();
});

test("REQ-042: a fix that hides an element tells the app it took effect", async ({ page }) => {
  // Act
  await openWithEngine(page, "engine-prompt.html", [promptFix]);
  await settle(page);

  // Assert
  expect(await sentMessages(page)).toEqual([{ type: "fixTookEffect", fix: "made-up-prompt" }]);
});

test("REQ-025: a fix with a marker that matches nothing does nothing", async ({ page }) => {
  // Arrange
  const fixWithMissingMarker = hideFix("missing-marker", [
    { selector: `[data-test='${PROMPT}']`, text: PROMPT_TEXT },
    { selector: "[data-test='not-on-the-page']" },
  ]);

  // Act
  await openWithEngine(page, "engine-prompt.html", [fixWithMissingMarker]);
  await settle(page);

  // Assert
  expect(await hiddenByEngineCount(page)).toBe(0);
});

test("REQ-012: no fix acts while an exercise is visible", async ({ page }) => {
  // Act
  await openWithEngine(page, "engine-prompt-with-exercise.html", [promptFix]);
  await settle(page);

  // Assert
  await expect(elementWith(page, PROMPT, PROMPT_TEXT)).toBeVisible();
});

const hiddenExercisePages = [
  { hiddenBy: "display: none on an ancestor", pageName: "engine-exercise-in-hidden-ancestor.html" },
  { hiddenBy: "visibility: hidden", pageName: "engine-exercise-visibility-hidden.html" },
  { hiddenBy: "a size of zero", pageName: "engine-exercise-zero-size.html" },
];
for (const { hiddenBy, pageName } of hiddenExercisePages) {
  test(`REQ-012: an exercise hidden by ${hiddenBy} counts as hidden, so the fix acts`, async ({ page }) => {
    // Act
    await openWithEngine(page, pageName, [promptFix]);

    // Assert
    await expect(elementWith(page, PROMPT, PROMPT_TEXT)).toBeHidden();
  });
}

test("REQ-012: once an exercise appears, no fix acts", async ({ page }) => {
  // Arrange
  await openWithEngine(page, "engine-prompt.html", [promptFix]);
  await expect(elementWith(page, PROMPT, PROMPT_TEXT)).toBeHidden();
  await addElement(page, EXERCISE, "Made-up exercise");

  // Act
  await addElement(page, PROMPT, "Made-up prompt, shown again");
  await settle(page);

  // Assert
  await expect(elementWith(page, PROMPT, "shown again")).toBeVisible();
});

test("REQ-012: when the exercise is removed, the fix acts again", async ({ page }) => {
  // Arrange
  await openWithEngine(page, "engine-prompt.html", [promptFix]);
  await addElement(page, EXERCISE, "Made-up exercise");
  await addElement(page, PROMPT, "Made-up prompt, shown again");

  // Act
  await removeElements(page, EXERCISE);

  // Assert
  await expect(elementWith(page, PROMPT, "shown again")).toBeHidden();
});

test("REQ-025: a fix that throws leaves its elements shown", async ({ page }) => {
  // Arrange
  const throwingFix = hideFix("throwing", [{ selector: `[data-test='${PROMPT}']` }, { selector: "[data-test=" }]);

  // Act
  await openWithEngine(page, "engine-two-prompts.html", [throwingFix]);
  await settle(page);

  // Assert
  await expect(elementWith(page, PROMPT, PROMPT_TEXT)).toBeVisible();
});

test("REQ-025: a fix that throws does not stop the other fixes", async ({ page }) => {
  // Arrange
  const throwingFix = hideFix("throwing", [{ selector: "[data-test=" }]);
  const otherFix = hideFix("other", [{ selector: "[data-test='other-made-up-prompt']" }]);

  // Act
  await openWithEngine(page, "engine-two-prompts.html", [throwingFix, otherFix]);

  // Assert
  await expect(elementWith(page, "other-made-up-prompt", "Other made-up prompt")).toBeHidden();
});

test("REQ-042: a fix that throws is reported once, with the kind of error and no page content", async ({ page }) => {
  // Arrange
  const throwingFix = hideFix("throwing", [{ selector: "[data-test=" }]);

  // Act
  await openWithEngine(page, "engine-two-prompts.html", [throwingFix]);
  await addElement(page, PROMPT, "A change after the failure");
  await settle(page);

  // Assert
  expect(await sentMessages(page)).toEqual([{ type: "fixFailed", fix: "throwing", errorKind: "SyntaxError" }]);
});
