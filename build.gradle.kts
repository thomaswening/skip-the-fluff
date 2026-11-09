// The plugins every module may use, declared once with their versions from gradle/libs.versions.toml.
// The one module, app/, applies them. See https://github.com/thomaswening/skip-the-fluff/blob/main/docs/development.md#building-and-testing.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
