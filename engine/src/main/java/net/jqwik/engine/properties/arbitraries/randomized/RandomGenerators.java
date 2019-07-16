package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.*;
import net.jqwik.engine.properties.shrinking.*;

public class RandomGenerators {

	public static final int DEFAULT_COLLECTION_SIZE = 255;
	private static final long MAX_MISSES = 10000;

	public static <U> RandomGenerator<U> choose(List<U> values) {
		if (values.size() == 0) {
			return fail("empty set of values");
		}
		return random -> {
			U value = chooseValue(values, random);
			return new ChooseValueShrinkable<>(value, values);
		};
	}

	private static <U> U chooseValue(List<U> values, Random random) {
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
		List<Character> validCharacters = new ArrayList<>(characters.length);
		for (char character : characters) {
			validCharacters.add(character);
		}
		return choose(validCharacters);
	}

	public static RandomGenerator<Character> chars(char min, char max) {
		return integers(min, max).map(anInt -> ((char) (int) anInt));
	}

	public static RandomGenerator<Byte> bytes(byte min, byte max) {
		return bigIntegers(
			BigInteger.valueOf(min),
			BigInteger.valueOf(max),
			defaultShrinkingTargetCalculator(BigInteger.valueOf(min), BigInteger.valueOf(max))
		).map(BigInteger::byteValueExact);
	}

	public static RandomGenerator<Short> shorts(short min, short max) {
		return bigIntegers(
			BigInteger.valueOf(min),
			BigInteger.valueOf(max),
			defaultShrinkingTargetCalculator(BigInteger.valueOf(min), BigInteger.valueOf(max))
		).map(BigInteger::shortValueExact);
	}

	public static RandomGenerator<Integer> integers(int min, int max) {
		return bigIntegers(
			BigInteger.valueOf(min),
			BigInteger.valueOf(max),
			defaultShrinkingTargetCalculator(BigInteger.valueOf(min), BigInteger.valueOf(max))
		).map(BigInteger::intValueExact);
	}

	public static RandomGenerator<Long> longs(long min, long max) {
		BigInteger min1 = BigInteger.valueOf(min);
		BigInteger max1 = BigInteger.valueOf(max);
		return bigIntegers(min1, max1, defaultShrinkingTargetCalculator(min1, max1)).map(BigInteger::longValueExact);
	}

	public static RandomGenerator<BigInteger> bigIntegers(
		BigInteger min,
		BigInteger max,
		Function<BigInteger, BigInteger> shrinkingTargetCalculator,
		BigInteger... partitionPoints
	) {
		Range<BigInteger> range = Range.of(min, max);
		return RandomIntegralGenerators.bigIntegers(range, partitionPoints, shrinkingTargetCalculator);
	}

	public static RandomGenerator<Double> doubles(double min, double max, int scale) {
		return bigDecimals(
			BigDecimal.valueOf(min),
			BigDecimal.valueOf(max), scale,
			defaultShrinkingTargetCalculator(BigDecimal.valueOf(min), BigDecimal.valueOf(max))
		).map(BigDecimal::doubleValue);
	}

	public static RandomGenerator<Float> floats(float min, float max, int scale) {
		return bigDecimals(
			BigDecimal.valueOf(min),
			BigDecimal.valueOf(max), scale,
			defaultShrinkingTargetCalculator(BigDecimal.valueOf(min), BigDecimal.valueOf(max))
		).map(BigDecimal::floatValue);
	}

	public static RandomGenerator<BigDecimal> bigDecimals(
		BigDecimal min,
		BigDecimal max,
		int scale,
		Function<BigDecimal, BigDecimal> shrinkingTargetCalculator,
		BigDecimal... partitionPoints
	) {
		return RandomDecimalGenerators.bigDecimals(Range.of(min, max), scale, partitionPoints, shrinkingTargetCalculator);
	}

	public static <T> RandomGenerator<List<T>> list(RandomGenerator<T> elementGenerator, int minSize, int maxSize) {
		int defaultCutoff = defaultCutoffSize(minSize, maxSize);
		return list(elementGenerator, minSize, maxSize, defaultCutoff);
	}

	public static <T> RandomGenerator<List<T>> list(
		RandomGenerator<T> elementGenerator, int minSize, int maxSize, int cutoffSize
	) {
		Function<List<Shrinkable<T>>, Shrinkable<List<T>>> createShrinkable = elements -> new ShrinkableList<>(elements, minSize);
		return container(elementGenerator, createShrinkable, minSize, maxSize, cutoffSize);
	}

	public static<T> RandomGenerator<T> oneOf(List<RandomGenerator<T>> all) {
		return choose(all).flatMap(Function.identity());
	}

