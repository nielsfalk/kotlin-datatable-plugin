plugins {
    `kotlin-dsl`
    `java-library`
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.3.1"
    kotlin("jvm") version "1.9.23" // Match your Kotlin version
}

group = "de.nielsfalk.kotlin"
version = "0.1.0"

gradlePlugin {
    // Shown on the plugin portal
    website = "https://github.com/nielsfalk/kotlin-datatable-plugin"
    vcsUrl = "https://github.com/nielsfalk/kotlin-datatable-plugin.git"

    plugins {
        create("dataTables") {
            id = "de.nielsfalk.kotlin.datatable"
            implementationClass = "de.nielsfalk.datatable.plugin.DataTablePlugin"
            displayName = "Kotlin Data Table Plugin"
            description = "Scans Kotlin files for data-tables and generate the code so this feature can be use."
            tags.set(
                listOf(
                    "kotlin",
                    "codegen",
                    "datatables",
                    "spock",
                    "test",
                    "parameterized test"
                )
            )
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
    implementation(kotlin("gradle-plugin", version = "1.9.23"))
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.0")
    testImplementation(kotlin("test-junit5"))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}