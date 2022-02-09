package net.jqwik.api.arbitraries

import net.jqwik.api.RandomDistribution
import org.apiguardian.api.API
import java.util.function.Function

/**
 * Fluent interface to add functionality to arbitraries that generate instances
 * of Arrays
 */
@API(status = API.Status.MAINTAINED, since = "1.4.0")
interface ArrayArbitrary<out T, A> : StreamableArbitrary<T, A> {
    /**
     * Fix the size to `size`.
     *
     * @param size The size of the generated set
     * @return new arbitrary instance
     */
    override fun ofSize(size: Int): ArrayArbitrary<T, A> {
        return ofMinSize(size).ofMaxSize(size)
    }

    /**
     * Set lower size boundary `minSize` (included).
     *
     * @param minSize The minimum size of the generated set
     * @return new arbitrary instance
     */
    override fun ofMinSize(minSize: Int): ArrayArbitrary<T, A>

    /**
     * Set upper size boundary `maxSize` (included).
     *
     * @param maxSize The maximum size of the generated set
     * @return new arbitrary instance
     */
    override fun ofMaxSize(maxSize: Int): ArrayArbitrary<T, A>

    /**
     * Set random distribution `distribution` of size of generated array.
     * The distribution's center is the minimum size of the generated array.
     */
    @API(status = API.Status.EXPERIMENTAL, since = "1.5.3")
    override fun withSizeDistribution(uniform: RandomDistribution): ArrayArbitrary<T, A>

    /**
     * Add the constraint that elements of the generated array must be unique,
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
    fun uniqueElements(): ArrayArbitrary<T, A>?

    /**
     * Add the constraint that elements of the generated array must be unique
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
    fun uniqueElements(by: Function<in T, Any?>?): ArrayArbitrary<T, A>?
}