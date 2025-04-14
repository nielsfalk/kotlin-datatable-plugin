package de.nielsfalk.datatable.testutil

import io.kotest.core.spec.style.FreeSpec

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