plugins {
    kotlin("multiplatform")
    kotlin("native.cocoapods")
    id("maven-publish")
    id("com.android.library")
    id("com.jfrog.artifactory") version "4.29.0"
    id("org.jetbrains.dokka") version "1.7.10"
}
group = "com.annalabellarte.testScan"
version = "1.0.1"

kotlin {

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    android {
        publishLibraryVariants("release", "debug")
    }

    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        name = "KBeaconScanner"

        framework {
            baseName = "KBeaconScanner"
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("co.touchlab:kermit:1.1.3")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                implementation("org.altbeacon:android-beacon-library:2.19.4")
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
}
publishing{
    publications {
        register("mavenKotlin", MavenPublication::class) {
            groupId = "com.annalabellarte.testScan"
            artifactId = "KBeaconScanner"
            version = "1.0.1"
            artifact("$buildDir/libs/KBeaconScanner-kotlin-1.0.1-sources.jar")

        }
        register("mavenAndroidDebug", MavenPublication::class) {
            groupId = "com.annalabellarte.testScan"
            artifactId = "KBeaconScanner-android-debug"
            version = "1.0.1"
            artifact("$buildDir/libs/KBeaconScanner-android-debug-1.0.1-sources.jar")

        }

        register("mavenAndroid", MavenPublication::class) {
            groupId = "com.annalabellarte.testScan"
            artifactId = "KBeaconScanner-android"
            version = "1.0.1"
            artifact("$buildDir/libs/KBeaconScanner-android-1.0.1-sources.jar")

        }

        register("mavenIosArm64", MavenPublication::class) {
            groupId = "com.annalabellarte.testScan"
            artifactId = "KBeaconScanner-iosarm64"
            version = "1.0.1"
            artifact("$buildDir/libs/KBeaconScanner-iosarm64-1.0.1-sources.jar")

        }
        register("mavenIosSimulatorArm64", MavenPublication::class) {
            groupId = "com.annalabellarte.testScan"
            artifactId = "KBeaconScanner-iossimulatorarm64"
            version = "1.0.1"
            artifact("$buildDir/libs/KBeaconScanner-iossimulatorarm64-1.0.1-sources.jar")

        }

        register("mavenIosx86", MavenPublication::class) {
            groupId = "com.annalabellarte.testScan"
            artifactId = "KBeaconScanner-iosx64"
            version = "1.0.1"

            artifact("$buildDir/libs/KBeaconScanner-iosx64-1.0.1-sources.jar")

        }


    }
}

artifactory{
    setContextUrl("https://annalabellarte.jfrog.io/artifactory")
    publish{
        setContextUrl("https://annalabellarte.jfrog.io/artifactory")
        repository {
            setRepoKey("playground")
            setUsername("labe.anna97@gmail.com")
            setPassword("15-rK9mTiGvX")
            setMavenCompatible(true)
        }

        defaults {
            publications("mavenKotlin", "mavenAndroid",
                "mavenAndroidDebug", "mavenIosx86", "mavenIosSimulatorArm64", "mavenIosArm64")
        }

    }
}
