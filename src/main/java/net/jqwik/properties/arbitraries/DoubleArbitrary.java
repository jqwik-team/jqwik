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
			return doubleGenerator(-max, max, precision); //.withSamples(samples);
		}
		return doubleGenerator(min, max, precision);
	}

	private RandomGenerator<Double> doubleGenerator(double min, double max, int precision) {
		DoubleShrinkCandidates doubleShrinkCandidates = new DoubleShrinkCandidates(min, max, precision);
		List<Shrinkable<Double>> samples = Arrays.stream(new Double[]{0.0, min, max}) //
			.filter(aDouble -> aDouble >= min && aDouble <= max) //
			.map(value -> new ShrinkableValue<>(value, doubleShrinkCandidates)) //
			.collect(Collectors.toList());
		return RandomGenerators.doubles(min, max, precision).withSamples(samples);
	}

	public void configure(DoubleRange doubleRange) {
		min = doubleRange.min();
		max = doubleRange.max();
	}

	public void configure(Precision precision) {
		this.precision = precision.value();
	}


}
