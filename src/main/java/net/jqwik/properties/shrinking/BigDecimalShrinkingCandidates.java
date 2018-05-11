package net.jqwik.properties.shrinking;

import net.jqwik.properties.arbitraries.*;

import java.math.*;
import java.util.*;
import java.util.stream.*;

public class BigDecimalShrinkingCandidates implements ShrinkingCandidates<BigDecimal> {
	private final Range<BigDecimal> range;
	private final BigIntegerShrinkingCandidates integralShrinkCandidates;

	public BigDecimalShrinkingCandidates(Range<BigDecimal> range, BigDecimal target) {
		this.range = range;
		this.integralShrinkCandidates = new BigIntegerShrinkingCandidates(target.toBigInteger());
	}

	@Override
	public Set<BigDecimal> candidatesFor(BigDecimal value) {
		Set<BigDecimal> candidates = new HashSet<>();
		if (hasDecimals(value))
			candidates.addAll(shrinkDecimals(value));
		candidates.addAll(shrinkIntegral(value));
		return candidates;
	}

	private Set<BigDecimal> shrinkDecimals(BigDecimal value) {
		Set<BigDecimal> shrunkDecimals = new HashSet<>();
		range.ifIncluded(roundOneDigitDown(value), shrunkDecimals::add);
		range.ifIncluded(roundOneDigitUp(value), shrunkDecimals::add);
		return shrunkDecimals;
	}

	private BigDecimal roundOneDigitUp(BigDecimal value) {
		return value.setScale(value.scale() -1, BigDecimal.ROUND_UP);
	}

	private BigDecimal roundOneDigitDown(BigDecimal value) {
		return value.setScale(value.scale() -1, BigDecimal.ROUND_DOWN);
	}

	private Set<BigDecimal> shrinkIntegral(BigDecimal value) {
		return integralShrinkCandidates.candidatesFor(value.toBigInteger()) //
									   .stream() //
									   .map(BigDecimal::new) //
									   .collect(Collectors.toSet());
	}

	private boolean hasDecimals(BigDecimal value) {
		if (value.scale() <= 0)
			return false;
		return value.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0;
	}

}
