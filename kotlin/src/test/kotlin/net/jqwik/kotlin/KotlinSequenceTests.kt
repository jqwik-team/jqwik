package net.jqwik.kotlin

import net.jqwik.api.*
import net.jqwik.api.constraints.Size
import net.jqwik.api.constraints.UniqueElements
import net.jqwik.kotlin.api.JqwikIntRange
import net.jqwik.kotlin.api.any
import net.jqwik.kotlin.api.orNull
import net.jqwik.kotlin.api.sequence
import net.jqwik.testing.TestingSupport.checkAllGenerated
import org.assertj.core.api.Assertions.assertThat
import java.util.*

class KotlinSequenceTests {

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
    fun sequenceFromNullableArbitrary(@ForAll random: Random) {
        val sequences = Int.any(1..10).orNull(0.1).sequence()

        checkAllGenerated(
            sequences,
            random
        ) { sequence ->
            sequence is Sequence<Int?> && sequence.all { i -> i == null || i in 1..10  }
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

    @Example
    fun sequenceWithSizeRange(@ForAll random: Random) {
        val sequences = Int.any(1..10).sequence().ofSize(5..10)

        checkAllGenerated(
            sequences,
            random
        ) { sequence ->
            sequence is Sequence<Int>
                && sequence.toList().size >= 5
                && sequence.toList().size <= 10
                && sequence.all { i -> i in 1..10 }
        }
    }

    @Example
    fun sequenceWithUniqueElements(@ForAll random: Random) {
        val sequences = Int.any(1..10).sequence().uniqueElements()

        checkAllGenerated(
            sequences,
            random
        ) { sequence -> sequence.toList().size == sequence.toSet().size }
    }

    @Property(tries = 10)
    fun sequenceAsForAllParameter(@ForAll sequence: Sequence<@JqwikIntRange(min = 1, max = 10) Int>) {
        assertThat(sequence.all { i -> i in 1..10 }).isTrue
    }

    @Property(tries = 10)
    fun sequenceAsForAllParameterWithSize(@ForAll @Size(min = 1, max = 5) sequence: Sequence<Int>) {
        assertThat(sequence.toList()).hasSizeBetween(1, 5)
    }

    @Property(tries = 10)
    fun sequenceAsForAllParameterWithUniqueness(@ForAll @UniqueElements sequence: Sequence<Int>) {
        assertThat(sequence.toList().size).isEqualTo(sequence.toSet().size)
    }

    @Property(tries = 10)
    fun providedSequenceWithUniqueness(@ForAll("sequences") @UniqueElements sequence: Sequence<Int>) {
        assertThat(sequence.toList().size).isEqualTo(sequence.toSet().size)
    }

    @Provide
    fun sequences() : Arbitrary<Sequence<Int>> = Int.any().sequence()
}