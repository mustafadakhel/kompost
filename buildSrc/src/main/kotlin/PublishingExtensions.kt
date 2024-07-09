import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.register

fun PublicationContainer.kompostPublication(
    name: String,
    project: Project,
    from: () -> SoftwareComponent,
    artifacts: List<Jar>,
    versionString: String,
    configure: MavenPublication.() -> Unit
) = register<MavenPublication>(name) {
    project.afterEvaluate {
        from(from())
    }

    setArtifacts(artifacts)

    groupId = project.rootProject.group.toString()
    artifactId = "${project.rootProject.name.lowercase()}-${project.name}"
    version = versionString

    kompostPOM(project)
    configure()
}

fun MavenPublication.kompostPOM(project: Project) = pom {
    name.set("${project.rootProject.group}:${project.name}")
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

fun RepositoryHandler.ossrh(
    project: Project,
    name: String,
    url: String
): MavenArtifactRepository = maven {
    this.name = "ossrh-$name"
    this.url = project.uri(url)

    val ossrhUsername: String by project.properties
    val ossrhPassword: String by project.properties

    credentials {
        username = ossrhUsername
        password = ossrhPassword
    }
}
