plugins {
	id("fullsyncbuild.library")
}

val dist: Configuration by configurations.creating

dependencies {
	dist(project(":fullsync-launcher"))
}

tasks.jar {
	manifest {
		attributes("Main-Class" to "net.sourceforge.fullsync.launcher.WindowsLauncher")
	}
}
