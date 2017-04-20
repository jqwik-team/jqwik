package net.jqwik.properties;

import java.util.*;
import java.util.concurrent.atomic.*;

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

	public static <T> RandomGenerator<T> samples(T... samples) {
		AtomicInteger position = new AtomicInteger(0);
		return ignored -> {
			if (position.get() >= samples.length)
				position.set(0);
			return samples[position.getAndIncrement()];
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

	public static RandomGenerator<String> string(char[] characters, int maxLength) {
		Character[] validCharacters = new Character[characters.length];
		for (int i = 0; i < characters.length; i++) {
			validCharacters[i] = characters[i];
		}
		RandomGenerator<Character> charGenerator = choose(validCharacters);
		return string(charGenerator, maxLength);
	}

	public static RandomGenerator<String> string(char from, char to, int maxLength) {
		RandomGenerator<Character> charGenerator = choose(from, to);
		return string(charGenerator, maxLength);
	}

	private static RandomGenerator<String> string(RandomGenerator<Character> charGenerator, int maxLength) {
		return random -> choose(0, maxLength).map(i -> {
			final char[] chars = new char[i];
			for (int j = 0; j < i; j++) {
				chars[j] = charGenerator.next(random);
			}
			return new String(chars);
		}).next(random);
	}

	public static RandomGenerator<Character> choose(char min, char max) {
		if (min == max) {
			return ignored -> min;
		} else {
			return random -> (char) (int) choose((int) min, (int) max).next(random);
		}
	}

}
