import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.ktlint) apply false
}

subprojects {
    afterEvaluate {
        if (
            plugins.hasPlugin("org.jetbrains.kotlin.multiplatform") ||
            plugins.hasPlugin("org.jetbrains.kotlin.jvm") ||
            plugins.hasPlugin("org.jetbrains.kotlin.android")
        ) {
            apply(plugin = "org.jlleitschuh.gradle.ktlint")

            extensions.configure<KtlintExtension> {
                android.set(true)
                ignoreFailures.set(false)
                reporters {
                    reporter(ReporterType.PLAIN)
                }
                filter {
                    exclude("**/build/**")
                    exclude("**/generated/**")
                }
            }

            tasks.register("cleanKtlintCheck") {
                dependsOn("clean", "ktlintCheck")
            }

            tasks.register("cleanKtlintFormat") {
                dependsOn("clean", "ktlintFormat")
            }
        }
    }
}
