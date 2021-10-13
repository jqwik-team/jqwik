package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary
import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.Property
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
    fun anyRangeBetween(@ForAll random: Random) {
        val ranges: Arbitrary<IntRange> = IntRange.any(20..100)

        checkAllGenerated(
            ranges,
            random
        ) { range -> range is IntRange && range.first >= 20 && range.last <= 100}
    }

    @Property(tries = 10)
    fun intRangeForAllParameter(@ForAll range: IntRange) {
        Assertions.assertThat(range).isInstanceOf(IntRange::class.java)
    }
}