package net.jqwik.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultBigDecimalArbitrary extends NullableArbitraryBase<BigDecimal> implements BigDecimalArbitrary {

	private static final BigDecimal DEFAULT_MIN = new BigDecimal(-1_000_000_000);
	private static final BigDecimal DEFAULT_MAX = new BigDecimal(1_000_000_000);
	private static final int DEFAULT_SCALE = 2;

	private BigDecimal min = DEFAULT_MIN;
	private BigDecimal max = DEFAULT_MAX;
	private int scale = DEFAULT_SCALE;

	public DefaultBigDecimalArbitrary() {
		super(BigDecimal.class);
	}

	@Override
	protected RandomGenerator<BigDecimal> baseGenerator(int tries) {
		if (min.equals(DEFAULT_MIN) && max.equals(DEFAULT_MAX)) {
			BigDecimal max = new BigDecimal(Arbitrary.defaultMaxFromTries(tries));
			return decimalGenerator(max.negate(), max, scale);
		}
		return decimalGenerator(min, max, scale);
	}

	private RandomGenerator<BigDecimal> decimalGenerator(BigDecimal minGenerate, BigDecimal maxGenerate, int scale) {
		BigDecimalShrinkCandidates decimalShrinkCandidates = new BigDecimalShrinkCandidates(min, max, scale);
		BigDecimal smallest = BigDecimal.ONE.movePointLeft(scale);
		BigDecimal[] sampleValues = { BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ONE.negate(), smallest, smallest.negate(), DEFAULT_MAX,
				DEFAULT_MIN, minGenerate, maxGenerate };
		List<Shrinkable<BigDecimal>> samples = Arrays.stream(sampleValues) //
				.distinct() //
				.filter(aDecimal -> aDecimal.compareTo(min) >= 0 && aDecimal.compareTo(max) <= 0) //
				.map(value -> new ShrinkableValue<>(value, decimalShrinkCandidates)) //
				.collect(Collectors.toList());
		return RandomGenerators.bigDecimals(minGenerate, maxGenerate, scale).withShrinkableSamples(samples);
	}

	@Override
	public BigDecimalArbitrary withMin(BigDecimal min) {
		DefaultBigDecimalArbitrary clone = typedClone();
		clone.min = min;
		return clone;
	}

	@Override
	public BigDecimalArbitrary withMax(BigDecimal max) {
		DefaultBigDecimalArbitrary clone = typedClone();
		clone.max = max;
		return clone;
	}

	@Override
	public BigDecimalArbitrary withScale(int scale) {
		DefaultBigDecimalArbitrary clone = typedClone();
		clone.scale = scale;
		return clone;
	}

}
