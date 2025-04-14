package de.nielsfalk.datatable.testutil

import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.scopes.FreeSpecContainerScope

fun <T> FreeSpec.test(
    where: List<T>,
    test: T.() -> Unit
) {
    where.forEach {
        "$it"{
            it.test()
        }
    }
}

suspend fun <T> FreeSpecContainerScope.test(
    where: List<T>,
    test: T.() -> Unit
) {
    where.forEach {
        "$it"{
            it.test()
        }
    }
}

