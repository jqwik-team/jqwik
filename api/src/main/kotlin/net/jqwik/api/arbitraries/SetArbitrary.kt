package net.jqwik.api.arbitraries

import net.jqwik.api.Arbitrary
import net.jqwik.api.RandomDistribution
import org.apiguardian.api.API
import java.util.function.BiFunction
import java.util.function.Function

/**
 * Fluent interface to add functionality to arbitraries that generate instances
 * of type [Set]
 */
@API(status = API.Status.MAINTAINED, since = "1.3.2")
interface SetArbitrary<out T> : Arbitrary<Set<T>?>, StreamableArbitrary<T, Set<T>?> {
    /**
     * Fix the size to `size`.
     *
     * @param size The size of the generated set
     * @return new arbitrary instance
     */
    override fun ofSize(size: Int): SetArbitrary<T> {
        return ofMinSize(size).ofMaxSize(size)
    }

    /**
     * Set lower size boundary `minSize` (included).
     *
     * @param minSize The minimum size of the generated set
     * @return new arbitrary instance
     */
    override fun ofMinSize(minSize: Int): SetArbitrary<T>

    /**
     * Set upper size boundary `maxSize` (included).
     *
     * @param maxSize The maximum size of the generated set
     * @return new arbitrary instance
     */
    override fun ofMaxSize(maxSize: Int): SetArbitrary<T>

    /**
     * Set random distribution `distribution` of size of generated set.
     * The distribution's center is the minimum size of the generated set.
     */
    @API(status = API.Status.EXPERIMENTAL, since = "1.5.3")
    override fun withSizeDistribution(uniform: RandomDistribution): SetArbitrary<T>

    /**
     * Map over each element of the generated set giving access to all elements of the set.
     * The number of the mapped elements within the set will stay the same.
     *
     * @param <U> The target type of a set to generate
     * @param mapper Mapper function to element type U
     * @return arbitrary of a set of Us
    </U> */
    @API(status = API.Status.MAINTAINED, since = "1.4.0")
    fun <U> mapEach(mapper: BiFunction<in Set<T>?, in T, out U>?): Arbitrary<Set<U>?>?

    /**
     * Flat-map over each element of the generated set giving access to all elements of the set.
     * The number of the mapped elements within the set will stay the same.
     *
     * @param <U> The target type of a set to generate
     * @param flatMapper Mapper function to arbitrary of element type U
     * @return arbitrary of a set of Us
    </U> */
    @API(status = API.Status.MAINTAINED, since = "1.4.0")
    fun <U> flatMapEach(flatMapper: BiFunction<in Set<T>?, in T, out Arbitrary<U>?>?): Arbitrary<Set<U>?>?

    /**
     * Add the constraint that elements of the generated set must be unique
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
    fun uniqueElements(by: Function<in T, out Any?>?): SetArbitrary<T>?
}