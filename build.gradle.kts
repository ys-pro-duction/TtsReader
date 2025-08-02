import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0" // Use the Kotlin version you are using
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
                implementation(files("libs/sherpa-onnx-v1.12.7-java21.jar"))
//                implementation("org.jetbrains.compose.material:material-icons-extended-desktop:1.5.0")
                implementation(compose.material3)
                implementation(compose.preview)
                implementation("com.russhwolf:multiplatform-settings:1.3.0")

            }
        }

        val jvmTest by getting

    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(
//                TargetFormat.Dmg,
                TargetFormat.Msi,
//                TargetFormat.Deb
            )
            packageName = "TtsReader - Text to Speech"
            packageVersion = "1.0.0"
            windows {
                dirChooser = true
                menuGroup = "TtsReader"
                // Ensure native libraries are included
                includeAllModules = true
                shortcut = true
                menu = true
                iconFile.set(project.file("src/jvmMain/resources/app_icon.ico")) // your .ico file
                appResourcesRootDir.set(project.layout.projectDirectory.dir("sherpa-lib"))

//                // Add distribution-specific JVM arguments
                jvmArgs += listOf(
                    "-Djava.library.path=app/resources;libs"
                )

            }
        }

    }
}
tasks.register("buildDestributableAndRun") {
    dependsOn("packageDistributionForCurrentOS", "runDistributable")
}
