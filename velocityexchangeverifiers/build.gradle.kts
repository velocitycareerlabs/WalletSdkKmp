import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.cocoapods)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.signing)
    alias(libs.plugins.dokka)
}

val publishGroupId = providers.gradleProperty("PUBLISH_GROUP_ID").get()
val publishArtifactId = providers.gradleProperty("PUBLISH_ARTIFACT_ID").get()
val publishVersion = providers.gradleProperty("PUBLISH_VERSION").get()

val cliProjectVersion = providers.gradleProperty("projectVersion").orNull
val isPrerelease =
    providers
        .gradleProperty("prerelease")
        .map { it.toBoolean() }
        .orElse(false)
        .get()

val effectiveBase = cliProjectVersion ?: publishVersion
val effectiveVersion = if (isPrerelease) "$effectiveBase-rc" else effectiveBase

group = publishGroupId
version = publishVersion

extra["publishVersion"] = publishVersion
extra["publishArtifactId"] = publishArtifactId
extra["publishGroupId"] = publishGroupId
extra["effectiveVersion"] = effectiveVersion

apply(from = "android-publish.gradle.kts")

// TODO: For future optimization of building only the targets that are needed
// val targetJs = providers.gradleProperty("targetJs").isPresent
// val targetIos = providers.gradleProperty("targetIos").isPresent
// val targetAndroid = providers.gradleProperty("targetAndroid").isPresent

kotlin {
    jvmToolchain(17)

//    explicitApi() // Requires explicit visibility and return types

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        version = publishVersion
        namespace = "$publishGroupId.$publishArtifactId"

        // read from a single source of truth
        compileSdk =
            providers
                .gradleProperty("ANDROID_COMPILE_SDK")
                .map(String::toInt)
                .get()

        minSdk =
            providers
                .gradleProperty("ANDROID_MIN_SDK")
                .map(String::toInt)
                .get()

        withHostTestBuilder { }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    // Android sources JAR (new Android DSL)
    withSourcesJar(publish = true)

    // For iOS targets, this is also where you should
    // configure native binary output. For more information, see:
    // https://kotlinlang.org/docs/multiplatform-build-native-binaries.html#build-xcframeworks

    // A step-by-step guide on how to include this library in an XCode
    // project can be found here:
    // https://developer.android.com/kotlin/multiplatform/migrate
    val xcf = XCFramework()

    iosX64 {
        binaries.framework {
            baseName = publishArtifactId
            version = publishVersion
            xcf.add(this)
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = publishArtifactId
            version = publishVersion
            xcf.add(this)
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = publishArtifactId
            version = publishVersion
            xcf.add(this)
        }
    }

    js(IR) {
        nodejs()
        useEsModules()
        outputModuleName = "$publishArtifactId-js"
        binaries.library()
        generateTypeScriptDefinitions()

        compilations["main"].packageJson {
            name = "@velocitycareerlabs/$publishArtifactId-js"
            version = publishVersion
            description = "Velocity SDK for Exchange Verifiers"
            customField("license", "Apache-2.0")
            customField("author", "Michael Avoyan")
            customField("repository", "https://github.com/velocitycareerlabs/WalletSdkKmp")
            customField("homepage", "https://github.com/velocitycareerlabs/WalletSdkKmp")
            customField("main", "$publishArtifactId-js.mjs")
            customField("types", "$publishArtifactId-js.d.ts")
            customField("module", "$publishArtifactId-js.mjs")
            customField("sideEffects", false)
            customField("publishConfig", mapOf("access" to "public"))
            customField(
                "keywords",
                listOf(
                    "velocity",
                    "vcl",
                    "wallet",
                    "exchange",
                    "verifier",
                    "velocityexchangeverifiers",
                    "velocity-exchange-verifiers",
                    "verifiable",
                    "credentials",
                    "identity",
                ),
            )
        }
    }

    wasmJs {
        browser {
            commonWebpackConfig {
                cssSupport { }
            }
        }
        binaries.executable()
    }

    cocoapods {
        version = publishVersion
        summary = "KMP SDK for credential verification"
        homepage = "https://github.com/velocitycareerlabs"
        ios.deploymentTarget = "13.0"
        framework {
            baseName = publishArtifactId
            isStatic = false
        }
    }

    sourceSets {
        commonMain {
            dependencies {
//                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.coroutines.test)
                // Add KMP dependencies here
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.coroutines.test)
            }
        }

        androidMain {
            dependencies {
                // Add Android-specific dependencies here. Note that this source set depends on
                // commonMain by default and will correctly pull the Android artifacts of any KMP
                // dependencies declared in commonMain.
            }
        }

        getByName("androidDeviceTest") {
            dependencies {
                implementation(libs.androidx.runner)
                implementation(libs.androidx.core)
                implementation(libs.androidx.testExt.junit)
            }
        }

        iosMain {
            dependencies {
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                // part of KMP’s default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.
            }
        }

        jsMain {
            dependencies {
                // Add JS-specific dependencies here.
            }
        }
    }
}

