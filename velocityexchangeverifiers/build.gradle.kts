import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.mavenPublish)
    alias(libs.plugins.cocoapods)
}

val publishCode = 1
val publishVersion = "0.1.0"
val publishArtifactId = "velocityexchangeverifiers"
val publishGroupId = "io.velocitycareerlabs"

kotlin {

//    explicitApi() // Requires explicit visibility and return types

    // Target declarations - add or remove as needed below. These define
    // which platforms this KMP module supports.
    // See: https://kotlinlang.org/docs/multiplatform-discover-project.html#targets
    androidLibrary {
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
            xcf.add(this)
        }
    }

    iosArm64 {
        binaries.framework {
            baseName = publishArtifactId
            xcf.add(this)
        }
    }

    iosSimulatorArm64 {
        binaries.framework {
            baseName = publishArtifactId
            xcf.add(this)
        }
    }

    js(IR) {
        nodejs() // Target Node.js environment
        useEsModules() // Output ES2015+ modules (produces .mjs or ES module .js files) [oai_citation:0‡kt.academy](https://kt.academy/article/ak-js-interop#:~:text=jvm%20,sourceSets)
        outputModuleName = publishArtifactId // Set module name (lowercase to avoid Node warnings) [oai_citation:1‡dev.to](https://dev.to/touchlab/different-ways-to-distribute-and-integrate-kotlinjs-library-1hg3#:~:text=Note%20that%20node%20module%20cannot,target%20to%20avoid%20that)
        binaries.library() // Build as a library (produces .js/.mjs + .d.ts + package.json) [oai_citation:2‡dev.to](https://dev.to/touchlab/different-ways-to-distribute-and-integrate-kotlinjs-library-1hg3#:~:text=As%20mentioned%20above%20in%20the,%60package.json)

//        TODO: Discuss with Andres
        compilations["main"].packageJson {
            name = "@velocitycareerlabs/$publishArtifactId"
            version = publishVersion
        }
    }
    sourceSets.all {
        languageSettings.optIn("kotlin.js.ExperimentalJsExport") // Opt-in to @JsExport (since it's experimental)
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

// https://dev.to/touchlab/different-ways-to-distribute-and-integrate-kotlinjs-library-1hg3#:~:text=As%20mentioned%20above%20in%20the,%60package.json
tasks.register("assembleAllTargets") {
    dependsOn(
        rootProject.tasks.named("kotlinUpgradeYarnLock"), // fixes yarn.lock before build
        "assemble", // Android AAR
        "assembleXCFramework", // iOS
        "jsNodeProductionLibraryDistribution", // Node.js .mjs
//      "wasmJsJar",
//      "wasmJsBrowserProductionWebpack",
//      "jsBrowserProductionWebpack",
//      "jsJar",
    )
}
