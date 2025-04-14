import de.nielsfalk.dataTables.DataTable
import de.nielsfalk.datatable.testutil.test
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

/**
 * this is a kotlin remake of the iconic Spock-Spec
 * https://github.com/spockframework/spock-example/blob/master/src/test/groovy/HelloSpockSpec.groovy
 */
class HelloSpockTest : FreeSpec({
    "length of Spock's and his friends' names" - {
        test(
            @DataTable("name"   , "expectedLength")
            Spock     <String   , Int             > {
                       "Spock"  ǀ 5
                       "Kirk"   ǀ 4
                       "Scotty" ǀ 6
            }
        ) {
            name.length shouldBe expectedLength
        }
    }
})