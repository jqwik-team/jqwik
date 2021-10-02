package net.jqwik.kotlin.api

import net.jqwik.api.*
import net.jqwik.api.Arbitraries.just
import net.jqwik.testing.TestingSupport
import org.assertj.core.api.Assertions.assertThat
import java.util.*
import java.util.function.Consumer

@Group
class CombinatorsExtensionsTests {

    @Group
    inner class Combine {
        @Example
        fun `combine 2 arbitraries`() {
            val combined = combine(
                just(1),
                just(2)
            ) { v1, v2 -> v1 + v2 }
            assertThat(combined.sample()).isEqualTo(3)
        }

        @Example
        fun `combine 3 arbitraries`() {
            val combined = combine(
                just(1),
                just(2),
                just(3)
            ) { v1, v2, v3 -> v1 + v2 + v3 }
            assertThat(combined.sample()).isEqualTo(6)
        }

        @Example
        fun `combine 4 arbitraries`() {
            val combined = combine(
                just(1),
                just(2),
                just(3),
                just(4)
            ) { v1, v2, v3, v4 -> v1 + v2 + v3 + v4 }
            assertThat(combined.sample()).isEqualTo(10)
        }

        @Example
        fun `combine 5 arbitraries`() {
            val combined = combine(
                just(1),
                just(2),
                just(3),
                just(4),
                just(5)
            ) { v1, v2, v3, v4, v5 -> v1 + v2 + v3 + v4 + v5 }
            assertThat(combined.sample()).isEqualTo(15)
        }

        @Example
        fun `combine 6 arbitraries`() {
            val combined = combine(
                just(1),
                just(2),
                just(3),
                just(4),
                just(5),
                just(6)
            ) { v1, v2, v3, v4, v5, v6 -> v1 + v2 + v3 + v4 + v5 + v6 }
            assertThat(combined.sample()).isEqualTo(21)
        }

        @Example
        fun `combine 7 arbitraries`() {
            val combined = combine(
                just(1),
                just(2),
                just(3),
                just(4),
                just(5),
                just(6),
                just(7)
            ) { v1, v2, v3, v4, v5, v6, v7 -> v1 + v2 + v3 + v4 + v5 + v6 + v7 }
            assertThat(combined.sample()).isEqualTo(28)
        }

        @Example
        fun `combine 8 arbitraries`() {
            val combined = combine(
                just(1),
                just(2),
                just(3),
                just(4),
                just(5),
                just(6),
                just(7),
                just(8)
            ) { v1, v2, v3, v4, v5, v6, v7, v8 -> v1 + v2 + v3 + v4 + v5 + v6 + v7 + v8 }
            assertThat(combined.sample()).isEqualTo(36)
        }

        @Example
        fun `combine list of arbitraries`(@ForAll random: Random) {
            val arbitraries: List<Arbitrary<Int>> = listOf(just(1), just(2), just(3))

            val combined = combine(arbitraries) { values: List<Int> -> values.sum() }
            assertAllGeneratedAreEqualTo(combined.generator(1000), random, 6)
        }
    }

    @Group
    inner class CombineFlat {
        @Example
        fun `combineFlat 2 arbitraries`() {
            val combined = combineFlat(
                just(1),
                just(2)
            ) { v1, v2 -> just(v1 + v2) }
            assertThat(combined.sample()).isEqualTo(3)
        }

        @Example
        fun `combineFlat 3 arbitraries`() {
            val combined = combineFlat(
                just(1),
                just(2),
                just(3)
            ) { v1, v2, v3 -> just(v1 + v2 + v3) }
            assertThat(combined.sample()).isEqualTo(6)
        }

        @Example
        fun `combineFlat 4 arbitraries`() {
            val combined = combineFlat(
                just(1),
                just(2),
                just(3),
                just(4)
            ) { v1, v2, v3, v4 -> just(v1 + v2 + v3 + v4) }
            assertThat(combined.sample()).isEqualTo(10)
        }

        @Example
        fun `combineFlat 5 arbitraries`() {
            val combined = combineFlat(
                just(1),
                just(2),
                just(3),
                just(4),
                just(5)
            ) { v1, v2, v3, v4, v5 -> just(v1 + v2 + v3 + v4 + v5) }
            assertThat(combined.sample()).isEqualTo(15)
        }

        @Example
        fun `combineFlat 6 arbitraries`() {
            val combined = combineFlat(
                just(1),
                just(2),
                just(3),
                just(4),
                just(5),
                just(6)
            ) { v1, v2, v3, v4, v5, v6 -> just(v1 + v2 + v3 + v4 + v5 + v6) }
            assertThat(combined.sample()).isEqualTo(21)
        }

        @Example
        fun `combineFlat 7 arbitraries`() {
            val combined = combineFlat(
                just(1),
                just(2),
                just(3),
                just(4),
                just(5),
                just(6),
                just(7)
            ) { v1, v2, v3, v4, v5, v6, v7 -> just(v1 + v2 + v3 + v4 + v5 + v6 + v7) }
            assertThat(combined.sample()).isEqualTo(28)
        }

        @Example
        fun `combineFlat 8 arbitraries`(@ForAll random: Random) {
            val combined = combineFlat(
                just(1),
                just(2),
                just(3),
                just(4),
                just(5),
                just(6),
                just(7),
                just(8)
            ) { v1, v2, v3, v4, v5, v6, v7, v8 -> just(v1 + v2 + v3 + v4 + v5 + v6 + v7 + v8) }
            assertAllGeneratedAreEqualTo(combined.generator(1000), random, 36)
        }

        @Example
        fun `combineFlat list of arbitraries`(@ForAll random: Random) {
            val arbitraries: List<Arbitrary<Int>> = listOf(just(1), just(2), just(3))
            val combined = combineFlat(arbitraries) { values: List<Int> -> just(values.sum()) }
            assertAllGeneratedAreEqualTo(combined.generator(1000), random, 6)
        }

    }

    private fun assertAllGeneratedAreEqualTo(
        generator: RandomGenerator<Int>,
        random: Random,
        expected: Int
    ) {
        TestingSupport.assertAllGenerated(
            generator,
            random,
            Consumer { value: Int -> assertThat(value).isEqualTo(expected) })
    }
}
