package net.jqwik.properties.newShrinking;

import net.jqwik.*;
import net.jqwik.properties.arbitraries.*;

import java.math.*;
import java.util.*;

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
	public Set<NShrinkable<BigDecimal>> shrinkCandidatesFor(NShrinkable<BigDecimal> shrinkable) {
		return null;
	}

	@Override
	public ShrinkingDistance distance() {
		return null;
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
