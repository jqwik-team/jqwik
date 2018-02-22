package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultShortArbitrary extends NullableArbitraryBase<Short> implements ShortArbitrary {

	private static final short DEFAULT_MIN = Short.MIN_VALUE;
	private static final short DEFAULT_MAX = Short.MAX_VALUE;

	private short min = DEFAULT_MIN;
	private short max = DEFAULT_MAX;

	public DefaultShortArbitrary() {
		super(Short.class);
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

	@Override
	public ShortArbitrary greaterOrEqual(short min) {
		DefaultShortArbitrary clone = typedClone();
		clone.min = min;
		return clone;
	}

	@Override
	public ShortArbitrary lessOrEqual(short max) {
		DefaultShortArbitrary clone = typedClone();
		clone.max = max;
		return clone;
	}

}
