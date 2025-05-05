plugins {
	`java-library`
	`kotlin-dsl`
}

dependencies {
	api("com.diffplug.spotless:spotless-plugin-gradle:${libs.versions.spotless.get()}")
}
