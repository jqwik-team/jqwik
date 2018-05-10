package net.jqwik.properties.arbitraries;

import net.jqwik.*;
import net.jqwik.api.*;

import java.math.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public class RandomGenerators {

	public static <U> RandomGenerator<U> choose(List<U> values) {
		if (values.size() == 0) {
			return fail("empty set of values");
		}
		ValuesShrinkCandidates<U> shrinkingCandidates = new ValuesShrinkCandidates<>(values);
		return random -> {
			U value = chooseValue(values, random);
			return new ShrinkableValue<>(value, shrinkingCandidates);
		};
	}

	public static <U> U chooseValue(List<U> values, Random random) {
		int index = random.nextInt(values.size());
		return values.get(index);
	}

	public static <U> RandomGenerator<U> choose(U[] values) {
		return choose(Arrays.asList(values));
	}

	public static <T extends Enum<T>> RandomGenerator<T> choose(Class<T> enumClass) {
		return choose(enumClass.getEnumConstants());
	}

	public static RandomGenerator<Character> choose(char[] characters) {
		Character[] validCharacters = new Character[characters.length];
		for (int i = 0; i < characters.length; i++) {
			validCharacters[i] = characters[i];
		}
		return choose(validCharacters);
	}

	public static RandomGenerator<Character> chars(char min, char max) {
		return integers(min, max).map(anInt -> ((char) (int) anInt));
	}

	public static RandomGenerator<Byte> bytes(byte min, byte max) {
		return bigIntegers(BigInteger.valueOf(min), BigInteger.valueOf(max)).map(BigInteger::byteValueExact);
	}

	public static RandomGenerator<Short> shorts(short min, short max) {
		return bigIntegers(BigInteger.valueOf(min), BigInteger.valueOf(max)).map(BigInteger::shortValueExact);
	}

	public static RandomGenerator<Integer> integers(int min, int max) {
		return bigIntegers(BigInteger.valueOf(min), BigInteger.valueOf(max)).map(BigInteger::intValueExact);
	}

	public static RandomGenerator<Long> longs(long min, long max) {
		return bigIntegers(BigInteger.valueOf(min), BigInteger.valueOf(max)).map(BigInteger::longValueExact);
	}

	public static RandomGenerator<BigInteger> bigIntegers(
		BigInteger min, BigInteger max, BigInteger... partitionPoints
	) {
		return RandomIntegralGenerators.bigIntegers(Range.of(min, max), partitionPoints);
	}

	public static RandomGenerator<Double> doubles(double min, double max, int scale) {
		return bigDecimals(BigDecimal.valueOf(min), BigDecimal.valueOf(max), scale).map(BigDecimal::doubleValue);
	}

	public static RandomGenerator<Float> floats(float min, float max, int scale) {
		return bigDecimals(BigDecimal.valueOf((double) min), BigDecimal.valueOf((double) max), scale).map(BigDecimal::floatValue);
	}

	public static RandomGenerator<BigDecimal> bigDecimals(BigDecimal min, BigDecimal max, int scale, BigDecimal... partitionPoints) {
		return RandomDecimalGenerators.bigDecimals(Range.of(min, max), scale, partitionPoints);
	}

	public static <T> RandomGenerator<List<T>> list(RandomGenerator<T> elementGenerator, int minSize, int maxSize) {
		int defaultCutoff = defaultCutoffSize(minSize, maxSize);
		return list(elementGenerator, minSize, maxSize, defaultCutoff);
	}

	public static <T> RandomGenerator<List<T>> list(
		RandomGenerator<T> elementGenerator, int minSize, int maxSize, int cutoffSize
	) {
		Function<List<Shrinkable<T>>, Shrinkable<List<T>>> createShrinkable = elements -> new ContainerShrinkable<>(elements, ArrayList::new, minSize);
		return container(elementGenerator, createShrinkable, minSize, maxSize, cutoffSize);
	}

	public static RandomGenerator<String> strings(
		RandomGenerator<Character> elementGenerator, int minLength, int maxLength, int cutoffLength
	) {
		Function<List<Shrinkable<Character>>, Shrinkable<String>> createShrinkable = elements -> new ContainerShrinkable<>(elements, ContainerShrinkable.CREATE_STRING, minLength);
		return container(elementGenerator, createShrinkable, minLength, maxLength, cutoffLength);
	}

	public static RandomGenerator<String> strings(
		RandomGenerator<Character> elementGenerator, int minLength, int maxLength
	) {
		int defaultCutoff = defaultCutoffSize(minLength, maxLength);
		return strings(elementGenerator, minLength, maxLength, defaultCutoff);
	}

	private static int defaultCutoffSize(int minSize, int maxSize) {
		int range = maxSize - minSize;
		int offset = (int) Math.max(Math.round(Math.sqrt(100)), 10);
		if (range <= offset)
			return maxSize;
		return Math.min(offset + minSize, maxSize);
	}

	private static <T, C> RandomGenerator<C> container( //
														RandomGenerator<T> elementGenerator, //
														Function<List<Shrinkable<T>>, Shrinkable<C>> createShrinkable, //
														int minSize, int maxSize, int cutoffSize
	) {
		Function<Random, Integer> sizeGenerator = sizeGenerator(minSize, maxSize, cutoffSize);
		return random -> {
			int listSize = sizeGenerator.apply(random);
			List<Shrinkable<T>> list = new ArrayList<>();
			while (list.size() < listSize) {
				list.add(elementGenerator.next(random));
			}
			return createShrinkable.apply(list);
		};
	}

	private static Function<Random, Integer> sizeGenerator(int minSize, int maxSize, int cutoffSize) {
		if (cutoffSize >= maxSize)
			return random -> randomSize(random, minSize, maxSize);
		// Choose size below cutoffSize with probability of 0.9
		return random -> {
			if (random.nextDouble() > 0.1)
				return randomSize(random, minSize, cutoffSize);
			else
				return randomSize(random, cutoffSize + 1, maxSize);
		};
	}

	private static int randomSize(Random random, int minSize, int maxSize) {
		int range = maxSize - minSize;
		return random.nextInt(range + 1) + minSize;
	}

	public static <T> RandomGenerator<Set<T>> set(RandomGenerator<T> elementGenerator, int minSize, int maxSize) {
		int defaultCutoffSize = defaultCutoffSize(minSize, maxSize);
		return set(elementGenerator, minSize, maxSize, defaultCutoffSize);
	}

	public static <T> RandomGenerator<Set<T>> set(
		RandomGenerator<T> elementGenerator, int minSize, int maxSize, int cutoffSize
	) {
		Function<Random, Integer> sizeGenerator = sizeGenerator(minSize, maxSize, cutoffSize);
		return random -> {
			int listSize = sizeGenerator.apply(random);
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

	public static <T> RandomGenerator<T> samplesFromShrinkables(List<Shrinkable<T>> samples) {
		AtomicInteger tryCount = new AtomicInteger(0);
		return ignored -> {
			if (tryCount.get() >= samples.size())
				tryCount.set(0);
			return samples.get(tryCount.getAndIncrement());
		};
	}

	public static <T> RandomGenerator<T> samples(T[] samples) {
		List<Shrinkable<T>> shrinkables = ShrinkableSample.of(samples);
		return samplesFromShrinkables(shrinkables);
	}

	public static <T> RandomGenerator<T> frequency(Tuples.Tuple2<Integer, T>[] frequencies) {
		return new FrequencyGenerator<>(frequencies);
	}


	public static <T> RandomGenerator<T> fail(String message) {
		return ignored -> {
			throw new JqwikException(message);
		};
	}

	static int defaultCutoffSize(int minSize, int maxSize, int genSize) {
		int range = maxSize - minSize;
		int offset = (int) Math.max(Math.round(Math.sqrt(genSize)), 10);
		if (range <= offset)
			return maxSize;
		return Math.min(offset + minSize, maxSize);
	}

}
