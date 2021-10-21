package net.jqwik.kotlin

import net.jqwik.api.Arbitrary
import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.constraints.StringLength
import net.jqwik.kotlin.api.JqwikIntRange
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.anyPair
import net.jqwik.kotlin.api.pair
import net.jqwik.testing.TestingSupport.checkAllGenerated
import org.assertj.core.api.Assertions
import java.util.*

class KotlinPairTests {

    @Example
    fun pairFromArbitraries(@ForAll random: Random) {
        val numbers = Int.any()
        val strings = String.any()
        val pairs: Arbitrary<Pair<Int, String>> = anyPair(numbers, strings)

        checkAllGenerated(
            pairs,
            random
        ) { pair -> pair is Pair<Int, String> && pair.first is Int && pair.second is String }
    }

    @Example
    fun pairFromSingleArbitrary(@ForAll random: Random) {
        val numbers = Int.any().between(0, 1000)
        val pairs: Arbitrary<Pair<Int, Int>> = numbers.pair()

        checkAllGenerated(
            pairs,
            random
        ) { pair -> pair.first in (0..1000) && pair.second in (0..10000) }
    }

    @Property
    fun pairAsForAllParameter(@ForAll pair: Pair<@JqwikIntRange(max = 1000) Int, @StringLength(5) String>) {
        Assertions.assertThat(pair.first).isBetween(0, 1000)
        Assertions.assertThat(pair.second).hasSize(5)
    }

}
