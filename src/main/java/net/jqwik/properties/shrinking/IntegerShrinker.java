package net.jqwik.properties.shrinking;

import java.util.*;

public class IntegerShrinker implements Shrinker<Integer> {

	private final int min;
	private final int max;

	public IntegerShrinker(int min, int max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public Shrinkable<Integer> shrink(Integer value) {
		ShrinkableChoice<Integer> tree = new ShrinkableChoice<>();
		Range range = Range.of(min, max);
		if (!range.includes(value)) {
			return tree;
		}
		if (range.includes(0) && value != 0)
			addTowardsZero(value, tree);
		if (!Range.of(value, max).includes(0) && max != Integer.MAX_VALUE)
			addTowardsMax(value, tree);
		if (!Range.of(value, min).includes(0) && min != Integer.MIN_VALUE)
			addTowardsMin(value, tree);
		return tree;
	}

	private void addTowardsMax(Integer value, ShrinkableChoice<Integer> tree) {
		tree.addChoice(routeTowards(value, max));
	}

	private void addTowardsMin(Integer value, ShrinkableChoice<Integer> tree) {
		tree.addChoice(routeTowards(value, min));
	}

	private void addTowardsZero(Integer value, ShrinkableChoice<Integer> tree) {
		tree.addChoice(routeTowards(value, 0));
	}

	private List<Shrinkable<Integer>> routeTowards(Integer value, int target) {
		List<Shrinkable<Integer>> route = new ArrayList<>();
		IntegerShrinker.shrinkTowards(value, target).forEach(shrinkValue -> route.add(shrinkValue));
		return route;
	}

	private static List<ShrinkableValue<Integer>> shrinkTowards(int value, int target) {
		List<ShrinkableValue<Integer>> shrinkValues = new ArrayList<>();
		int current = value;
		while (Math.abs(current - target) > 1) {
			current = current + calculateDelta(target, current);
			int distance = Math.abs(target - current);
			ShrinkableValue<Integer> shrinkValue = ShrinkableValue.of(current, distance);
			shrinkValues.add(shrinkValue);
		}
		shrinkValues.add(ShrinkableValue.of(target, 0));
		return shrinkValues;
	}

	private static int calculateDelta(int target, int current) {
		if (target > current)
			return (int) Math.ceil((target - current) / 2.0);
		else
			return (int) Math.floor((target - current) / 2.0);
	}

}
