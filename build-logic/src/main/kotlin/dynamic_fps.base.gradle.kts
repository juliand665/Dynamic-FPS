import org.gradle.accessors.dm.LibrariesForLibs

plugins {
	id("base")
}

val libs = the<LibrariesForLibs>()

val modVersion = project.property("mod_version").toString()
val minecraft = project.property("minecraft_version").toString()

group = "net.lostluma"
base.archivesName = "dynamic-fps"
version = "${modVersion}+minecraft-${minecraft}-${project.name}"

tasks.withType<ProcessResources> {
	inputs.property("version", modVersion)

	filesMatching(listOf("fabric.mod.json", "META-INF/neoforge.mods.toml")) {
		expand(inputs.properties)
	}
}
