package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.Collectors;

import net.jqwik.api.*;
import net.jqwik.api.constraints.IntRange;

public class IntegerArbitrary extends NullableArbitrary<Integer> {

	private static final int DEFAULT_MIN = Integer.MIN_VALUE;
	private static final int DEFAULT_MAX = Integer.MAX_VALUE;

	private int min;
	private int max;

	public IntegerArbitrary(int min, int max) {
		super(Integer.class);
		this.min = min;
		this.max = max;
	}

	public IntegerArbitrary() {
		this(DEFAULT_MIN, DEFAULT_MAX);
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

	public void configure(IntRange intRange) {
		min = intRange.min();
		max = intRange.max();
	}

}
