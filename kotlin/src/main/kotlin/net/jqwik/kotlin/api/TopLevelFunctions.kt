package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary
import org.apiguardian.api.API

/**
 * Return a single generated sample for an arbitrary
 *
 * @param arbitrary The arbitrary to use for generating a sample value
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <T> sample(arbitrary: Arbitrary<T>) : T {
    return arbitrary.sample();
}