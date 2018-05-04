package net.jqwik.properties.newShrinking;

import net.jqwik.*;
import net.jqwik.properties.arbitraries.*;

import java.math.*;
import java.util.*;

public class ShrinkableBigInteger extends AbstractShrinkable<BigInteger> {
	private final Range<BigInteger> range;
	private final BigInteger target;

	public ShrinkableBigInteger(BigInteger value, Range<BigInteger> range) {
		super(value);
		this.range = range;
		this.target = determineTarget(value);
	}

	@Override
	public Set<NShrinkable<BigInteger>> shrinkCandidatesFor(NShrinkable<BigInteger> shrinkable) {
		return null;
	}

	@Override
	public ShrinkingDistance distance() {
		BigInteger distance = value().subtract(target).abs();
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
