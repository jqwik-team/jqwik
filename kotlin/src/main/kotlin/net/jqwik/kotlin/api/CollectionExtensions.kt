package net.jqwik.kotlin.api

import net.jqwik.api.Arbitraries
import net.jqwik.api.arbitraries.SetArbitrary
import org.apiguardian.api.API

/**
 * Convenience function to replace Arbitraries.subsetOf(..)
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.5")
fun <T> Collection<T>.anySubset(): SetArbitrary<T> = Arbitraries.subsetOf(this)
