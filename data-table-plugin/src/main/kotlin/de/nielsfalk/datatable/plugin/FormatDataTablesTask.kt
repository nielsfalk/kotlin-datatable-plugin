package de.nielsfalk.datatable.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Files
import kotlin.io.path.absolutePathString
import kotlin.io.path.isRegularFile
import kotlin.io.path.readText
import kotlin.io.path.writeText

abstract class FormatDataTablesTask : DefaultTask() {
    @get:Input
    abstract val config: ListProperty<String>

    @TaskAction
    fun formatDataTables() {
        config.get().flatMap {
            File(it)
                .takeIf(File::exists)
                ?.let(File::toPath)
                ?.let {
                    Files.walk(it)
                        .filter { it.isRegularFile() && it.toString().endsWith(".kt") }
                        .toList()
                }
                ?: listOf()
        }.forEach { file ->
            val fileContent = file.readText()
            if (fileContent.contains("import de.nielsfalk.dataTables.Data\n")) {
                val newContent = format(fileContent)
                if (newContent != fileContent) {
                    file.writeText(newContent)
                    println("✅ formatted ${file.absolutePathString()}")
                }
            }
        }
    }
}