package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultByteArbitrary extends NullableArbitraryBase<Byte> implements ByteArbitrary {

	private static final byte DEFAULT_MIN = Byte.MIN_VALUE;
	private static final byte DEFAULT_MAX = Byte.MAX_VALUE;

	private byte min = DEFAULT_MIN;
	private byte max = DEFAULT_MAX;

	public DefaultByteArbitrary() {
		super(Byte.class);
	}

	@Override
	protected RandomGenerator<Byte> baseGenerator(int tries) {
		return byteGenerator(min, max);
	}

	private RandomGenerator<Byte> byteGenerator(byte minGenerate, byte maxGenerate) {
		ByteShrinkCandidates byteShrinkCandidates = new ByteShrinkCandidates(min, max);
		List<Shrinkable<Byte>> samples = Arrays.stream(new Byte[] { 0, 1, -1, Byte.MIN_VALUE, Byte.MAX_VALUE, minGenerate, maxGenerate }) //
				.distinct() //
				.filter(aByte -> aByte >= min && aByte <= max) //
				.map(aByte -> new ShrinkableValue<>(aByte, byteShrinkCandidates)) //
				.collect(Collectors.toList());
		return RandomGenerators.choose(minGenerate, maxGenerate).withShrinkableSamples(samples);
	}

	@Override
	public ByteArbitrary greaterOrEqual(byte min) {
		DefaultByteArbitrary clone = typedClone();
		clone.min = min;
		return clone;
	}

	@Override
	public ByteArbitrary lessOrEqual(byte max) {
		DefaultByteArbitrary clone = typedClone();
		clone.max = max;
		return clone;
	}

}
