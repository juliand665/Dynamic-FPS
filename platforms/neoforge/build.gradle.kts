import me.modmuss50.mpp.ReleaseType

plugins {
	id("dynamic_fps.base")
	id("dynamic_fps.java")
	id("dynamic_fps.common")
	alias(libs.plugins.moddev)
	alias(libs.plugins.mod.publish)
}

dependencies {
	jarJar(libs.battery)
	implementation(libs.battery)

	jarJar(project(":platforms:common"))
	implementation(project(":platforms:common"))
}

neoForge {
	version = libs.versions.neoforge.get()

	runs {
		create("client") {
			type = "client"
		}
	}

	mods {
		create("dynamic_fps") {
			sourceSet(sourceSets.main.get())
		}
	}
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
	modLoaders.addAll("neoforge")

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

		optional("cloth-config")
	}

	modrinth {
		accessToken = providers.environmentVariable("MODRINTH_SECRET")
		projectId = "LQ3K71Q1"

		minecraftVersionRange {
			start = project.property("minecraft_version_min").toString()
			end = project.property("minecraft_version_max").toString()
		}

		optional("cloth-config")
	}
}
