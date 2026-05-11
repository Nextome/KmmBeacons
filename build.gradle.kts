plugins {
    kotlin("multiplatform") version "2.0.10" apply false
}

buildscript {

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.10")
        classpath("com.android.tools.build:gradle:8.13.2")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    group = "com.nextome.kmmbeacons"
    val libraryVersion = "1.3.1"
    version = System.getenv("GITHUB_REF")?.split('/')?.last() ?: libraryVersion
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}