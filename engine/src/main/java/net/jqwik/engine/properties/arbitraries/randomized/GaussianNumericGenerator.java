package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.RandomDistribution.*;

public class GaussianNumericGenerator implements RandomNumericGenerator {
	private final double borderSigma;
	private final BigInteger min;
	private final BigInteger max;
	private final BigInteger center;
	private final BigInteger leftRange;
	private final BigInteger rightRange;

	public GaussianNumericGenerator(double borderSigma, BigInteger min, BigInteger max, BigInteger center) {
		this.borderSigma = borderSigma;
		this.min = min;
		this.max = max;
		this.center = center;
		this.leftRange = center.subtract(min).abs();
		this.rightRange = center.subtract(max).abs();
	}

	@Override
	public BigInteger next(JqwikRandom random) {
		Random rnd = random.asJdkRandom();
		while (true) {
			double gaussianFactor = rnd.nextGaussian() / borderSigma;
			BigInteger value = center;
			if (gaussianFactor < 0.0 && leftRange.compareTo(BigInteger.ZERO) > 0) {
				BigDecimal bigDecimalLeft = new BigDecimal(leftRange).multiply(BigDecimal.valueOf(gaussianFactor).abs());
				value = center.subtract(bigDecimalLeft.toBigInteger());
			}
			if (gaussianFactor > 0.0 && rightRange.compareTo(BigInteger.ZERO) > 0) {
				BigDecimal bigDecimalRight = new BigDecimal(rightRange).multiply(BigDecimal.valueOf(gaussianFactor).abs());
				value = center.add(bigDecimalRight.toBigInteger());
			}
			if (value.compareTo(min) >= 0 && value.compareTo(max) <= 0) {
				return value;
			}
		}
	}
}
