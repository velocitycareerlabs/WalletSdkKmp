import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.bundling.Jar

val publishArtifactId: String by extra
val publishGroupId: String by extra
val publishVersion: String by extra

val androidExtension = extensions.getByName("android") as com.android.build.gradle.LibraryExtension

val mainSourceSet = androidExtension.sourceSets.getByName("main")

tasks.register<Jar>("generateSourcesJar") {
    group = "assemble"
    archiveClassifier.set("sources")
    archiveBaseName.set(publishArtifactId.lowercase())
    from(mainSourceSet.java.srcDirs + mainSourceSet.kotlin.srcDirs)
}

tasks.register<Jar>("generateJavadocJar") {
    group = "assemble"
    archiveClassifier.set("javadoc")
    archiveBaseName.set(publishArtifactId.lowercase())

    doFirst {
        val dummyDir =
            layout.buildDirectory
                .dir("empty-javadoc")
                .get()
                .asFile
        dummyDir.mkdirs()
        val dummyJava = dummyDir.resolve("placeholder.java")
        if (!dummyJava.exists()) {
            dummyJava.writeText("/** Placeholder for javadoc */")
        }
        from(dummyDir)
    }
}

tasks.register("generateSourcesAndJavadocJar") {
    group = "assemble"
    description = "Generates sources.jar and javadoc.jar"
    dependsOn("generateSourcesJar", "generateJavadocJar")
}

tasks.register("assembleAllRelease") {
    group = "assemble"
    description = "Generates AAR, sources.jar and javadoc.jar for release"
    dependsOn("assembleRelease", "generateSourcesAndJavadocJar")
}

tasks.register("assembleAllRc") {
    group = "assemble"
    description = "Generates AAR, sources.jar and javadoc.jar for rc"
    dependsOn("assembleRc", "generateSourcesAndJavadocJar")
}

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

tasks.register<Copy>("stageArtifacts") {
    val mavenPath = "${publishGroupId.replace('.', '/')}/$publishArtifactId/$publishVersion/"
    from(layout.buildDirectory.dir("outputs/aar")) {
        include("**/*.aar")
    }
    from(layout.buildDirectory.dir("libs")) {
        include("**/*.jar")
    }
    into("target/staging-deploy/$mavenPath")
}
