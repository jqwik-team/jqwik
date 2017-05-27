package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import net.jqwik.properties.*;

public class RandomGenerators {

	public static <U> RandomGenerator<U> choose(U[] values) {
		if (values.length == 0) {
			return fail("empty set of values");
		} else {
			return random -> choose(0, values.length - 1).map(i -> values[i]).next(random);
		}
	}

	public static RandomGenerator<Integer> choose(int min, int max) {
		if (min == max) {
			return ignored -> new ShrinkableValue<>(min, ignore -> Collections.emptySet());
		} else {
			final int _min = Math.min(min, max);
			final int _max = Math.max(min, max);
			return random -> {
				int value = random.nextInt(Math.abs(_max - _min) + 1) + _min;
				return new ShrinkableValue<>(value, new IntegerShrinkCandidates(min, max));
			};
		}
	}

	public static RandomGenerator<Long> choose(long min, long max) {
		if (min == max) {
			return ignored -> Shrinkable.unshrinkable(min);
		} else {
			final long _min = Math.min(min, max);
			final long _max = Math.max(min, max);
			return random -> {
				final double d = random.nextDouble();
				long value = (long) ((d * _max) + ((1.0 - d) * _min) + d);
				return new ShrinkableValue<>(value, new LongShrinkCandidates(min, max));
			};
		}
	}

	public static <T extends Enum<T>> RandomGenerator<T> choose(Class<T> enumClass) {
		return random -> choose(enumClass.getEnumConstants()).next(random);
	}

	public static RandomGenerator<Character> choose(char[] characters) {
		Character[] validCharacters = new Character[characters.length];
		for (int i = 0; i < characters.length; i++) {
			validCharacters[i] = characters[i];
		}
		return choose(validCharacters);
	}

	private static <T, C> RandomGenerator<C> container( //
														RandomGenerator<T> elementGenerator, //
														Function<List<T>, C> containerFunction, //
														int maxSize) {
		RandomGenerator<Integer> lengthGenerator = choose(0, maxSize);
		return random -> {
			int listSize = lengthGenerator.next(random).value();
			List<Shrinkable<T>> list = new ArrayList<>();
			for (int j = 0; j < listSize; j++) {
				list.add(elementGenerator.next(random));
			}
			return new ContainerShrinkable<>(list, containerFunction);
		};
	}

	public static <T> RandomGenerator<List<T>> list(RandomGenerator<T> elementGenerator, int maxSize) {
		return container(elementGenerator, ArrayList::new, maxSize);
	}

	public static RandomGenerator<String> string(RandomGenerator<Character> elementGenerator, int maxSize) {
		return container(elementGenerator, ContainerShrinkable.CREATE_STRING, maxSize);
	}

	public static RandomGenerator<Character> choose(char min, char max) {
		if (min == max) {
			return ignored -> Shrinkable.unshrinkable(min);
		} else {
			return random -> {
				Shrinkable<Integer> shrinkableInt = choose((int) min, (int) max).next(random);
				return shrinkableInt.map(anInt -> (char) anInt.intValue());
			};
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> RandomGenerator<T> samples(List<Shrinkable<T>> samples) {
		AtomicInteger tryCount = new AtomicInteger(0);
		return ignored -> {
			if (tryCount.get() >= samples.size())
				tryCount.set(0);
			return samples.get(tryCount.getAndIncrement());
		};
	}

	@SafeVarargs
	public static <T> RandomGenerator<T> samples(Shrinkable<T>... samples) {
		return samples(Arrays.asList(samples));
	}

	public static <T> RandomGenerator<T> fail(String message) {
		return ignored -> {
			throw new RuntimeException(message);
		};
	}

}
