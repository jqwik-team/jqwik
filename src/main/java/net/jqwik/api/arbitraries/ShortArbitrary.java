package net.jqwik.api.arbitraries;

public interface ShortArbitrary extends NullableArbitrary<Short> {
	ShortArbitrary withMin(short min);

	ShortArbitrary withMax(short max);
}
