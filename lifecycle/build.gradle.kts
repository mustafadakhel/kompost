import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    com.android.library
    `kotlin-android`
    `kotlin-kapt`
    alias(libs.plugins.dokka)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

android {
    namespace = "com.mustafadakhel.kompost.lifecycle"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
}

kotlin.explicitApi = ExplicitApiMode.Strict

dependencies {
    implementation(project(":core"))
    implementation(project(":android"))
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.activity.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.junit.ktx)

    testImplementation(libs.mockk)

    testImplementation(libs.robolectric)

    testImplementation(libs.junit.jupiter.api)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.test.junit)

    debugImplementation(libs.fragment.testing)
}

kapt {
    correctErrorTypes = true
}

apply(plugin = "kompost.publish.android")
apply(plugin = "kompost.signing")

tasks.dokkaJavadoc.configure {
    outputDirectory.set(file("${layout.buildDirectory.get()}/dokka/javadoc"))
}
