package net.jqwik.api.arbitraries;

public interface LongArbitrary extends NullableArbitrary<Long> {

	default LongArbitrary between(long min, long max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	LongArbitrary greaterOrEqual(long min);

	LongArbitrary lessOrEqual(long max);
}