// Dokka config (safe for KMP; no README includes)
tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets.configureEach {
        skipEmptyPackages.set(true)
        moduleName.set("velocityexchangeverifiers")
    }
}

// Build a real javadoc jar from Dokka HTML output
tasks.register<Jar>("androidDokkaJavadocJar") {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    description = "Assembles Dokka HTML into a javadoc-classified jar for Maven Central"
    archiveClassifier.set("javadoc")
    dependsOn(tasks.named("dokkaHtml"))
    from(layout.buildDirectory.dir("dokka/html"))
}

tasks.register("assembleAndroid") {
    group = "build"
    dependsOn(
        tasks.named("assemble"),
        tasks.named("androidDokkaJavadocJar"),
    )
}

tasks.register("assembleIos") {
    group = "build"
    dependsOn(
        tasks.named("assemble"),
        tasks.named("assembleXCFramework"),
    )
}

tasks.register("assembleJs") {
    group = "build"
    dependsOn(
        rootProject.tasks.named("kotlinUpgradeYarnLock"),
        tasks.named("jsNodeProductionLibraryDistribution"),
    )
}

tasks.register("assembleAllTargets") {
    group = "build"
    dependsOn(
        rootProject.tasks.named("kotlinUpgradeYarnLock"),
        tasks.named("assemble"),
        tasks.named("androidDokkaJavadocJar"),
        tasks.matching { it.name == "assembleXCFramework" },
        tasks.matching { it.name == "jsNodeProductionLibraryDistribution" },
    )
}

// Aggregate build helper
tasks.register("verifyExpectedArtifactsExist") {
    group = "verification"
    description = "Prints the contents of key artifact directories"

    doLast {
        fun printDirContents(
            title: String,
            dirPath: String,
        ) {
            println("📂 Contents of $dirPath/")
            val dir = file(dirPath)
            if (dir.exists() && dir.isDirectory) {
                dir.listFiles()?.forEach { println(" - ${it.name}") }
            } else {
                println("❌ Directory does not exist: $dirPath")
            }
        }

        printDirContents("AAR", "build/outputs/aar")
        printDirContents("LIBS", "build/libs")
    }
}

// Stage for JReleaser (Android only, Maven friendly names)
tasks.register<Sync>("stageArtifacts") {
    val groupPath = publishGroupId.replace('.', '/')
    val mavenPath = "$groupPath/$publishArtifactId/$effectiveVersion/"
    val dest = layout.projectDirectory.dir("target/staging-deploy/$mavenPath")
    into(dest)

    // AAR -> velocityexchangeverifiers-<effectiveVersion>.aar
    from(layout.buildDirectory.dir("outputs/aar")) {
        include("*.aar")
        rename { "$publishArtifactId-$effectiveVersion.aar" }
        includeEmptyDirs = false
    }

    // Sources and Javadoc jars
    // We rename whatever is produced to the exact names Central expects
    from(layout.buildDirectory.dir("libs")) {
        include("**/*sources*.jar", "**/*javadoc*.jar")
        exclude("**/*-metadata.jar*", "**/*-kotlin-tooling-metadata.jar")
        rename { name ->
            when {
                name.contains("sources") -> "$publishArtifactId-$effectiveVersion-sources.jar"
                name.contains("javadoc") -> "$publishArtifactId-$effectiveVersion-javadoc.jar"
                else -> name
            }
        }
        includeEmptyDirs = false
    }
}
