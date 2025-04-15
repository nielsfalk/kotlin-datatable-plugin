package de.nielsfalk.datatable.plugin

fun DataClassData.generate(eachFunctionNames: List<String> = listOf("each")): String {
    val constructorProperties =
        parameter.joinToString(",") { it.run { "val $name: T$index" } }
    val outGenericTypes = parameter.takeIf(List<Parameter>::isNotEmpty)
        ?.joinToString(
            prefix = "<",
            separator = ",",
            postfix = ">"
        ) { it.run { "out T$index" } }
        ?: ""
    val genericTypesWithOut = (parameter).takeIf(List<Parameter>::isNotEmpty)
        ?.joinToString(
            prefix = "<",
            separator = ",",
            postfix = ",OUT>"
        ) { it.run { "T$index" } }
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
                            |            @JvmName("addRow_${operator.length}")
                            |            infix fun T0.$operator(next: T1) {
                            |                _values += $dataClassName(this, next)
                            |            }
                            """.trimMargin()
                        )
                    else {
                        append(
                            """
                            |            @JvmName("pair${operator.length}")
                            |            infix fun <T_0, T_1> T_0.$operator(next: T_1) =
                            |                this to next
                            """.trimMargin()
                        )
                        val receiverType = buildString {
                            append((2..parameter.lastIndex).joinToString(separator = "<") { "Pair" })
                            append("<T0, T1>")
                            append((2 until parameter.lastIndex).joinToString(separator = "") { ", T$it>" })
                        }
                        append("\n\n")

                        val firstParameters = mutableListOf(
                            (1 until parameter.lastIndex).joinToString(separator = ".") { "first" }
                        )
                        if (parameter.size > 3) {
                            (0..parameter.lastIndex - 3).reversed().forEach {
                                firstParameters.add(
                                    (0..it).joinToString(
                                        separator = ".",
                                        postfix = ".second"
                                    ) { "first" }
                                )
                            }
                        }

                        val firstParameterString = firstParameters.joinToString(", ")
                        append(
                            """
                            |            @JvmName("toRow_${operator.length}")
                            |            infix fun $receiverType.$operator(next: T${parameter.lastIndex}) {
                            |               _values += $dataClassName($firstParameterString, second, next)   
                            |            }
                            """.trimMargin()
                        )
                    }
                }
            }
    } else ""

    val eachFunctions = eachFunctionNames.joinToString(
        prefix = "\n",
        separator = "\n\n",
        postfix = "\n"
    ) {
        """
        |        inline fun $genericTypesWithOut List<$dataClassName$genericTypes>.each(
        |           function: $dataClassName$genericTypes.() -> OUT
        |        ) =
        |           map { it.function() } 
        """.trimMargin()
    }
    val toStringFun = parameterNames?.joinToString(
        prefix = "override fun toString() = \"",
        separator = ", ",
        postfix = "\""
    ) { "$it=\$$it" }
        ?: ""

    val result = """
        ${packageString?.let { "package $it" } ?: ""}

        data class $dataClassName$outGenericTypes($constructorProperties){
            $toStringFun
        }
        
        fun $genericTypes ${dataClassName}(function: ${dataClassName}Context$genericTypes.() -> Unit): List<$dataClassName$genericTypes> =
            ${dataClassName}Context$genericTypes()
                .apply(function)
                .values
        $eachFunctions
        class ${dataClassName}Context$genericTypes {
            private val _values = mutableListOf<$dataClassName$genericTypes>()
            val values: List<$dataClassName$genericTypes>
                get() = _values.toList()
            $concatMethods
        }
        """.trimIndent()
    return result
}
