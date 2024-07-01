plugins {
    alias(libs.plugins.android.library)
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
}

group = "com.dakhel.kompost.lifecycle"
version = libs.versions.kompost.get()

android {
    namespace = "com.dakhel.kompost.lifecycle"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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

dependencies {
    api(project(":core"))
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


tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
}

tasks.register<Jar>("javadocJar") {
    dependsOn("dokkaJavadoc")
    archiveClassifier.set("javadoc")
    from(tasks.named("dokkaJavadoc").get().outputs)
}
