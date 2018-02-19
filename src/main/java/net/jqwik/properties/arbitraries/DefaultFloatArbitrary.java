package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultFloatArbitrary extends NullableArbitraryBase<Float> implements FloatArbitrary {

	private static final float DEFAULT_MIN = -Float.MAX_VALUE;
	private static final float DEFAULT_MAX = Float.MAX_VALUE;
	private static final int DEFAULT_SCALE = 2;

	private float min = DEFAULT_MIN;
	private float max = DEFAULT_MAX;
	private int scale = DEFAULT_SCALE;

	public DefaultFloatArbitrary() {
		super(Float.class);
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

	@Override
	public FloatArbitrary withMin(float min) {
		DefaultFloatArbitrary clone = typedClone();
		clone.min = min;
		return clone;
	}

	@Override
	public FloatArbitrary withMax(float max) {
		DefaultFloatArbitrary clone = typedClone();
		clone.max = max;
		return clone;
	}

	@Override
	public FloatArbitrary withScale(int scale) {
		DefaultFloatArbitrary clone = typedClone();
		clone.scale = scale;
		return clone;
	}

}
