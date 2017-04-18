package net.jqwik.properties.arbitraries;

import javaslang.test.*;
import net.jqwik.properties.*;
import net.jqwik.properties.Arbitrary;

import java.util.*;

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
			return random -> random.nextInt(Math.abs(_max - _min) + 1) + _min;
		}
	}

	public static RandomGenerator<Long> choose(long min, long max) {
		if (min == max) {
			return ignored -> min;
		} else {
			return random -> {
				final double d = random.nextDouble();
				final long _min = Math.min(min, max);
				final long _max = Math.max(min, max);
				return (long) ((d * _max) + ((1.0 - d) * _min) + d);
			};
		}
	}

	static <T extends Enum<T>> RandomGenerator<T> choose(Class<T> enumClass) {
		return random -> choose(enumClass.getEnumConstants()).next(random);
	}


	public static <T> RandomGenerator<T> fail(String message) {
		return ignored -> {
			throw new RuntimeException(message);
		};
	}

	public static <T> RandomGenerator<List<T>> list(RandomGenerator<T> elementGenerator, int maxSize) {
		return random -> choose(0, maxSize).map(i -> {
			List<T> list = new ArrayList<>();
			for (int j = 0; j < i; j++) {
				list.add(elementGenerator.next(random));
			}
			return list;
		}).next(random);
	}
}
