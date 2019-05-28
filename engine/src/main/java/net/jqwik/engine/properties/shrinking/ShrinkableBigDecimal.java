package net.jqwik.engine.properties.shrinking;

import java.math.*;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.*;

public class ShrinkableBigDecimal extends AbstractShrinkable<BigDecimal> {

	private final int scale;
	private final Range<BigDecimal> range;
	private final BigDecimal target;
	private final BigDecimalShrinkingCandidates shrinkingCandidates;

	public static BigDecimal defaultShrinkingTarget(BigDecimal value, Range<BigDecimal> range) {
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

	public ShrinkableBigDecimal(BigDecimal value, Range<BigDecimal> range, int scale, BigDecimal shrinkingTarget) {
		super(value);
		this.range = range;
		this.scale = scale;
		this.target = shrinkingTarget;
		checkTargetInRange(shrinkingTarget);
		checkValueInRange(value);
		this.shrinkingCandidates = new BigDecimalShrinkingCandidates(this.range, this.target);
	}

	@Override
	public Set<Shrinkable<BigDecimal>> shrinkCandidatesFor(Shrinkable<BigDecimal> shrinkable) {
		return shrinkingCandidates.candidatesFor(shrinkable.value())
								  .stream() //
								  .map(aBigDecimal -> new ShrinkableBigDecimal(aBigDecimal, range, scale, defaultShrinkingTarget(aBigDecimal, range))) //
								  .collect(Collectors.toSet());
	}

	@Override
	public ShrinkingDistance distance() {
		BigDecimal differenceToTarget = target.subtract(value());
		ShrinkingDistance bigIntegerDistance =
			ShrinkableBigInteger.distanceFor(differenceToTarget.toBigInteger(), BigInteger.ZERO);
		BigDecimal fractionalPart = differenceToTarget.remainder(BigDecimal.ONE).abs();
		BigDecimal fractionalPartScaled = fractionalPart.scaleByPowerOfTen(scale);
		ShrinkingDistance decimalDistance = fractionalPartScaled.compareTo(BigDecimal.valueOf(Long.MAX_VALUE)) < 0
			? ShrinkingDistance.of(fractionalPartScaled.longValue())
			: ShrinkingDistance.of(Long.MAX_VALUE);
		return bigIntegerDistance.append(decimalDistance);
	}

	private void checkTargetInRange(BigDecimal value) {
		if (!range.includes(value)) {
			String message = String.format("Shrinking target <%s> is outside allowed range %s", value, range);
			throw new JqwikException(message);
		}
	}

	private void checkValueInRange(BigDecimal value) {
		if (!range.includes(value)) {
			String message = String.format("Number <%s> is outside allowed range %s", value, range);
			throw new JqwikException(message);
		}
	}

}
