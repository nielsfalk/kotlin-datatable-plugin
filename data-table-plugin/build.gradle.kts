plugins {
    `kotlin-dsl`
    `java-library`
    `java-gradle-plugin`
    `maven-publish`
    id("com.gradle.plugin-publish") version "1.3.1"
    kotlin("jvm") version "2.1.20" // Match your Kotlin version
    kotlin("plugin.power-assert") version "2.1.20"
}

group = "io.github.nielsfalk"
version = "0.2.1"

gradlePlugin {
    // Shown on the plugin portal
    website = "https://github.com/nielsfalk/kotlin-datatable-plugin"
    vcsUrl = "https://github.com/nielsfalk/kotlin-datatable-plugin.git"

    plugins {
        create("dataTables") {
            id = "io.github.nielsfalk.datatable"
            implementationClass = "de.nielsfalk.datatable.plugin.DataTablePlugin"
            displayName = "Kotlin Data Table Plugin"
            description = "Scans Kotlin files for data-tables and generate the code so this feature can be used."
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
    implementation(kotlin("gradle-plugin", version = "2.1.20"))
    testImplementation("io.kotest:kotest-runner-junit5:6.2.3")
    testImplementation("io.kotest:kotest-assertions-core:6.2.3")
    testImplementation("org.junit.platform:junit-platform-launcher:1.13.4")
    testImplementation(kotlin("test-junit5"))
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
