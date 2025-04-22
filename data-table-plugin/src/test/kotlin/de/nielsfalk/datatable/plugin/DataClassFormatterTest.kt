package de.nielsfalk.datatable.plugin

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.equals.shouldBeEqual
import kotlin.test.assertEquals

class DataClassFormatterTest : FreeSpec({



    "format" {
        val code = """
            import de.nielsfalk.dataTables.Data
            
            @Data("name"   , "expectedLength") Spock {
                  "Spock"  ǀ 5 //test
                                       "//Kirkiriki"   ǀǀ 4
            "Scotty" ǀ 6ǀ 6ǀ 6ǀ 6ǀ 6
            }
        """.trimIndent()

        assertEquals(
            """
                import de.nielsfalk.dataTables.Data
                
                @Data("name"        ,  "expectedLength" , "col3" , "col4" , "col5" , "col6") Spock {
                      "Spock"       ǀ  5 //test         ǀ null   ǀ null   ǀ null   ǀ null
                      "//Kirkiriki" ǀǀ 4                ǀ null   ǀ null   ǀ null   ǀ null
                      "Scotty"      ǀ  6                ǀ 6      ǀ 6      ǀ 6      ǀ 6
                }
            """.trimIndent(),
            format(code)
        )
    }
    "format with Dataclass in new line" {
        val code = """
            import de.nielsfalk.dataTables.Data
            
            @Data("name", "expectedLength") 
            TestData {
                  "Spock"  ǀ 5 
            }
        """.trimIndent()

        assertEquals(
            """
                import de.nielsfalk.dataTables.Data
                
                @Data(    "name"  , "expectedLength")
                TestData {
                          "Spock" ǀ 5
                }
            """.trimIndent(),
            format(code)
        )
    }
    "format with Types" {
        val code = """
            import de.nielsfalk.dataTables.Data
            
            @Data("name", "expectedLength") 
            TestData<String> {
                  "Mr.Spock"  ǀ 5 
            }
        """.trimIndent()

        assertEquals(
            """
                import de.nielsfalk.dataTables.Data
                
                @Data(   "name"     , "expectedLength")
                TestData<String     , Any             > {
                         "Mr.Spock" ǀ 5
                }
            """.trimIndent(),
            format(code)
        )
    }
    "format with unknown lines" {
        val code = """
            import de.nielsfalk.dataTables.Data
            
            @Data("name", "expectedLength") 
            TestData<String> 
            {
            // something in a comment
                  "Mr.Spock"  ǀ 5 
            }
        """.trimIndent()

        assertEquals(
            """
                import de.nielsfalk.dataTables.Data
        
                @Data(   "name"     , "expectedLength")
                TestData<String     , Any             >
                {
                // something in a comment
                         "Mr.Spock" ǀ 5
                }
            """.trimIndent(),
            format(code)
        )
    }
})