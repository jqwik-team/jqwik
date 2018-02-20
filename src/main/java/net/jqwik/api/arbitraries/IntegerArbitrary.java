package net.jqwik.api.arbitraries;

public interface IntegerArbitrary extends NullableArbitrary<Integer> {
	IntegerArbitrary withMin(int min);

	IntegerArbitrary withMax(int max);
}
