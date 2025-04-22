package de.nielsfalk.datatable.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.SourceSet


abstract class DataTableScannerPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension =
            project.extensions.create("dataTableScanner", DataTableScannerExtension::class.java)

        project.tasks.register("formatDataTables", FormatDataTablesTask::class.java) {
            project.extensions.findByType(JavaPluginExtension::class.java)?.apply {
                config.set(
                    getFilteredSourceSets(extension).flatMap {
                        it.allSource.srcDirs.map { it.absolutePath }
                            .filter { it.endsWith("/kotlin") }
                    }
                )
            }
        }
        val taskProvider =
            project.tasks.register("scanDataTables", ScanDataTablesTask::class.java) {
                project.extensions.findByType(JavaPluginExtension::class.java)?.apply {
                    config.set(
                        getFilteredSourceSets(extension)
                            .map { sourceSet ->
                                val outputDir = "generated/dataTable-${sourceSet.name}"
                                ScanDataTablesTaskConfigItem(
                                    name = sourceSet.name,
                                    srcDirs = sourceSet.allSource.srcDirs.map { it.absolutePath }
                                        .filter { it.endsWith("/kotlin") },
                                    outputDirectory = outputDir,
                                    outputDirectoryAbsolut = project.layout.buildDirectory.dir(
                                        outputDir
                                    )
                                        .get().asFile.also {
                                            it.mkdirs()
                                        }
                                        .absolutePath,
                                )
                            }
                            .filter { it.srcDirs.isNotEmpty() }
                    )
                    eachFunctions.set(extension.eachFunctions.get())
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

    private fun JavaPluginExtension.getFilteredSourceSets(
        extension: DataTableScannerExtension
    ): List<SourceSet> {
        val testSourcesOnly = extension.testSourcesOnly.get()
        val sourceSetNames = extension.sourceSets.get()
            .takeIf { it.isNotEmpty() }
            ?: listOfNotNull(
                if (testSourcesOnly) null else "main",
                if (testSourcesOnly) null else "commonMain",
                "test",
                "commonTest"
            )
        return sourceSets.filter { it.name in sourceSetNames }
    }
}

abstract class DataTableScannerExtension {
    abstract val testSourcesOnly: Property<Boolean>
    abstract val addGeneratedSourcesToSourceSet: Property<Boolean>
    abstract val sourceSets: ListProperty<String>
    abstract val eachFunctions: ListProperty<String>

    init {
        testSourcesOnly.convention(false)
        addGeneratedSourcesToSourceSet.convention(true)
        sourceSets.convention(listOf())
        eachFunctions.convention(listOf("each"))
    }
}