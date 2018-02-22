package net.jqwik.api.arbitraries;

public interface FloatArbitrary extends NullableArbitrary<Float> {

	default FloatArbitrary between(float min, float max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	FloatArbitrary greaterOrEqual(float min);

	FloatArbitrary lessOrEqual(float max);

	FloatArbitrary ofScale(int scale);
}
