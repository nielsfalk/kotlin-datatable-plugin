package de.nielsfalk.kotlin.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property


abstract class DataTableScannerPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension =
            project.extensions.create("dataTableScanner", DataTableScannerExtension::class.java)

        val taskProvider =
            project.tasks.register("scanDataTables", ScanDataTablesTask::class.java) {
                project.extensions.findByType(JavaPluginExtension::class.java)?.apply {
                    val testSourcesOnly = extension.testSourcesOnly.get()
                    val sourceSetNames = extension.sourceSets.get()
                        .takeIf { it.isNotEmpty() }
                        ?: listOfNotNull(
                            if (testSourcesOnly) null else "main",
                            if (testSourcesOnly) null else "commonMain",
                            "test",
                            "commonTest"
                        )
                    config.set(
                        sourceSets.filter { it.name in sourceSetNames }
                            .map { sourceSet ->
                                val outputDir = "generated/dataTable-${sourceSet.name}"
                                ScanDataTablesTaskConfigItem(
                                    name = sourceSet.name,
                                    srcDirs = sourceSet.allSource.srcDirs.map { it.absolutePath }
                                        .filter { it.endsWith("/kotlin") },
                                    outputDirectory = outputDir,
                                    outputDirectoryAbsolut = project.layout.buildDirectory.dir(outputDir)
                                        .get().asFile.also {
                                            it.mkdirs()
                                        }
                                        .absolutePath,
                                )
                            }
                            .filter { it.srcDirs.isNotEmpty() }
                    )
                }
            }

        project.afterEvaluate {
            project.extensions.findByType(JavaPluginExtension::class.java)?.apply {
                taskProvider.get().config.get().forEach {
                    sourceSets.getByName(it.name).java.srcDirs(project.layout.buildDirectory.dir(it.outputDirectory))
                }
            }
        }
    }
}

abstract class DataTableScannerExtension {
    abstract val testSourcesOnly: Property<Boolean>
    abstract val addGeneratedSourcesToSourceSet: Property<Boolean>
    abstract val sourceSets: ListProperty<String>

    init {
        testSourcesOnly.convention(false)
        addGeneratedSourcesToSourceSet.convention(true)
        sourceSets.convention(listOf())
    }
}