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
		addZeroNode(tree);
		addTowardsMax(value, tree);
		addTowardsMin(value, tree);
		return tree;
	}

	private void addTowardsMax(Integer value, ShrinkTree<Integer> tree) {
		IntegerShrinker.shrinkTowardsMax(value, max).forEach(shrinkValue -> tree.add(shrinkValue));
	}

	private void addTowardsMin(Integer value, ShrinkTree<Integer> tree) {
		IntegerShrinker.shrinkTowardsMin(value, min).forEach(shrinkValue -> tree.add(shrinkValue));
	}

	private void addZeroNode(ShrinkTree<Integer> tree) {
		tree.add(ShrinkValue.of(0, 0));
	}

	private static List<ShrinkValue<Integer>> shrinkTowardsMax(int value, int target) {
		List<ShrinkValue<Integer>> shrinkValues = new ArrayList<>();
		int current = value;
		while (Math.abs(current - target) > 1) {
			// TODO: Get rid of difference with shrinkTowardsMin
			int delta = (int) Math.ceil((target - current) / 2.0);
			current = current + delta;
			int distance = (int) Math.round(Math.abs(((target - current) * 1.0 / (target - value)) * 100.0));
			ShrinkValue<Integer> shrinkValue = ShrinkValue.of(current, distance);
			shrinkValues.add(shrinkValue);
		}
		return shrinkValues;
	}

	private static List<ShrinkValue<Integer>> shrinkTowardsMin(int value, int target) {
		List<ShrinkValue<Integer>> shrinkValues = new ArrayList<>();
		int current = value;
		while (Math.abs(current - target) > 1) {
			// TODO: Get rid of difference with shrinkTowardsMax
			int delta = (int) Math.floor((target - current) / 2.0);
			current = current + delta;
			int distance = (int) Math.round(Math.abs(((target - current) * 1.0 / (target - value)) * 100.0));
			ShrinkValue<Integer> shrinkValue = ShrinkValue.of(current, distance);
			shrinkValues.add(shrinkValue);
		}
		return shrinkValues;
	}
}
