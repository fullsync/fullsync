pluginManagement {
	includeBuild("build-logic")
	// TODO: move this to the build-logic project?
	repositories {
		maven {
			url = uri("offline-repository")
		}
		mavenCentral()
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
	repositories {
		maven {
			url = uri("offline-repository")
		}
		mavenCentral()
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

rootProject.name = "FullSync"
include("fullsync-assets")
include("fullsync-build-utils")
include("fullsync-core")
include("fullsync-ui")
include("fullsync-utils")
include("fullsync-launcher")
include("fullsync-logger")
