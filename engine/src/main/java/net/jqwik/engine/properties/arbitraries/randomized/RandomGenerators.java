package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.*;

public class RandomGenerators {

	public static final int DEFAULT_COLLECTION_SIZE = 255;

	public static <U> RandomGenerator<U> choose(List<U> values) {
		if (values.size() == 0) {
			return fail("empty set of values");
		}
		return random -> {
			U value = chooseValue(values, random);
			return new ChooseValueShrinkable<>(value, values);
		};
	}

	public static <U> U chooseValue(List<U> values, Random random) {
		int index = random.nextInt(values.size());
		return values.get(index);
	}

	public static <U> RandomGenerator<U> choose(U[] values) {
		return choose(Arrays.asList(values));
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

	public static RandomGenerator<Integer> integers(int min, int max) {
		BigInteger minBig = BigInteger.valueOf(min);
		BigInteger maxBig = BigInteger.valueOf(max);
		return bigIntegers(
			minBig,
			maxBig,
			RandomIntegralGenerators.defaultShrinkingTarget(Range.of(minBig, maxBig)),
			Collections.emptyList()
		).map(BigInteger::intValueExact);
	}

	public static RandomGenerator<BigInteger> bigIntegers(
		BigInteger min,
		BigInteger max,
		BigInteger shrinkingTarget
	) {
		return bigIntegers(min, max, shrinkingTarget, Collections.emptyList());
	}

	public static RandomGenerator<BigInteger> bigIntegers(
		BigInteger min,
		BigInteger max,
		BigInteger shrinkingTarget,
		List<BigInteger> partitionPoints
	) {
		Range<BigInteger> range = Range.of(min, max);
		return RandomIntegralGenerators.bigIntegers(range, partitionPoints, shrinkingTarget);
	}

	public static RandomGenerator<BigDecimal> bigDecimals(
		Range<BigDecimal> range,
		int scale,
		BigDecimal shrinkingTarget
	) {
		return bigDecimals(range, scale, shrinkingTarget, Collections.emptyList());
	}

	public static RandomGenerator<BigDecimal> bigDecimals(
		Range<BigDecimal> range,
		int scale,
		BigDecimal shrinkingTarget,
		List<BigDecimal> partitionPoints
	) {
		checkRangeIsSound(range, scale);
		return RandomDecimalGenerators.bigDecimals(range, scale, partitionPoints, shrinkingTarget);
	}

	private static void checkRangeIsSound(Range<BigDecimal> range, int scale) {
		if (range.minIncluded || range.maxIncluded) {
			return;
		}
		BigDecimal minimumDifference = BigDecimal.ONE.movePointLeft(scale);
		if (range.min.add(minimumDifference).compareTo(range.max) >= 0) {
			String message = String.format("No number with scale <%s> can be generated in %s", scale, range);
			throw new JqwikException(message);
		}
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

	public static <T> RandomGenerator<T> oneOf(List<RandomGenerator<T>> all) {
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
			MaxTriesLoop.loop(
				() -> elements.size() < listSize,
				ignore -> {
					Shrinkable<T> next = elementGenerator.next(random);
					if (values.contains(next.value())) {
						return Tuple.of(false, ignore);
					}
					elements.add(next);
					values.add(next.value());
					return Tuple.of(false, ignore);
				},
				maxMisses -> {
					String message = String.format(
						"Generating values for set of size %s missed more than %s times.",
						listSize, maxMisses
					);
					return new JqwikException(message);
				}
			);
			return new ShrinkableSet<>(elements, minSize);
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
		List<Shrinkable<T>> shrinkables = SampleShrinkable.listOf(samples);
		return samplesFromShrinkables(shrinkables);
	}

	public static <T> RandomGenerator<T> frequency(List<Tuple.Tuple2<Integer, T>> frequencies) {
		return new FrequencyGenerator<>(frequencies);
	}

	public static <T> RandomGenerator<T> withEdgeCases(RandomGenerator<T> self, int genSize, EdgeCases<T> edgeCases) {
		if (edgeCases.isEmpty()) {
			return self;
		}
		return new WithEdgeCasesGenerator<>(self, edgeCases, genSize);
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

	public static List<BigInteger> calculateBiasedPartitionPoints(
		int genSize,
		BigInteger min,
		BigInteger max,
		BigInteger shrinkingTarget
	) {
		return BiasedPartitionPointsCalculator.calculatePartitionPoints(genSize, min, max, shrinkingTarget);
	}

	public static List<BigDecimal> calculateBiasedPartitionPoints(
		int genSize,
		Range<BigDecimal> range,
		BigDecimal shrinkingTarget
	) {
		List<BigInteger> integerPartitionPoints = calculateBiasedPartitionPoints(
			genSize,
			range.min.toBigInteger(),
			range.max.toBigInteger(),
			shrinkingTarget.toBigInteger()
		);
		return integerPartitionPoints
				   .stream()
				   .map(BigDecimal::new)
				   .collect(Collectors.toList());
	}
}
