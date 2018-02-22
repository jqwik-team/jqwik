package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultIntegerArbitrary extends NullableArbitraryBase<Integer> implements IntegerArbitrary {

	private static final int DEFAULT_MIN = Integer.MIN_VALUE;
	private static final int DEFAULT_MAX = Integer.MAX_VALUE;

	private int min = DEFAULT_MIN;
	private int max = DEFAULT_MAX;

	public DefaultIntegerArbitrary() {
		super(Integer.class);
	}

	@Override
	protected RandomGenerator<Integer> baseGenerator(int tries) {
		if (min == DEFAULT_MIN && max == DEFAULT_MAX) {
			int max = Arbitrary.defaultMaxFromTries(tries);
			return integerGenerator(-max, max);
		}
		return integerGenerator(min, max);
	}

	private RandomGenerator<Integer> integerGenerator(int minGenerate, int maxGenerate) {
		IntegerShrinkCandidates integerShrinkCandidates = new IntegerShrinkCandidates(min, max);
		List<Shrinkable<Integer>> samples = Arrays
				.stream(new int[] { 0, 1, -1, Integer.MIN_VALUE, Integer.MAX_VALUE, minGenerate, maxGenerate }) //
				.distinct() //
				.filter(anInt -> anInt >= min && anInt <= max) //
				.mapToObj(anInt -> new ShrinkableValue<>(anInt, integerShrinkCandidates)) //
				.collect(Collectors.toList());
		return RandomGenerators.choose(minGenerate, maxGenerate).withShrinkableSamples(samples);
	}

	@Override
	public IntegerArbitrary greaterOrEqual(int min) {
		DefaultIntegerArbitrary clone = typedClone();
		clone.min = min;
		return clone;
	}

	@Override
	public IntegerArbitrary lessOrEqual(int max) {
		DefaultIntegerArbitrary clone = typedClone();
		clone.max = max;
		return clone;
	}

}
