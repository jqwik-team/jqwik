package net.jqwik.api.arbitraries;

public interface ByteArbitrary extends NullableArbitrary<Byte> {

	default ByteArbitrary between(byte min, byte max) {
		return greaterOrEqual(min).lessOrEqual(max);
	}

	ByteArbitrary greaterOrEqual(byte min);

	ByteArbitrary lessOrEqual(byte max);
}
