package net.jqwik.api.arbitraries;

import net.jqwik.api.*;

public interface LongArbitrary extends Arbitrary<Long> {
	LongArbitrary withMin(long min);

	LongArbitrary withMax(long max);
}
