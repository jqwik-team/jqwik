package net.jqwik.api.arbitraries;

public interface ShortArbitrary extends NullableArbitrary<Short> {

	default ShortArbitrary withRange(short min, short max) {
		return withMin(min).withMax(max);
	}

	ShortArbitrary withMin(short min);

	ShortArbitrary withMax(short max);
}
