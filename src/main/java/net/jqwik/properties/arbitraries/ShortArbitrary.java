package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

public class ShortArbitrary extends NullableArbitraryBase<Short> {

	private static final short DEFAULT_MIN = Short.MIN_VALUE;
	private static final short DEFAULT_MAX = Short.MAX_VALUE;

	private short min;
	private short max;

	public ShortArbitrary(short min, short max) {
		super(Short.class);
		this.min = min;
		this.max = max;
	}

	public ShortArbitrary() {
		this(DEFAULT_MIN, DEFAULT_MAX);
	}

	@Override
	protected RandomGenerator<Short> baseGenerator(int tries) {
		return shortGenerator(min, max);
	}

	private RandomGenerator<Short> shortGenerator(short minGenerate, short maxGenerate) {
		ShortShrinkCandidates shortShrinkCandidates = new ShortShrinkCandidates(min, max);
		List<Shrinkable<Short>> samples = Arrays
				.stream(new Short[] { 0, 1, -1, Short.MIN_VALUE, Short.MAX_VALUE, minGenerate, maxGenerate }) //
				.distinct() //
				.filter(aShort -> aShort >= min && aShort <= max) //
				.map(aShort -> new ShrinkableValue<>(aShort, shortShrinkCandidates)) //
				.collect(Collectors.toList());
		return RandomGenerators.choose(minGenerate, maxGenerate).withShrinkableSamples(samples);
	}

	public void configure(ShortRange shortRange) {
		min = shortRange.min();
		max = shortRange.max();
	}

}
