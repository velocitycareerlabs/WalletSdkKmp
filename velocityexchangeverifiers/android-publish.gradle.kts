import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

val publishArtifactId: String by project
val publishGroupId: String by project
val publishVersion: String by project

afterEvaluate {
    extensions.configure<PublishingExtension>("publishing") {
        publications {
            // ---- RELEASE ----
            create<MavenPublication>("release") {
                groupId = publishGroupId
                artifactId = publishArtifactId
                version = publishVersion

                // This will pick up the AAR from the 'assemble' task output directory
                artifact("$buildDir/outputs/aar/$publishArtifactId-$publishVersion.aar") {
                    builtBy(tasks.named("assemble"))
                }
                artifact(tasks.named("sourcesJar").get())
                artifact(tasks.named("javadocJar").get())

                pom {
                    name.set(publishArtifactId)
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
                        developerConnection.set("scm:git:ssh://[email protected]/velocitycareerlabs/WalletSdkKmp.git")
                        url.set("https://github.com/velocitycareerlabs/WalletSdkKmp")
                    }
                }
            }

            // ---- RC ----
            create<MavenPublication>("rc") {
                groupId = publishGroupId
                artifactId = publishArtifactId
                version = "$publishVersion-rc"

                artifact("$buildDir/outputs/aar/$publishArtifactId-$publishVersion-rc.aar") {
                    builtBy(tasks.named("assemble"))
                }
                artifact(tasks.named("sourcesJar").get())
                artifact(tasks.named("javadocJar").get())

                pom {
                    name.set("$publishArtifactId-rc")
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
                        developerConnection.set("scm:git:ssh://[email protected]/velocitycareerlabs/WalletSdkKmp.git")
                        url.set("https://github.com/velocitycareerlabs/WalletSdkKmp")
                    }
                }
            }
        }
    }
}
