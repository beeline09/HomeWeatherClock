@file:Suppress("UnstableApiUsage")

apply(from = "${rootProject.projectDir}/composeApp/constants.gradle.kts")
private val appName = extra["appName"].toString()
rootProject.name = appName

//rootProject.name = "HomeWeatherClock"
include(":composeApp")

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io")
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io")
    }
}
