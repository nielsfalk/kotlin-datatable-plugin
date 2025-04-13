package de.nielsfalk.kotlin.plugin

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.string.shouldStartWith
import java.nio.file.Paths

class DataClassGeneratorTest : FreeSpec({
    val path = Paths.get("src/aPath.kt")
    "generateDataClass" {
        val data = DataClassData.of(
            path,
            """
                package de.nielsfalk.dataTables.plugin
    
                import de.nielsfalk.dataTables.DataTable
    
    
                fun main() {
                    @DataTable("name"  ,"length", "truthy", "aLong", "aFloat")
                    Spock.data<String  , Int    , Boolean , Long   , Float   > {
                               "sdfsd" ǀ 15     ǀ true    ǀ 1L     ǀ 2.0
                               "dfsff" ǀ 12     ǀ true    ǀ 1L     ǀ 2.0
                    }
                }
            """.trimIndent()
        ).first()

        val generated = data.generate()

        generated shouldStartWith """
            package de.nielsfalk.dataTables.plugin

            data class Spock<out T0,out T1,out T2,out T3,out T4>(val name: T0,val length: T1,val truthy: T2,val aLong: T3,val aFloat: T4){
                companion object {
                    fun <T0,T1,T2,T3,T4> data(function: SpockContext<T0,T1,T2,T3,T4>.() -> Unit): List<Spock<T0,T1,T2,T3,T4>> =
                        SpockContext<T0,T1,T2,T3,T4>()
                            .apply(function)
                            .values
                }
            }
            
            class SpockContext<T0,T1,T2,T3,T4> {
                private val _values = mutableListOf<Spock<T0,T1,T2,T3,T4>>()
                val values: List<Spock<T0,T1,T2,T3,T4>>
                    get() = _values.toList()
        """.trimIndent()
    }
})