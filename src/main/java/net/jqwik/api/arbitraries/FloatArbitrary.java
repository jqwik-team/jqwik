package net.jqwik.api.arbitraries;

public interface FloatArbitrary extends NullableArbitrary<Float> {
	FloatArbitrary withMin(float min);

	FloatArbitrary withMax(float max);

	FloatArbitrary withScale(int scale);
}
