package net.jqwik.engine.properties.shrinking;

import java.math.*;
import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

class ShrinkTowardsTests {

	@Property(tries = 50)
	void bytes(@ForAll Random random, @ForAll byte target) {
		Arbitrary<Byte> bytes = Arbitraries.bytes().shrinkTowards(target);
		byte shrunkValue = ArbitraryTestHelper.shrinkToEnd(bytes, random);
		Assertions.assertThat(shrunkValue).isEqualTo(target);
	}

	@Property(tries = 50)
	void shorts(@ForAll Random random, @ForAll short target) {
		Arbitrary<Short> shorts = Arbitraries.shorts().shrinkTowards(target);
		short shrunkValue = ArbitraryTestHelper.shrinkToEnd(shorts, random);
		Assertions.assertThat(shrunkValue).isEqualTo(target);
	}

	@Property(tries = 50)
	void integers(@ForAll Random random, @ForAll int target) {
		Arbitrary<Integer> integers = Arbitraries.integers().shrinkTowards(target);
		int shrunkValue = ArbitraryTestHelper.shrinkToEnd(integers, random);
		Assertions.assertThat(shrunkValue).isEqualTo(target);
	}

	@Property(tries = 50)
	void longs(@ForAll Random random, @ForAll long target) {
		Arbitrary<Long> longs = Arbitraries.longs().shrinkTowards(target);
		long shrunkValue = ArbitraryTestHelper.shrinkToEnd(longs, random);
		Assertions.assertThat(shrunkValue).isEqualTo(target);
	}

	@Property(tries = 50)
	void bigIntegers(@ForAll Random random, @ForAll BigInteger target) {
		Arbitrary<BigInteger> bigs = Arbitraries.bigIntegers().shrinkTowards(target);
		BigInteger shrunkValue = ArbitraryTestHelper.shrinkToEnd(bigs, random);
		Assertions.assertThat(shrunkValue).isEqualTo(target);
	}
}
