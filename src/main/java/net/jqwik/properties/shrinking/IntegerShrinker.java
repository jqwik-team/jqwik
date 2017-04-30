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
	public ShrinkTree<Integer> shrink(Integer value) {
		ShrinkTree<Integer> tree = new ShrinkTree<>();
		Range range = Range.of(min, max);
		if (!range.includes(value)) {
			return tree;
		}
		if (range.includes(0))
			addTowardsZero(value, tree);
		if (!Range.of(value, max).includes(0))
			addTowardsMax(value, tree);
		if (!Range.of(value, min).includes(0))
			addTowardsMin(value, tree);
		return tree;
	}

	private void addTowardsMax(Integer value, ShrinkTree<Integer> tree) {
		shrinkTowards(value, max, tree);
	}

	private void addTowardsMin(Integer value, ShrinkTree<Integer> tree) {
		shrinkTowards(value, min, tree);
	}

	private void addTowardsZero(Integer value, ShrinkTree<Integer> tree) {
		shrinkTowards(value, 0, tree);
	}

	private void shrinkTowards(Integer value, int target, ShrinkTree<Integer> tree) {
		IntegerShrinker.shrinkTowards(value, target).forEach(shrinkValue -> tree.add(shrinkValue));
	}

	private static List<ShrinkValue<Integer>> shrinkTowards(int value, int target) {
		List<ShrinkValue<Integer>> shrinkValues = new ArrayList<>();
		int current = value;
		while (Math.abs(current - target) > 1) {
			current = current + calculateDelta(target, current);
			int distance = Math.abs(target - current);
			ShrinkValue<Integer> shrinkValue = ShrinkValue.of(current, distance);
			shrinkValues.add(shrinkValue);
		}
		shrinkValues.add(ShrinkValue.of(target, 0));
		return shrinkValues;
	}

	private static int calculateDelta(int target, int current) {
		if (target > current)
			return (int) Math.ceil((target - current) / 2.0);
		else
			return (int) Math.floor((target - current) / 2.0);
	}

}
