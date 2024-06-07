package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.*;

import org.jspecify.annotations.*;

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

	public static <U extends @Nullable Object> RandomGenerator<U> choose(List<? extends U> values) {
		if (values.isEmpty()) {
			return fail("empty set of values");
		}
		return random -> {
			U value = chooseValue(values, random);
			return new ChooseValueShrinkable<>(value, values);
		};
	}

	public static <U extends @Nullable Object> U chooseValue(List<? extends U> values, Random random) {
		int index = random.nextInt(values.size());
		return values.get(index);
	}

	public static <U extends @Nullable Object> RandomGenerator<U> choose(U[] values) {
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

	public static <T extends @Nullable Object> RandomGenerator<T> oneOf(List<? extends RandomGenerator<T>> all) {
		return choose(all).flatMap(Function.identity());
	}

	public static <T extends @Nullable Object> RandomGenerator<List<T>> shuffle(List<T> values) {
		return random -> {
			List<T> clone = new ArrayList<>(values);
			Collections.shuffle(clone, random);
			return Shrinkable.unshrinkable(clone);
		};
	}

	public static RandomGenerator<String> strings(
		RandomGenerator<Character> elementGenerator,
		int minLength, int maxLength, long maxUniqueChars,
		int genSize, RandomDistribution lengthDistribution,
		Arbitrary<Character> characterArbitrary,
		boolean uniqueChars
	) {
		Function<List<? extends Shrinkable<Character>>, Shrinkable<String>> createShrinkable = elements -> new ShrinkableString(elements, minLength, maxLength, characterArbitrary, uniqueChars);
		Set<FeatureExtractor<Character>> featureExtractors = uniqueChars ? ShrinkableString.UNIQUE_CHARS_EXTRACTOR : Collections.emptySet();
		return container(elementGenerator, createShrinkable, minLength, maxLength, maxUniqueChars, genSize, lengthDistribution, featureExtractors);
	}

	private static <T extends @Nullable Object, C extends @Nullable Object> RandomGenerator<C> container(
		RandomGenerator<T> elementGenerator,
		Function<? super List<? extends Shrinkable<T>>, ? extends Shrinkable<C>> createShrinkable,
		int minSize, int maxSize, long maxUniqueElements,
		int genSize, RandomDistribution sizeDistribution,
		Set<? extends FeatureExtractor<T>> uniquenessExtractors
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

	public static <T extends @Nullable Object> RandomGenerator<List<T>> list(
		RandomGenerator<T> elementGenerator,
		int minSize, int maxSize, long maxUniqueElements,
		int genSize, RandomDistribution sizeDistribution,
		Set<? extends FeatureExtractor<T>> uniquenessExtractors,
		Arbitrary<T> elementArbitrary
	) {
		Function<List<? extends Shrinkable<T>>, Shrinkable<List<T>>> createShrinkable =
			elements -> new ShrinkableList<>(elements, minSize, maxSize, uniquenessExtractors, elementArbitrary);
		return container(elementGenerator, createShrinkable, minSize, maxSize, maxUniqueElements, genSize, sizeDistribution, uniquenessExtractors);
	}

	public static <T extends @Nullable Object> RandomGenerator<Set<T>> set(
		RandomGenerator<T> elementGenerator,
		int minSize, int maxSize, int genSize,
		Arbitrary<T> elementArbitrary
	) {
		return set(elementGenerator, minSize, maxSize, genSize, null, Collections.emptySet(), elementArbitrary);
	}

	public static <T extends @Nullable Object> RandomGenerator<Set<T>> set(
			RandomGenerator<T> elementGenerator,
			int minSize, int maxSize, int genSize, RandomDistribution sizeDistribution,
			Set<? extends FeatureExtractor<T>> uniquenessExtractors,
			Arbitrary<T> elementArbitrary
	) {
		Set<FeatureExtractor<T>> extractors = new LinkedHashSet<>(uniquenessExtractors);
		extractors.add(FeatureExtractor.identity());
		Function<List<? extends Shrinkable<T>>, ? extends Shrinkable<Set<T>>> createShrinkable =
			elements -> new ShrinkableSet<T>(elements, minSize, maxSize, uniquenessExtractors, elementArbitrary);
		return container(elementGenerator, createShrinkable, minSize, maxSize, maxSize, genSize, sizeDistribution, extractors);
	}

	public static <T extends @Nullable Object> RandomGenerator<T> samplesFromShrinkables(List<? extends Shrinkable<T>> samples) {
		AtomicInteger tryCount = new AtomicInteger(0);
		return ignored -> {
			if (tryCount.get() >= samples.size())
				tryCount.set(0);
			return samples.get(tryCount.getAndIncrement());
		};
	}

	public static <T extends @Nullable Object> RandomGenerator<T> samples(T[] samples) {
		List<Shrinkable<T>> shrinkables = SampleShrinkable.listOf(samples);
		return samplesFromShrinkables(shrinkables);
	}

	public static <T extends @Nullable Object> RandomGenerator<T> frequency(List<? extends Tuple2<Integer, ? extends T>> frequencies) {
		return new FrequencyGenerator<>(frequencies);
	}

	public static <T extends @Nullable Object> RandomGenerator<T> frequencyOf(
			List<? extends Tuple2<Integer, ? extends Arbitrary<T>>> frequencies,
			int genSize,
			boolean withEmbeddedEdgeCases
	) {
		return frequency(frequencies).flatMap(Function.identity(), genSize, withEmbeddedEdgeCases);
	}

	public static <T extends @Nullable Object> RandomGenerator<T> withEdgeCases(RandomGenerator<T> self, int genSize, EdgeCases<T> edgeCases) {
		if (edgeCases.isEmpty()) {
			return self;
		}
		return new WithEdgeCasesGenerator<>(self, edgeCases, genSize);
	}

	public static <T extends @Nullable Object> RandomGenerator<T> fail(String message) {
		return ignored -> {
			throw new JqwikException(message);
		};
	}

}
