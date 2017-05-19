package net.jqwik.newArbitraries;

import java.util.*;
import java.util.concurrent.atomic.*;

public class NShrinkableGenerators {

	public static <U> NShrinkableGenerator<U> choose(U[] values) {
		if (values.length == 0) {
			return fail("empty set of values");
		} else {
			return random -> choose(0, values.length - 1).map(i -> values[i]).next(random);
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

	public static NShrinkableGenerator<Long> choose(long min, long max) {
		if (min == max) {
			return ignored -> new NShrinkableValue<>(min, 0, () -> Collections.emptySet());
		} else {
			final long _min = Math.min(min, max);
			final long _max = Math.max(min, max);
			return random -> {
				final double d = random.nextDouble();
				long value = (long) ((d * _max) + ((1.0 - d) * _min) + d);
				return new NShrinkableLong(value, min, max);
			};
		}
	}

	public static <T extends Enum<T>> NShrinkableGenerator<T> choose(Class<T> enumClass) {
		return random -> choose(enumClass.getEnumConstants()).next(random);
	}

	public static <T> NShrinkableGenerator<T> samples(T... samples) {
		AtomicInteger tryCount = new AtomicInteger(0);
		return ignored -> {
			if (tryCount.get() >= samples.length)
				tryCount.set(0);
			return NShrinkableValue.unshrinkable(samples[tryCount.getAndIncrement()]);
		};
	}

	public static <T> NShrinkableGenerator<T> fail(String message) {
		return ignored -> {
			throw new RuntimeException(message);
		};
	}

}
