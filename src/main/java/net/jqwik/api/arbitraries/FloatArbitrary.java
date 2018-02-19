package net.jqwik.api.arbitraries;

import net.jqwik.api.*;

public interface FloatArbitrary extends Arbitrary<Float> {
	FloatArbitrary withMin(float min);

	FloatArbitrary withMax(float max);

	FloatArbitrary withScale(int scale);
}
