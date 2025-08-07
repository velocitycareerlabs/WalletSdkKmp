import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.cocoapods)
    alias(libs.plugins.mavenPublish)
    id("signing")
}

// ----- Artifact coordinates -----
val publishVersion = "0.1.0"
val publishArtifactId = "velocityexchangeverifiers"
val publishGroupId = "io.velocitycareerlabs"

extra["publishVersion"] = publishVersion
extra["publishArtifactId"] = publishArtifactId
extra["publishGroupId"] = publishGroupId

//apply(from = "android-publish.gradle.kts")

kotlin {

//    explicitApi() // Requires explicit visibility and return types

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
        version = publishVersion
        namespace = "$publishGroupId.$publishArtifactId"
        compileSdk = 36
        minSdk = 24

        withHostTestBuilder { }

        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }.configure {
            instrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }
    // This generates and attaches a proper sources jar for Android (containing *real* androidMain sources)
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
                implementation(libs.kotlin.stdlib)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.coroutines.test)
                // Add KMP dependencies here
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
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

// --- Aggregate target builds for convenience ---
tasks.register("assembleAllTargets") {
    group = "build"
    dependsOn(
        rootProject.tasks.named("kotlinUpgradeYarnLock"),
        "assemble", // Android AAR
        "sourcesJar", // Android sources JAR (auto by withSourcesJar)
        "assembleXCFramework", // iOS
        "jsNodeProductionLibraryDistribution", // JS
//      "wasmJsJar",
//      "wasmJsBrowserProductionWebpack",
//      "jsBrowserProductionWebpack",
//      "jsJar",
    )
}
