import java.io.File
import java.util.Base64
import java.util.Properties

val propertiesFilePath = "/Volumes/Keybase/team/velocitycareers/mobile/android/maven2/maven2.properties"

fun loadProperties(filePath: String): Properties {
    val props = Properties()
    val file = File(filePath)
    if (file.exists()) {
        file.inputStream().use { props.load(it) }
    }
    return props
}

val properties = loadProperties(propertiesFilePath)

val githubSecretNames =
    listOf(
        "MAVEN_CENTRAL_TOKEN_USERNAME",
        "MAVEN_CENTRAL_TOKEN_PASSWORD",
        "MAVEN_CENTRAL_SIGNING_KEY_ID",
        "MAVEN_CENTRAL_SIGNING_PASSWORD",
        "MAVEN_CENTRAL_GPG_PUBLIC_KEY_B64",
        "MAVEN_CENTRAL_GPG_PRIVATE_KEY_B64",
    )

fun loadSecret(name: String): String? = System.getenv(name)?.trim() ?: properties.getProperty(name)?.trim()

val mavenCentralTokenUsername = loadSecret(githubSecretNames[0])
val mavenCentralTokenPassword = loadSecret(githubSecretNames[1])
val mavenCentralSigningKeyId = loadSecret(githubSecretNames[2])
val mavenCentralSigningPassword = loadSecret(githubSecretNames[3])
val mavenCentralSigningGpgPublicKeyB64 = loadSecret(githubSecretNames[4])
val mavenCentralSigningGpgPrivateKeyB64 = loadSecret(githubSecretNames[5])

val mavenCentralSigningGpgPublicKey =
    mavenCentralSigningGpgPublicKeyB64
        ?.let { String(Base64.getDecoder().decode(it)) }

val mavenCentralSigningGpgPrivateKey =
    mavenCentralSigningGpgPrivateKeyB64
        ?.let { String(Base64.getDecoder().decode(it)) }

// Optionally expose as extra properties if needed by Groovy scripts
extra.apply {
    set("mavenCentralTokenUsername", mavenCentralTokenUsername)
    set("mavenCentralTokenPassword", mavenCentralTokenPassword)
    set("mavenCentralSigningKeyId", mavenCentralSigningKeyId)
    set("mavenCentralSigningPassword", mavenCentralSigningPassword)
    set("mavenCentralSigningGpgPublicKeyB64", mavenCentralSigningGpgPublicKeyB64)
    set("mavenCentralSigningGpgPrivateKeyB64", mavenCentralSigningGpgPrivateKeyB64)
    set("mavenCentralSigningGpgPublicKey", mavenCentralSigningGpgPublicKey)
    set("mavenCentralSigningGpgPrivateKey", mavenCentralSigningGpgPrivateKey)
}
