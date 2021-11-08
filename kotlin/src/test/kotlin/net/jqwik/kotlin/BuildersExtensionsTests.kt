package net.jqwik.kotlin

import net.jqwik.api.Arbitraries
import net.jqwik.api.Builders
import net.jqwik.api.Example
import net.jqwik.kotlin.api.use
import org.assertj.core.api.Assertions

class BuildersExtensionsTests {

    @Example
    fun `combine 2 arbitraries`() {
        val combined = Builders.withBuilder { PairBuilder() }
            .use(Arbitraries.just(1)) { b, v -> b.first = v; b }
            .use(Arbitraries.just(2)) { b, v -> b.second = v; b }
            .build { it.build() }
        Assertions.assertThat(combined.sample()).isEqualTo(Pair(1, 2))
    }

    private class PairBuilder {
        var first: Int = 0
        var second: Int = 0

        fun build(): Pair<Int, Int> = Pair(first, second)
    }

}