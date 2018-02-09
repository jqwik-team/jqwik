package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

public class FloatArbitrary extends NullableArbitraryBase<Float> {

	private static final float DEFAULT_MIN = -Float.MAX_VALUE;
	private static final float DEFAULT_MAX = Float.MAX_VALUE;

	private float min;
	private float max;
	private int scale;

	public FloatArbitrary(float min, float max, int scale) {
		super(Float.class);
		this.min = min;
		this.max = max;
		this.scale = scale;
	}

	public FloatArbitrary() {
		this(DEFAULT_MIN, DEFAULT_MAX, 2);
	}

	@Override
	protected RandomGenerator<Float> baseGenerator(int tries) {
		if (min == DEFAULT_MIN && max == DEFAULT_MAX) {
			float max = Arbitrary.defaultMaxFromTries(tries);
			return floatGenerator(-max, max, scale); // .withSamples(samples);
		}
		return floatGenerator(min, max, scale);
	}

	private RandomGenerator<Float> floatGenerator(float minGenerate, float maxGenerate, int scale) {
		FloatShrinkCandidates floatShrinkCandidates = new FloatShrinkCandidates(min, max, scale);
		float smallest = (float) (1.0 / Math.pow(10, scale));
		List<Shrinkable<Float>> samples = Arrays
				.stream(new Float[] { 0.0f, 1.0f, -1.0f, smallest, -smallest, DEFAULT_MAX, DEFAULT_MIN, minGenerate, maxGenerate }) //
				.distinct() //
				.filter(aFloat -> aFloat >= min && aFloat <= max) //
				.map(value -> new ShrinkableValue<>(value, floatShrinkCandidates)) //
				.collect(Collectors.toList());
		return RandomGenerators.floats(minGenerate, maxGenerate, scale).withShrinkableSamples(samples);
	}

	public void configure(FloatRange doubleRange) {
		min = doubleRange.min();
		max = doubleRange.max();
	}

	public void configure(Scale scale) {
		this.scale = scale.value();
	}

}
