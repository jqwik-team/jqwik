package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.*;

import java.util.*;
import java.util.stream.*;

public class DoubleArbitrary extends NullableArbitrary<Double> {

	private double min;
	private double max;
	private int precision;

	public DoubleArbitrary(double min, double max, int precision) {
		super(Double.class);
		this.min = min;
		this.max = max;
		this.precision = precision;
	}

	public DoubleArbitrary() {
		this(0,0,2);
	}

	@Override
	protected RandomGenerator<Double> baseGenerator(int tries) {
		if (min == 0.0 && max == 0.0) {
			double max = Arbitrary.defaultMaxFromTries(tries);
//			DoubleShrinkCandidates integerShrinkCandidates = new DoubleShrinkCandidates(Double.MIN_VALUE, Double.MAX_VALUE, precision);
//			List<Shrinkable<Integer>> samples = Arrays.stream(new int[] { 0, Integer.MIN_VALUE, Integer.MAX_VALUE }) //
//													  .mapToObj(anInt -> new ShrinkableValue<>(anInt, integerShrinkCandidates)) //
//													  .collect(Collectors.toList());
			return RandomGenerators.doubles(-max, max); //.withSamples(samples);
		}
		return RandomGenerators.doubles(min, max);
	}

	public void configure(DoubleRange doubeRange) {
		min = doubeRange.min();
		max = doubeRange.max();
		precision = doubeRange.precision();
	}


}
