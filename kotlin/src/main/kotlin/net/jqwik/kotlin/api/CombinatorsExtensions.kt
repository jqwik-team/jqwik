package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary
import net.jqwik.api.Combinators


fun <T1, T2, R> combine(a1: Arbitrary<T1>, a2: Arbitrary<T2>, f: (v1: T1, v2: T2) -> R): Arbitrary<R> =
    Combinators.combine(a1, a2).`as`(f)

fun <T1, T2, R> combineFlat(a1: Arbitrary<T1>, a2: Arbitrary<T2>, f: (v1: T1, v2: T2) -> Arbitrary<R>): Arbitrary<R> =
    Combinators.combine(a1, a2).flatAs(f)
