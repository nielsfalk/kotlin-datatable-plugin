package de.nielsfalk.datatable.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileFilter
import java.nio.file.Files
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText

abstract class DataTablesGeneratorTask : DefaultTask() {
    @get:Input
    abstract val config: ListProperty<DataTablesGeneratorTaskConfigItem>

    @get:Input
    abstract val eachFunctions: ListProperty<String>

    @TaskAction
    fun scanAndGenerate() {
        config.get().forEach { configEntry ->
            val outputDirectory = File(configEntry.outputDirectoryAbsolut)
            outputDirectory.mkdirs()


            val dataClasses = configEntry.srcDirs
                .mapNotNull { File(it).takeIf(File::exists)?.let(File::toPath) }
                .flatMap { path ->
                    Files.walk(path)
                        .filter { it.isRegularFile() && it.toString().endsWith(".kt") }
                        .toList()
                        .flatMap {
                            DataClassData.of(it, it.readText())
                        }
                }.groupByClass()
            val filenames = dataClasses.map { it.generatedFileName }

            outputDirectory.listFiles(FileFilter { it.name.startsWith("___") && it.name.endsWith(".kt") })!!
                .filter { it.name !in filenames }
                .forEach {
                    it.delete()
                }

            dataClasses.forEach { dataClassData ->
                dataClassData.run {
                    outputDirectory.resolve(generatedFileName).writeText(
                        generate(eachFunctionNames = eachFunctions.get())
                    )
                }
            }

            if (configEntry.generateAnnotation){
                outputDirectory.resolve("DataAnnotation.kt").writeText(
                    """
                    package de.nielsfalk.dataTables
    
                    @Target(AnnotationTarget.EXPRESSION)
                    @Retention(AnnotationRetention.SOURCE)
                    annotation class Data(vararg val values: String)
                    """.trimIndent()
                )
            }

            println("✅ Generated: ${outputDirectory.absolutePath}")
        }
    }
}

data class DataTablesGeneratorTaskConfigItem(
    val name: String,
    val outputDirectory: String,
    val outputDirectoryAbsolut: String,
    val srcDirs: List<String>,
    val generateAnnotation: Boolean
)