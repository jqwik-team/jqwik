package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

public class DefaultLongArbitrary extends NullableArbitraryBase<Long> implements LongArbitrary {

	private static final long DEFAULT_MIN = Long.MIN_VALUE;
	private static final long DEFAULT_MAX = Long.MAX_VALUE;

	private long min = DEFAULT_MIN;
	private long max = DEFAULT_MAX;

	public DefaultLongArbitrary() {
		super(Long.class);
	}

	@Override
	protected RandomGenerator<Long> baseGenerator(int tries) {
		if (min == DEFAULT_MIN && max == DEFAULT_MAX) {
			long max = Arbitrary.defaultMaxFromTries(tries);
			return longGenerator(-max, max);
		}
		return longGenerator(min, max);
	}

	private RandomGenerator<Long> longGenerator(long minGenerate, long maxGenerate) {
		LongShrinkCandidates shrinkCandidates = new LongShrinkCandidates(min, max);
		List<Shrinkable<Long>> samples = Arrays.stream(new long[] { 0, 1, -1, Long.MIN_VALUE, Long.MAX_VALUE, minGenerate, maxGenerate }) //
				.distinct() //
				.filter(anInt -> anInt >= min && anInt <= max) //
				.mapToObj(anInt -> new ShrinkableValue<>(anInt, shrinkCandidates)) //
				.collect(Collectors.toList());
		return RandomGenerators.choose(minGenerate, maxGenerate).withShrinkableSamples(samples);
	}

	@Override
	public LongArbitrary greaterOrEqual(long min) {
		DefaultLongArbitrary clone = typedClone();
		clone.min = min;
		return clone;
	}

	@Override
	public LongArbitrary lessOrEqual(long max) {
		DefaultLongArbitrary clone = typedClone();
		clone.max = max;
		return clone;
	}

}
