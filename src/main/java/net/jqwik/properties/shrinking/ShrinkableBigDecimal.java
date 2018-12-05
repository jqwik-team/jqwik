package net.jqwik.properties.shrinking;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.*;

import java.math.*;
import java.util.*;
import java.util.stream.*;

public class ShrinkableBigDecimal extends AbstractShrinkable<BigDecimal> {

	private final int scale;
	private final Range<BigDecimal> range;
	private final BigDecimal target;
	private final BigDecimalShrinkingCandidates shrinkingCandidates;

	public ShrinkableBigDecimal(BigDecimal value, Range<BigDecimal> range, int scale) {
		super(value);
		this.range = range;
		this.scale = scale;
		this.target = determineTarget(value);
		this.shrinkingCandidates = new BigDecimalShrinkingCandidates(this.range, this.target);
	}

	@Override
	public Set<Shrinkable<BigDecimal>> shrinkCandidatesFor(Shrinkable<BigDecimal> shrinkable) {
		return shrinkingCandidates.candidatesFor(shrinkable.value())
			.stream() //
			.map(aBigDecimal -> new ShrinkableBigDecimal(aBigDecimal, range, scale)) //
			.collect(Collectors.toSet());
	}

	@Override
	public ShrinkingDistance distance() {
		ShrinkingDistance bigIntegerDistance =
			ShrinkableBigInteger.distanceFor(value().toBigInteger(), target.toBigInteger());
		BigDecimal fractionalPart = value().remainder(BigDecimal.ONE).abs();
		BigDecimal fractionalPartScaled = fractionalPart.scaleByPowerOfTen(scale);
		ShrinkingDistance decimalDistance = fractionalPartScaled.compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) < 0
			? ShrinkingDistance.of(fractionalPartScaled.longValue())
			: ShrinkingDistance.of(Long.MAX_VALUE);
		return bigIntegerDistance.append(decimalDistance);
	}

	//TODO: Remove duplication with ShrinkableBigInteger
	private BigDecimal determineTarget(BigDecimal value) {
		if (!range.includes(value)) {
			String message = String.format("Number <%s> is outside allowed range %s", value, range);
			throw new JqwikException(message);
		}
		if (range.includes(BigDecimal.ZERO))
			return BigDecimal.ZERO;
		else {
			if (value.compareTo(BigDecimal.ZERO) < 0)
				return range.max;
			if (value.compareTo(BigDecimal.ZERO) > 0)
				return range.min;
		}
		return value; // Should never get here
	}


}
