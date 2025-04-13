package de.nielsfalk.kotlin.plugin

import io.kotest.core.spec.style.FreeSpec
import java.nio.file.Paths
import kotlin.test.assertEquals

class DataClassGeneratorTest : FreeSpec({
    val path = Paths.get("src/aPath.kt")

    "generate data class" {
        val data = DataClassData.of(
            path,
            """
                package de.nielsfalk.dataTables.plugin
                import de.nielsfalk.dataTables.DataTable
    
                @DataTable("name"  ,"length", "truthy", "aLong", "aFloat")
                Spock.data<String  , Int    , Boolean , Long   , Float   >{
                
                } 
            """.trimIndent()
        ).first()

        val generated = data.generate()

        assertEquals(
            """
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
                        
                    @JvmName("context1")
                    infix fun T0.ǀ(next: T1) =
                        this to next
                }
            """.trimIndent(),
            generated
        )
    }

    "generate data class special case with only 2 Parameters" {
        val data = DataClassData.of(
            path,
            """
                package de.nielsfalk.dataTables.plugin
                import de.nielsfalk.dataTables.DataTable
    
                @DataTable("name"  ,"length")
                Spock.data<String  , Int    >{
                
                } 
            """.trimIndent()
        ).first()

        val generated = data.generate()

        assertEquals(
            """
                package de.nielsfalk.dataTables.plugin

                data class Spock<out T0,out T1>(val name: T0,val length: T1){
                    companion object {
                        fun <T0,T1> data(function: SpockContext<T0,T1>.() -> Unit): List<Spock<T0,T1>> =
                            SpockContext<T0,T1>()
                                .apply(function)
                                .values
                    }
                }

                class SpockContext<T0,T1> {
                    private val _values = mutableListOf<Spock<T0,T1>>()
                    val values: List<Spock<T0,T1>>
                        get() = _values.toList()
                        
                    @JvmName("context1")
                    infix fun T0.ǀ(next: T1) {
                        _values += Spock(this, next)
                    } 
                }
            """.trimIndent(),
            generated
        )
    }
})