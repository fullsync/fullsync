import com.diffplug.gradle.spotless.SpotlessExtension
import java.time.LocalDate

plugins {
	`java-base`
	`java-library`
	id("com.diffplug.spotless")
	id("jacoco")
}

group = "net.sourceforge.fullsync"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

tasks.withType<Jar>().configureEach {
	val thisYear = LocalDate.now().year
	manifest {
		attributes["License"] = "GPLv2+"
		attributes["FullSync-Version"] = rootProject.version
		attributes["Copyright"] = "Copyright (c) $thisYear the FullSync Team"
	}
}

testing {
	suites {
		withType<JvmTestSuite> {
			val libs = versionCatalogs.named("libs")
			useJUnitJupiter(libs.findVersion("junit").get().requiredVersion)
			dependencies {
				implementation(libs.findLibrary("hamcrest").get())
			}
		}
	}
}

tasks.withType<Test>().configureEach {
	maxParallelForks = 8
}

tasks.withType<JacocoReport>().configureEach {
	dependsOn(tasks.test)
}

configure<SpotlessExtension> {
	java {
		licenseHeaderFile(layout.settingsDirectory.file("resources/License-header.txt"))
		eclipse().configFile(layout.settingsDirectory.file("resources/eclipse-jdt-formatter.xml"))
		removeUnusedImports()
		trimTrailingWhitespace()
		endWithNewline()
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.encoding = "UTF-8"
}
