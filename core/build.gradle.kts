import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
            allWarningsAsErrors = true
            freeCompilerArgs = listOf("-Xexplicit-api=strict")
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
