package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary
import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.constraints.Size
import net.jqwik.testing.TestingSupport.checkAllGenerated
import org.assertj.core.api.Assertions
import java.util.*

class IntRangeArbitraryTests {

    @Example
    fun anyRange(@ForAll random: Random) {
        val ranges: Arbitrary<IntRange> = IntRange.any()

        checkAllGenerated(
            ranges,
            random
        ) { range -> range is IntRange && range.first <= range.last && range.step == 1}

    }

    @Example
    fun anyWithSize(@ForAll random: Random) {
        val ranges: Arbitrary<IntRange> = IntRange.any(-100..100)
            .ofMinSize(10).ofMaxSize(100)

        checkAllGenerated(
            ranges,
            random
        ) { range ->
            range is IntRange
                && range.first >= -100 && range.last <= 100
                && (range.last - range.first + 1) >= 10
                && (range.last - range.first + 1) <= 100
        }
    }

    @Example
    fun anyWithFixedSize(@ForAll random: Random) {
        val ranges: Arbitrary<IntRange> = IntRange.any(-1000..1000).ofSize(42)

        checkAllGenerated(
            ranges,
            random
        ) { range ->
            range is IntRange
                && range.first >= -1000 && range.last <= 1000
                && (range.last - range.first + 1) == 42
        }
    }

    @Property(tries = 10)
    fun intRangeForAllParameter(@ForAll range: IntRange) {
        Assertions.assertThat(range).isInstanceOf(IntRange::class.java)
    }

    @Property(tries = 10)
    fun intRangeForAllParameterWithRangeAnnotation(@ForAll @JqwikIntRange(min = 10, max = 42) range: IntRange) {
        Assertions.assertThat(range.first).isBetween(10, 42)
        Assertions.assertThat(range.last).isBetween(10, 42)
    }

    @Property(tries = 10)
    fun intRangeForAllParameterWithSizeAnnotation(@ForAll @Size(42) range: IntRange) {
        val size = range.last - range.first + 1
        Assertions.assertThat(size).isEqualTo(42)
    }
}