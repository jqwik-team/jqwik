package net.jqwik.engine.properties.shrinking;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

public class ShrinkableBigInteger extends AbstractShrinkable<BigInteger> {
	private final Range<BigInteger> range;
	private final BigInteger shrinkingTarget;
	private final BigIntegerShrinkingCandidates shrinkingCandidates;

	public static BigInteger defaultShrinkingTarget(BigInteger value, Range<BigInteger> range) {
		if (range.includes(BigInteger.ZERO)) {
			return BigInteger.ZERO;
		}
		if (value.compareTo(BigInteger.ZERO) < 0) return range.max;
		if (value.compareTo(BigInteger.ZERO) > 0) return range.min;
		return value; // Should never get here
	}

	public ShrinkableBigInteger(BigInteger value, Range<BigInteger> range, BigInteger shrinkingTarget) {
		super(value);
		this.range = range;
		this.shrinkingTarget = shrinkingTarget;
		this.shrinkingCandidates = new BigIntegerShrinkingCandidates(this.shrinkingTarget);
		checkTargetInRange(shrinkingTarget);
		checkValueInRange(value);
	}

	@Override
	public Set<Shrinkable<BigInteger>> shrinkCandidatesFor(Shrinkable<BigInteger> shrinkable) {
		return shrinkingCandidates.candidatesFor(shrinkable.value()) //
			.stream() //
			.map(aBigInteger -> new ShrinkableBigInteger(aBigInteger, range, shrinkingTarget)) //
			.collect(Collectors.toSet());
	}

	@Override
	public ShrinkingDistance distance() {
		return distanceFor(value(), shrinkingTarget);
	}

	static ShrinkingDistance distanceFor(BigInteger value, BigInteger target) {
		BigInteger distance = value.subtract(target).abs();
		if (distance.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) >= 0) return ShrinkingDistance.of(Long.MAX_VALUE);
		return ShrinkingDistance.of(distance.longValueExact());
	}

	private void checkTargetInRange(BigInteger value) {
		if (!range.includes(value)) {
			String message = String.format("Shrinking target <%s> is outside allowed range %s", value, range);
			throw new JqwikException(message);
		}
	}

	private void checkValueInRange(BigInteger value) {
		if (!range.includes(value)) {
			String message = String.format("Value <%s> is outside allowed range %s", value, range);
			throw new JqwikException(message);
		}
	}

}
