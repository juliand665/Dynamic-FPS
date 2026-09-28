repositories {
	exclusiveContent {
		forRepository {
			maven {
				name = "LostLuma Mirrors"
				url = uri("https://maven.lostluma.net/mirrors")
			}
		}
		filter {
			includeGroup("com.terraformersmc")
			includeGroup("me.shedaniel.cloth")
		}
	}
	exclusiveContent {
		forRepository {
			maven {
				name = "LostLuma Releases"
				url = uri("https://maven.lostluma.net/releases")
			}
		}
		filter {
			includeGroup("net.lostluma")
		}
	}
}
