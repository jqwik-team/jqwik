package net.jqwik.newArbitraries;

import net.jqwik.properties.shrinking.*;

import java.util.*;
import java.util.function.*;

/**
 * TODO: Merge with NShrinkableInteger which is basically identically except of distance() which always returns an int
 */
public class NShrinkableLong implements NShrinkable<Long> {
	private final long value;
	private final long shrinkingTarget;

	public NShrinkableLong(long value, long min, long max) {
		this(value, determineTarget(value, min, max));
	}

	private NShrinkableLong(Long value, Long shrinkingTarget) {
		this.value = value;
		this.shrinkingTarget = shrinkingTarget;
	}

	@Override
	public Set<NShrinkable<Long>> shrink() {
		if (shrinkingTarget == value) return Collections.emptySet();
		return Collections.singleton(shrinkTowards(value, shrinkingTarget));
	}

	@Override
	public boolean falsifies(Predicate<Long> falsifier) {
		return falsifier.negate().test(value);
	}

	@Override
	public Long value() {
		return value;
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
		if (range.includes(0L)) return 0;
		else {
			if (value < 0) return max;
			if (value > 0) return min;
		}
		return value; // Should never get here
	}

	private static NShrinkable<Long> shrinkTowards(long value, long target) {
		long next = value - calculateDelta(target, value);
		return new NShrinkableLong(next, target);
	}

	private static long calculateDelta(long current, long target) {
		if (target > current) return (int) Math.max(Math.floor((target - current) / 2.0), 1);
		else return (int) Math.min(Math.ceil((target - current) / 2.0), -1);
	}

}
