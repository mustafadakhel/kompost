plugins {
    kotlin
    `maven-publish`
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.map { it.allSource })
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    dependsOn(tasks.named("dokkaJavadoc"))
    from(tasks.named("dokkaJavadoc").get().outputs)
}

tasks.withType<Jar> {
    archiveBaseName.set("${project.rootProject.name.lowercase()}-${project.name}")
    archiveVersion.set(rootProject.version.toString())
}

publishing {
    publications {
        kompostPublication(
            name = "release",
            project = project,
            from = { components["java"] },
            versionString = rootProject.version.toString(),
            artifacts = listOf(sourcesJar, javadocJar.get())
        ) {
            repositories {
                ossrh(
                    project = project,
                    name = "release",
                    url = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                )
                ossrh(
                    project = project,
                    name = "snapshot",
                    url = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                )
            }
        }
    }
}

tasks.withType<AbstractPublishToMaven> {
    doFirst {
        publication.setArtifacts(publication.artifacts.distinctBy { a -> a.extension + a.classifier })
    }
}
tasks.named("assemble") {
    dependsOn(sourcesJar)
    dependsOn(javadocJar)
}
