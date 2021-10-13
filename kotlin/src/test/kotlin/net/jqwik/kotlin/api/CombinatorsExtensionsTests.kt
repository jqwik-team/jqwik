package net.jqwik.kotlin.api

import net.jqwik.api.Arbitraries.just
import net.jqwik.api.Arbitrary
import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.Group
import net.jqwik.testing.TestingSupport.*
import org.assertj.core.api.Assertions.assertThat
import java.util.*

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
        fun `combine nullable arbitraries`(@ForAll random: Random) {
            val combined = combine(
                just(1).orNull(0.5),
                just(2)
            ) { v1, v2 -> (v1 ?: 0) + v2 }

            val generator = combined.generator(1000)
            checkAllGenerated(
                generator,
                random
            ) { value -> value == 2 || value == 3 }

            assertAtLeastOneGeneratedOf(generator, random, 2)
            assertAtLeastOneGeneratedOf(generator, random, 3)
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
            assertAllGeneratedEqualTo(
                combined,
                random,
                6
            )
        }
    }

    @Group
    inner class FlatCombine {
        @Example
        fun `flatCombine 2 arbitraries`() {
            val combined = flatCombine(
                just(1),
                just(2)
            ) { v1, v2 -> just(v1 + v2) }
            assertThat(combined.sample()).isEqualTo(3)
        }

        @Example
        fun `flatCombine 3 arbitraries`() {
            val combined = flatCombine(
                just(1),
                just(2),
                just(3)
            ) { v1, v2, v3 -> just(v1 + v2 + v3) }
            assertThat(combined.sample()).isEqualTo(6)
        }

        @Example
        fun `flatCombine 4 arbitraries`() {
            val combined = flatCombine(
                just(1),
                just(2),
                just(3),
                just(4)
            ) { v1, v2, v3, v4 -> just(v1 + v2 + v3 + v4) }
            assertThat(combined.sample()).isEqualTo(10)
        }

        @Example
        fun `flatCombine 5 arbitraries`() {
            val combined = flatCombine(
                just(1),
                just(2),
                just(3),
                just(4),
                just(5)
            ) { v1, v2, v3, v4, v5 -> just(v1 + v2 + v3 + v4 + v5) }
            assertThat(combined.sample()).isEqualTo(15)
        }

        @Example
        fun `flatCombine 6 arbitraries`() {
            val combined = flatCombine(
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
        fun `flatCombine 7 arbitraries`() {
            val combined = flatCombine(
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
        fun `flatCombine 8 arbitraries`(@ForAll random: Random) {
            val combined = flatCombine(
                just(1),
                just(2),
                just(3),
                just(4),
                just(5),
                just(6),
                just(7),
                just(8)
            ) { v1, v2, v3, v4, v5, v6, v7, v8 -> just(v1 + v2 + v3 + v4 + v5 + v6 + v7 + v8) }
            assertAllGeneratedEqualTo(
                combined.generator(1000),
                random,
                36
            )
        }

        @Example
        fun `flatCombine list of arbitraries`(@ForAll random: Random) {
            val arbitraries: List<Arbitrary<Int>> = listOf(just(1), just(2), just(3))
            val combined = flatCombine(arbitraries) { values: List<Int> -> just(values.sum()) }
            assertAllGeneratedEqualTo(
                combined.generator(1000),
                random,
                6
            )
        }

    }

}
