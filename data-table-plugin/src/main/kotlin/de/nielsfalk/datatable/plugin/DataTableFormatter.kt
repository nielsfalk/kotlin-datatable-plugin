package de.nielsfalk.datatable.plugin

import de.nielsfalk.datatable.plugin.LineType.*

fun format(fileContent: String): String {
    var newFileContent = fileContent
    extractDataClassDataBlocks(fileContent).map {
        val dataTableFormated = DataTableFormated(it)

        newFileContent= newFileContent.replace(dataTableFormated.original, dataTableFormated.output)
    }
    return newFileContent
}

private data class DataTableFormated(
    val original: String
) {
    val lines: List<Line> by lazy {
        val lineIterator = original.lines().iterator()
        val lines = mutableListOf<Line>()
        if (lineIterator.hasNext()) {
            val firstLine = lineIterator.next()
            val startDelimiter = "@Data("
            val (prefixStart, rest) = firstLine.splitToPair(startDelimiter)
            val (parameterNames, postfix) = rest.splitToPair(")")
            lines += Line(
                originalContent = firstLine,
                type = Header,
                prefix = "$prefixStart$startDelimiter",
                postfix = ")$postfix",
                columns = parameterNames.splitKeepingDelimiters(",")
                    .map(String::trim),
                delimiter = ","
            )

            if ((postfix == null || !postfix.contains("{")) && lineIterator.hasNext()) {
                val typeLine = lineIterator.next()
                if (typeLine.contains("<") && typeLine.contains(">")) {
                    val (prefix, restOfTypeLine) = typeLine.splitToPair("<")
                    val (types, postfixTypeLine) = restOfTypeLine.splitToPair(">")
                    lines += Line(
                        originalContent = typeLine,
                        type = Types,
                        prefix = "$prefix<",
                        postfix = ">$postfixTypeLine",
                        columns = types.splitKeepingDelimiters(",")
                            .map(String::trim),
                        delimiter = ","
                    )
                } else {
                    lines += Line(
                        originalContent = typeLine,
                        type = Types,
                        prefix = typeLine
                    )
                }
            }
        }
        while (lineIterator.hasNext()) {
            val line = lineIterator.next()
            if (line.contains(singleDataSeperator) || line.contains(doubleDataSeperator)) {
                lines += Line(
                    originalContent = line,
                    type = Row,
                    columns = line.splitKeepingDelimiters(doubleDataSeperator, singleDataSeperator)
                        .map(String::trim),
                    delimiter = singleDataSeperator
                )
            } else if (!lineIterator.hasNext() && line.contains("}")) {
                lines += Line(
                    originalContent = line,
                    type = Closing,
                    prefix = line
                )
            } else {
                lines += unknownLine(line)
            }
        }
        lines
    }


    val output by lazy {
        val longestPrefixSize = lines.maxOf {
            if (it.type == Unknown || it.type == Closing) 0
            else it.prefix?.length ?: 0
        }
        val maxColumnCount = lines.maxOf { it.columns?.size ?: 0 }
        val lines = lines.map { line ->
            if (line.columns.isNullOrEmpty() || line.columns.size >= maxColumnCount) {
                line
            } else {
                line.copy(
                    columns = line.columns + (line.columns.size until maxColumnCount).map {
                        if (it % 2 == 1) line.delimiter ?: ""
                        else {
                            when (line.type) {
                                Header -> """"col${(it/2)+1}""""
                                Row -> "null"
                                Types -> "Any"
                                else -> ""
                            }
                        }
                    }
                )
            }
        }

        val columnsLength = (0 until maxColumnCount).map { colIndex ->
            lines.maxOf { if (it.columns == null) 0 else it.columns[colIndex].length }
        }

        val paddedLines = lines.map { line ->
            line.copy(
                prefix = (line.prefix?:"").padEnd(longestPrefixSize),
                columns = line.columns?.mapIndexed{i,column->
                    column.padEnd(columnsLength[i])
                }
            )
        }


        paddedLines.joinToString(separator = "\n"){
            it.run { "$prefix${columns?.joinToString(separator = " ")?:""}${postfix?:""}".trimEnd()}
        }
    }
}

private fun unknownLine(typeLine: String) = Line(
    originalContent = typeLine,
    type = Unknown,
    prefix = typeLine
)

private data class Line(
    val originalContent: String,
    val type: LineType,
    val prefix: String? = null,
    val postfix: String? = null,
    val columns: List<String>? = null,
    val delimiter: String? = null
)

private enum class LineType {
    Header, Types, Row, Closing, Unknown
}