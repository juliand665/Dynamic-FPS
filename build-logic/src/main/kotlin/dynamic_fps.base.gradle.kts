plugins {
    id("base")
}

val baseName = project.property("archives_name").toString()

val mavenGroup = project.property("maven_group").toString()
val modVersion = project.property("mod_version").toString()

base {
    archivesName = baseName
}

group = mavenGroup
version = modVersion

if (project.hasProperty("loom.platform")) {
    val platform = project.property("loom.platform")
    val minecraft = project.property("minecraft_version_min")

    version = "${modVersion}+minecraft-${minecraft}-${platform}"
}

tasks.withType<ProcessResources> {
    inputs.property("version", modVersion)

    filesMatching(listOf("fabric.mod.json", "mods.toml", "neoforge.mods.toml", "quilt.mod.json")) {
        expand(inputs.properties)
    }
}
