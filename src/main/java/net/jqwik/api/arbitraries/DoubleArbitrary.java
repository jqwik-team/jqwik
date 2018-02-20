package net.jqwik.api.arbitraries;

public interface DoubleArbitrary extends NullableArbitrary<Double> {

	DoubleArbitrary withMin(double min);

	DoubleArbitrary withMax(double max);

	DoubleArbitrary withScale(int scale);

}
