package net.jqwik.newArbitraries;

import net.jqwik.properties.shrinking.*;

import java.util.*;
import java.util.function.*;

public class NShrinkableInteger implements NShrinkable<Integer> {
	private final int value;
	private final int shrinkingTarget;

	public NShrinkableInteger(int value, int min, int max) {
		this(value, determineTarget(value, min, max));
	}

	private NShrinkableInteger(int value, int shrinkingTarget) {
		this.value = value;
		this.shrinkingTarget = shrinkingTarget;
	}

	@Override
	public Set<NShrinkable<Integer>> shrink() {
		if (shrinkingTarget == value) return Collections.emptySet();
		return Collections.singleton(shrinkTowards(value, shrinkingTarget));
	}

	@Override
	public boolean falsifies(Predicate<Integer> falsifier) {
		return falsifier.negate().test(value);
	}

	@Override
	public Integer value() {
		return value;
	}

	@Override
	public int distance() {
		return Math.abs(shrinkingTarget - value);
	}

	private static int determineTarget(int value, int min, int max) {
		Range<Integer> range = Range.of(min, max);
		if (!range.includes(value)) {
			return value;
		}
		if (range.includes(0)) return 0;
		else {
			if (value < 0) return max;
			if (value > 0) return min;
		}
		return value; // Should never get here
	}

	private static NShrinkable<Integer> shrinkTowards(int value, int target) {
		int next = value - calculateDelta(target, value);
		return new NShrinkableInteger(next, target);
	}

	private static int calculateDelta(int current, int target) {
		if (target > current) return (int) Math.max(Math.floor((target - current) / 2.0), 1);
		else return (int) Math.min(Math.ceil((target - current) / 2.0), -1);
	}

}
