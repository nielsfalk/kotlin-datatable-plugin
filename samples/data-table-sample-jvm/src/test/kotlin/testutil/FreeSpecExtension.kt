package testutil

fun <E> List<E>.applyNames(
    keySelector: (E) -> Any = { it.toString() }
): Map<String, E> =
    associateBy { keySelector(it).toString() }

