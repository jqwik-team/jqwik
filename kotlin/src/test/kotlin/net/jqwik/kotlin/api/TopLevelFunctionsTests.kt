package net.jqwik.kotlin.api

import net.jqwik.api.Arbitraries
import net.jqwik.api.Example
import org.assertj.core.api.Assertions.assertThat

class TopLevelFunctionsTests {

    @Example
    fun `generate sample`() {
        val stringArbitrary = Arbitraries.strings()
        val sample = sample(stringArbitrary)

        assertThat(sample is String).isTrue
    }

}