include(":bindings", ":plugin")

pluginManagement {
    val kotlinVersion: String by settings
    val kspVersion: String by settings
    val intellijVersion: String by settings

    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
        id("org.jetbrains.intellij") version intellijVersion
        id("com.google.devtools.ksp") version kspVersion
    }
}

rootProject.name = "CLSI"