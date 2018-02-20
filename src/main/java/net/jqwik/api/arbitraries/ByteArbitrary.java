package net.jqwik.api.arbitraries;

public interface ByteArbitrary extends NullableArbitrary<Byte> {

	default ByteArbitrary withRange(byte min, byte max) {
		return withMin(min).withMax(max);
	}

	ByteArbitrary withMin(byte min);

	ByteArbitrary withMax(byte max);
}
