package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary

/**
 * Function to create an arbitrary for [Pair<A, B>][Pair].
 */
fun <A, B> anyPair(firstArbitrary: Arbitrary<A>, secondArbitrary: Arbitrary<B>) =
    combine(firstArbitrary, secondArbitrary) { a, b -> Pair(a, b) }

/**
 * Function to create an arbitrary for [Triple<A, B, C>][Triple].
 */
fun <A, B, C> anyTriple(firstArbitrary: Arbitrary<A>, secondArbitrary: Arbitrary<B>, thirdArbitrary: Arbitrary<C>) =
    combine(firstArbitrary, secondArbitrary, thirdArbitrary) { a, b, c -> Triple(a, b, c) }
