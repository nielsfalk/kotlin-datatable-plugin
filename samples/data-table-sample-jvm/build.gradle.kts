plugins {
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.power-assert") version "2.1.20"
    id("io.github.nielsfalk.datatable") version "0.2.1"
}

group = "io.github.nielsfalk"
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
