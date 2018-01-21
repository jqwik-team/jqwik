package net.jqwik.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import net.jqwik.api.*;

public class RandomGenerators {

	public static <U> RandomGenerator<U> choose(U[] values) {
		if (values.length == 0) {
			return fail("empty set of values");
		} else {
			return random -> choose(0, values.length - 1).map(i -> values[i]).next(random);
		}
	}

	public static RandomGenerator<Byte> choose(byte min, byte max) {
		if (min == max) {
			return ignored -> Shrinkable.unshrinkable(min);
		} else {
			final int _min = Math.min(min, max);
			final int _max = Math.max(min, max);
			return random -> {
				int bound = Math.abs(_max - _min) + 1;
				byte value = (byte) (random.nextInt(bound >= 0 ? bound : Integer.MAX_VALUE) + _min);
				return new ShrinkableValue<>(value, new ByteShrinkCandidates(min, max));
			};
		}
	}

	public static RandomGenerator<Short> choose(short min, short max) {
		if (min == max) {
			return ignored -> Shrinkable.unshrinkable(min);
		} else {
			final int _min = Math.min(min, max);
			final int _max = Math.max(min, max);
			return random -> {
				int bound = Math.abs(_max - _min) + 1;
				short value = (short) (random.nextInt(bound >= 0 ? bound : Integer.MAX_VALUE) + _min);
				return new ShrinkableValue<>(value, new ShortShrinkCandidates(min, max));
			};
		}
	}

	public static RandomGenerator<Integer> choose(int min, int max) {
		if (min == max) {
			return ignored -> Shrinkable.unshrinkable(min);
		} else {
			final int _min = Math.min(min, max);
			final int _max = Math.max(min, max);
			return random -> {
				int bound = Math.abs(_max - _min) + 1;
				int value = random.nextInt(bound >= 0 ? bound : Integer.MAX_VALUE) + _min;
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

	public static RandomGenerator<Double> doubles(double min, double max, int scale) {
		return random -> {
			double randomDouble = randomDecimal(random, new BigDecimal(min), new BigDecimal(max), scale).doubleValue();
			return new ShrinkableValue<>(randomDouble, new DoubleShrinkCandidates(min, max, scale));
		};
	}

	public static RandomGenerator<Float> floats(float min, float max, int scale) {
		return random -> {
			float randomDouble = randomDecimal(random, new BigDecimal(min), new BigDecimal(max), scale).floatValue();
			return new ShrinkableValue<>(randomDouble, new FloatShrinkCandidates(min, max, scale));
		};
	}

	public static RandomGenerator<BigDecimal> bigDecimals(BigDecimal min, BigDecimal max, int scale) {
		return random -> {
			BigDecimal randomDecimal = randomDecimal(random, min, max, scale);
			return new ShrinkableValue<>(randomDecimal, new BigDecimalShrinkCandidates(min, max, scale));
		};
	}

	/**
	 * Random decimal are not equally distributed but randomly scaled down towards 0. Thus random decimals are more likely
	 * to be closer to 0 than
	 *
	 * @param random
	 *            source of randomness
	 * @param min
	 *            lower bound (included) of value to generate
	 * @param max
	 *            upper bound (included) of value to generate
	 * @param precision
	 *            The number of decimals to the right of decimal point
	 *
	 * @return a generated instance of BigDecimal
	 */
	public static BigDecimal randomDecimal(Random random, BigDecimal min, BigDecimal max, int precision) {
		BigDecimal range = max.subtract(min);
		BigDecimal randomFactor = new BigDecimal(random.nextDouble());
		BigDecimal unscaledRandom = randomFactor.multiply(range).add(min);
		int digits = Math.max(1, unscaledRandom.precision() - unscaledRandom.scale());
		int randomScaleDown = random.nextInt(digits);
		BigDecimal scaledRandom = unscaledRandom.movePointLeft(randomScaleDown);
		return scaledRandom.setScale(precision, BigDecimal.ROUND_DOWN);
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
			int minSize, int maxSize) {
		RandomGenerator<Integer> lengthGenerator = choose(minSize, maxSize);
		return random -> {
			int listSize = lengthGenerator.next(random).value();
			List<Shrinkable<T>> list = new ArrayList<>();
			while (list.size() < listSize) {
				list.add(elementGenerator.next(random));
			}
			return new ContainerShrinkable<>(list, containerFunction, minSize);
		};
	}

	public static <T> RandomGenerator<List<T>> list(RandomGenerator<T> elementGenerator, int minSize, int maxSize) {
		return container(elementGenerator, ArrayList::new, minSize, maxSize);
	}

	public static RandomGenerator<String> strings(RandomGenerator<Character> elementGenerator, int minLength, int maxLength) {
		return container(elementGenerator, ContainerShrinkable.CREATE_STRING, minLength, maxLength);
	}

	// TODO: Get rid of duplication with container(...)
	public static <T> RandomGenerator<Set<T>> set(RandomGenerator<T> elementGenerator, int minSize, int maxSize) {
		RandomGenerator<Integer> lengthGenerator = choose(minSize, maxSize);
		return random -> {
			int listSize = lengthGenerator.next(random).value();
			List<Shrinkable<T>> list = new ArrayList<>();
			Set<T> elements = new HashSet<>();
			while (list.size() < listSize) {
				Shrinkable<T> next = elementGenerator.next(random);
				if (elements.contains(next.value()))
					continue;
				list.add(next);
				elements.add(next.value());
			}
			return new ContainerShrinkable<>(list, HashSet::new, minSize);
		};
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
