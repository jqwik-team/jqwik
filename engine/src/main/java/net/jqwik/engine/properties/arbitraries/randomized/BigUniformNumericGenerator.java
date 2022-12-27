package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;

import net.jqwik.api.*;

class BigUniformNumericGenerator implements RandomDistribution.RandomNumericGenerator {

	private final BigInteger min;
	private final BigInteger max;
	private final BigInteger range;
	private final int bits;

	BigUniformNumericGenerator(BigInteger min, BigInteger max) {
		this.min = min;
		this.max = max;
		this.range = max.subtract(min);
		this.bits = range.bitLength();
	}

	@Override
	public BigInteger next(JqwikRandom random) {
		while (true) {
			BigInteger rawValue = new BigInteger(bits, random.asJdkRandom());
			BigInteger value = rawValue.add(min);
			if (value.compareTo(min) >= 0 && value.compareTo(max) <= 0) {
				return value;
			}
		}
	}
}
