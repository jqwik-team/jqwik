package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary
import net.jqwik.api.arbitraries.ArbitraryDecorator

data class IntRangeArbitrary(private val min: Int, private val max: Int) : ArbitraryDecorator<IntRange>() {
    constructor() : this(Int.MIN_VALUE, Int.MAX_VALUE)

    override fun arbitrary(): Arbitrary<IntRange> {
        val mins = Int.any(min..max)
        return mins.flatMap { minValue -> Int.any(minValue..max).map { maxValue -> (minValue..maxValue) } }
    }

    fun between(min: Int, max: Int): IntRangeArbitrary {
        if (min > max) {
            val message = String.format("min=%s must be less or equal to max=%s", min, max)
            throw IllegalArgumentException(message)
        }
        return IntRangeArbitrary(min, max)
    }

}