	public static <T> RandomGenerator<List<T>> shuffle(List<T> values) {
		return random -> {
			List<T> clone = new ArrayList<>(values);
			Collections.shuffle(clone, random);
			return Shrinkable.unshrinkable(clone);
		};
	}

	public static RandomGenerator<String> strings(
		RandomGenerator<Character> elementGenerator, int minLength, int maxLength, int cutoffLength
	) {
		Function<List<Shrinkable<Character>>, Shrinkable<String>> createShrinkable = elements -> new ShrinkableString(elements, minLength);
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

	private static <T, C> RandomGenerator<C> container(
		RandomGenerator<T> elementGenerator, //
		Function<List<Shrinkable<T>>, Shrinkable<C>> createShrinkable,//
		int minSize, int maxSize, int cutoffSize
	) {
		Function<Random, Integer> sizeGenerator = sizeGenerator(minSize, maxSize, cutoffSize);
		return new ContainerGenerator<>(elementGenerator, createShrinkable, sizeGenerator);
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
			Set<Shrinkable<T>> elements = new HashSet<>();
			Set<T> values = new HashSet<>();
			long count = 0;
			while (elements.size() < listSize) {
				Shrinkable<T> next = elementGenerator.next(random);
				if (values.contains(next.value())) {
					if (++count > MAX_MISSES) {
						String message = String.format(
							"Generating values for set of size %s missed more than %s times.",
							listSize, MAX_MISSES
						);
						throw new JqwikException(message);
					}
					continue;
				}
				elements.add(next);
				values.add(next.value());
			}
			return new ShrinkableSet<>(elements, minSize);
		};
	}

	public static <T> RandomGenerator<T> chooseShrinkable(List<Shrinkable<T>> shrinkables) {
		if (shrinkables.size() == 0) {
			return fail("empty set of shrinkables");
		}
		return random -> chooseValue(shrinkables, random);
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
		List<Shrinkable<T>> shrinkables = SampleShrinkable.listOf(samples);
		return samplesFromShrinkables(shrinkables);
	}

	public static <T> RandomGenerator<T> frequency(List<Tuple.Tuple2<Integer, T>> frequencies) {
		return new FrequencyGenerator<>(frequencies);
	}


	public static <T> RandomGenerator<T> withEdgeCases(RandomGenerator<T> self, int genSize, List<Shrinkable<T>> edgeCases) {
		if (edgeCases.isEmpty()) {
			return self;
		}

		int baseToEdgeCaseRatio =
			Math.min(
				Math.max(Math.round(genSize / 5), 1),
				100 / edgeCases.size()
			) + 1;

		RandomGenerator<T> edgeCasesGenerator = RandomGenerators.chooseShrinkable(edgeCases);

		return random -> {
			if (random.nextInt(baseToEdgeCaseRatio) == 0) {
				return edgeCasesGenerator.next(random);
			} else {
				return self.next(random);
			}
		};
	}


	public static <T> RandomGenerator<T> fail(String message) {
		return ignored -> {
			throw new JqwikException(message);
		};
	}

	public static int defaultCutoffSize(int minSize, int maxSize, int genSize) {
		int range = maxSize - minSize;
		int offset = (int) Math.max(Math.round(Math.sqrt(genSize)), 10);
		if (range <= offset)
			return maxSize;
		return Math.min(offset + minSize, maxSize);
	}

	public static Function<BigInteger, BigInteger> defaultShrinkingTargetCalculator(BigInteger min, BigInteger max) {
		return value -> ShrinkableBigInteger.defaultShrinkingTarget(value, Range.of(min, max));
	}

	public static Function<BigDecimal, BigDecimal> defaultShrinkingTargetCalculator(BigDecimal min, BigDecimal max) {
		return value -> ShrinkableBigDecimal.defaultShrinkingTarget(value, Range.of(min, max));
	}

	// TODO: This could be way more sophisticated
	public static BigInteger[] calculateDefaultPartitionPoints(int tries, BigInteger min, BigInteger max) {
		int partitionPoint = Math.max(tries / 2, 10);
		BigInteger upperPartitionPoint = BigInteger.valueOf(partitionPoint).min(max);
		BigInteger lowerPartitionPoint = BigInteger.valueOf(partitionPoint).negate().max(min);
		return new BigInteger[]{lowerPartitionPoint, upperPartitionPoint};
	}

	// TODO: This could be way more sophisticated
	public static BigDecimal[] calculateDefaultPartitionPoints(int genSize, BigDecimal min, BigDecimal max) {
		int partitionPoint = Math.max(genSize / 2, 10);
		BigDecimal upperPartitionPoint = BigDecimal.valueOf(partitionPoint).min(max);
		BigDecimal lowerPartitionPoint = BigDecimal.valueOf(partitionPoint).negate().max(min);
		return new BigDecimal[]{lowerPartitionPoint, upperPartitionPoint};
	}
}
