import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0" // Use the Kotlin version you are using
//    id("org.jetbrains.compose.hot-reload") version "1.0.0-beta02"
//    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

group = "com.yogesh"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    flatDir {
        dirs(".")
    }
}

kotlin {
    jvmToolchain(21)
    jvm {
        withJava()
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(files("libs/sherpa-onnx-v1.10.46-java17.jar"))
                implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.5.0")
                implementation("org.jetbrains.compose.ui:ui-tooling-preview:1.8.2")
                implementation(compose.material3)
                implementation(compose.preview)
                implementation("com.russhwolf:multiplatform-settings:1.3.0")
                // compose hot reload
//                implementation("org.jetbrains.compose.hot-reload:runtime-reload:1.5.0")

            }
            kotlin.srcDir("libs")
        }

        val jvmTest by getting

    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

//        jvmArgs += listOf("-Djava.library.path=$projectDir/libIsHere")


        nativeDistributions {
            targetFormats(
//                TargetFormat.Dmg,
                TargetFormat.Msi,
//                TargetFormat.Deb
            )
            packageName = "TtsReader"
            packageVersion = "1.0.0"
            windows {
                dirChooser = true
                menuGroup = "TtsReader"
                // Ensure native libraries are included
                includeAllModules = true

                appResourcesRootDir.set(project.layout.projectDirectory.dir("sherpa-lib"))

//                // Add distribution-specific JVM arguments
//                jvmArgs += listOf(
//                    "-Djava.library.path=libIsHere"
//                )

            }
//            val libIsHereDir = project.projectDir.resolve("libIsHere") // Or "libIsHere" if case sensitive
//            if (libIsHereDir.exists() && libIsHereDir.isDirectory) {
//                fromFiles(libIsHereDir) // Specifies source directory and target subdirectory
//            } else {
//                println("Warning: libIsHere directory not found at ${libIsHereDir.absolutePath}. DLLs might not be packaged.")
//            }

            // Specify resources to include
//            modules("libIsHere/sherpa-onnx-jni.dll")

            // Copy native libraries to the distribution
//            distributionPath.set(project.buildDir.resolve("dist"))
            // Add resources for MSI packaging (as discussed before)
//            appResourcesRootDir.set(project.layout.projectDirectory.dir("libIsHere")) // Make sure libIsHere is at project root
            // Or if libIsHere is in src/main/resources/
            // fromFiles(fileTree("src/main/resources/libIsHere") { include "*.dll" }) {
            //     into("libIsHere") // Copy the folder into the app root
            // }
        }

        // --- THIS IS THE KEY PART FOR DEVELOPMENT RUNS ---
//        run {
//            // Determine the absolute path to your libIsHere directory
//            // This assumes 'libIsHere' is in your project's root directory.
//            // Adjust if it's elsewhere (e.g., "src/main/resources/libIsHere")
//            val absoluteLibDirPath = project.projectDir.resolve("libIsHere").absolutePath
//
//            jvmArgs += listOf("-Djava.library.path=$absoluteLibDirPath")
//            // For debugging:
//            // jvmArgs += "-verbose:jni" // To see JNI related loading messages
//        }

    }
}
// Add a task to copy native libraries to the distribution
tasks.register<Copy>("copyNativeLibs") {
    from("libs") {
        include("*.dll")
    }
    into("${buildDir}/compose/binaries/main/app/${rootProject.name}/libs")
}

// Make the distribution task depend on copyNativeLibs
//tasks.named("createDistributable") {
//    dependsOn("copyNativeLibs")
//}