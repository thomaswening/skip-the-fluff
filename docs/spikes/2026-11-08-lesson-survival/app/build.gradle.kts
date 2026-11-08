plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "io.github.thomaswening.skipthefluff.spike"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.thomaswening.skipthefluff.spike"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "spike-2026-11-08-round-2"
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
