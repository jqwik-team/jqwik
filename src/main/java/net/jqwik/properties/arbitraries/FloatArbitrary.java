package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.properties.*;

public class FloatArbitrary extends NullableArbitrary<Float> {

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
		this(0,0,2);
	}

	@Override
	protected RandomGenerator<Float> baseGenerator(int tries) {
		if (min == 0.0 && max == 0.0) {
			float max = Arbitrary.defaultMaxFromTries(tries);
			return floatGenerator(-max, max, scale); //.withSamples(samples);
		}
		return floatGenerator(min, max, scale);
	}

	private RandomGenerator<Float> floatGenerator(float min, float max, int scale) {
		FloatShrinkCandidates doubleShrinkCandidates = new FloatShrinkCandidates(min, max, scale);
		List<Shrinkable<Float>> samples = Arrays.stream(new Float[]{0.0f, min, max}) //
			.filter(aFloat -> aFloat >= min && aFloat <= max) //
			.map(value -> new ShrinkableValue<>(value, doubleShrinkCandidates)) //
			.collect(Collectors.toList());
		return RandomGenerators.floats(min, max, scale).withSamples(samples);
	}

	public void configure(FloatRange doubleRange) {
		min = doubleRange.min();
		max = doubleRange.max();
	}

	public void configure(Scale scale) {
		this.scale = scale.value();
	}

}
