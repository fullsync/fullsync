plugins {
	id("fullsyncbuild.library")
}

fun Configuration.addSWTDependency(osName: String, archName: String) {
	var os = ""
	if (osName.contains("windows")) {
		os = "win32.win32"
	}
	else if (osName.contains("linux")) {
		os = "gtk.linux"
	}
	else if (osName.contains("mac")) {
		os = "cocoa.macosx"
	}
	// SWT opts to call AMD64 "x86_64"
	val arch = if (archName == "amd64") "x86_64" else archName
	project.dependencies.add(this.name, "org.eclipse.platform:org.eclipse.swt.${os}.${arch}:${libs.versions.swt.get()}") {
		exclude(group = "org.eclipse.platform", module = "org.eclipse.swt")
	}
}

fun Configuration.addLocalSWTVersion() {
	val properties = System.getProperties()
	return this.addSWTDependency(
		properties.getProperty("os.name").lowercase(),
		properties.getProperty("os.arch").lowercase(),
	)
}

val dist by configurations.creating
dist.extendsFrom(configurations.runtimeClasspath.get())

dependencies {
	implementation(project(":fullsync-core"))
	implementation(project(":fullsync-utils"))
	implementation(project(":fullsync-assets"))
	implementation(libs.guice)
	implementation(libs.guice.assistedInject)
	implementation(libs.jcl.over.slf4j)
	implementation(libs.commons.vfs) {
		exclude(group = "commons-logging")
		exclude(group = "org.apache.maven.scm")
		exclude(group = "org.apache.hadoop")
	}
	dist(project(":fullsync-ui"))
}

dist.addSWTDependency("linux", "x86_64")
dist.addSWTDependency("mac", "aarch64")
dist.addSWTDependency("windows", "x86_64")
configurations.implementation.get().addLocalSWTVersion()

tasks.register<JavaExec>("run") {
	jvmArgs = listOf("-XstartOnFirstThread")
	mainClass = "net.sourceforge.fullsync.ui.GuiMain"
	classpath = sourceSets.main.get().runtimeClasspath + configurations.runtimeClasspath.get()
}
