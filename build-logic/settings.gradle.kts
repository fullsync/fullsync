include("library")

rootProject.name = "build-logic"

dependencyResolutionManagement {
	repositories {
		maven {
			url = uri("../offline-repository")
		}
		mavenCentral()
		gradlePluginPortal()

	}
	versionCatalogs {
		create("libs") {
			from(files("../gradle/libs.versions.toml"))
		}
	}
}
