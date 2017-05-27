package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.properties.*;

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
			LongShrinkCandidates longShrinkCandidates = new LongShrinkCandidates(Long.MIN_VALUE, Long.MAX_VALUE);
			List<Shrinkable<Long>> samples = Arrays.stream(new long[] { 0L, Long.MIN_VALUE, Long.MAX_VALUE }) //
					.mapToObj(aLong -> new ShrinkableValue<>(aLong, longShrinkCandidates)) //
					.collect(Collectors.toList());
			return RandomGenerators.choose(-max, max).withSamples(samples);
		}
		return RandomGenerators.choose(min, max);
	}

	public void configure(LongRange longRange) {
		min = longRange.min();
		max = longRange.max();
	}


}
