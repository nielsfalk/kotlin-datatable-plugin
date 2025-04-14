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