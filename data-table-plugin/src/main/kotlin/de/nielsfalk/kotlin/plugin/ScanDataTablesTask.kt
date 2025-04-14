package de.nielsfalk.kotlin.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.FileFilter
import java.nio.file.Files
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText

abstract class ScanDataTablesTask : DefaultTask() {
    @get:Input
    abstract val config: ListProperty<ScanDataTablesTaskConfigItem>

    @TaskAction
    fun scanAndGenerate() {
        config.get().forEach {
            val genDir = File(it.outputDirAbsolut)
            genDir.mkdirs()


            val dataClasses = it.srcDirs.flatMap { dir ->
                val toPath = File(dir).toPath()
                Files.walk(toPath)
                    .filter { it.isRegularFile() && it.toString().endsWith(".kt") }
                    .toList()
                    .flatMap { path ->
                        DataClassData.of(path, path.readText())
                    }
            }.groupByClass()
            val filenames = dataClasses.map { it.generatedFileName }

            genDir.listFiles(FileFilter { it.name.startsWith("___") && it.name.endsWith(".kt") })!!
                .filter { it.name !in filenames }
                .forEach {
                    it.delete()
                }

            dataClasses.forEach { dataClassData ->
                dataClassData.run {
                    genDir.resolve(generatedFileName).writeText(
                        generate()
                    )
                }
            }


            genDir.resolve("DataTablesAnnotation.kt").writeText(
                """
                package de.nielsfalk.dataTables

                @Target(AnnotationTarget.EXPRESSION)
                @Retention(AnnotationRetention.SOURCE)
                annotation class DataTable(vararg val values: String)
            """.trimIndent()
            )

            println("✅ Generated: ${genDir.absolutePath}")
        }
    }
}

data class ScanDataTablesTaskConfigItem(
    val name: String,
    val outputDir: String,
    val outputDirAbsolut: String,
    val srcDirs: List<String>
)