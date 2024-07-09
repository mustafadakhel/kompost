plugins {
    com.android.library
    `maven-publish`
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(android.sourceSets.map { it.java.srcDirs })
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        kompostPublication(
            name = "snapshot",
            project = project,
            from = { components["release"] },
            artifacts = listOf(sourcesJar.get(), javadocJar.get())
        ) {
            repositories {
                ossrh(
                    project = project,
                    name = "snapshot",
                    url = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
                )
            }
        }
        kompostPublication(
            name = "release",
            project = project,
            from = { components["release"] },
            artifacts = listOf(sourcesJar.get(), javadocJar.get())
        ) {
            repositories {
                ossrh(
                    project = project,
                    name = "release",
                    url = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
                )
            }
        }
    }
}
