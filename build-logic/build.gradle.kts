plugins {
	`kotlin-dsl`
}

repositories {
	gradlePluginPortal()
}

dependencies {
	// Enable using version catalog in local plugins
	// https://github.com/gradle/gradle/issues/15383
	implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

kotlin {
	jvmToolchain(25)
}
