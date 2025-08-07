import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

// val isPublishTask = gradle.startParameter.taskNames.any { it.contains("publish", ignoreCase = true) }
// if (isPublishTask) {
//    afterEvaluate {
//        // ...publishing block
//    }
// } else {
//    logger.lifecycle("🟡 Skipping publishing config: not a publishing task.")
// }

val publishArtifactId: String by project
val publishGroupId: String by project
val publishVersion: String by project

afterEvaluate {
    val releaseAar =
        layout.buildDirectory
            .file("outputs/aar/$publishArtifactId-$publishVersion.aar")
            .get()
            .asFile
    val rcAar =
        layout.buildDirectory
            .file("outputs/aar/$publishArtifactId-$publishVersion-rc.aar")
            .get()
            .asFile

    project.extensions.configure<PublishingExtension>("publishing") {
        publications {
            create<MavenPublication>("release") {
                groupId = publishGroupId
                artifactId = publishArtifactId
                version = publishVersion

                artifact(releaseAar) {
                    builtBy(tasks.named("assemble"))
                }
                artifact(tasks.named("generateSourcesJar").get()) {
                    classifier = "sources"
                }
                artifact(tasks.named("generateJavadocJar").get()) {
                    classifier = "javadoc"
                }

                pom {
                    name.set("vcl")
                    packaging = "aar"
                    description.set("Velocity Career Labs Android SDK consumer app.")
                    url.set("https://github.com/velocitycareerlabs/WalletSdkKmp")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("velocitycareerlabs")
                            name.set("Michael Avoyan")
                            email.set("michael.avoyan@gmail.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/velocitycareerlabs/WalletSdkKmp.git")
                        developerConnection.set("scm:git:ssh://[email protected]/velocitycareerlabs/WalletSdkKmp")
                        url.set("https://github.com/velocitycareerlabs/WalletSdkKmp")
                    }
                }
            }

            create<MavenPublication>("rc") {
                groupId = publishGroupId
                artifactId = publishArtifactId
                version = "$publishVersion-rc"

                artifact(rcAar) {
                    builtBy(tasks.named("assemble"))
                }
                artifact(tasks.named("generateSourcesJar").get()) {
                    classifier = "sources"
                }
                artifact(tasks.named("generateJavadocJar").get()) {
                    classifier = "javadoc"
                }

                pom {
                    name.set("vcl")
                    packaging = "aar"
                    description.set("Velocity Career Labs Android SDK RC build.")
                    url.set("https://github.com/velocitycareerlabs/WalletSdkKmp")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("velocitycareerlabs")
                            name.set("Michael Avoyan")
                            email.set("michael.avoyan@gmail.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/velocitycareerlabs/WalletSdkKmp.git")
                        developerConnection.set("scm:git:ssh://[email protected]/velocitycareerlabs/WalletSdkKmp")
                        url.set("https://github.com/velocitycareerlabs/WalletSdkKmp")
                    }
                }
            }
        }
    }
}
