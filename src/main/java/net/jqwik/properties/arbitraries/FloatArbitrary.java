package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.properties.*;

public class FloatArbitrary extends NullableArbitrary<Float> {

	private float min;
	private float max;
	private int precision;

	public FloatArbitrary(float min, float max, int precision) {
		super(Float.class);
		this.min = min;
		this.max = max;
		this.precision = precision;
	}

	public FloatArbitrary() {
		this(0,0,2);
	}

	@Override
	protected RandomGenerator<Float> baseGenerator(int tries) {
		if (min == 0.0 && max == 0.0) {
			float max = Arbitrary.defaultMaxFromTries(tries);
			return floatGenerator(-max, max, precision); //.withSamples(samples);
		}
		return floatGenerator(min, max, precision);
	}

	private RandomGenerator<Float> floatGenerator(float min, float max, int precision) {
		FloatShrinkCandidates doubleShrinkCandidates = new FloatShrinkCandidates(min, max, precision);
		List<Shrinkable<Float>> samples = Arrays.stream(new Float[]{0.0f, min, max}) //
			.filter(aFloat -> aFloat >= min && aFloat <= max) //
			.map(value -> new ShrinkableValue<>(value, doubleShrinkCandidates)) //
			.collect(Collectors.toList());
		return RandomGenerators.floats(min, max, precision).withSamples(samples);
	}

	public void configure(FloatRange doubleRange) {
		min = doubleRange.min();
		max = doubleRange.max();
	}

	public void configure(Precision precision) {
		this.precision = precision.value();
	}


}
