package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary
import net.jqwik.api.arbitraries.ArrayArbitrary
import org.apiguardian.api.API

/**
 * Create a new arbitrary of the same type but inject null values with a probability of `nullProbability`.
 *
 * This is a type-safe version of [Arbitrary.injectNull()][Arbitrary.injectNull].
 *
 * @param nullProbability the probability. &ge; 0 and  &le; 1.
 * @return a new arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T> Arbitrary<T>.orNull(nullProbability: Double): Arbitrary<T?> {
    return this.injectNull(nullProbability)
}

/**
 * Create a new arbitrary of type [SequenceArbitrary<T>][SequenceArbitrary]
 * using the existing arbitrary for generating the elements of the sequence.
 *
 * @return a new arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T> Arbitrary<T>.sequence(): SequenceArbitrary<T> {
    return SequenceArbitrary(this)
}

/**
 * Create a new arbitrary for type [Pair<T, T>][Pair]
 * using the existing arbitrary for generating the elements of the pair.
 *
 * @return a new arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T> Arbitrary<T>.pair(): Arbitrary<Pair<T, T>> {
    return anyPair(this, this)
}

/**
 * Create a new arbitrary for type [Triple<T, T, T>][Triple]
 * using the existing arbitrary for generating the elements of the triple.
 *
 * @return a new arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T> Arbitrary<T>.triple(): Arbitrary<Triple<T, T, T>> {
    return anyTriple(this, this, this)
}

/**
 * Create a new arbitrary of type [Array<T>] using the existing arbitrary for generating the elements of the array.
 *
 * @param <A> Type of resulting array class
 * @return a new arbitrary instance
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
inline fun <T, reified A: Any> Arbitrary<T>.array(): ArrayArbitrary<T, A> {
    return array(A::class.java)
}
