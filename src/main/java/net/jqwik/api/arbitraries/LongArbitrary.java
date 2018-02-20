package net.jqwik.api.arbitraries;

public interface LongArbitrary extends NullableArbitrary<Long> {

	default LongArbitrary withRange(long min, long max) {
		return withMin(min).withMax(max);
	}

	LongArbitrary withMin(long min);

	LongArbitrary withMax(long max);
}
