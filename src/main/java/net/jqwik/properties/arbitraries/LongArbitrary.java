package net.jqwik.properties.arbitraries;

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
			return RandomGenerators.choose(-max, max).withSamples(0L, Long.MIN_VALUE, Long.MAX_VALUE);
		}
		return RandomGenerators.choose(min, max);
	}

	public void configure(LongRange longRange) {
		min = longRange.min();
		max = longRange.max();
	}


}
