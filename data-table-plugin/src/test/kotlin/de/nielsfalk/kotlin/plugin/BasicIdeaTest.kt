package de.nielsfalk.kotlin.plugin

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainOnly

class BasicIdeaTest : FreeSpec({

    "BasicIdea" {
        val basicIdeaDataContext = BasicIdeaDataContext<Long, Int, String, Boolean, Double>()

        basicIdeaDataContext.run {
            13L ǀ 12 ǀ "something" ǀ true ǀ 2.0

        }

        basicIdeaDataContext.values shouldContainOnly listOf(
            BasicIdeaData(
                13L,
                12,
                "something",
                true,
                2.0
            )
        )
    }
})

data class BasicIdeaData<out T0, out T1, out T2, out T3, out T4>(
    val aString: T0,
    val anInt: T1,
    val aLong: T2,
    val aBool: T3,
    val aDouble: T4
) {
    companion object {
        fun <T0, T1, T2, T3, T4> data(function: BasicIdeaDataContext<T0, T1, T2, T3, T4>.() -> Unit): List<BasicIdeaData<T0, T1, T2, T3, T4>> =
            BasicIdeaDataContext<T0, T1, T2, T3, T4>()
                .apply(function)
                .values
    }
}

@Suppress("NonAsciiCharacters", "TestFunctionName")
class BasicIdeaDataContext<T0, T1, T2, T3, T4> {
    private val _values = mutableListOf<BasicIdeaData<T0, T1, T2, T3, T4>>()
    val values: List<BasicIdeaData<T0, T1, T2, T3, T4>>
        get() = _values.toList()

    @JvmName("context1")
    infix fun T0.ǀ(next: T1): Pair<T0, T1> =
        this to next

    @JvmName("context2")
    infix fun Pair<T0, T1>.ǀ(next: T2) =
        this to next

    @JvmName("context3")
    infix fun Pair<Pair<T0, T1>, T2>.ǀ(next: T3) =
        this to next

    @JvmName("context4")
    infix fun Pair<Pair<Pair<T0, T1>, T2>, T3>.ǀ(last: T4) {
        _values += BasicIdeaData(first.first.first, first.first.second, first.second, second, last)
    }

}