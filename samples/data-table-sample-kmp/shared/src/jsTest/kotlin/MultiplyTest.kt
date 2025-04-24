import de.nielsfalk.dataTables.Data
import kotlin.test.*

class MultiplyTest {

    @Test
    fun multiplyNumbers() {
        @Data("first" , "second" ,  "expectedSum") MultiplyTests {
              1       ǀ 3        ǀǀ 3
              2       ǀ 4        ǀǀ 8
        }.each {
            assertEquals(
                expectedSum,
                first * second,
                "$first * $second should be $expectedSum"
            )
        }
    }
}