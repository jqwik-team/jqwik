package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;

import net.jqwik.api.*;

public class BiasedRandomDistribution implements RandomDistribution {
	@Override
	public RandomNumericGenerator createGenerator(int genSize, BigInteger min, BigInteger max, BigInteger center) {
		return new BiasedNumericGenerator(genSize, min, max, center);
	}
}
