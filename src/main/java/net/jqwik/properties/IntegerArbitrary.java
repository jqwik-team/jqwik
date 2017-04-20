package net.jqwik.properties;

import net.jqwik.execution.*;

public class IntegerArbitrary extends NullableArbitrary<Integer> {

	private final int min;
	private final int max;

	protected IntegerArbitrary(int min, int max) {
		super(Integer.class);
		this.min = min;
		this.max = max;
	}

	protected IntegerArbitrary() {
		this(0, 0);
	}

	@Override
	protected RandomGenerator<Integer> baseGenerator(int tries) {
		if (min == 0 && max == 0) {
			int max = Arbitrary.defaultMaxFromTries(tries);
			return RandomGenerators.choose(-max, max);
		}
		return RandomGenerators.choose(min, max);
	}
}
