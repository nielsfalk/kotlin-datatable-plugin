import de.nielsfalk.dataTables.Data
import kotlin.test.*

class SumTest {

    @Test
    fun sumNumbers() {
        @Data("first" , "second" ,  "expectedSum") SumTests {
              1       ǀ 3        ǀǀ 4
              2       ǀ 4        ǀǀ 6
        }.each {
            assertEquals(
                expectedSum,
                first + second,
                "$first + $second should be $expectedSum"
            )
        }
    }
}