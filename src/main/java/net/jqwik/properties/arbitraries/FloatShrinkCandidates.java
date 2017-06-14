package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

// TODO: Remove duplication with DoubleShrinkCandidates
public class FloatShrinkCandidates implements ShrinkCandidates<Float> {
	private final Range<Float> range;
	private final int precision;
	private final LongShrinkCandidates integralShrinkCandidates;

	public FloatShrinkCandidates(float min, float max, int precision) {
		range = Range.of(min, max);
		this.precision = precision;
		this.integralShrinkCandidates = new LongShrinkCandidates((long) min, (long) max);
	}

	@Override
	public int distance(Float value) {
		return (int) (Math.min(Math.abs((determineTarget(value) - value) * Math.pow(10, precision)), Integer.MAX_VALUE));
	}

	@Override
	public Set<Float> nextCandidates(Float value) {
		if (!range.includes(value))
			return Collections.emptySet();
		if (hasDecimals(value))
			return shrinkDecimals(value);
		else
			return shrinkIntegral(value);
	}

	private Set<Float> shrinkDecimals(float value) {
		Set<Float> shrunkDecimals = new HashSet<>();
		shrunkDecimals.add(roundOneDigitDown(value));
		shrunkDecimals.add(roundOneDigitUp(value));
		return shrunkDecimals;
	}

	private Float roundOneDigitUp(float value) {
		return (float) Math.ceil(value * 10) / 10;
	}

	private Float roundOneDigitDown(float value) {
		return (float) Math.floor(value * 10) / 10;
	}

	private Set<Float> shrinkIntegral(float value) {
		return integralShrinkCandidates.nextCandidates((long) value) //
				.stream() //
				.map(aLong -> (float) aLong) //
				.collect(Collectors.toSet());
	}

	private boolean hasDecimals(double value) {
		long integralValue = (long) value;
		return value != integralValue;
	}

	// TODO: Remove duplication with IntegralShrinkCandidates.determineTarget
	private double determineTarget(float value) {
		if (!range.includes(value)) {
			return value;
		}
		if (range.includes(0.0f))
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
