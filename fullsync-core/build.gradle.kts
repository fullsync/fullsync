plugins {
	id("fullsyncbuild.library")
}

dependencies {
	implementation(project(":fullsync-logger"))
	implementation(project(":fullsync-utils"))
	implementation(libs.commons.cli)
	implementation(libs.commons.net)
	implementation(libs.commons.vfs) {
		exclude(group = "commons-logging")
		exclude(group = "org.apache.maven.scm")
		exclude(group = "org.apache.hadoop")
	}
	implementation(libs.jcifs)
	implementation(libs.jsch)
	implementation(libs.guice)
	implementation(libs.guice.assistedInject)
	implementation(files("lib/commons-vfs2-sandbox.jar"))
	testImplementation(libs.junit.jupiter)
	testImplementation(libs.hamcrest)
	testImplementation(libs.testcontainers)
	testImplementation(libs.testcontainers.junit)
}

fun ResolvedArtifact.mapArtifactToFilename(): String {
	val group = moduleVersion.id.group
	var classifier = classifier ?: ""
	if (classifier.isNotEmpty()) {
		classifier = "-${classifier}"
	}
	return "${group}-${name}${classifier}.${extension}"
}

tasks.jar {
	manifest {
		attributes(
			"Main-Class" to "net.sourceforge.fullsync.cli.Main",
			"Class-Path" to providers.provider {
				val runtimeConfiguration = configurations.runtimeClasspath.get()
				val resolvedRuntime = runtimeConfiguration.resolvedConfiguration
				val jarFiles = resolvedRuntime.resolvedArtifacts.map { a -> a.mapArtifactToFilename() }
				return@provider jarFiles.joinToString(" ")
			},
		)
	}
}
