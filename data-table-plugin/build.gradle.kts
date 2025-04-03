plugins {
    `kotlin-dsl`
    `java-library`
    `java-gradle-plugin`
    `maven-publish`
    kotlin("jvm") version "1.9.23" // Match your Kotlin version
}

group = "de.nielsfalk.kotlin"
version = "0.1.0"

gradlePlugin {
    plugins {
        create("dataTableScanner") {
            id = "de.nielsfalk.kotlin.data-table-scanner"
            implementationClass = "de.nielsfalk.kotlin.plugin.DataTableScannerPlugin"
            displayName = "Data Table Scanner Plugin"
            description = "Scans Kotlin files for @DataTable and generates a map"
        }
    }
}

publishing {
    repositories {
        mavenLocal()
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.0")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}