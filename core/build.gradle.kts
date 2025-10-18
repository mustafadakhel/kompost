import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin
    alias(libs.plugins.dokka)
    id("com.vanniktech.maven.publish")
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

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    configure(
        KotlinJvm(
            javadocJar = JavadocJar.Empty(),
            sourcesJar = true
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
