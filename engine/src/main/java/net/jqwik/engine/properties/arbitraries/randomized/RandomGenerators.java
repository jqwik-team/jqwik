package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.*;

import static java.lang.Math.*;

public class RandomGenerators {

	public static final int DEFAULT_COLLECTION_SIZE = 255;

	public static int collectionMaxSize(int minSize, Integer maxSize) {
		if (maxSize != null) {
			return maxSize;
		}
		int doubleMinSize = toIntExact(min(Integer.MAX_VALUE, (long) minSize * 2));
		return max(RandomGenerators.DEFAULT_COLLECTION_SIZE, doubleMinSize);
	}

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
				RandomDistribution.uniform()
		).map(BigInteger::intValueExact);
	}

	public static RandomGenerator<BigInteger> bigIntegers(
			BigInteger min,
			BigInteger max,
			BigInteger shrinkingTarget,
			RandomDistribution distribution
	) {
		return RandomIntegralGenerators.bigIntegers(1000, min, max, shrinkingTarget, distribution);
	}

	public static RandomGenerator<BigDecimal> bigDecimals(
			Range<BigDecimal> range,
			int scale,
			BigDecimal shrinkingTarget,
			RandomDistribution distribution
	) {
		return RandomDecimalGenerators.bigDecimals(1000, range, scale, distribution, shrinkingTarget);
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
		RandomGenerator<Character> elementGenerator,
		int minLength, int maxLength, long maxUniqueChars,
		int genSize, RandomDistribution lengthDistribution
	) {
		Function<List<Shrinkable<Character>>, Shrinkable<String>> createShrinkable = elements -> new ShrinkableString(elements, minLength, maxLength);
		return container(elementGenerator, createShrinkable, minLength, maxLength, maxUniqueChars, genSize, lengthDistribution, Collections.emptySet());
	}

	private static <T, C> RandomGenerator<C> container(
		RandomGenerator<T> elementGenerator,
		Function<List<Shrinkable<T>>, Shrinkable<C>> createShrinkable,
		int minSize, int maxSize, long maxUniqueElements,
		int genSize, RandomDistribution sizeDistribution,
		Set<FeatureExtractor<T>> uniquenessExtractors
	) {
		if (minSize > maxSize) {
			String message = String.format("minSize <%s> must not be larger than maxSize <%s>.", minSize, maxSize);
			throw new JqwikException(message);
		}
		return new ContainerGenerator<>(
			elementGenerator, createShrinkable,
			minSize, maxSize, maxUniqueElements,
			genSize, sizeDistribution,
			uniquenessExtractors
		);
	}

	public static <T> RandomGenerator<List<T>> list(
		RandomGenerator<T> elementGenerator,
		int minSize, int maxSize, long maxUniqueElements,
		int genSize, RandomDistribution sizeDistribution,
		Set<FeatureExtractor<T>> uniquenessExtractors,
		Arbitrary<T> elementArbitrary
	) {
		Function<List<Shrinkable<T>>, Shrinkable<List<T>>> createShrinkable =
			elements -> new ShrinkableList<>(elements, minSize, maxSize, uniquenessExtractors, elementArbitrary);
		return container(elementGenerator, createShrinkable, minSize, maxSize, maxUniqueElements, genSize, sizeDistribution, uniquenessExtractors);
	}

	public static <T> RandomGenerator<Set<T>> set(RandomGenerator<T> elementGenerator, int minSize, int maxSize, int genSize) {
		return set(elementGenerator, minSize, maxSize, genSize, null, Collections.emptySet());
	}

	public static <T> RandomGenerator<Set<T>> set(
			RandomGenerator<T> elementGenerator,
			int minSize, int maxSize, int genSize, RandomDistribution sizeDistribution,
			Set<FeatureExtractor<T>> uniquenessExtractors
	) {
		Set<FeatureExtractor<T>> extractors = new LinkedHashSet<>(uniquenessExtractors);
		extractors.add(FeatureExtractor.identity());
		Function<List<Shrinkable<T>>, Shrinkable<Set<T>>> createShrinkable =
			elements -> new ShrinkableSet<T>(elements, minSize, maxSize, uniquenessExtractors);
		return container(elementGenerator, createShrinkable, minSize, maxSize, maxSize, genSize, sizeDistribution, extractors);
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

	public static <T> RandomGenerator<T> frequency(List<Tuple2<Integer, T>> frequencies) {
		return new FrequencyGenerator<>(frequencies);
	}

	public static <T> RandomGenerator<T> frequencyOf(
			List<Tuple2<Integer, Arbitrary<T>>> frequencies,
			int genSize,
			boolean withEmbeddedEdgeCases
	) {
		return frequency(frequencies).flatMap(Function.identity(), genSize, withEmbeddedEdgeCases);
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

}
