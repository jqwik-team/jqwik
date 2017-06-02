package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.*;

import java.util.*;
import java.util.stream.*;

public class LongArbitrary extends NullableArbitrary<Long> {

	private long min;
	private long max;

	public LongArbitrary(long min, long max) {
		super(Long.class);
		this.min = min;
		this.max = max;
	}

	public LongArbitrary() {
		this(0, 0);
	}

	@Override
	protected RandomGenerator<Long> baseGenerator(int tries) {
		if (min == 0 && max == 0) {
			long max = Arbitrary.defaultMaxFromTries(tries);
			return longGenerator(-max, max);
		}
		return longGenerator(min, max);
	}

	private RandomGenerator<Long> longGenerator(long min, long max) {
		LongShrinkCandidates shrinkCandidates = new LongShrinkCandidates(min, max);
		List<Shrinkable<Long>> samples = Arrays.stream(new long[] { 0, min, max }) //
			.filter(anInt -> anInt >= min && anInt <= max) //
			.mapToObj(anInt -> new ShrinkableValue<>(anInt, shrinkCandidates)) //
			.collect(Collectors.toList());
		return RandomGenerators.choose(min, max).withSamples(samples);
	}


	public void configure(LongRange longRange) {
		min = longRange.min();
		max = longRange.max();
	}


}
