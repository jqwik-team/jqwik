package net.jqwik.api.arbitraries;

public interface DoubleArbitrary extends NullableArbitrary<Double> {

	default DoubleArbitrary between(double min, double max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	DoubleArbitrary greaterOrEqual(double min);

	DoubleArbitrary lessOrEqual(double max);

	DoubleArbitrary ofScale(int scale);

}
