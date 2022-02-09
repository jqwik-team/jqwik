package net.jqwik.api.arbitraries

import net.jqwik.api.Arbitrary
import net.jqwik.api.RandomDistribution
import org.apiguardian.api.API
import java.util.function.Function

/**
 * Fluent interface to add functionality to arbitraries that generate instances
 * of type [Iterator]
 */
@API(status = API.Status.MAINTAINED, since = "1.3.2")
interface IteratorArbitrary<out T> : Arbitrary<Iterator<T>?>, StreamableArbitrary<T, Iterator<T>?> {
    /**
     * Fix the size to `size`.
     *
     * @param size The size of the generated iterator
     * @return new arbitrary instance
     */
    override fun ofSize(size: Int): IteratorArbitrary<T> {
        return ofMinSize(size).ofMaxSize(size)
    }

    /**
     * Set lower size boundary `minSize` (included).
     *
     * @param minSize The minimum size of the generated iterator
     * @return new arbitrary instance
     */
    override fun ofMinSize(minSize: Int): IteratorArbitrary<T>

    /**
     * Set upper size boundary `maxSize` (included).
     *
     * @param maxSize The maximum size of the generated iterator
     * @return new arbitrary instance
     */
    override fun ofMaxSize(maxSize: Int): IteratorArbitrary<T>

    /**
     * Set random distribution `distribution` of size of generated iterator.
     * The distribution's center is the minimum size of the generated iterator.
     */
    @API(status = API.Status.EXPERIMENTAL, since = "1.5.3")
    override fun withSizeDistribution(uniform: RandomDistribution): IteratorArbitrary<T>

    /**
     * Add the constraint that elements of the generated iterator must be unique,
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
    fun uniqueElements(): IteratorArbitrary<T>?

    /**
     * Add the constraint that elements of the generated iterator must be unique
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
    fun uniqueElements(by: Function<in T, Any?>?): IteratorArbitrary<T>?
}