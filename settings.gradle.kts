pluginManagement {
	repositories {
		maven {
			name = "Fabric"
			url = uri("https://maven.fabricmc.net/")
		}
		maven {
			name = "NeoForged"
			url = uri("https://maven.neoforged.net/releases/")
		}
		gradlePluginPortal()
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "dynamic-fps"
includeBuild("build-logic")

include(":platforms:common")
include(":platforms:fabric")
// include(":platforms:neoforge")
