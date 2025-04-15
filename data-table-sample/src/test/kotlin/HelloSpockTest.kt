import de.nielsfalk.dataTables.Data
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

/**
 * this is a kotlin remake of the iconic Spock-Spec
 * https://github.com/spockframework/spock-example/blob/master/src/test/groovy/HelloSpockSpec.groovy
 */
class HelloSpockTest : FreeSpec({
    "length of Spock's and his friends' names" - {
        @Data("name"   , "expectedLength") Spock {
              "Spock"  ǀ 5
              "Kirk"   ǀ 4
              "Scotty" ǀ 6
        }.each {
            "${name}'s name has length $expectedLength" {
                name.length shouldBe expectedLength
            }
        }
    }
})
