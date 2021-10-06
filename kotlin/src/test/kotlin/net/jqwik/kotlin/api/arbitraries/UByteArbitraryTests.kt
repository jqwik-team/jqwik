package net.jqwik.kotlin.api.arbitraries

import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.testing.ShrinkingSupport.falsifyThenShrink
import net.jqwik.testing.TestingSupport.assertAtLeastOneGeneratedOf
import net.jqwik.testing.TestingSupport.checkAllGenerated
import org.assertj.core.api.Assertions
import java.util.*

class UByteArbitraryTests {

    @Example
    fun plainArbitrary(@ForAll random: Random) {
        val arbitrary = UByteArbitrary()

        checkAllGenerated(
            arbitrary.generator(100),
            random
        ) { value -> value >= UByte.MIN_VALUE && value <= UByte.MAX_VALUE }

        assertAtLeastOneGeneratedOf(
            arbitrary.generator(100),
            random,
            UByte.MIN_VALUE, UByte.MAX_VALUE
        )
    }

    @Example
    fun between(@ForAll random: Random) {
        val min = 20.toUByte()
        val max = 199.toUByte()
        val arbitrary = UByteArbitrary().between(min, max)

        checkAllGenerated(
            arbitrary.generator(100),
            random
        ) { value -> value >= min && value <= max }

        assertAtLeastOneGeneratedOf(
            arbitrary.generator(100),
            random,
            min, max
        )
    }

    @Example
    fun shrinkTowards(@ForAll random: Random) {
        val arbitrary = UByteArbitrary().shrinkTowards(20.toUByte())

        checkAllGenerated(
            arbitrary.generator(100),
            random
        ) { value -> value >= UByte.MIN_VALUE && value <= UByte.MAX_VALUE }

        val value = falsifyThenShrink(arbitrary, random)
        Assertions.assertThat(value).isEqualTo(20.toUByte())
    }

}