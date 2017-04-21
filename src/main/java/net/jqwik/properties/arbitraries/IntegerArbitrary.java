package net.jqwik.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.*;

public class IntegerArbitrary extends NullableArbitrary<Integer> {

	private int min;
	private int max;

	public IntegerArbitrary(int min, int max) {
		super(Integer.class);
		this.min = min;
		this.max = max;
	}

	public IntegerArbitrary() {
		this(0, 0);
	}

	@Override
	protected RandomGenerator<Integer> baseGenerator(int tries) {
		if (min == 0 && max == 0) {
			int max = Arbitrary.defaultMaxFromTries(tries);
			return RandomGenerators.choose(-max, max).withSamples(0, Integer.MIN_VALUE, Integer.MAX_VALUE);
		}
		return RandomGenerators.choose(min, max);
	}

	public void configure(IntRange intRange) {
		min = intRange.min();
		max = intRange.max();
	}


}
