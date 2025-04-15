package de.nielsfalk.datatable.plugin

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
        private fun extractDataClassDataBlocks(text: String): List<String> {
            val dataAnnotationRegex = Regex("(?m)^.*@Data\\(.*$")
            val found = dataAnnotationRegex.findAll(text).map { it.groupValues[0] }
                .toList()
            val parts = text.split(regex = dataAnnotationRegex).drop(1)
            return found.zip(parts)
                .map { (first, second) -> first + second }
                .mapNotNull { part ->
                    val indexOpeningBrace = part.indexOf("{")
                    var braceDepth = 0
                    for (i in indexOpeningBrace until part.length) {
                        when (part[i]) {
                            '{' -> braceDepth++
                            '}' -> braceDepth--
                        }
                        if (braceDepth == 0) {
                            return@mapNotNull part.substring(0, i)
                        }
                    }
                    null
                }
        }

        private fun String.splitToPair(delimiter: String): Pair<String, String>? {
            val split = split(delimiter, limit = 2)
            return if (split.size >= 2) return split[0] to split[1]
            else null
        }

        fun of(path: Path, text: String): List<DataClassData> =
            if (text.contains("@Data") && text.contains("import de.nielsfalk.dataTables.Data")) {
                val packageString = text.lineSequence().firstOrNull { it.startsWith("package") }
                    ?.removePrefix("package")?.trim()

                extractDataClassDataBlocks(text).mapNotNull {
                    it.splitToPair("@Data(")?.let { (_, afterAnnotation) ->
                        afterAnnotation.splitToPair(")")
                            ?.let { (parameterString, afterParameterString) ->
                                afterParameterString.split(
                                    delimiters = arrayOf("<", "{"),
                                    limit = 2
                                ).firstOrNull()
                                    ?.let { dataClassName ->
                                        val parameterNames =
                                            parameterString.split(",").map { it.replace('"', ' ').trim() }
                                        DataClassData(
                                            dataClassName = dataClassName.trim(),
                                            parameterNames = parameterNames,
                                            lineParameterCount = parameterNames.size,
                                            path = path.toString(),
                                            packageString = packageString,
                                        )
                                    }
                            }
                    }
                }.groupDuplicats()
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
