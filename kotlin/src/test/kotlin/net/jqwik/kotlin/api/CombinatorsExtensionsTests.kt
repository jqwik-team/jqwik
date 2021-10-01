package net.jqwik.kotlin.api

import net.jqwik.api.Arbitraries.just
import net.jqwik.api.Arbitrary
import net.jqwik.api.Example
import net.jqwik.api.Group
import org.assertj.core.api.Assertions

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
            Assertions.assertThat(combined.sample()).isEqualTo(3)
        }

        @Example
        fun `combine 3 arbitraries`() {
            val combined = combine(
                just(1),
                just(2),
                just(3)
            ) { v1, v2, v3 -> v1 + v2 + v3}
            Assertions.assertThat(combined.sample()).isEqualTo(6)
        }

        @Example
        fun `combine 4 arbitraries`() {
            val combined = combine(
                just(1),
                just(2),
                just(3),
                just(4)
            ) { v1, v2, v3, v4 -> v1 + v2 + v3 + v4}
            Assertions.assertThat(combined.sample()).isEqualTo(10)
        }

        @Example
        fun `combine 5 arbitraries`() {
            val combined = combine(
                just(1),
                just(2),
                just(3),
                just(4),
                just(5)
            ) { v1, v2, v3, v4, v5 -> v1 + v2 + v3 + v4 + v5}
            Assertions.assertThat(combined.sample()).isEqualTo(15)
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
            ) { v1, v2, v3, v4, v5, v6 -> v1 + v2 + v3 + v4 + v5 + v6}
            Assertions.assertThat(combined.sample()).isEqualTo(21)
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
            ) { v1, v2, v3, v4, v5, v6, v7 -> v1 + v2 + v3 + v4 + v5 + v6 + v7}
            Assertions.assertThat(combined.sample()).isEqualTo(28)
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
            ) { v1, v2, v3, v4, v5, v6, v7, v8 -> v1 + v2 + v3 + v4 + v5 + v6 + v7 + v8}
            Assertions.assertThat(combined.sample()).isEqualTo(36)
        }

        @Example
        fun `combine list of arbitraries`() {
            val arbitraries : List<Arbitrary<Int>> = listOf(just(1), just(2), just(3))

            val combined = combine(arbitraries) {values:List<Int> -> values.sum() }
            Assertions.assertThat(combined.sample()).isEqualTo(6)
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
            Assertions.assertThat(combined.sample()).isEqualTo(3)
        }
    }
}
