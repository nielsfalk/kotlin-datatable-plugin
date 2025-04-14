package de.nielsfalk.kotlin.plugin

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import java.nio.file.Paths

class DataClassDataTest : FreeSpec({
    val path = Paths.get("src/aPath.kt")

    "should return null when the content does not contain @DataTable" {
        val text = """
                package test
                
                val x = "no annotation here"
            """.trimIndent()

        DataClassData.of(path, text).shouldBeEmpty()
    }

    "parse dataTable" {
        val text = """
            package de.nielsfalk.dataTables.plugin

            import de.nielsfalk.dataTables.DataTable


            fun main() {
                @DataTable("name"  ,"length", "truthy")
                Spock     <String  , Int    , Boolean > {
                           "sdfsd" ǀ 15     ǀ true
                           "dfsff" ǀ 12     ǀ true
                }
            }
        """.trimIndent()

        val result = DataClassData.of(path, text).firstOrNull().shouldNotBeNull()
        result shouldBeEqual DataClassData(
            dataClassName = "Spock",
            parameterNames = listOf("name", "length", "truthy"),
            lineParameterCount = 3,
            path = "src/aPath.kt",
            packageString = "de.nielsfalk.dataTables.plugin",
            generatedFileName = "___de_nielsfalk_dataTables_plugin___Spock.kt"
        )
    }
})