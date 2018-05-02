package net.jqwik.properties.arbitraries;

import java.math.*;
import java.util.*;

public class BigIntegerShrinkCandidates implements ShrinkCandidates<BigInteger> {

	private final Range<BigInteger> range;

	public BigIntegerShrinkCandidates(Range<BigInteger> range) {
		this.range = range;
	}

	@Override
	public Set<BigInteger> nextCandidates(BigInteger value) {
		Set<BigInteger> candidates = new HashSet<>();
		BigInteger target = determineTarget(value);
		BigInteger lower = target.min(value);
		BigInteger higher = target.max(value);
		addFibbonaci(candidates, lower, BigInteger.valueOf(0), BigInteger.valueOf(1), higher);
		subFibbonaci(candidates, higher, BigInteger.valueOf(0), BigInteger.valueOf(1), lower);
		candidates.add(target);
		candidates.remove(value);
		return candidates;
	}

	private void subFibbonaci(
		Set<BigInteger> candidates, BigInteger target, BigInteger butLast, BigInteger last, BigInteger border
	) {
		while (true) {
			BigInteger step = butLast.add(last);
			BigInteger candidate = target.subtract(step);
			if (candidate.compareTo(border) <= 0) {
				break;
			}
			candidates.add(candidate);
			butLast = last;
			last = step;
		}
	}

	private void addFibbonaci(
		Set<BigInteger> candidates, BigInteger target, BigInteger butLast, BigInteger last, BigInteger border
	) {
		while (true) {
			BigInteger step = butLast.add(last);
			BigInteger candidate = target.add(step);
			if (candidate.compareTo(border) >= 0) {
				break;
			}
			candidates.add(candidate);
			butLast = last;
			last = step;
		}
	}

	@Override
	public int distance(BigInteger value) {
		BigInteger diff = determineTarget(value).subtract(value).abs();
		if (diff.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0)
			return diff.intValue();
		return Integer.MAX_VALUE;
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
