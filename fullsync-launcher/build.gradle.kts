plugins {
	`java-library`
}
configurations {
	create("dist")
}

dependencies {
	"dist"(project(":fullsync-launcher"))
}

tasks.jar {
	manifest {
		attributes("Main-Class" to "net.sourceforge.fullsync.launcher.WindowsLauncher")
	}
}
