package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary
import net.jqwik.api.Combinators
import org.apiguardian.api.API


/**
 * Combine 2 arbitraries into one.
 *
 * @return new Arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T1, T2, R> combine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    filter: ((T1, T2) -> Boolean)? = null,
    combinator: (v1: T1, v2: T2) -> R
): Arbitrary<R> = if (filter == null) {
    Combinators.combine(a1, a2).`as`(combinator)
} else {
    Combinators.combine(a1, a2).filter(filter).`as`(combinator)
}

/**
 * Combine 3 arbitraries into one.
 *
 * @return new Arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T1, T2, T3, R> combine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    filter: ((v1: T1, v2: T2, v3: T3) -> Boolean)? = null,
    combinator: (v1: T1, v2: T2, v3: T3) -> R
): Arbitrary<R> = if (filter == null) {
    Combinators.combine(a1, a2, a3).`as`(combinator)
} else {
    Combinators.combine(a1, a2, a3).filter(filter).`as`(combinator)
}

/**
 * Combine 4 arbitraries into one.
 *
 * @return new Arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T1, T2, T3, T4, R> combine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    filter: ((v1: T1, v2: T2, v3: T3, v4: T4) -> Boolean)? = null,
    combinator: (v1: T1, v2: T2, v3: T3, v4: T4) -> R
): Arbitrary<R> = if (filter == null) {
    Combinators.combine(a1, a2, a3, a4).`as`(combinator)
} else {
    Combinators.combine(a1, a2, a3, a4).filter(filter).`as`(combinator)
}

/**
 * Combine 5 arbitraries into one.
 *
 * @return new Arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T1, T2, T3, T4, T5, R> combine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    a5: Arbitrary<T5>,
    filter: ((v1: T1, v2: T2, v3: T3, v4: T4, v5: T5) -> Boolean)? = null,
    combinator: (v1: T1, v2: T2, v3: T3, v4: T4, v5: T5) -> R
): Arbitrary<R> = if (filter == null) {
    Combinators.combine(a1, a2, a3, a4, a5).`as`(combinator)
} else {
    Combinators.combine(a1, a2, a3, a4, a5).filter(filter).`as`(combinator)
}

/**
 * Combine 6 arbitraries into one.
 *
 * @return new Arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T1, T2, T3, T4, T5, T6, R> combine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    a5: Arbitrary<T5>,
    a6: Arbitrary<T6>,
    filter: ((v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6) -> Boolean)? = null,
    combinator: (v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6) -> R
): Arbitrary<R> = if (filter == null) {
    Combinators.combine(a1, a2, a3, a4, a5, a6).`as`(combinator)
} else {
    Combinators.combine(a1, a2, a3, a4, a5, a6).filter(filter).`as`(combinator)
}

/**
 * Combine 7 arbitraries into one.
 *
 * @return new Arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T1, T2, T3, T4, T5, T6, T7, R> combine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    a5: Arbitrary<T5>,
    a6: Arbitrary<T6>,
    a7: Arbitrary<T7>,
    filter: ((v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7) -> Boolean)? = null,
    combinator: (v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7) -> R
): Arbitrary<R> = if (filter == null) {
    Combinators.combine(a1, a2, a3, a4, a5, a6, a7).`as`(combinator)
} else {
    Combinators.combine(a1, a2, a3, a4, a5, a6, a7).filter(filter).`as`(combinator)
}

/**
 * Combine 8 arbitraries into one.
 *
 * @return new Arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T1, T2, T3, T4, T5, T6, T7, T8, R> combine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    a5: Arbitrary<T5>,
    a6: Arbitrary<T6>,
    a7: Arbitrary<T7>,
    a8: Arbitrary<T8>,
    filter: ((v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8) -> Boolean)? = null,
    combinator: (v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8) -> R
): Arbitrary<R> = if (filter == null) {
    Combinators.combine(a1, a2, a3, a4, a5, a6, a7, a8).`as`(combinator)
} else {
    Combinators.combine(a1, a2, a3, a4, a5, a6, a7, a8).filter(filter).`as`(combinator)
}


/**
 * Combine list of arbitraries into one.
 *
 * @return new Arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T, R> combine(
    arbitraries: List<Arbitrary<T>>,
    filter: ((v1: List<T>) -> Boolean)? = null,
    combinator: (v1: List<T>) -> R
): Arbitrary<R> = if (filter == null) {
    Combinators.combine(arbitraries).`as`(combinator)
} else {
    Combinators.combine(arbitraries).filter(filter).`as`(combinator)
}

/**
 * Flat-combine 2 arbitraries into one.
 *
 * @return new Arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T1, T2, R> flatCombine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    flatCombinator: (v1: T1, v2: T2) -> Arbitrary<R>
): Arbitrary<R> =
    Combinators.combine(a1, a2).flatAs(flatCombinator)

/**
 * Flat-combine 3 arbitraries into one.
 *
 * @return new Arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T1, T2, T3, R> flatCombine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    flatCombinator: (v1: T1, v2: T2, v3: T3) -> Arbitrary<R>
): Arbitrary<R> =
    Combinators.combine(a1, a2, a3).flatAs(flatCombinator)

/**
 * Flat-combine 4 arbitraries into one.
 *
 * @return new Arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T1, T2, T3, T4, R> flatCombine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    flatCombinator: (v1: T1, v2: T2, v3: T3, v4: T4) -> Arbitrary<R>
): Arbitrary<R> =
    Combinators.combine(a1, a2, a3, a4).flatAs(flatCombinator)

/**
 * Flat-combine 5 arbitraries into one.
 *
 * @return new Arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T1, T2, T3, T4, T5, R> flatCombine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    a5: Arbitrary<T5>,
    flatCombinator: (v1: T1, v2: T2, v3: T3, v4: T4, v5: T5) -> Arbitrary<R>
): Arbitrary<R> =
    Combinators.combine(a1, a2, a3, a4, a5).flatAs(flatCombinator)

/**
 * Flat-combine 6 arbitraries into one.
 *
 * @return new Arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T1, T2, T3, T4, T5, T6, R> flatCombine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    a5: Arbitrary<T5>,
    a6: Arbitrary<T6>,
    flatCombinator: (v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6) -> Arbitrary<R>
): Arbitrary<R> =
    Combinators.combine(a1, a2, a3, a4, a5, a6).flatAs(flatCombinator)

/**
 * Flat-combine 7 arbitraries into one.
 *
 * @return new Arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T1, T2, T3, T4, T5, T6, T7, R> flatCombine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    a5: Arbitrary<T5>,
    a6: Arbitrary<T6>,
    a7: Arbitrary<T7>,
    flatCombinator: (v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7) -> Arbitrary<R>
): Arbitrary<R> =
    Combinators.combine(a1, a2, a3, a4, a5, a6, a7).flatAs(flatCombinator)

/**
 * Flat-combine 8 arbitraries into one.
 *
 * @return new Arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T1, T2, T3, T4, T5, T6, T7, T8, R> flatCombine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    a5: Arbitrary<T5>,
    a6: Arbitrary<T6>,
    a7: Arbitrary<T7>,
    a8: Arbitrary<T8>,
    flatCombine: (v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8) -> Arbitrary<R>
): Arbitrary<R> =
    Combinators.combine(a1, a2, a3, a4, a5, a6, a7, a8).flatAs(flatCombine)


/**
 * Flat-combine list of arbitraries into one.
 *
 * @return new Arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T, R> flatCombine(
    arbitraries: List<Arbitrary<T>>,
    flatCombinator: (v1: List<T>) -> Arbitrary<R>
): Arbitrary<R> {
    val combine = Combinators.combine(arbitraries)
    return combine.flatAs(flatCombinator)
}

