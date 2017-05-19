package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.properties.shrinking.*;

abstract class NShrinkableIntegral<T> implements NShrinkable<T> {
	protected final long value;
	protected final long shrinkingTarget;

	protected NShrinkableIntegral(long value, long min, long max) {
		this(value, determineTarget(value, min, max));
	}

	protected NShrinkableIntegral(long value, long shrinkingTarget) {
		this.value = value;
		this.shrinkingTarget = shrinkingTarget;
	}

	@Override
	public Set<NShrinkable<T>> shrink() {
		if (shrinkingTarget == value)
			return Collections.emptySet();
		return Collections.singleton(shrinkTowardsTarget());
	}

	protected abstract NShrinkable<T> shrinkTowardsTarget();

	@Override
	public boolean falsifies(Predicate<T> falsifier) {
		return falsifier.negate().test(value());
	}

	@Override
	public int distance() {
		long diff = Math.abs(shrinkingTarget - value);
		if (diff < Integer.MAX_VALUE)
			return (int) diff;
		return Integer.MAX_VALUE;
	}

	private static long determineTarget(long value, long min, long max) {
		Range<Long> range = Range.of(min, max);
		if (!range.includes(value)) {
			return value;
		}
		if (range.includes(0L))
			return 0;
		else {
			if (value < 0)
				return max;
			if (value > 0)
				return min;
		}
		return value; // Should never get here
	}

	protected static long nextShrinkValue(long value, long target) {
		return value - calculateDelta(target, value);
	}

	private static long calculateDelta(long current, long target) {
		if (target > current)
			return (int) Math.max(Math.floor((target - current) / 2.0), 1);
		else
			return (int) Math.min(Math.ceil((target - current) / 2.0), -1);
	}

}
