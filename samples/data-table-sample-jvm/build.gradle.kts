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
    testImplementation("io.kotest:kotest-runner-junit5:6.2.3")
    testImplementation("io.kotest:kotest-assertions-core:6.2.3")
    testImplementation("org.junit.platform:junit-platform-launcher:6.1.2")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
