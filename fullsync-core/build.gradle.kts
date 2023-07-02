plugins {
	`java-library`
}

dependencies {
	implementation(project(":fullsync-logger"))
	implementation(project(":fullsync-utils"))
	implementation(libs.apache.commons.cli)
	implementation(libs.apache.commons.net)
	implementation(libs.apache.commons.vfs) {
		exclude(group = "commons-logging")
		exclude(group = "org.apache.maven.scm")
		exclude(group = "org.apache.hadoop")
	}
	implementation(libs.org.samba.jcifs)
	implementation(libs.com.jcraft.jsch)
	implementation(libs.org.slf4j.jcl.over.slf4j)
	implementation(libs.guice)
	implementation(libs.guice.assistedinject)
	implementation(files("lib/commons-vfs2-sandbox.jar"))
	testImplementation(libs.junit.jupiter)
	testImplementation(libs.hamcrest)
	testImplementation(libs.testcontainers)
	testImplementation(libs.testcontainers.junit)
}

tasks.named<Test>("test") {
	useJUnitPlatform()
}

tasks.jar {
	manifest {
		attributes("Main-Class" to "net.sourceforge.fullsync.cli.Main")
		attributes("Class-Path" to provider {
			val resolvedRuntime: ResolvedConfiguration = configurations["runtimeClasspath"].resolvedConfiguration
			val jarFiles = resolvedRuntime.resolvedArtifacts.map { a ->
				val group = a.moduleVersion.id.group
				val classifier = if ((a.classifier ?: "").isNotEmpty()) "-${a.classifier}" else ""
				"${group}-${a.name}${classifier}.${a.extension}"
			}
			//FIXME: add back
			//(resolvedRuntime.files - resolvedRuntime.resolvedArtifacts*.file).collect { f ->
			//	jarFiles.add(f.name)
			//}
			jarFiles.joinToString(" ")
		})
	}
}
