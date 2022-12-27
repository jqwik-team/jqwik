package net.jqwik.engine.properties.shrinking;

import java.math.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;

class ShrinkTowardsTests {

	@Property(tries = 50)
	void bytes(@ForAll JqwikRandom random, @ForAll byte target) {
		Arbitrary<Byte> bytes = Arbitraries.bytes().shrinkTowards(target);
		byte shrunkValue = falsifyThenShrink(bytes, random);
		assertThat(shrunkValue).isEqualTo(target);
	}

	@Property(tries = 50)
	void shorts(@ForAll JqwikRandom random, @ForAll short target) {
		Arbitrary<Short> shorts = Arbitraries.shorts().shrinkTowards(target);
		short shrunkValue = falsifyThenShrink(shorts, random);
		assertThat(shrunkValue).isEqualTo(target);
	}

	@Property(tries = 50)
	void integers(@ForAll JqwikRandom random, @ForAll int target) {
		Arbitrary<Integer> integers = Arbitraries.integers().shrinkTowards(target);
		int shrunkValue = falsifyThenShrink(integers, random);
		assertThat(shrunkValue).isEqualTo(target);
	}

	@Property(tries = 50)
	void longs(@ForAll JqwikRandom random, @ForAll long target) {
		Arbitrary<Long> longs = Arbitraries.longs().shrinkTowards(target);
		long shrunkValue = falsifyThenShrink(longs, random);
		assertThat(shrunkValue).isEqualTo(target);
	}

	@Property(tries = 50)
	void bigIntegers(@ForAll JqwikRandom random, @ForAll BigInteger target) {
		Arbitrary<BigInteger> bigs = Arbitraries.bigIntegers().shrinkTowards(target);
		BigInteger shrunkValue = falsifyThenShrink(bigs, random);
		assertThat(shrunkValue).isEqualTo(target);
	}

	@Property(tries = 10)
	void floats(@ForAll JqwikRandom random, @ForAll @FloatRange(min = -10000, max = 10000) @Scale(0) float target) {
		Arbitrary<Float> floats = Arbitraries.floats().shrinkTowards(target);
		float shrunkValue = falsifyThenShrink(floats, random);
		assertThat(shrunkValue).isEqualTo(target);
	}

	@Property(tries = 10)
	void doubles(@ForAll JqwikRandom random, @ForAll @DoubleRange(min = -10000, max = 10000) @Scale(0) double target) {
		Arbitrary<Double> doubles = Arbitraries.doubles().shrinkTowards(target);
		double shrunkValue = falsifyThenShrink(doubles, random);
		assertThat(shrunkValue).isEqualTo(target);
	}

	@Property(tries = 10)
	void bigDecimals(@ForAll JqwikRandom random, @ForAll @BigRange(min = "-1000", max = "1000") @Scale(0) BigDecimal target) {
		Arbitrary<BigDecimal> bigDecimals = Arbitraries.bigDecimals().shrinkTowards(target);
		BigDecimal shrunkValue = falsifyThenShrink(bigDecimals, random);
		assertThat(shrunkValue).isEqualByComparingTo(target);
	}

}
