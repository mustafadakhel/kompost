import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin
    alias(libs.plugins.dokka)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
            explicitApiMode = ExplicitApiMode.Strict
            allWarningsAsErrors = true
        }
    }

    compileTestKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
}

dependencies {
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.test.junit)
}

apply(plugin = "kompost.publish.kotlin")
apply(plugin = "kompost.signing")

tasks.dokkaJavadoc.configure {
    outputDirectory.set(file("${layout.buildDirectory.get()}/dokka/javadoc"))
}
