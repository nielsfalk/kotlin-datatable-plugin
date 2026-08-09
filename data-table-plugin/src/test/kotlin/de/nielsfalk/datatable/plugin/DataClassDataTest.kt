package de.nielsfalk.datatable.plugin

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import java.nio.file.Paths

class DataClassDataTest : FreeSpec({
    val path = Paths.get("src/aPath.kt")

    "should return null when the content does not contain @Data" {
        val text = """
                package test
                
                val x = "no annotation here"
            """.trimIndent()

        assert(DataClassData.of(path, text).isEmpty())
    }

    "parse dataTable" {
        val text = """
            package de.nielsfalk.dataTables.plugin

            import de.nielsfalk.dataTables.Data


            fun main() {
                @Data("name"  ,"length", "truthy")
                Spock<String  , Int    , Boolean > {
                      "sdfsd" ǀ 15     ǀ true
                      "dfsff" ǀ 12     ǀ true
                }
            }
        """.trimIndent()

        val result = DataClassData.of(path, text).firstOrNull()
        assert(
            result == DataClassData(
                dataClassName = "Spock",
                parameterNames = listOf("name", "length", "truthy"),
                path = "src/aPath.kt",
                packageString = "de.nielsfalk.dataTables.plugin",
                generatedFileName = "___de_nielsfalk_dataTables_plugin___Spock.kt"
            )
        )
    }

    "parse dataTable in default package" {
        val text = """
           import de.nielsfalk.dataTables.Data


            fun main() {
                @Data("name"  ,"length", "truthy")
                Spock<String  , Int    , Boolean > {
                      "sdfsd" ǀ 15     ǀ true
                      "dfsff" ǀ 12     ǀ true
                }
            }
        """.trimIndent()

        val result = DataClassData.of(path, text).firstOrNull()
        assert(
            result == DataClassData(
                dataClassName = "Spock",
                parameterNames = listOf("name", "length", "truthy"),
                path = "src/aPath.kt",
                packageString = null,
                generatedFileName = "______Spock.kt"
            )
        )
    }

    "parse dataTable with import to qualify data class in default package" {
        val text = """
            package de.nielsfalk.dataTables.plugin

            import de.nielsfalk.dataTables.Data
            import Spock


            fun main() {
                @Data("name"  ,"length", "truthy")
                Spock<String  , Int    , Boolean > {
                      "sdfsd" ǀ 15     ǀ true
                      "dfsff" ǀ 12     ǀ true
                }
            }
        """.trimIndent()

        val result = DataClassData.of(path, text).firstOrNull()
        assert(
            result == DataClassData(
                dataClassName = "Spock",
                parameterNames = listOf("name", "length", "truthy"),
                path = "src/aPath.kt",
                packageString = null,
                generatedFileName = "______Spock.kt"
            )
        )
    }

    "parse dataTable with import to qualify data class" {
        val text = """
            package de.nielsfalk.dataTables.plugin

            import de.nielsfalk.dataTables.Data
            import com.startrek.Spock


            fun main() {
                @Data("name"  ,"length", "truthy")
                Spock<String  , Int    , Boolean > {
                      "sdfsd" ǀ 15     ǀ true
                      "dfsff" ǀ 12     ǀ true
                }
            }
        """.trimIndent()

        val result = DataClassData.of(path, text).firstOrNull()
        assert(
            result == DataClassData(
                dataClassName = "Spock",
                parameterNames = listOf("name", "length", "truthy"),
                path = "src/aPath.kt",
                packageString = "com.startrek",
                generatedFileName = "___com_startrek___Spock.kt"
            )
        )
    }

    "parse dataTable with import as to qualify data class" {
        val text = """
            package de.nielsfalk.dataTables.plugin

            import de.nielsfalk.dataTables.Data
            import com.startrek.Someone as Spock


            fun main() {
                @Data("name"  ,"length", "truthy")
                Spock<String  , Int    , Boolean > {
                      "sdfsd" ǀ 15     ǀ true
                      "dfsff" ǀ 12     ǀ true
                }
            }
        """.trimIndent()

        val result = DataClassData.of(path, text).firstOrNull()
        assert(
            result == DataClassData(
                dataClassName = "Someone",
                parameterNames = listOf("name", "length", "truthy"),
                path = "src/aPath.kt",
                packageString = "com.startrek",
                generatedFileName = "___com_startrek___Someone.kt"
            )
        )
    }

    "parse dataTable with qualified name" {
        val text = """
            package de.nielsfalk.dataTables.plugin

            import de.nielsfalk.dataTables.Data


            fun main() {
                @Data(             "name"  ,"length", "truthy")
                com.starterk.Spock<String  , Int    , Boolean > {
                                   "sdfsd" ǀ 15     ǀ true
                                   "dfsff" ǀ 12     ǀ true
                }
            }
        """.trimIndent()

        val result = DataClassData.of(path, text).firstOrNull()
        assert(
            result == DataClassData(
                dataClassName = "Spock",
                parameterNames = listOf("name", "length", "truthy"),
                path = "src/aPath.kt",
                packageString = "com.starterk",
                generatedFileName = "___com_starterk___Spock.kt"
            )
        )
    }

    "conflict on parameter names"{
        val dataClassData = DataClassData(
            dataClassName = "Spock",
            parameterNames = listOf("name", "length", "truthy"),
            path = "src/aPath.kt",
            packageString = "com.starterk",
            generatedFileName = "___com_starterk___Spock.kt"
        )
        val list = listOf(
            dataClassData,
            dataClassData.copy(parameterNames = listOf("conflict", "on", "names"))
        )

        assert(
            shouldThrow<IllegalArgumentException> {
                list.groupByClass()
            }.message?.contains("conflicting data table parameter names") == true
        )
    }
})
