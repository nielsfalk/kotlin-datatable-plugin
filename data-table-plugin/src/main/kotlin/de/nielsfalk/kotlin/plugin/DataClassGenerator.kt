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
        val operator = "ǀ"
        buildString {
            append("@JvmName(\"context1\")\n")
            append(
                if (parameter.size == 2)
                    """
                    |            infix fun T0.$operator(next: T1) {
                    |                _values += $dataClassName(this, next)
                    |            } 
                    """.trimMargin()
                else
                    """
                    |            infix fun T0.$operator(next: T1) =
                    |                this to next
                    """.trimMargin()
            )

            (2..parameter.size).joinToString(separator = "") { i ->
                append("")

            }
        }
    } else ""

    val result = """
        package $packageString

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
