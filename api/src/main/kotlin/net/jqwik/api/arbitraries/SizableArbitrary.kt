package net.jqwik.api.arbitraries

import net.jqwik.api.Arbitrary
import net.jqwik.api.RandomDistribution
import org.apiguardian.api.API

/**
 * Fluent interface to configure arbitraries that have size constraints for generated values, e.g. collections and arrays.
 */
@API(status = API.Status.MAINTAINED, since = "1.0")
interface SizableArbitrary<out U> : Arbitrary<U> {
    /**
     * Fix the size to `size`.
     */
    fun ofSize(size: Int): SizableArbitrary<U>? {
        return ofMinSize(size).ofMaxSize(size)
    }

    /**
     * Set lower size boundary `minSize` (included).
     */
    fun ofMinSize(minSize: Int): SizableArbitrary<U>

    /**
     * Set upper size boundary `maxSize` (included).
     */
    fun ofMaxSize(maxSize: Int): SizableArbitrary<U>?

    /**
     * Set distribution `distribution` of size of generated arbitrary
     */
    @API(status = API.Status.EXPERIMENTAL, since = "1.5.3")
    fun withSizeDistribution(distribution: RandomDistribution): SizableArbitrary<U>
}