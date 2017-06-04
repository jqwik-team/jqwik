package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

public class DoubleShrinkCandidates implements ShrinkCandidates<Double> {
	private final Range<Double> range;
	private final int precision;
	private final LongShrinkCandidates integralShrinkCandidates;

	public DoubleShrinkCandidates(double min, double max, int precision) {
		range = Range.of(min, max);
		this.precision = precision;
		this.integralShrinkCandidates = new LongShrinkCandidates((long) min, (long) max);
	}

	@Override
	public int distance(Double value) {
		return (int) (Math.min(Math.abs((determineTarget(value) - value) * Math.pow(10, precision)), Integer.MAX_VALUE));
	}

	@Override
	public Set<Double> nextCandidates(Double value) {
		if (!range.includes(value))
			return Collections.emptySet();
		if (hasDecimals(value))
			return shrinkDecimals(value);
		else
			return shrinkIntegral(value);
	}

	private Set<Double> shrinkDecimals(double value) {
		Set<Double> shrunkDecimals = new HashSet<>();
		shrunkDecimals.add(roundOneDigitDown(value));
		shrunkDecimals.add(roundOneDigitUp(value));
		return shrunkDecimals;
	}

	private Double roundOneDigitUp(double value) {
		return Math.ceil(value * 10) / 10;
	}

	private Double roundOneDigitDown(double value) {
		return Math.floor(value * 10) / 10;
	}

	private Set<Double> shrinkIntegral(double value) {
		return integralShrinkCandidates.nextCandidates((long) value) //
				.stream() //
				.map(aLong -> (double) aLong) //
				.collect(Collectors.toSet());
	}

	private boolean hasDecimals(double value) {
		long integralValue = (long) value;
		return value != integralValue;
	}

	// TODO: Remove duplication with IntegralShrinkCandidates.determineTarget
	private double determineTarget(double value) {
		if (!range.includes(value)) {
			return value;
		}
		if (range.includes(0.0))
			return 0;
		else {
			if (value < 0)
				return range.max;
			if (value > 0)
				return range.min;
		}
		return value; // Should never get here
	}

}
