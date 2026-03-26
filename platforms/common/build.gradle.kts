plugins {
	id("dynamic_fps.base")
	id("dynamic_fps.java")
	id("dynamic_fps.common")
	alias(libs.plugins.loom)
}

loom {
	accessWidenerPath = file("src/main/resources/dynamic_fps.accesswidener")
}

dependencies {
	minecraft(libs.minecraft)

	implementation(libs.battery)
	implementation(libs.cloth.config) { isTransitive = false }

	// Note: This is only here for the @Environment annotation, do not use!
	compileOnly(libs.fabric.loader)
}
