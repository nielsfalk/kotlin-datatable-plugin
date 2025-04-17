package de.nielsfalk.datatable.plugin

import java.nio.file.Path

fun extractDataClassDataBlocks(text: String): List<String> =
    if (text.contains("\nimport de.nielsfalk.dataTables.Data\n")) {
        val dataAnnotationRegex = Regex("(?m)^.*@Data\\(.*$")
        val found = dataAnnotationRegex.findAll(text).map { it.groupValues[0] }
            .toList()
        val parts = text.split(regex = dataAnnotationRegex).drop(1)
        found.zip(parts)
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
    }else  listOf()

fun String?.splitToPair(delimiter: String): Pair<String?, String?> =
    if (this == null)
        null to null
    else {
        val split = split(delimiter, limit = 2)
        when (split.size) {
            1 -> split[0] to null
            0 -> null to null
            else -> split[0] to split[1]
        }
    }

fun readDataClassData(
    text: String,
    path: Path
): List<DataClassData> {
    val packageString = text.lineSequence().firstOrNull { it.startsWith("package") }
        ?.removePrefix("package")?.trim()

    return extractDataClassDataBlocks(text).mapNotNull { block ->
        val (_, afterAnnotation) = block.splitToPair("@Data(")


        val (parameterString, afterParameterString) = afterAnnotation.splitToPair(")")
        if (parameterString == null){
            null
        }else{
            afterParameterString?.split(
                delimiters = arrayOf("<", "{"),
                limit = 2
            )?.firstOrNull()
                ?.let { dataClassName ->
                    val parameterNames =
                        parameterString.split(",")
                            .map { it.replace('"', ' ').trim() }
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
}
