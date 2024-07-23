plugins {
    `kotlin-dsl`
}

repositories {
    maven {
        name = "Architectury"
        url = uri("https://maven.architectury.dev")
    }
    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net")
    }
    maven {
        name = "NeoForge"
        url = uri("https://maven.neoforged.net/releases")
    }
    gradlePluginPortal()
}

dependencies {
    implementation(libs.architectury.loom)
    implementation(libs.architectury.plugin)

    // Enable using version catalog in local plugins
    // https://github.com/gradle/gradle/issues/15383
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
