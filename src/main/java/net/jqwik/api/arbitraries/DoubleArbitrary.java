package net.jqwik.api.arbitraries;

import net.jqwik.api.*;

public interface DoubleArbitrary extends Arbitrary<Double> {

	DoubleArbitrary withMin(double min);

	DoubleArbitrary withMax(double max);

	DoubleArbitrary withScale(int scale);

}
