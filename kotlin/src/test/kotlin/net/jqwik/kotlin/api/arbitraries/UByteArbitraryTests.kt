package net.jqwik.kotlin.api.arbitraries

import net.jqwik.api.Example
import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.kotlin.api.any
import net.jqwik.testing.ShrinkingSupport.falsifyThenShrink
import net.jqwik.testing.TestingSupport.assertAtLeastOneGeneratedOf
import net.jqwik.testing.TestingSupport.checkAllGenerated
import org.assertj.core.api.Assertions.assertThat
import java.util.*

class UByteArbitraryTests {

    @Example
    fun `UByte any()`() {
        val arbitrary = UByte.any()
        assertThat(arbitrary).isInstanceOf(UByteArbitrary::class.java)
    }

    @Property
    fun unsignedBytes(@ForAll anInt: UByte) {
        assertThat(anInt is UByte).isTrue
        assertThat(anInt).isBetween(UByte.MIN_VALUE, UByte.MAX_VALUE)
    }

    @Example
    fun generateAll(@ForAll random: Random) {
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
    fun exhaustiveGeneration() {
        val arbitrary = UByteArbitrary()
        val exhaustiveGenerator = arbitrary.exhaustive()

        assertThat(exhaustiveGenerator).isPresent
        assertThat(exhaustiveGenerator.get().maxCount()).isEqualTo(256)
        assertThat(exhaustiveGenerator.get()).hasSize(256)
        assertThat(exhaustiveGenerator.get()).contains(
            0.toUByte(),
            1.toUByte(),
            254.toUByte(),
            255.toUByte()
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
        assertThat(value).isEqualTo(20.toUByte())
    }

}