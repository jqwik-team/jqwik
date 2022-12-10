package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;

import net.jqwik.api.*;

class SmallUniformNumericGenerator implements RandomDistribution.RandomNumericGenerator {

	private final int min;
	private final int max;

	SmallUniformNumericGenerator(BigInteger min, BigInteger max) {
		this.min = min.intValueExact();
		this.max = max.intValueExact();
	}

	@Override
	public BigInteger next(JqwikRandom random) {
		int bound = Math.abs(max - min) + 1;
		int value = random.nextInt(bound >= 0 ? bound : Integer.MAX_VALUE) + min;
		return BigInteger.valueOf(value);
	}
}
