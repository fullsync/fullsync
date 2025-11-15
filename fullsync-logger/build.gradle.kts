plugins {
	id("fullsyncbuild.library")
}

dependencies {
	api(libs.slf4j.api)
	api(libs.jcl.over.slf4j)

	runtimeOnly(libs.slf4j.simple)
}
