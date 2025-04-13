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
        }
        """.trimIndent()
    return result
}
