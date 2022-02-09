package net.jqwik.api.arbitraries

import net.jqwik.api.Arbitrary
import net.jqwik.api.RandomDistribution
import org.apiguardian.api.API
import java.util.function.BiFunction

/**
 * Fluent interface to add functionality to arbitraries whose generation artefacts
 * can be streamed, e.g. [List], [Set], [Stream] and Arrays
 */
@API(status = API.Status.MAINTAINED, since = "1.2.1")
interface StreamableArbitrary<out T, out U> : SizableArbitrary<U> {
    /**
     * Given an `initial` argument use `accumulator` to produce
     * the final result.
     *
     * @param initial     The initial argument. Also the result if streamable is empty
     * @param accumulator The function used to reduce a streamable into a result one by one
     * @param <R>         The result type
     * @return The result of accumulating all elements in streamable
    </R> */
    fun <R> reduce(initial: R, accumulator: BiFunction<in R, in T, out R>): Arbitrary<R>

    /**
     * Fix the size to `size`.
     */
    override fun ofSize(size: Int): StreamableArbitrary<T, U> {
        return ofMinSize(size).ofMaxSize(size)
    }

    /**
     * Set lower size boundary `minSize` (included).
     */
    override fun ofMinSize(minSize: Int): StreamableArbitrary<T, U>

    /**
     * Set upper size boundary `maxSize` (included).
     */
    override fun ofMaxSize(maxSize: Int): StreamableArbitrary<T, U>

    /**
     * Set distribution `distribution` of size of generated arbitrary
     */
    @API(status = API.Status.EXPERIMENTAL, since = "1.5.3")
    override fun withSizeDistribution(distribution: RandomDistribution): StreamableArbitrary<T, U>
}