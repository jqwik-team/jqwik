package net.jqwik.api.arbitraries;

public interface IntegerArbitrary extends NullableArbitrary<Integer> {

	default IntegerArbitrary between(int min, int max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	IntegerArbitrary greaterOrEqual(int min);

	IntegerArbitrary lessOrEqual(int max);
}
