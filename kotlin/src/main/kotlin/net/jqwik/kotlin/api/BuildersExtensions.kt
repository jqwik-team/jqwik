package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary
import net.jqwik.api.Builders
import net.jqwik.api.Builders.BuilderCombinator
import org.apiguardian.api.API
import java.util.function.BiFunction

/**
 * Convenience function for Kotlin to not use backticked `in` function.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
fun <B, T> BuilderCombinator<B & Any>.use(arbitrary: Arbitrary<T>, combinator: (B, T) -> B): BuilderCombinator<B> {
    // This explicit conversion form combinator to toFunction is necessary
    // since the implicit conversion (just handing in combinator) creates nullability warning
    val toFunction: BiFunction<B & Any, T?, B & Any> = BiFunction<B & Any, T?, B & Any> { b, t -> combinator(b, t) }
    return this.use(arbitrary as Arbitrary<T?>).`in`(toFunction) as BuilderCombinator<B>
}
