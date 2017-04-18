package net.jqwik.properties.arbitraries;

import net.jqwik.properties.*;

public class RandomGenerators {

	@SafeVarargs
	public static <T> RandomGenerator<T> choose(T... values) {
		if (values.length == 0) {
			return fail("empty set of values");
		} else {
			return random -> RandomGenerators.choose(0, values.length - 1).map(i -> values[i]).next(random);
		}
	}

	public static RandomGenerator<Integer> choose(int min, int max) {
		if (min == max) {
			return ignored -> min;
		} else {
			final int _min = Math.min(min, max);
			final int _max = Math.max(min, max);
			return rng -> rng.nextInt(Math.abs(_max - _min) + 1) + _min;
		}
	}

	public static <T> RandomGenerator<T> fail(String message) {
		return ignored -> {
			throw new RuntimeException(message);
		};
	}
}
