buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.20")
        classpath("com.android.tools.build:gradle:7.3.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }

    group = "com.nextome.kbeaconscanner"
    version = System.getenv("GITHUB_REF")?.split('/')?.last() ?: "development"
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
