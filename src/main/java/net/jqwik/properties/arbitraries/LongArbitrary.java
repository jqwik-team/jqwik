package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.Collectors;

import net.jqwik.api.*;
import net.jqwik.api.constraints.LongRange;

public class LongArbitrary extends NullableArbitrary<Long> {

	private static final long DEFAULT_MIN = Long.MIN_VALUE;
	private static final long DEFAULT_MAX = Long.MAX_VALUE;

	private long min;
	private long max;

	public LongArbitrary(long min, long max) {
		super(Long.class);
		this.min = min;
		this.max = max;
	}

	public LongArbitrary() {
		this(DEFAULT_MIN, DEFAULT_MAX);
	}

	@Override
	protected RandomGenerator<Long> baseGenerator(int tries) {
		if (min == DEFAULT_MIN && max == DEFAULT_MAX) {
			long max = Arbitrary.defaultMaxFromTries(tries);
			return longGenerator(-max, max);
		}
		return longGenerator(min, max);
	}

	private RandomGenerator<Long> longGenerator(long minGenerate, long maxGenerate) {
		LongShrinkCandidates shrinkCandidates = new LongShrinkCandidates(min, max);
		List<Shrinkable<Long>> samples = Arrays.stream(new long[] { 0, 1, -1, Long.MIN_VALUE, Long.MAX_VALUE, minGenerate, maxGenerate }) //
											   .distinct() //
											   .filter(anInt -> anInt >= min && anInt <= max) //
											   .mapToObj(anInt -> new ShrinkableValue<>(anInt, shrinkCandidates)) //
											   .collect(Collectors.toList());
		return RandomGenerators.choose(minGenerate, maxGenerate).withShrinkableSamples(samples);
	}

	public void configure(LongRange longRange) {
		min = longRange.min();
		max = longRange.max();
	}

}
