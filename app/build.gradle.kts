// The one Gradle module: the whole app ([ADR-0006](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0006-structure-and-build.md), point 3).
// What each part does, with its .NET counterpart: https://github.com/thomaswening/skip-the-fluff/blob/main/docs/development.md#building-and-testing.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "io.github.thomaswening.skipthefluff"
    compileSdk = 36

    defaultConfig {
        // The app's identity together with the signing key. It never changes ([ADR-0006](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0006-structure-and-build.md), point 7).
        applicationId = "io.github.thomaswening.skipthefluff"
        minSdk = 29 // Android 10 ([ADR-0004](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0004-minimum-android-10.md))
        targetSdk = 36
        // Written by release-please only ([ADR-0006](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0006-structure-and-build.md), point 6). Do not edit this line by hand.
        versionName = "0.1.0" // x-release-please-version
        versionCode = versionCodeOf(versionName!!)
    }

    buildTypes {
        debug {
            // A debug build is signed with this computer's debug key, not the release key. Its own ID
            // lets it sit next to an installed release instead of blocking the release's update.
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)

    testImplementation(libs.junit)
    testImplementation(libs.konsist)
}

// The fix engine and the fix files go into the APK's assets as they are, as engine/ and fixes/
// ([ADR-0006](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0006-structure-and-build.md), points 2 and 3). Only the shipped files are copied, not the type check's configuration.
val collectWebAssets = tasks.register<CollectWebAssets>("collectWebAssets") {
    engineFiles.from(rootProject.fileTree("engine") { include("*.js") })
    fixFiles.from(rootProject.fileTree("fixes") { include("*.json") })
}
androidComponents {
    onVariants { variant ->
        variant.sources.assets?.addGeneratedSourceDirectory(collectWebAssets, CollectWebAssets::outputDir)
    }
}

/** Copies the engine's script and the fix files into one folder that becomes part of the assets. */
abstract class CollectWebAssets : DefaultTask() {
    @get:InputFiles
    abstract val engineFiles: ConfigurableFileCollection

    @get:InputFiles
    abstract val fixFiles: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:Inject
    abstract val fileSystem: FileSystemOperations

    @TaskAction
    fun collect() {
        fileSystem.sync {
            into(outputDir)
            from(engineFiles) { into("engine") }
            from(fixFiles) { into("fixes") }
        }
    }
}

/**
 * Android's versionCode for a SemVer versionName: major × 1,000,000 + minor × 1,000 + patch,
 * e.g. 0.2.1 → 2,001 ([ADR-0006](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/adr/0006-structure-and-build.md), point 6). It rises with every release, so every release installs
 * over the previous one ([REQ-043](https://github.com/thomaswening/skip-the-fluff/blob/main/docs/requirements.md#req-043)).
 */
fun versionCodeOf(versionName: String): Int {
    val parts = versionName.split(".").map(String::toInt)
    require(parts.size == 3) { "versionName '$versionName' is not major.minor.patch" }
    val (major, minor, patch) = parts
    require(minor < 1_000 && patch < 1_000) { "minor and patch must stay below 1,000: $versionName" }
    return major * 1_000_000 + minor * 1_000 + patch
}
