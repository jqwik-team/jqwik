package net.jqwik.kotlin.api

import net.jqwik.api.Arbitraries
import net.jqwik.api.Arbitrary
import net.jqwik.api.arbitraries.SetArbitrary
import org.apiguardian.api.API

/**
 * Convenience function to replace Arbitraries.subsetOf(..)
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.5")
fun <T> Collection<T & Any>.anySubset(): SetArbitrary<T & Any> = Arbitraries.subsetOf(this)

/**
 * Convenience function to replace Arbitraries.of(..)
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.7.1")
fun <T> Collection<T & Any>.anyValue(): Arbitrary<T & Any> = Arbitraries.of(this)
