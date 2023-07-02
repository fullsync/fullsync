plugins {
	`java-library`
}

dependencies {
	testImplementation(libs.junit.jupiter)
	testImplementation(libs.hamcrest)
}

tasks.named<Test>("test") {
	useJUnitPlatform()
}
