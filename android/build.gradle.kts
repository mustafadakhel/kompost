import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    com.android.library
    `kotlin-android`
    alias(libs.plugins.dokka)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

android {
    namespace = "com.mustafadakhel.kompost.android"
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
    api(project(":core"))
    implementation(libs.core.ktx)
    implementation(libs.appcompat.v161)
    implementation(libs.material)

    testImplementation(libs.mockk)
    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.test.junit)

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

apply(plugin = "kompost.publish.android")
apply(plugin = "kompost.signing")

tasks.dokkaJavadoc.configure {
    outputDirectory.set(file("${layout.buildDirectory.get()}/dokka/javadoc"))
}
