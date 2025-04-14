plugins {
    kotlin("jvm") version "2.1.20"
    id("de.nielsfalk.kotlin.data-table-scanner") version "0.1.0"
}

repositories {
    mavenLocal()
    mavenCentral()
    google()
}

tasks.named("compileKotlin") {
    dependsOn("scanDataTables")
}

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.0")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}