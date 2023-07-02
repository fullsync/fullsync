pluginManagement {
	repositories {
		maven {
			url = uri(extra["offlineRepositoryRoot"] as String)
		}
		mavenCentral()
		gradlePluginPortal()
	}
}

dependencyResolutionManagement {
	versionCatalogs {
		create("libs") {
			library("apache-commons-cli", "commons-cli:commons-cli:1.4")
			library("apache-commons-net", "commons-net:commons-net:3.8.0")
			library("apache-commons-vfs", "org.apache.commons:commons-vfs2:2.8.0")
			library("org-samba-jcifs", "jcifs:jcifs:1.3.17")
			library("com-jcraft-jsch", "com.jcraft:jsch:0.1.55")
			library("org-slf4j-jcl-over-slf4j", "org.slf4j:jcl-over-slf4j:1.7.30")
			library("guice", "com.google.inject:guice:5.1.0")
			library("guice-assistedinject", "com.google.inject.extensions:guice-assistedinject:5.1.0")
			library("junit-jupiter", "org.junit.jupiter:junit-jupiter:5.6.2")
			library("hamcrest", "org.hamcrest:hamcrest:2.2")
			library("slf4j-api", "org.slf4j:slf4j-api:1.7.30")
			library("testcontainers", "org.testcontainers:testcontainers:1.16.0")
			library("testcontainers-junit", "org.testcontainers:junit-jupiter:1.16.0")
		}
	}
}

rootProject.name = "FullSync"
include("fullsync-assets")
include("fullsync-build-utils")
include("fullsync-core")
include("fullsync-ui")
include("fullsync-utils")
include("fullsync-launcher")
include("fullsync-logger")
