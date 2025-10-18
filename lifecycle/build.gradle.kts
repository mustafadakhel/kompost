import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    com.android.library
    `kotlin-android`
    `kotlin-kapt`
    id("com.vanniktech.maven.publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

android {
    namespace = "com.mustafadakhel.kompost.lifecycle"
    compileSdk = 36

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

    testImplementation(libs.fragment.testing)
}

kapt {
    correctErrorTypes = true
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    configure(
        AndroidSingleVariantLibrary(
            variant = "release",
            sourcesJar = true,
            publishJavadocJar = false
        )
    )

    coordinates(rootProject.group as String, "kompost-${project.name}", rootProject.version as String)

    pom {
        name.set("Kompost ${project.name}")
        description.set("Gardening-Inspired Scoping and DI")
        url.set("https://github.com/mustafadakhel/kompost")

        licenses {
            license {
                name.set("The Apache Software License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/mustafadakhel/kompost.git")
            developerConnection.set("scm:git:ssh://github.com/mustafadakhel/kompost.git")
            url.set("https://github.com/mustafadakhel/kompost")
        }

        developers {
            developer {
                id.set("mustafadakhel")
                name.set("Mustafa M. Dakhel")
                email.set("mstfdakhel@gmail.com")
            }
        }
    }
}
