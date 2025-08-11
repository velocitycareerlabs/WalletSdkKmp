import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

val publishArtifactId: String by project
val publishGroupId: String by project
val publishVersion: String by project

// Provided by build.gradle.kts
val effectiveVersion: String =
    (project.extra["effectiveVersion"] as? String) ?: publishVersion

afterEvaluate {
    // Build the AAR with standard assemble; release vs rc is only the version string
    val aarTaskName = "assemble"

    // New Android KMP DSL: <module>.aar
    val aarRelPath = "outputs/aar/${project.name}.aar"
    val aarFile = layout.buildDirectory.file(aarRelPath)

    // Sources jar task name
    val sourcesJarTaskName =
        listOf(
            "sourcesJar",
            "androidSourcesJar",
            "androidReleaseSourcesJar",
        ).firstOrNull { tasks.findByName(it) != null }
            ?: error("No sourcesJar task found. Ensure withSourcesJar(publish = true) is enabled in kotlin { androidLibrary { ... } }")

    extensions.configure<PublishingExtension>("publishing") {
        publications {
            // RELEASE (used when prerelease=false)
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
                    description.set("Velocity Career Labs Android SDK.")
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

            // RC (used when prerelease=true) – same version/effectiveVersion
            create<MavenPublication>("rc") {
                groupId = publishGroupId
                artifactId = publishArtifactId
                version = effectiveVersion

                artifact(aarFile) { builtBy(tasks.named(aarTaskName)) }
                artifact(tasks.named(sourcesJarTaskName).get())
                artifact(tasks.named("androidDokkaJavadocJar").get())

                pom {
                    name.set(publishArtifactId) // do NOT append "-rc" here; version already has it
                    packaging = "aar"
                    description.set("Velocity Career Labs Android SDK.")
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
