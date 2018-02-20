package net.jqwik.api.arbitraries;

public interface FloatArbitrary extends NullableArbitrary<Float> {

	default FloatArbitrary withRange(float min, float max) {
		return withMin(min).withMax(max);
	}

	FloatArbitrary withMin(float min);

	FloatArbitrary withMax(float max);

	FloatArbitrary withScale(int scale);
}
