package net.jqwik.kotlin.api

import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.constraints.Size
import net.jqwik.testing.TestingSupport.checkAllGenerated
import org.assertj.core.api.Assertions.assertThat
import java.util.*

class SequencesTests {

    @Example
    fun sequenceFromArbitrary(@ForAll random: Random) {
        val sequences = Int.any(1..10).sequence()

        checkAllGenerated(
            sequences,
            random
        ) { sequence ->
            sequence is Sequence<Int> && sequence.all { i -> i in 1..10 }
        }
    }

    @Example
    fun sequenceWithSize(@ForAll random: Random) {
        val sequences = Int.any(1..10).sequence().ofSize(5)

        checkAllGenerated(
            sequences,
            random
        ) { sequence ->
            sequence is Sequence<Int>
                && sequence.toList().size == 5
                && sequence.all { i -> i in 1..10 }
        }
    }

    @Property(tries = 10)
    fun sequenceAsForAllParameter(@ForAll sequence: Sequence<@JqwikIntRange(min = 1, max = 10) Int>) {
        assertThat(sequence.all { i -> i in 1..10 }).isTrue()
    }

    @Property(tries = 10)
    fun sequenceAsForAllParameterWithSize(@ForAll @Size(min = 1, max = 5) sequence: Sequence<Int>) {
        assertThat(sequence.toList()).hasSizeBetween(1, 5)
    }
}