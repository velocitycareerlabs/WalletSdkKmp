import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

val publishArtifactId: String by project
val publishGroupId: String by project
val publishVersion: String by project

val effectiveVersion: String = (project.extra["effectiveVersion"] as? String)
    ?: publishVersion

afterEvaluate {
    // Use assemble; RC vs Release is only the version string
    val aarTaskName = "assemble"

    val aarRelPath = "outputs/aar/${project.name}.aar"

    val aarFile = layout.buildDirectory.file(aarRelPath)

    val sourcesJarTaskName =
        listOf(
            "sourcesJar",
            "androidSourcesJar",
            "androidReleaseSourcesJar",
        ).firstOrNull { tasks.findByName(it) != null }
            ?: error("No sourcesJar task found. Ensure withSourcesJar(publish = true) is enabled in kotlin { androidLibrary { ... } }")

    extensions.configure<PublishingExtension>("publishing") {
        publications {
            // ---- RELEASE ----
            create<MavenPublication>("release") {
                groupId = publishGroupId
                artifactId = publishArtifactId
                version = effectiveVersion

                artifact(aarFile) { builtBy(tasks.named(aarTaskName)) }
                artifact(tasks.named(sourcesJarTaskName).get())
                artifact(tasks.named("androidDokkaJavadocJar").get())

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
                        developerConnection.set("scm:git:ssh://git@github.com/velocitycareerlabs/WalletSdkKmp.git")
                        url.set("https://github.com/velocitycareerlabs/WalletSdkKmp")
                    }
                }
            }

            // ---- RC ----
            create<MavenPublication>("rc") {
                groupId = publishGroupId
                artifactId = publishArtifactId
                version = effectiveVersion

                artifact(aarFile) { builtBy(tasks.named(aarTaskName)) }
                artifact(tasks.named(sourcesJarTaskName).get())
                artifact(tasks.named("androidDokkaJavadocJar").get())

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
                        developerConnection.set("scm:git:ssh://git@github.com/velocitycareerlabs/WalletSdkKmp.git")
                        url.set("https://github.com/velocitycareerlabs/WalletSdkKmp")
                    }
                }
            }
        }
    }
}
