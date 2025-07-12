import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
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
    jvm {
        jvmToolchain(17)
        withJava()
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(":libs/sherpa-onnx-v1.10.46-java17")
                // https://mvnrepository.com/artifact/androidx.compose.material/material-icons-extended
                runtimeOnly("androidx.compose.material:material-icons-extended:1.7.8")

            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        jvmArgs += listOf("-Djava.library.path=$projectDir/libs")


        nativeDistributions {
            targetFormats(
                TargetFormat.Dmg,
                TargetFormat.Msi,
                TargetFormat.Deb
            )
            packageName = "TtsReader"
            packageVersion = "1.0.0"

            windows {
                dirChooser = true
                menuGroup = "TtsReader"
                // Ensure native libraries are included
                includeAllModules = true

                // Add distribution-specific JVM arguments
                jvmArgs += listOf(
                    "-Djava.library.path=libs",
                    "-Djna.library.path=libs"
                )
            }

            // Specify resources to include
            modules("libs/sherpa-onnx-jni.dll")

            // Copy native libraries to the distribution
//            distributionPath.set(project.buildDir.resolve("dist"))

            appResourcesRootDir.set(project.layout.projectDirectory.dir("libs"))
        }
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