import Hand.*
import de.nielsfalk.dataTables.DataTable
import de.nielsfalk.datatable.testutil.test
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class RockPaperScissorsTest : FreeSpec({
    test(
        @DataTable("first"  , "second" ,  "expectedWinner")
        TestCase  <Hand     , Hand     ,  Hand?           > {
                   Rock     ǀ Rock     ǀǀ null
                   Paper    ǀ Paper    ǀǀ null
                   Scissors ǀ Scissors ǀǀ null
                   Rock     ǀ Scissors ǀǀ Rock
                   Paper    ǀ Rock     ǀǀ Paper
                   Scissors ǀ Paper    ǀǀ Scissors
                   Rock     ǀ Paper    ǀǀ Paper
                   Paper    ǀ Scissors ǀǀ Scissors
                   Scissors ǀ Rock     ǀǀ Rock
        }
    ) {
        val result = first defend second

        result shouldBe expectedWinner
    }
})

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
