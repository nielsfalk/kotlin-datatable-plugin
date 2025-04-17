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

    companion object {
        fun of(path: Path, text: String): List<DataClassData> {
            val mapNotNull = readDataClassData(text, path)
            return mapNotNull.groupDuplicates()
        }

        private fun List<DataClassData>.groupDuplicates(): List<DataClassData> {
            groupBy { it.generatedFileName }
            return this
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
