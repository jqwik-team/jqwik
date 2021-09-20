package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary

/**
 * Return a single generated sample for an arbitrary
 *
 * @param arbitrary The arbitrary to use for generating a sample value
 */
fun <T> sample(arbitrary: Arbitrary<T>) : T {
    return arbitrary.sample();
}