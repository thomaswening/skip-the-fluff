// Playwright Test runs the engine tests in headless Chromium ([ADR-0007](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0007-testing-fixes.md)).
// It lives in test/, not next to fix-engine.js, because every *.js beside the engine is copied into the APK.
"use strict";

const { defineConfig, devices } = require("@playwright/test");

module.exports = defineConfig({
  testDir: ".",
  testMatch: "*.spec.js",
  forbidOnly: true,
  reporter: [["list"]],
  projects: [{ name: "chromium", use: { ...devices["Desktop Chrome"] } }],
});
