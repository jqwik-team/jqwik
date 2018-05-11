package net.jqwik.properties.shrinking;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.*;

import java.math.*;
import java.util.*;
import java.util.stream.*;

public class ShrinkableBigInteger extends AbstractShrinkable<BigInteger> {
	private final Range<BigInteger> range;
	private final BigInteger target;
	private final BigIntegerShrinkingCandidates shrinkingCandidates;

	public ShrinkableBigInteger(BigInteger value, Range<BigInteger> range) {
		super(value);
		this.range = range;
		this.target = determineTarget(value);
		this.shrinkingCandidates = new BigIntegerShrinkingCandidates(this.target);
	}

	@Override
	public Set<NShrinkable<BigInteger>> shrinkCandidatesFor(NShrinkable<BigInteger> shrinkable) {
		return shrinkingCandidates.candidatesFor(shrinkable.value()) //
			.stream() //
			.map(aBigInteger -> new ShrinkableBigInteger(aBigInteger, range)) //
			.collect(Collectors.toSet());
	}

	@Override
	public ShrinkingDistance distance() {
		return distanceFor(value(), target);
	}

	static ShrinkingDistance distanceFor(BigInteger value, BigInteger target) {
		BigInteger distance = value.subtract(target).abs();
		if (distance.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) >= 0) return ShrinkingDistance.of(Long.MAX_VALUE);
		return ShrinkingDistance.of(distance.longValueExact());
	}

	private BigInteger determineTarget(BigInteger value) {
		if (!range.includes(value)) {
			String message = String.format("Number <%s> is outside allowed range %s", value, range);
			throw new JqwikException(message);
		}
		if (range.includes(BigInteger.ZERO)) {
			return BigInteger.ZERO;
		}
		if (value.compareTo(BigInteger.ZERO) < 0) return range.max;
		if (value.compareTo(BigInteger.ZERO) > 0) return range.min;
		return value; // Should never get here
	}

}
