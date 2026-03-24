import me.modmuss50.mpp.ReleaseType

plugins {
	id("dynamic_fps.base")
	id("dynamic_fps.java")
	id("dynamic_fps.common")
	alias(libs.plugins.loom)
	alias(libs.plugins.mod.publish)
}

repositories {
	maven {
		name = "Terraformers"
		url = uri("https://maven.terraformersmc.com/")
	}
}

dependencies {
	minecraft(libs.minecraft)
	implementation(libs.fabric.loader)

	include(libs.battery)
	implementation(libs.modmenu)

	include(project(":platforms:common"))
	implementation(project(":platforms:common"))

	implementation(fabricApi.module("fabric-resource-loader-v0", libs.versions.fabric.api.get()))
	implementation(fabricApi.module("fabric-lifecycle-events-v1", libs.versions.fabric.api.get()))
}

var modVersion = project.property("mod_version").toString()

fun getVersionType(): ReleaseType {
	return if (modVersion.startsWith("0.") || modVersion.contains("-alpha.")) {
		ReleaseType.ALPHA
	} else if (modVersion.contains("-")) {
		ReleaseType.BETA
	} else {
		ReleaseType.STABLE
	}
}

publishMods {
	version = modVersion
	displayName = "v${modVersion}"

	type = getVersionType()
	modLoaders.addAll("fabric", "quilt")

	changelog = file(rootDir.toPath().resolve("changelog.txt")).readText()

	file = tasks.withType<Jar>()["jar"].archiveFile
	additionalFiles = files(tasks.withType<Jar>()["sourcesJar"].archiveFile)

	curseforge {
		accessToken = providers.environmentVariable("CURSEFORGE_SECRET")
		projectId = "335493"

		minecraftVersionRange {
			start = project.property("minecraft_version_min").toString()
			end = project.property("minecraft_version_max").toString()
		}

		clientRequired = true
		serverRequired = false

		requires("fabric-api")
		optional("cloth-config", "modmenu")
	}

	modrinth {
		accessToken = providers.environmentVariable("MODRINTH_SECRET")
		projectId = "LQ3K71Q1"

		minecraftVersionRange {
			start = project.property("minecraft_version_min").toString()
			end = project.property("minecraft_version_max").toString()
		}

		requires("fabric-api")
		optional("cloth-config", "modmenu")
	}
}
