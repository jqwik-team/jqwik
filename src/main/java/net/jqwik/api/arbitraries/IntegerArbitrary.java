package net.jqwik.api.arbitraries;

public interface IntegerArbitrary extends NullableArbitrary<Integer> {

	default IntegerArbitrary withRange(int min, int max) {
		return withMin(min).withMax(max);
	}

	IntegerArbitrary withMin(int min);

	IntegerArbitrary withMax(int max);
}
