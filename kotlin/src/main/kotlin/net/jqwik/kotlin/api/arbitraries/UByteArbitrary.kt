package net.jqwik.kotlin.api.arbitraries

import net.jqwik.api.Arbitrary
import net.jqwik.api.arbitraries.ArbitraryDecorator
import net.jqwik.api.arbitraries.ByteArbitrary
import net.jqwik.kotlin.api.any
import org.apiguardian.api.API
import java.math.BigInteger

/**
 * Fluent interface to configure the generation of UByte values.
 */
@API(status = API.Status.EXPERIMENTAL, since = "1.6.0")
class UByteArbitrary : ArbitraryDecorator<UByte>() {

    private val base = Int.any().between(UByte.MIN_VALUE.toInt(), UByte.MAX_VALUE.toInt())

    override fun arbitrary(): Arbitrary<UByte> = base.map() { b -> b.toUByte() }

    /**
     * Set the allowed lower `min` (included) and upper `max` (included) bounds of generated numbers.
     *
     * @param min min value (included)
     * @param max max value (included)
     * @return new instance of arbitrary
     */
    fun between(min: UByte, max: UByte): UByteArbitrary {
        return greaterOrEqual(min).lessOrEqual(max)
    }

    /**
     * Set the allowed lower `min` (included) bound of generated numbers.
     *
     * @param min min value (included)
     * @return new instance of arbitrary
     */
    fun greaterOrEqual(min: UByte): UByteArbitrary {
        val clone: UByteArbitrary = this.clone() as UByteArbitrary
        clone.base.greaterOrEqual(min.toInt())
        return clone
    }

    /**
     * Set the allowed upper `max` (included) bound of generated numbers.
     *
     * @param max max value (included)
     * @return new instance of arbitrary
     */
    fun lessOrEqual(max: UByte): UByteArbitrary {
        val clone: UByteArbitrary = this.clone() as UByteArbitrary
        clone.base.lessOrEqual(max.toInt())
        return clone
    }

    /**
     * Set shrinking target to {@code target} which must be between the allowed bounds.
     *
     * @param target target shrinking value
     * @return new instance of arbitrary
     */
    fun shrinkTowards(target: UByte): UByteArbitrary {
        val clone: UByteArbitrary = this.clone() as UByteArbitrary
        clone.base.shrinkTowards(target.toInt())
        return clone
    }


}