package net.jqwik.engine.properties.arbitraries.randomized;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

import static net.jqwik.engine.properties.UniquenessChecker.*;

class ContainerGenerator<T, C> implements RandomGenerator<C> {
	private final RandomGenerator<T> elementGenerator;
	private final Function<List<Shrinkable<T>>, Shrinkable<C>> createShrinkable;
	private final int minSize;
	private final int cutoffSize;
	private final RandomDistribution sizeDistribution;
	private final Collection<FeatureExtractor<T>> uniquenessExtractors;

	private boolean noDuplicatesHadToBeSwitchedOff = false;

	private Function<Random, Integer> sizeGenerator;

	private static Function<Random, Integer> sizeGenerator(
		int minSize,
		int maxSize,
		int cutoffSize,
		RandomDistribution sizeDistribution
	) {
		if (sizeDistribution != null) {
			return sizeGeneratorWithDistribution(minSize, maxSize, sizeDistribution);
		}
		return sizeGeneratorWithCutoff(minSize, maxSize, cutoffSize);
	}

	private static Function<Random, Integer> sizeGeneratorWithCutoff(int minSize, int maxSize, int cutoffSize) {
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

	private static Function<Random, Integer> sizeGeneratorWithDistribution(int minSize, int maxSize, RandomDistribution distribution) {
		RandomDistribution.RandomNumericGenerator generator =
			distribution.createGenerator(
				1000, // ignored for everything except biased distribution
				BigInteger.valueOf(minSize), BigInteger.valueOf(maxSize),
				BigInteger.valueOf(minSize)
			);
		return random -> generator.next(random).intValueExact();
	}

	private static int randomSize(Random random, int minSize, int maxSize) {
		int range = maxSize - minSize;
		return random.nextInt(range + 1) + minSize;
	}

	ContainerGenerator(
		RandomGenerator<T> elementGenerator,
		Function<List<Shrinkable<T>>, Shrinkable<C>> createShrinkable,
		int minSize,
		int maxSize,
		int cutoffSize,
		RandomDistribution sizeDistribution,
		Collection<FeatureExtractor<T>> uniquenessExtractors
	) {
		this.elementGenerator = elementGenerator;
		this.createShrinkable = createShrinkable;
		this.minSize = minSize;
		this.cutoffSize = cutoffSize;
		this.sizeDistribution = sizeDistribution;
		this.uniquenessExtractors = uniquenessExtractors;
		this.sizeGenerator = sizeGenerator(minSize, maxSize, cutoffSize, sizeDistribution);
	}

	@Override
	public Shrinkable<C> next(Random random) {
		int listSize = sizeGenerator.apply(random);
		List<Shrinkable<T>> listOfShrinkables = new ArrayList<>();
		List<T> existingValues = new ArrayList<>();

		// Raise probability for no duplicates even in large containers to above 5 percent
		boolean noDuplicates = !noDuplicatesHadToBeSwitchedOff
								   && uniquenessExtractors.isEmpty()
								   && random.nextInt(100) <= 5;

		while (listOfShrinkables.size() < listSize) {
			try {
				Shrinkable<T> next = nextUntilAccepted(random, existingValues, elementGenerator::next, noDuplicates);
				listOfShrinkables.add(next);
			} catch (TooManyFilterMissesException tooManyFilterMissesException) {
				// Switch off noDuplicates
				if (noDuplicates) {
					noDuplicates = false;
					noDuplicatesHadToBeSwitchedOff = true;
				} else {
					// Ignore if list.size() >= minSize, because uniqueness constraints influence possible max size
					if (listOfShrinkables.size() < minSize) {
						throw tooManyFilterMissesException;
					} else {
						listSize = listOfShrinkables.size();
						sizeGenerator = sizeGenerator(minSize, listSize, cutoffSize, sizeDistribution);
					}
				}
			}

		}
		return createShrinkable.apply(listOfShrinkables);
	}

	private Shrinkable<T> nextUntilAccepted(
		Random random,
		List<T> existingValues,
		Function<Random, Shrinkable<T>> fetchShrinkable,
		boolean noDuplicates
	) {
		Shrinkable<T> accepted = MaxTriesLoop.loop(
			() -> true,
			next -> {
				next = fetchShrinkable.apply(random);
				T value = next.value();
				if (noDuplicates && existingValues.contains(value)) {
					return Tuple.of(false, next);
				}
				if (!checkSpecifiedUniqueness(existingValues, value)) {
					return Tuple.of(false, next);
				}
				existingValues.add(value);
				return Tuple.of(true, next);
			},
			(maxMisses) -> {
				String message = String.format("Trying to fulfill uniqueness constraint missed more than %s times.", maxMisses);
				return new TooManyFilterMissesException(message);
			}
		);
		return accepted;
	}

	private boolean checkSpecifiedUniqueness(List<T> elements, T value) {
		return checkValueUniqueIn(uniquenessExtractors, value, elements);
	}

}
