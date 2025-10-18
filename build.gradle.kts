group = "com.mustafadakhel.kompost"
version = libs.versions.kompost.get()

plugins {
    alias(libs.plugins.kotlin.compose.compiler) apply false
    alias(libs.plugins.jetbrains.compose) apply false
    alias(libs.plugins.dokka) apply false
    id("com.vanniktech.maven.publish") version "0.33.0" apply false
}
