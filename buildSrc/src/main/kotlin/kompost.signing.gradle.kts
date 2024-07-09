plugins {
    signing
}
if (!project.version.toString().endsWith("SNAPSHOT"))
    signing {
        val keyId: String = properties["signing.keyId"].toString()
        val password: String = properties["signing.password"].toString()
        val secretKeyRingFilePath: String = properties["signing.secretKeyRingFile"].toString()
        setRequired(
            !project.version.toString().endsWith("SNAPSHOT")
        )
        useInMemoryPgpKeys(
            keyId,
            file(secretKeyRingFilePath).readText(),
            password
        )
        sign(extensions.getByType<PublishingExtension>().publications["snapshot"])
    }
