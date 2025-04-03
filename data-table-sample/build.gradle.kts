plugins {
    kotlin("jvm") version "2.1.20"
    id("de.nielsfalk.kotlin.data-table-scanner") version "0.1.0"
}

repositories {
    mavenLocal()
    mavenCentral()
    google()
}

dataTableScanner {
    sourceDirs.set(listOf(layout.projectDirectory.dir("src/main/kotlin")))
}

tasks.named("compileKotlin") {
    dependsOn("scanDataTables")
}