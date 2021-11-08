package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary
import net.jqwik.api.Builders
import net.jqwik.api.Builders.CombinableBuilder
import org.apiguardian.api.API

/**
 * Convenience function for Kotlin to not use quoted `in` function.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <B, T> Builders.BuilderCombinator<B>.use(arbitrary: Arbitrary<T>, combinator: Function2<B, T, B>): Builders.BuilderCombinator<B> {
    return this.use(arbitrary).`in`(combinator)
}
