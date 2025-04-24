package de.nielsfalk.datatable.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import java.io.File


abstract class DataTablePlugin : Plugin<Project> {

    override fun apply(project: Project) {
        val extension =
            project.extensions.create("dataTableScanner", DataTableScannerExtension::class.java)

        project.tasks.register("formatDataTables", FormatDataTablesTask::class.java) {
            project.withSourceSets { sourceDir ->
                config.set(
                    sourceDir.getFilteredSourceSets(extension).flatMap { sourceSet ->
                        sourceSet.srcDirs.map { it.absolutePath }
                            .filter { it.endsWith("/kotlin") }
                    }
                )
            }
        }

        val taskProvider =
            project.tasks.register("scanDataTables", ScanDataTablesTask::class.java) {
                project.withSourceSets { sourceDir ->
                    config.set(
                        sourceDir.getFilteredSourceSets(extension)
                            .map { sourceSet ->
                                val outputDir = "generated/dataTable-${sourceSet.name}"
                                ScanDataTablesTaskConfigItem(
                                    name = sourceSet.name,
                                    srcDirs = sourceSet.srcDirs.map { it.absolutePath }
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
            taskProvider.get().config.get().forEach { taskConfig ->
                project.withSourceSets { sourceDir ->
                    sourceDir.firstOrNull { it.name == taskConfig.name }
                        ?.addSrcDirs(project.layout.buildDirectory.dir(taskConfig.outputDirectory))

                }
            }
        }
    }

    interface SourceSetWrapper {
        val name: String
        val srcDirs: Set<File>
        fun addSrcDirs(vararg srcPaths: Provider<Directory>)
    }

    fun Project.withSourceSets(function: (List<SourceSetWrapper>) -> Unit) {
        var foundSourceDirs = false
        project.extensions.findByType(KotlinMultiplatformExtension::class.java)?.apply {
            sourceSets
                .takeIf { it.isNotEmpty() }
                ?.map {
                    foundSourceDirs = true
                    object : SourceSetWrapper {
                        override val name: String
                            get() = it.name

                        override val srcDirs: Set<File>
                            get() = it.kotlin.srcDirs

                        override fun addSrcDirs(vararg srcPaths: Provider<Directory>) {
                            it.kotlin.srcDirs(srcPaths)
                        }
                    }
                }
                ?.let (function)
        }
        project.extensions.findByType(JavaPluginExtension::class.java)?.apply {
            sourceSets
                .takeIf { it.isNotEmpty() }
                ?.map {
                    foundSourceDirs = true
                    object : SourceSetWrapper {
                        override val name: String
                            get() = it.name
                        override val srcDirs: Set<File>
                            get() = it.allSource.srcDirs

                        override fun addSrcDirs(vararg srcPaths: Provider<Directory>) {
                            it.java.srcDirs(srcPaths)
                        }
                    }
                }
                ?.let(function)
        }
        if (!foundSourceDirs) {
            println("⚠️no sourceSets found")
        }
    }

    private fun List<SourceSetWrapper>.getFilteredSourceSets(
        extension: DataTableScannerExtension
    ): List<SourceSetWrapper> {
        val testSourcesOnly = extension.testSourcesOnly.get()
        val sourceSetNames = extension.sourceSets.get()
            .takeIf { it.isNotEmpty() }
            ?: listOfNotNull(
                if (testSourcesOnly) null else "main",
                if (testSourcesOnly) null else "commonMain",
                "test",
                "commonTest"
            )

        return filter { it.name in sourceSetNames }
            .also {
                if (it.isEmpty()) {
                    println(
                        "⚠️no sourceSets with names $sourceSetNames found in ${
                            map(
                                SourceSetWrapper::name
                            )
                        }"
                    )
                }
            }
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