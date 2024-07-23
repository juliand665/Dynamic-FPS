import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("dynamic_fps.base")
    id("dynamic_fps.java")
    id("architectury-plugin")
    id("dev.architectury.loom")
}

val libs = the<LibrariesForLibs>()

architectury {
    minecraft = libs.versions.minecraft.get()
}

loom {
    silentMojangMappingsLicense()

    mods.create("dynamic_fps") {
        sourceSet(sourceSets.main.get())
    }
}

repositories {
    maven {
        name = "LostLuma"
        url = uri("https://maven.lostluma.net/releases")
    }
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())

    implementation(libs.battery)
}
