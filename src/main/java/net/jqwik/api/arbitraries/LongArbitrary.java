package net.jqwik.api.arbitraries;

public interface LongArbitrary extends NullableArbitrary<Long> {
	LongArbitrary withMin(long min);

	LongArbitrary withMax(long max);
}
