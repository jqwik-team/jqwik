package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.*;

import java.util.*;
import java.util.stream.*;

public class DoubleArbitrary extends NullableArbitrary<Double> {

	private static final double DEFAULT_MIN = Double.MIN_VALUE;
	private static final double DEFAULT_MAX = Double.MAX_VALUE;

	private double min;
	private double max;
	private int scale;

	public DoubleArbitrary(double min, double max, int scale) {
		super(Double.class);
		this.min = min;
		this.max = max;
		this.scale = scale;
	}

	public DoubleArbitrary() {
		this(DEFAULT_MIN,DEFAULT_MAX,2);
	}

	@Override
	protected RandomGenerator<Double> baseGenerator(int tries) {
		if (min == DEFAULT_MIN && max == DEFAULT_MAX) {
			double max = Arbitrary.defaultMaxFromTries(tries);
			return doubleGenerator(-max, max, scale); //.withSamples(samples);
		}
		return doubleGenerator(min, max, scale);
	}

	private RandomGenerator<Double> doubleGenerator(double minGenerate, double maxGenerate, int scale) {
		DoubleShrinkCandidates doubleShrinkCandidates = new DoubleShrinkCandidates(minGenerate, maxGenerate, scale);
		List<Shrinkable<Double>> samples = Arrays.stream(new Double[]{0.0, Double.MIN_VALUE, Double.MAX_VALUE, minGenerate, maxGenerate}) //
			.filter(aDouble -> aDouble >= min && aDouble <= max) //
			.map(value -> new ShrinkableValue<>(value, doubleShrinkCandidates)) //
			.collect(Collectors.toList());
		return RandomGenerators.doubles(minGenerate, maxGenerate, scale).withSamples(samples);
	}

	public void configure(DoubleRange doubleRange) {
		min = doubleRange.min();
		max = doubleRange.max();
	}

	public void configure(Scale scale) {
		this.scale = scale.value();
	}


}
