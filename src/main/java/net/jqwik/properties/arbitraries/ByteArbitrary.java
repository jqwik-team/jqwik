package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

public class ByteArbitrary extends NullableArbitrary<Byte> {

	private static final byte DEFAULT_MIN = Byte.MIN_VALUE;
	private static final byte DEFAULT_MAX = Byte.MAX_VALUE;

	private byte min;
	private byte max;

	public ByteArbitrary(byte min, byte max) {
		super(Byte.class);
		this.min = min;
		this.max = max;
	}

	public ByteArbitrary() {
		this(DEFAULT_MIN, DEFAULT_MAX);
	}

	@Override
	protected RandomGenerator<Byte> baseGenerator(int tries) {
		return byteGenerator(min, max);
	}

	private RandomGenerator<Byte> byteGenerator(byte minGenerate, byte maxGenerate) {
		ByteShrinkCandidates byteShrinkCandidates = new ByteShrinkCandidates(min, max);
		List<Shrinkable<Byte>> samples = Arrays
				.stream(new Byte[] { 0, 1, -1, Byte.MIN_VALUE, Byte.MAX_VALUE, minGenerate, maxGenerate }) //
				.distinct() //
				.filter(aByte -> aByte >= min && aByte <= max) //
				.map(aByte -> new ShrinkableValue<>(aByte, byteShrinkCandidates)) //
				.collect(Collectors.toList());
		return RandomGenerators.choose(minGenerate, maxGenerate).withShrinkableSamples(samples);
	}

	public void configure(ByteRange byteRange) {
		min = byteRange.min();
		max = byteRange.max();
	}

}
