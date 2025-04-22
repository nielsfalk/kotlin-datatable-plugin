import de.nielsfalk.dataTables.Data
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldNotHaveSize
import io.kotest.matchers.equals.shouldBeEqual
import java.time.Instant
import java.time.LocalDate

class ManyParametersTest : FreeSpec({
    @Data(          "string" , "int" , "long" , "double" , "float" , "localDate"            , "instant"                , "bigDecimal"         ,  "expectedNotNullCount" , "expectedNotNullJoin"                                          )
    ManyParameters {
                    null     ǀ null  ǀ null   ǀ null     ǀ null    ǀ null                   ǀ null                     ǀ null                 ǀǀ 1                      ǀ null
                    "string" ǀ 3     ǀ 4L     ǀ 2.0      ǀ 1.2f    ǀ LocalDate.of(12,12,12) ǀ Instant.ofEpochSecond(0) ǀ "1.3".toBigDecimal() ǀǀ 3                      ǀ "string, 3, 4, 2.0, 1.2, 0012-12-12, 1970-01-01T00:00:00Z, 1.3"
    }.each {
        "many parameters $this" {
            val listOfNotNull = listOfNotNull(string, int, long, double, float, localDate, instant, bigDecimal)
            listOfNotNull shouldNotHaveSize expectedNotNullCount
            listOfNotNull.joinToString() shouldBeEqual (expectedNotNullJoin?:"")
        }
    }
})