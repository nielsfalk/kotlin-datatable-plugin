import Hand.*
import de.nielsfalk.dataTables.Data
import de.nielsfalk.datatable.testutil.applyNames
import io.kotest.core.spec.style.FreeSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class KotestDatatestTest : FreeSpec({
    withData(
        @Data("first"  , "second" ,  "expectedWinner") TestCase {
              Rock     ǀ Rock     ǀǀ null
              Paper    ǀ Paper    ǀǀ null
              Scissors ǀ Scissors ǀǀ null
              Rock     ǀ Scissors ǀǀ Rock
              Paper    ǀ Rock     ǀǀ Paper
              Scissors ǀ Paper    ǀǀ Scissors
              Rock     ǀ Paper    ǀǀ Paper
              Paper    ǀ Scissors ǀǀ Scissors
              Scissors ǀ Rock     ǀǀ Rock
        }.applyNames ()
    ) {
        val result = it.first defend it.second

        result shouldBe it.expectedWinner
    }
})

data class Foo(val foo: String){
    override fun toString() = "Foo(foo='$foo')"
}

enum class Hand {
    Rock, Paper, Scissors
}

val rules = mapOf(
    Rock to Scissors,
    Scissors to Paper,
    Paper to Rock
)

infix fun Hand.defend(second: Hand): Hand? =
    when (second) {
        this -> null
        rules[this] -> this
        else -> second
    }
