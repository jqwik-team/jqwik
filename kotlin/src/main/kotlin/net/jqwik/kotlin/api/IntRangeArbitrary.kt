package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary
import net.jqwik.api.RandomDistribution
import net.jqwik.api.arbitraries.ArbitraryDecorator
import net.jqwik.api.arbitraries.SizableArbitrary

data class IntRangeArbitrary(
    private val min: Int, private val max: Int,
    private val minSize: Int, private val maxSize: Int
) : ArbitraryDecorator<IntRange>(), SizableArbitrary<IntRange> {
    constructor() : this(Int.MIN_VALUE, Int.MAX_VALUE, 1, 0)

    override fun arbitrary(): Arbitrary<IntRange> {
        val minDiff = this.minSize - 1
        val effectiveMaxSize =
            if (maxSize > 0) maxSize.toLong() else (Int.MAX_VALUE.toLong() - Int.MIN_VALUE.toLong())
        val mins = Int.any(min..(max - minDiff))
        return mins.flatMap { minValue ->
            val maxDiff: Long = Math.min(max.toLong() - minValue.toLong(), effectiveMaxSize - 1)
            Long.any(minDiff..maxDiff).map { diff -> (minValue..(minValue + diff).toInt()) }
        }
    }

    fun between(min: Int, max: Int): IntRangeArbitrary {
        if (min > max) {
            val message = String.format("min=%s must be less or equal to max=%s", min, max)
            throw IllegalArgumentException(message)
        }
        return copy(min = min, max = max)
    }

    /**
     * Fix the size (the difference between first and last value of a range).
     *
     * @param size The size of the generated IntRange
     * @return new arbitrary instance
     */
    override fun ofSize(size: Int): IntRangeArbitrary {
        return ofMinSize(size).ofMaxSize(size)
    }

    /**
     * Set lower size boundary `minSize` (included).
     *
     * @param minSize The minimum size of the generated IntRange
     * @return new arbitrary instance
     */
    override fun ofMinSize(minSize: Int): IntRangeArbitrary {
        if (minSize < 1) {
            val message = String.format("minSize=%s must be greater than 0", minSize)
            throw IllegalArgumentException(message)
        }
        return copy(minSize = minSize)
    }

    /**
     * Set upper size boundary `maxSize` (included).
     *
     * @param maxSize The maximum size of the generated IntRange
     * @return new arbitrary instance
     */
    override fun ofMaxSize(maxSize: Int): IntRangeArbitrary {
        if (maxSize < minSize) {
            val message = String.format("maxSize=%s must be greater or equal to minSize=%s", maxSize, minSize)
            throw IllegalArgumentException(message)
        }
        return copy(maxSize = maxSize)
    }

    override fun withSizeDistribution(distribution: RandomDistribution): SizableArbitrary<IntRange> {
        throw NotImplementedError()
    }

}
