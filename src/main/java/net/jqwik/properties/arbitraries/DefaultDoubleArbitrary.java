package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultDoubleArbitrary extends NullableArbitraryBase<Double> implements DoubleArbitrary {

	private static final double DEFAULT_MIN = -Double.MAX_VALUE;
	private static final double DEFAULT_MAX = Double.MAX_VALUE;
	private static final int DEFAULT_SCALE = 2;

	private double min = DEFAULT_MIN;
	private double max = DEFAULT_MAX;
	private int scale = DEFAULT_SCALE;

	public DefaultDoubleArbitrary() {
		super(Double.class);
	}

	@Override
	protected RandomGenerator<Double> baseGenerator(int tries) {
		if (min == DEFAULT_MIN && max == DEFAULT_MAX) {
			double max = Arbitrary.defaultMaxFromTries(tries);
			return doubleGenerator(-max, max, scale); // .withSamples(samples);
		}
		return doubleGenerator(min, max, scale);
	}

	private RandomGenerator<Double> doubleGenerator(double minGenerate, double maxGenerate, int scale) {
		DoubleShrinkCandidates doubleShrinkCandidates = new DoubleShrinkCandidates(min, max, scale);
		double smallest = 1.0 / Math.pow(10, scale);
		List<Shrinkable<Double>> samples = Arrays
				.stream(new Double[] { 0.0, 1.0, -1.0, smallest, -smallest, DEFAULT_MAX, DEFAULT_MIN, minGenerate, maxGenerate }) //
				.distinct() //
				.filter(aDouble -> aDouble >= min && aDouble <= max) //
				.map(value -> new ShrinkableValue<>(value, doubleShrinkCandidates)) //
				.collect(Collectors.toList());
		return RandomGenerators.doubles(minGenerate, maxGenerate, scale).withShrinkableSamples(samples);
	}

	@Override
	public DoubleArbitrary greaterOrEqual(double min) {
		DefaultDoubleArbitrary clone = typedClone();
		clone.min = min;
		return clone;
	}

	@Override
	public DoubleArbitrary lessOrEqual(double max) {
		DefaultDoubleArbitrary clone = typedClone();
		clone.max = max;
		return clone;
	}

	@Override
	public DoubleArbitrary ofScale(int scale) {
		DefaultDoubleArbitrary clone = typedClone();
		clone.scale = scale;
		return clone;
	}

}
