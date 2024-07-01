plugins {
    alias(libs.plugins.android.library)
    kotlin("android")
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
}

group = "io.github.mustafadakhel"
version = libs.versions.kompost.get()

android {
    namespace = "com.dakhel.kompost.core"
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
}

dependencies {

    testImplementation(libs.mockk)

    testImplementation(libs.robolectric)

    testImplementation(libs.junit.jupiter.api)

    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlin.test.junit)
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
