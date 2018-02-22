package net.jqwik.api.arbitraries;

public interface ShortArbitrary extends NullableArbitrary<Short> {

	default ShortArbitrary between(short min, short max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	ShortArbitrary greaterOrEqual(short min);

	ShortArbitrary lessOrEqual(short max);
}
