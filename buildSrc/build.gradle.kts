repositories {
    google()
    mavenCentral()
}

plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.plugins.gradle.tools.get().toString())
    api(libs.plugins.kotlin.gradle.plugin.get().toString())
}
