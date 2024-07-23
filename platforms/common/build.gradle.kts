plugins {
	id("dynamic_fps.module")
}

val platforms = project.property("enabled_platforms").toString()

architectury {
	common(platforms.split(","))
}

loom {
	accessWidenerPath = file("src/main/resources/dynamic_fps.accesswidener")
}

dependencies {
    modImplementation(libs.cloth.config)
    // Note: This is only here for the @Environment annotation, do not use!
	modImplementation(libs.fabric.loader)
}
