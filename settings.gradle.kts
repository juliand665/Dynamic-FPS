pluginManagement {
    repositories {
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.architectury.dev/") }
        maven { url = uri("https://maven.minecraftforge.net/") }
		maven { url = uri("https://maven.neoforged.net/releases/") }
        gradlePluginPortal()
    }
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "dynamic-fps"
includeBuild("build-logic")

include(":platforms:common")
include(":platforms:fabric")
// include(":platforms:forge")
// include(":platforms:neoforge")
include(":platforms:quilt")
include(":platforms:textile")
