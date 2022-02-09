package net.jqwik.api.arbitraries

import net.jqwik.api.Arbitrary
import net.jqwik.api.RandomDistribution
import org.apiguardian.api.API
import java.util.function.BiFunction
import java.util.function.Function

/**
 * Fluent interface to add functionality to arbitraries that generate instances
 * of type [List]
 */
@API(status = API.Status.MAINTAINED, since = "1.3.2")
interface ListArbitrary<out T> : Arbitrary<List<T>>, StreamableArbitrary<T, List<T>> {
    /**
     * Fix the size to `size`.
     *
     * @param size The size of the generated list
     * @return new arbitrary instance
     */
    override fun ofSize(size: Int): ListArbitrary<T> {
        return ofMinSize(size).ofMaxSize(size)
    }

    /**
     * Set lower size boundary `minSize` (included).
     *
     * @param minSize The minimum size of the generated list
     * @return new arbitrary instance
     */
    override fun ofMinSize(minSize: Int): ListArbitrary<T>

    /**
     * Set upper size boundary `maxSize` (included).
     *
     * @param maxSize The maximum size of the generated list
     * @return new arbitrary instance
     */
    override fun ofMaxSize(maxSize: Int): ListArbitrary<T>

    /**
     * Set random distribution `distribution` of size of generated list.
     * The distribution's center is the minimum size of the generated list.
     */
    @API(status = API.Status.EXPERIMENTAL, since = "1.5.3")
    override fun withSizeDistribution(uniform: RandomDistribution): ListArbitrary<T>

    /**
     * Map over each element of the generated list giving access to all elements of the list.
     * The number and position of the mapped elements within the list will stay the same.
     *
     * @param <U> The target type of a list to generate
     * @param mapper Mapper function to element type U
     * @return arbitrary of a list of Us
    </U> */
    @API(status = API.Status.MAINTAINED, since = "1.4.0")
    fun <U> mapEach(mapper: BiFunction<in List<T>, in T, out U>): Arbitrary<List<U>>

    /**
     * Flat-map over each element of the generated list giving access to all elements of the list.
     * The number and position of the mapped elements within the list will stay the same.
     *
     * @param <U> The target type of a list to generate
     * @param flatMapper Mapper function to arbitrary of element type U
     * @return arbitrary of a list of Us
    </U> */
    @API(status = API.Status.MAINTAINED, since = "1.4.0")
    fun <U> flatMapEach(flatMapper: BiFunction<in List<T>, in T, out Arbitrary<U>>): Arbitrary<List<U>>

    /**
     * Add the constraint that elements of the generated list must be unique,
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
    fun uniqueElements(): ListArbitrary<T>

    /**
     * Add the constraint that elements of the generated list must be unique
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
    fun uniqueElements(by: Function<in T, Any>): ListArbitrary<T>
}