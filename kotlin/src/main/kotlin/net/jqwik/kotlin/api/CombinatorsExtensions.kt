package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary
import net.jqwik.api.Combinators


fun <T1, T2, R> combine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    f: (v1: T1, v2: T2) -> R
): Arbitrary<R> =
    Combinators.combine(a1, a2).`as`(f)

fun <T1, T2, T3, R> combine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    f: (v1: T1, v2: T2, v3: T3) -> R
): Arbitrary<R> =
    Combinators.combine(a1, a2, a3).`as`(f)

fun <T1, T2, T3, T4, R> combine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    f: (v1: T1, v2: T2, v3: T3, v4: T4) -> R
): Arbitrary<R> =
    Combinators.combine(a1, a2, a3, a4).`as`(f)

fun <T1, T2, T3, T4, T5, R> combine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    a5: Arbitrary<T5>,
    f: (v1: T1, v2: T2, v3: T3, v4: T4, v5: T5) -> R
): Arbitrary<R> =
    Combinators.combine(a1, a2, a3, a4, a5).`as`(f)

fun <T1, T2, T3, T4, T5, T6, R> combine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    a5: Arbitrary<T5>,
    a6: Arbitrary<T6>,
    f: (v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6) -> R
): Arbitrary<R> =
    Combinators.combine(a1, a2, a3, a4, a5, a6).`as`(f)

fun <T1, T2, T3, T4, T5, T6, T7, R> combine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    a5: Arbitrary<T5>,
    a6: Arbitrary<T6>,
    a7: Arbitrary<T7>,
    f: (v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7) -> R
): Arbitrary<R> =
    Combinators.combine(a1, a2, a3, a4, a5, a6, a7).`as`(f)

fun <T1, T2, T3, T4, T5, T6, T7, T8, R> combine(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    a5: Arbitrary<T5>,
    a6: Arbitrary<T6>,
    a7: Arbitrary<T7>,
    a8: Arbitrary<T8>,
    f: (v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8) -> R
): Arbitrary<R> =
    Combinators.combine(a1, a2, a3, a4, a5, a6, a7, a8).`as`(f)


fun <T, R> combine(
    arbitraries: List<Arbitrary<T>>,
    f: (v1: List<T>) -> R
): Arbitrary<R> =
    Combinators.combine(arbitraries).`as`(f)

fun <T1, T2, R> combineFlat(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>, f: (v1: T1, v2: T2) -> Arbitrary<R>
): Arbitrary<R> =
    Combinators.combine(a1, a2).flatAs(f)

fun <T1, T2, T3, R> combineFlat(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    f: (v1: T1, v2: T2, v3: T3) -> Arbitrary<R>
): Arbitrary<R> =
    Combinators.combine(a1, a2, a3).flatAs(f)

fun <T1, T2, T3, T4, R> combineFlat(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    f: (v1: T1, v2: T2, v3: T3, v4: T4) -> Arbitrary<R>
): Arbitrary<R> =
    Combinators.combine(a1, a2, a3, a4).flatAs(f)

fun <T1, T2, T3, T4, T5, R> combineFlat(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    a5: Arbitrary<T5>,
    f: (v1: T1, v2: T2, v3: T3, v4: T4, v5: T5) -> Arbitrary<R>
): Arbitrary<R> =
    Combinators.combine(a1, a2, a3, a4, a5).flatAs(f)

fun <T1, T2, T3, T4, T5, T6, R> combineFlat(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    a5: Arbitrary<T5>,
    a6: Arbitrary<T6>,
    f: (v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6) -> Arbitrary<R>
): Arbitrary<R> =
    Combinators.combine(a1, a2, a3, a4, a5, a6).flatAs(f)

fun <T1, T2, T3, T4, T5, T6, T7, R> combineFlat(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    a5: Arbitrary<T5>,
    a6: Arbitrary<T6>,
    a7: Arbitrary<T7>,
    f: (v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7) -> Arbitrary<R>
): Arbitrary<R> =
    Combinators.combine(a1, a2, a3, a4, a5, a6, a7).flatAs(f)

fun <T1, T2, T3, T4, T5, T6, T7, T8, R> combineFlat(
    a1: Arbitrary<T1>,
    a2: Arbitrary<T2>,
    a3: Arbitrary<T3>,
    a4: Arbitrary<T4>,
    a5: Arbitrary<T5>,
    a6: Arbitrary<T6>,
    a7: Arbitrary<T7>,
    a8: Arbitrary<T8>,
    f: (v1: T1, v2: T2, v3: T3, v4: T4, v5: T5, v6: T6, v7: T7, v8: T8) -> Arbitrary<R>
): Arbitrary<R> =
    Combinators.combine(a1, a2, a3, a4, a5, a6, a7, a8).flatAs(f)


fun <T, R> combineFlat(
    arbitraries: List<Arbitrary<T>>,
    f: (v1: List<T>) -> Arbitrary<R>
): Arbitrary<R> {
    val combine = Combinators.combine(arbitraries)
    return combine.flatAs(f)
}

