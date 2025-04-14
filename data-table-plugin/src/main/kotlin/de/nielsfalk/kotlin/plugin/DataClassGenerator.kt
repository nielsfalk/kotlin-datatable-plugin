package de.nielsfalk.kotlin.plugin

fun DataClassData.generate(): String {
    val constructorProperties =
        parameter.joinToString(",") { it.run { "val $name: T$index" } }
    val outGenericTypes = parameter.takeIf(List<Parameter>::isNotEmpty)
        ?.joinToString(
            prefix = "<",
            separator = ",",
            postfix = ">"
        ) { it.run { "out T$index" } }
        ?: ""
    val genericTypes = parameter.takeIf(List<Parameter>::isNotEmpty)
        ?.joinToString(
            prefix = "<",
            separator = ",",
            postfix = ">"
        ) { it.run { "T$index" } }
        ?: ""

    val concatMethods = if (parameter.size >= 2) {
        listOf(singleDataSeperator, doubleDataSeperator)
            .joinToString(prefix = "\n", separator = "\n\n") { operator ->
                buildString {
                    if (parameter.size == 2)
                        append(
                            """
                            |            @JvmName("context1_${operator.length}")
                            |            infix fun T0.$operator(next: T1) {
                            |                _values += $dataClassName(this, next)
                            |            }
                            """.trimMargin()
                        )
                    else {
                        append(
                            """
                            |            @JvmName("context1_${operator.length}")
                            |            infix fun T0.$operator(next: T1) =
                            |                this to next
                            """.trimMargin()
                        )
                        (2 until parameter.size).map { i ->
                            val receiverType = buildString {
                                append((2..i).joinToString(separator = "<") { "Pair" })
                                append("<T0, T1>")
                                append((2 until i).joinToString(separator = "") { ", T$it>" })
                            }
                            append("\n\n")


                            append(
                                if (i == parameter.size - 1) {
                                    val firstParameters = mutableListOf(
                                        (1 until i).joinToString(separator = ".") { "first" }
                                    )
                                    if (parameter.size > 3) {
                                        (0..i - 3).reversed().forEach {
                                            firstParameters.add(
                                                (0..it).joinToString(
                                                    separator = ".",
                                                    postfix = ".second"
                                                ) { "first" }
                                            )
                                        }
                                    }

                                    val firstParameterString = firstParameters.joinToString(", ")
                                    """
                                    |            @JvmName("context${i}_${operator.length}")
                                    |            infix fun $receiverType.$operator(next: T$i) {
                                    |               _values += $dataClassName($firstParameterString, second, next)   
                                    |            }
                                    """.trimMargin()
                                } else
                                    """
                                    |            @JvmName("context${i}_${operator.length}")
                                    |            infix fun $receiverType.$operator(next: T$i) =
                                    |               this to next
                                    """.trimMargin()
                            )
                        }
                    }
                }

            }
    } else ""

    val result = """
        ${packageString?.let { "package $it" } ?: ""}

        data class $dataClassName$outGenericTypes($constructorProperties){
            companion object {
                fun $genericTypes data(function: ${dataClassName}Context$genericTypes.() -> Unit): List<$dataClassName$genericTypes> =
                    ${dataClassName}Context$genericTypes()
                        .apply(function)
                        .values
            }
        }
        
        class ${dataClassName}Context$genericTypes {
            private val _values = mutableListOf<$dataClassName$genericTypes>()
            val values: List<$dataClassName$genericTypes>
                get() = _values.toList()
            $concatMethods
        }
        """.trimIndent()
    return result
}
