package de.nielsfalk.kotlin.plugin

import java.nio.file.Path

data class DataClassData(
    val dataClassName: String,
    val parameterNames: List<String>?,
    val lineParameterCount: Int?,
    val path: String,
    val packageString: String?,
    val generatedFileName: String = """___${
        packageString?.replace(
            '.',
            '_'
        ) ?: ""
    }___$dataClassName.kt"""
) {
    companion object {
        fun of(path: Path, text: String): List<DataClassData> =
            if (text.contains("@Data") && text.contains("import de.nielsfalk.dataTables.Data")) {
                val packageString = text.lineSequence().firstOrNull { it.startsWith("package") }
                    ?.removePrefix("package")?.trim()
                text.split("@Data")
                    .drop(1)
                    .mapNotNull { contantAfterAnnotation ->
                        val lineIterator = contantAfterAnnotation.lineSequence().iterator()
                        val parameterNames = if (lineIterator.hasNext()) {
                            Regex("\"(.*?)\"")
                                .findAll(lineIterator.next())
                                .map { it.groupValues[1] }
                                .toList()
                        } else null
                        val dataClassName = if (lineIterator.hasNext()) {
                            val typeParts =
                                lineIterator.next().split(delimiters = arrayOf("<", "{"), limit = 2)
                                    .iterator()
                            val dataClassName =
                                if (typeParts.hasNext()) typeParts.next().trim() else null
                            dataClassName
                        } else null
                        val lineParameterCount = lineIterator.nextParameterCount()
                            .takeIf {
                                while (lineIterator.hasNext()) {
                                    when (lineIterator.nextParameterCount()) {
                                        null -> return@takeIf true
                                        it -> {}
                                        else -> return@takeIf false
                                    }
                                }
                                true
                            }
                        dataClassName?.let {
                            DataClassData(
                                dataClassName = it,
                                parameterNames = parameterNames,
                                lineParameterCount = lineParameterCount,
                                path = path.toString(),
                                packageString = packageString,
                            )
                        }
                    }
                    .groupDuplicats()
            } else listOf()

        private fun List<DataClassData>.groupDuplicats(): List<DataClassData> {
            groupBy { it.generatedFileName }
            return this
        }
    }

    val parameter: List<Parameter> by lazy {
        val parameterCount = parameterNames?.count()
            ?: lineParameterCount
            ?: 0
        (0 until parameterCount).map {
            Parameter(
                name = parameterNames?.getOrNull(it) ?: "v$it",
                index = it,
            )
        }
    }
}

data class Parameter(
    val name: String,
    val index: Int,
)


private fun Iterator<String>.nextParameterCount(): Int? {
    while (hasNext()) {
        val line = next()
        if (line.isNotBlank()) {
            return if (line.contains(singleDataSeperator))
                line.split(doubleDataSeperator).flatMap { it.split(singleDataSeperator) }.count()
            else {
                null
            }
        }
    }
    return null
}


fun List<DataClassData>.groupByClass(): List<DataClassData> =
    groupBy { it.generatedFileName }
        .values
        .map { list ->
            list.maxBy {
                if (it.parameterNames == null) 0 else 2 +
                        if (it.lineParameterCount == null) 0 else 1
            }
        }

const val singleDataSeperator = "ǀ"
const val doubleDataSeperator = "ǀǀ"
