package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;

import net.jqwik.api.*;

public class GaussianRandomDistribution implements RandomDistribution {

	private final double borderSigma;

	public GaussianRandomDistribution(double borderSigma) {
		if (borderSigma <= 0) {
			throw new IllegalArgumentException("borderSigma must be greater than 0");
		}
		this.borderSigma = borderSigma;
	}

	@Override
	public RandomNumericGenerator createGenerator(int genSize, BigInteger min, BigInteger max, BigInteger center) {
		return new GaussianNumericGenerator(borderSigma, min, max, center);
	}

	@Override
	public String toString() {
		return String.format("GaussianDistribution(%s)", borderSigma);
	}
}
