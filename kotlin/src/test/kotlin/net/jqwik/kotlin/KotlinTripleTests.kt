package net.jqwik.kotlin

import net.jqwik.api.Arbitrary
import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.constraints.NumericChars
import net.jqwik.api.constraints.StringLength
import net.jqwik.kotlin.api.JqwikIntRange
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.anyTriple
import net.jqwik.kotlin.api.triple
import net.jqwik.testing.TestingSupport.checkAllGenerated
import org.assertj.core.api.Assertions
import java.util.*

class KotlinTripleTests {

    @Example
    fun tripleFromArbitraries(@ForAll random: Random) {
        val numbers = Int.any()
        val strings = String.any()
        val chars = Char.any()
        val triples: Arbitrary<Triple<Int, String, Char>> = anyTriple(numbers, strings, chars)

        checkAllGenerated(
            triples,
            random
        ) { triple -> triple is Triple<Int, String, Char>
            && triple.first is Int && triple.second is String && triple.third is Char}
    }

    @Example
    fun tripleFromSingleArbitrary(@ForAll random: Random) {
        val numbers = Int.any().between(0, 1000)
        val triples: Arbitrary<Triple<Int, Int, Int>> = numbers.triple()

        checkAllGenerated(
            triples,
            random
        ) { triple -> triple.first in (0..1000) && triple.second in (0..1000) && triple.third in (0..1000) }
    }

    @Property
    fun tripleAsForAllParameter(@ForAll triple: Triple<@JqwikIntRange(max = 1000) Int, @StringLength(5) String, @NumericChars Char>) {
        Assertions.assertThat(triple.first).isBetween(0, 1000)
        Assertions.assertThat(triple.second).hasSize(5)
        Assertions.assertThat(triple.third in ('0'..'9')).isTrue
    }

}
