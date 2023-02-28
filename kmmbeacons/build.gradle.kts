plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("maven-publish")
    id("com.android.library")
    id("org.jetbrains.dokka") version "1.7.20"
}

kotlin {

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    android {
        publishLibraryVariants("release")
        publishLibraryVariantsGroupedByFlavor = true
    }

    cocoapods {
        summary = "Beacon scanner for KMM"
        homepage = "https://github.com/Nextome/KmmBeacons"
        ios.deploymentTarget = "14.1"
        name = "kmmbeacons"

        framework {
            baseName = "kmmbeacons"
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("co.touchlab:kermit:1.1.3")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation("co.touchlab:stately-concurrent-collections:2.0.0-rc1")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("org.altbeacon:android-beacon-library:2.19.5")
                implementation("androidx.startup:startup-runtime:1.1.1")
            }
        }

        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 23
        targetSdk = 32
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
    }
}

System.getenv("GITHUB_REPOSITORY")?.let {
    publishing {
        repositories {
            maven {
                name = "github"
                url = uri("https://maven.pkg.github.com/$it")
                credentials(PasswordCredentials::class)
            }
        }
    }
}