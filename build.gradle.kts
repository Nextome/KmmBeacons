plugins {
    kotlin("multiplatform") version "1.8.0" apply false
}

buildscript {

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.0")
        classpath("com.android.tools.build:gradle:7.3.1")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    group = "com.nextome.kmmbeacons"
    val libraryVersion = "1.1.1"
    version = System.getenv("GITHUB_REF")?.split('/')?.last() ?: libraryVersion
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}