package net.jqwik.properties.arbitraries;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import net.jqwik.api.Arbitrary;
import net.jqwik.api.constraints.*;
import net.jqwik.properties.*;

public class BigDecimalArbitrary extends NullableArbitrary<BigDecimal> {

	private static final BigDecimal DEFAULT_MIN = new BigDecimal(-1_000_000_000);
	private static final BigDecimal DEFAULT_MAX = new BigDecimal(1_000_000_000);

	private BigDecimal min;
	private BigDecimal max;
	private int scale;

	public BigDecimalArbitrary(BigDecimal min, BigDecimal max, int scale) {
		super(BigDecimal.class);
		this.min = min;
		this.max = max;
		this.scale = scale;
	}

	public BigDecimalArbitrary() {
		this(DEFAULT_MIN, DEFAULT_MAX, 2);
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

	public void configure(DoubleRange doubleRange) {
		min = new BigDecimal(doubleRange.min());
		max = new BigDecimal(doubleRange.max());
	}

	public void configure(Scale scale) {
		this.scale = scale.value();
	}

}
