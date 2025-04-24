package de.nielsfalk.datatable.plugin

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
                import de.nielsfalk.dataTables.Data
    
                @Data("name"  ,"length", "truthy", "aLong", "aFloat")
                Spock<String  , Int    , Boolean , Long   , Float   >{
                
                } 
            """.trimIndent()
        ).first()

        val generated = data.generate()

        //language=kotlin
        assertEquals(
            """
                package de.nielsfalk.dataTables.plugin

                data class Spock<out T0,out T1,out T2,out T3,out T4>(val name: T0,val length: T1,val truthy: T2,val aLong: T3,val aFloat: T4){
                    override fun toString() = "name=${'$'}name, length=${'$'}length, truthy=${'$'}truthy, aLong=${'$'}aLong, aFloat=${'$'}aFloat"
                }

                fun <T0,T1,T2,T3,T4> Spock(function: SpockContext<T0,T1,T2,T3,T4>.() -> Unit): List<Spock<T0,T1,T2,T3,T4>> =
                    SpockContext<T0,T1,T2,T3,T4>()
                        .apply(function)
                        .values

                inline fun <T0,T1,T2,T3,T4,OUT> List<Spock<T0,T1,T2,T3,T4>>.each(
                   function: Spock<T0,T1,T2,T3,T4>.() -> OUT
                ) =
                   map { it.function() } 

                class SpockContext<T0,T1,T2,T3,T4> {
                    private val _values = mutableListOf<Spock<T0,T1,T2,T3,T4>>()
                    val values: List<Spock<T0,T1,T2,T3,T4>>
                        get() = _values.toList()
                    
                    infix fun <T_0, T_1> T_0.ǀ(next: T_1) =
                        this to next

                    infix fun Pair<Pair<Pair<T0, T1>, T2>, T3>.ǀ(next: T4) {
                       _values += Spock(first.first.first, first.first.second, first.second, second, next)   
                    }

                    infix fun <T_0, T_1> T_0.ǀǀ(next: T_1) =
                        this to next

                    infix fun Pair<Pair<Pair<T0, T1>, T2>, T3>.ǀǀ(next: T4) {
                       _values += Spock(first.first.first, first.first.second, first.second, second, next)   
                    }
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
                import de.nielsfalk.dataTables.Data
    
                @Data("name"  ,"length")
                Spock<String  , Int    >{
                
                } 
            """.trimIndent()
        ).first()

        val generated = data.generate()

        //language=kotlin
        assertEquals(
            """
                package de.nielsfalk.dataTables.plugin

                data class Spock<out T0,out T1>(val name: T0,val length: T1){
                    override fun toString() = "name=${'$'}name, length=${'$'}length"
                }

                fun <T0,T1> Spock(function: SpockContext<T0,T1>.() -> Unit): List<Spock<T0,T1>> =
                    SpockContext<T0,T1>()
                        .apply(function)
                        .values

                inline fun <T0,T1,OUT> List<Spock<T0,T1>>.each(
                   function: Spock<T0,T1>.() -> OUT
                ) =
                   map { it.function() } 

                class SpockContext<T0,T1> {
                    private val _values = mutableListOf<Spock<T0,T1>>()
                    val values: List<Spock<T0,T1>>
                        get() = _values.toList()
                    
                    infix fun T0.ǀ(next: T1) {
                        _values += Spock(this, next)
                    }

                    infix fun T0.ǀǀ(next: T1) {
                        _values += Spock(this, next)
                    }
                }
            """.trimIndent(),
            generated
        )
    }
})