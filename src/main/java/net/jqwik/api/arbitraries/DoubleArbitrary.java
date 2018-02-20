package net.jqwik.api.arbitraries;

public interface DoubleArbitrary extends NullableArbitrary<Double> {

	default DoubleArbitrary withRange(double min, double max) {
		return withMin(min).withMax(max);
	}

	DoubleArbitrary withMin(double min);

	DoubleArbitrary withMax(double max);

	DoubleArbitrary withScale(int scale);

}
