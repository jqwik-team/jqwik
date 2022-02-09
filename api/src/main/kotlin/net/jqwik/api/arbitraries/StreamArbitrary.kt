package net.jqwik.api.arbitraries

import net.jqwik.api.Arbitrary
import net.jqwik.api.RandomDistribution
import org.apiguardian.api.API
import java.util.function.Function
import java.util.stream.Stream

/**
 * Fluent interface to add functionality to arbitraries that generate instances
 * of type [Stream]
 */
@API(status = API.Status.MAINTAINED, since = "1.3.2")
interface StreamArbitrary<out T> : Arbitrary<Stream<@UnsafeVariance T>?>, StreamableArbitrary<T, Stream<@UnsafeVariance T>?> {
    /**
     * Fix the size to `size`.
     *
     * @param size The size of the generated stream
     * @return new arbitrary instance
     */
    override fun ofSize(size: Int): StreamArbitrary<T> {
        return ofMinSize(size).ofMaxSize(size)
    }

    /**
     * Set lower size boundary `minSize` (included).
     *
     * @param minSize The minimum size of the generated stream
     * @return new arbitrary instance
     */
    override fun ofMinSize(minSize: Int): StreamArbitrary<T>

    /**
     * Set upper size boundary `maxSize` (included).
     *
     * @param maxSize The maximum size of the generated stream
     * @return new arbitrary instance
     */
    override fun ofMaxSize(maxSize: Int): StreamArbitrary<T>

    /**
     * Set random distribution `distribution` of size of generated stream.
     * The distribution's center is the minimum size of the generated stream.
     */
    @API(status = API.Status.EXPERIMENTAL, since = "1.5.3")
    override fun withSizeDistribution(uniform: RandomDistribution): StreamArbitrary<T>

    /**
     * Add the constraint that elements of the generated stream must be unique,
     * i.e. no two elements must return true when being compared using [Object.equals].
     *
     *
     *
     * The constraint can be combined with other [.uniqueElements] constraints.
     *
     *
     * @return new arbitrary instance
     */
    @API(status = API.Status.MAINTAINED, since = "1.4.0")
    fun uniqueElements(): StreamArbitrary<T>?

    /**
     * Add the constraint that elements of the generated stream must be unique
     * relating to an element's "feature" being extracted using the
     * `by` function.
     * The extracted features are being compared using [Object.equals].
     *
     *
     *
     * The constraint can be combined with other [.uniqueElements] constraints.
     *
     *
     * @return new arbitrary instance
     */
    @API(status = API.Status.MAINTAINED, since = "1.4.0")
    fun uniqueElements(by: Function<in T, out Any?>?): StreamArbitrary<T>?
}