package net.jqwik.properties.arbitraries;

import java.math.*;
import java.util.*;

public class BigIntegerShrinkCandidates implements ShrinkCandidates<BigInteger> {

	private final Range<BigInteger> range;

	public BigIntegerShrinkCandidates(BigInteger min, BigInteger max) {
		this.range = Range.of(min, max);
	}

	@Override
	public Set<BigInteger> nextCandidates(BigInteger value) {
		BigInteger shrunkValue = nextShrinkValue(value);
		if (value.equals(shrunkValue))
			return Collections.emptySet();
		Set<BigInteger> candidates = new HashSet<>();
		candidates.add(shrunkValue);
		candidates.add(nextShrinkOne(value));
		return candidates;
	}

	@Override
	public int distance(BigInteger value) {
		BigInteger diff = determineTarget(value).subtract(value).abs();
		if (diff.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0)
			return diff.intValue();
		return Integer.MAX_VALUE;
	}

	private BigInteger nextShrinkValue(BigInteger value) {
		return value.subtract(calculateDelta(determineTarget(value), value));
	}

	private BigInteger nextShrinkOne(BigInteger value) {
		return value.subtract(calculateDeltaOne(determineTarget(value), value));
	}

	private static BigInteger calculateDelta(BigInteger current, BigInteger target) {
		if (target.compareTo(current) > 0)
			return target.subtract(current).divide(BigInteger.valueOf(2)).max(BigInteger.ONE);
		if (target.compareTo(current) < 0)
			return target.subtract(current).divide(BigInteger.valueOf(2)).min(BigInteger.ONE.negate());
		return BigInteger.ZERO;
	}

	private static BigInteger calculateDeltaOne(BigInteger current, BigInteger target) {
		if (target.compareTo(current) > 0)
			return BigInteger.ONE;
		if (target.compareTo(current) < 0)
			return BigInteger.ONE.negate();
		return BigInteger.ZERO;
	}

	private BigInteger determineTarget(BigInteger value) {
		if (!range.includes(value)) {
			return value;
		}
		if (range.includes(BigInteger.ZERO))
			return BigInteger.ZERO;
		else {
			if (value.compareTo(BigInteger.ZERO) < 0)
				return range.max;
			if (value.compareTo(BigInteger.ZERO) > 0)
				return range.min;
		}
		return value; // Should never get here
	}


}
