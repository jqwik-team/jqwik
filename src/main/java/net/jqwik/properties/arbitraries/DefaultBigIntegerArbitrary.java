package net.jqwik.properties.arbitraries;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import java.math.*;
import java.util.*;
import java.util.stream.*;

public class DefaultBigIntegerArbitrary extends NullableArbitraryBase<BigInteger> implements BigIntegerArbitrary {

	private static final long DEFAULT_MIN = Long.MIN_VALUE;
	private static final long DEFAULT_MAX = Long.MAX_VALUE;

	private long min = DEFAULT_MIN;
	private long max = DEFAULT_MAX;

	public DefaultBigIntegerArbitrary() {
		super(BigInteger.class);
	}

	@Override
	public BigIntegerArbitrary greaterOrEqual(BigInteger min) {
		checkBoundaries(min);
		DefaultBigIntegerArbitrary clone = typedClone();
		clone.min = min.longValue();
		return clone;
	}

	@Override
	public BigIntegerArbitrary lessOrEqual(BigInteger max) {
		checkBoundaries(max);
		DefaultBigIntegerArbitrary clone = typedClone();
		clone.max = max.longValue();
		return clone;
	}

	@Override
	protected RandomGenerator<BigInteger> baseGenerator(int tries) {
		if (min == DEFAULT_MIN && max == DEFAULT_MAX) {
			long max = Arbitrary.defaultMaxFromTries(tries);
			return longGenerator(-max, max).map(BigInteger::valueOf);
		}
		return longGenerator(min, max).map(BigInteger::valueOf);
	}

	private void checkBoundaries(BigInteger min) {
		if (min.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0) {
			throw new JqwikException("Min  and max values must not be smaller than Long.MIN_VALUE");
		}
		if (min.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0) {
			throw new JqwikException("Min  and max values must not be larger than Long.MAX_VALUE");
		}
	}

	/**
	 * TODO: This is a copy of DefaultLongArbitrary and should be replaced with real big integer generation
	 */
	private RandomGenerator<Long> longGenerator(long minGenerate, long maxGenerate) {
		LongShrinkCandidates shrinkCandidates = new LongShrinkCandidates(min, max);
		List<Shrinkable<Long>> samples = Arrays.stream(new long[] { 0, 1, -1, Long.MIN_VALUE, Long.MAX_VALUE, minGenerate, maxGenerate }) //
											   .distinct() //
											   .filter(anInt -> anInt >= min && anInt <= max) //
											   .mapToObj(anInt -> new ShrinkableValue<>(anInt, shrinkCandidates)) //
											   .collect(Collectors.toList());
		return RandomGenerators.choose(minGenerate, maxGenerate).withShrinkableSamples(samples);
	}
}
