package net.jqwik.newArbitraries;

import net.jqwik.api.*;
import net.jqwik.properties.*;

public class NIntegerArbitrary extends NNullableArbitrary<Integer> {

	private int min;
	private int max;

	public NIntegerArbitrary(int min, int max) {
		super(Integer.class);
		this.min = min;
		this.max = max;
	}

	public NIntegerArbitrary() {
		this(0, 0);
	}

	@Override
	protected NShrinkableGenerator<Integer> baseGenerator(int tries) {
		if (min == 0 && max == 0) {
			int max = NArbitrary.defaultMaxFromTries(tries);
			return NShrinkableGenerators.choose(-max, max).withSamples(0, Integer.MIN_VALUE, Integer.MAX_VALUE);
		}
		return NShrinkableGenerators.choose(min, max);
	}

	public void configure(IntRange intRange) {
		min = intRange.min();
		max = intRange.max();
	}


}
