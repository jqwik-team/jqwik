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
		Range range = Range.of(min, max);
		if (!range.includes(value)) {
			return Shrinkable.empty();
		}
		if (range.includes(0))
			return towards(value, 0);
		else {
			if (value < 0)
				return towards(value, max);
			if (value > 0)
				return towards(value, min);
		}
		return Shrinkable.empty(); // Should never get here
	}

	private ShrinkableSequence<Integer> towards(Integer value, int target) {
		ShrinkableSequence<Integer> sequence = new ShrinkableSequence<>();
		IntegerShrinker.shrinkTowards(value, target).forEach(shrinkValue -> sequence.addStep(shrinkValue));
		return sequence;
	}

	private static List<ShrinkableValue<Integer>> shrinkTowards(int value, int target) {
		List<ShrinkableValue<Integer>> shrinkValues = new ArrayList<>();
		int current = value;
		while (Math.abs(current - target) > 0) {
			int distance = Math.abs(target - current);
			ShrinkableValue<Integer> shrinkValue = ShrinkableValue.of(current, distance);
			shrinkValues.add(shrinkValue);
			current = current + calculateDelta(target, current);
		}
		shrinkValues.add(ShrinkableValue.of(target, 0));
		return shrinkValues;
	}

	private static int calculateDelta(int target, int current) {
		if (target > current)
			return (int) Math.max(Math.floor((target - current) / 2.0), 1);
		else
			return (int) Math.min(Math.ceil((target - current) / 2.0), -1);
	}

}
