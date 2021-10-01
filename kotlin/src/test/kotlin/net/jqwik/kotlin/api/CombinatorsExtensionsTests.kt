package net.jqwik.kotlin.api

import net.jqwik.api.Arbitraries.just
import net.jqwik.api.Example
import org.assertj.core.api.Assertions

class CombinatorsExtensionsTests {

    @Example
    fun `combine 2 arbitraries`() {
        val combined = combine(
            just(1),
            just(2)
        ) { v1, v2 -> v1 + v2 }
        Assertions.assertThat(combined.sample()).isEqualTo(3)
    }

    @Example
    fun `combineFlat 2 arbitraries`() {
        val combined = combineFlat(
            just(1),
            just(2)
        ) { v1, v2 -> just(v1 + v2) }
        Assertions.assertThat(combined.sample()).isEqualTo(3)
    }
}