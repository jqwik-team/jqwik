package net.jqwik.newArbitraries;

import net.jqwik.properties.shrinking.*;

import java.util.*;

abstract class NIntegralShrinker<T extends Number> implements NShrinker<T> {

	private final Range<Long> range;

	protected NIntegralShrinker(long min, long max) {
		this.range = Range.of(min, max);
	}

	@Override
	public Set<T> shrink(T value) {
		return Collections.singleton(shrinkTowardsTarget(value));
	}

	protected abstract T shrinkTowardsTarget(T value);

	protected int distanceFromLong(long value) {
		long diff = Math.abs(determineTarget(value) - value);
		if (diff < Integer.MAX_VALUE)
			return (int) diff;
		return Integer.MAX_VALUE;
	}

	protected long nextShrinkValue(long value) {
		return value - calculateDelta(determineTarget(value), value);
	}

	private static long calculateDelta(long current, long target) {
		if (target > current)
			return (int) Math.max(Math.floor((target - current) / 2.0), 1);
		else
			return (int) Math.min(Math.ceil((target - current) / 2.0), -1);
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
