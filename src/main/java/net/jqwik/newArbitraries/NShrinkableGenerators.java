package net.jqwik.newArbitraries;

import java.util.*;

public class NShrinkableGenerators {

	public static <U> NShrinkableGenerator<U> choose(U[] values) {
		if (values.length == 0) {
			return fail("empty set of values");
		} else {
			return random -> NShrinkableGenerators.choose(0, values.length - 1) //
				.map(i -> values[i]).next(random);
		}
	}

	public static NShrinkableGenerator<Integer> choose(int min, int max) {
		if (min == max) {
			return ignored -> new NShrinkableValue<>(min, 0, () -> Collections.emptySet());
		} else {
			final int _min = Math.min(min, max);
			final int _max = Math.max(min, max);
			return random -> {
				int value = random.nextInt(Math.abs(_max - _min) + 1) + _min;
				return new NShrinkableInteger(value, min, max);
			};
		}
	}


	public static <T> NShrinkableGenerator<T> fail(String message) {
		return ignored -> {
			throw new RuntimeException(message);
		};
	}


}
