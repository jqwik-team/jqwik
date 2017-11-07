package net.jqwik.properties.arbitraries;

import java.util.*;

abstract class IntegralShrinkCandidates<T extends Number> implements ShrinkCandidates<T> {

	private final Range<Long> range;

	protected IntegralShrinkCandidates(long min, long max) {
		this.range = Range.of(min, max);
	}

	@Override
	public Set<T> nextCandidates(T value) {
		T shrunkValue = shrinkTowardsTarget(value);
		if (value.equals(shrunkValue))
			return Collections.emptySet();
		Set<T> candidates = new HashSet<>();
		candidates.add(shrunkValue);
		candidates.add(shrinkOneTowardsTarget(value));
		return candidates;
	}

	protected abstract T shrinkTowardsTarget(T value);

	protected abstract T shrinkOneTowardsTarget(T value);

	protected int distanceFromLong(long value) {
		long diff = Math.abs(determineTarget(value) - value);
		if (diff < Integer.MAX_VALUE)
			return (int) diff;
		return Integer.MAX_VALUE;
	}

	protected long nextShrinkValue(long value) {
		return value - calculateDelta(determineTarget(value), value);
	}

	protected long nextShrinkOne(long value) {
		return value - calculateDeltaOne(determineTarget(value), value);
	}

	private static long calculateDelta(long current, long target) {
		if (target > current)
			return Math.max((target - current) / 2L, 1);
		if (target < current)
			return Math.min((target - current) / 2L, -1);
		return 0;
	}

	private static long calculateDeltaOne(long current, long target) {
		if (target > current)
			return 1L;
		if (target < current)
			return -1L;
		return 0;
	}

	private long determineTarget(long value) {
		if (!range.includes(value)) {
			return value;
		}
		if (range.includes(0L))
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
