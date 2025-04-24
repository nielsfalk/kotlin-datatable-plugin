plugins {
    kotlin("jvm") version "2.1.20"
    id("de.nielsfalk.kotlin.datatable") version "0.1.0"
}

group = "de.nielsfalk.kotlin.data-tables.sample"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

tasks.named("compileKotlin") {
    dependsOn("generateDataTables")
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-framework-datatest:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.0")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}