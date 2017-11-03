package net.jqwik.properties.arbitraries;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class BigDecimalShrinkCandidates implements ShrinkCandidates<BigDecimal> {
	private final Range<BigDecimal> range;
	private final int scale;
	private final LongShrinkCandidates integralShrinkCandidates;

	public BigDecimalShrinkCandidates(BigDecimal min, BigDecimal max, int scale) {
		range = Range.of(min, max);
		this.scale = scale;
		this.integralShrinkCandidates = new LongShrinkCandidates(min.longValue(), max.longValue());
	}

	@Override
	public int distance(BigDecimal value) {
		double rawDistance = determineTarget(value).subtract(value).abs().doubleValue();
		return (int) (Math.min(rawDistance * Math.pow(10, scale), Integer.MAX_VALUE));
	}

	@Override
	public Set<BigDecimal> nextCandidates(BigDecimal value) {
		if (!range.includes(value))
			return Collections.emptySet();
		if (hasDecimals(value))
			return shrinkDecimals(value);
		else
			return shrinkIntegral(value);
	}

	private Set<BigDecimal> shrinkDecimals(BigDecimal value) {
		Set<BigDecimal> shrunkDecimals = new HashSet<>();
		shrunkDecimals.add(roundOneDigitDown(value));
		shrunkDecimals.add(roundOneDigitUp(value));
		return shrunkDecimals;
	}

	private BigDecimal roundOneDigitUp(BigDecimal value) {
		return value.setScale(value.scale() -1, BigDecimal.ROUND_UP);
	}

	private BigDecimal roundOneDigitDown(BigDecimal value) {
		return value.setScale(value.scale() -1, BigDecimal.ROUND_DOWN);
	}

	private Set<BigDecimal> shrinkIntegral(BigDecimal value) {
		return integralShrinkCandidates.nextCandidates(value.longValue()) //
				.stream() //
				.map(aLong -> new BigDecimal(aLong)) //
				.collect(Collectors.toSet());
	}

	private boolean hasDecimals(BigDecimal value) {
		if (value.scale() <= 0)
			return false;
		return value.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0;
	}

	// TODO: Remove duplication with IntegralShrinkCandidates.determineTarget
	private BigDecimal determineTarget(BigDecimal value) {
		if (!range.includes(value)) {
			return value;
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
