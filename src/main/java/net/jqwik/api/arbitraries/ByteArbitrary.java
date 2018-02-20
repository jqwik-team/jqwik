package net.jqwik.api.arbitraries;

public interface ByteArbitrary extends NullableArbitrary<Byte> {
	ByteArbitrary withMin(byte min);

	ByteArbitrary withMax(byte max);
}
