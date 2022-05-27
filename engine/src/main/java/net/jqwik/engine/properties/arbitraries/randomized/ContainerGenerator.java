package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

import static net.jqwik.engine.properties.UniquenessChecker.*;

class ContainerGenerator<T, C> implements RandomGenerator<C> {
	private final RandomGenerator<T> elementGenerator;
	private final Function<List<Shrinkable<T>>, Shrinkable<C>> createShrinkable;
	private final int minSize;
	private final long maxUniqueElements;
	private final Collection<FeatureExtractor<T>> uniquenessExtractors;
	private final Function<Random, Integer> sizeGenerator;
	private final long maxAttempts;

	private static Function<Random, Integer> sizeGenerator(
		int minSize,
		int maxSize,
		int genSize,
		RandomDistribution sizeDistribution
	) {
		return SizeGenerator.create(minSize, maxSize, genSize, sizeDistribution);
	}

	ContainerGenerator(
		RandomGenerator<T> elementGenerator,
		Function<List<Shrinkable<T>>, Shrinkable<C>> createShrinkable,
		int minSize,
		int maxSize,
		long maxUniqueElements,
		int genSize,
		RandomDistribution sizeDistribution,
		Collection<FeatureExtractor<T>> uniquenessExtractors
	) {
		this.elementGenerator = elementGenerator;
		this.createShrinkable = createShrinkable;
		this.minSize = minSize;
		this.maxUniqueElements = maxUniqueElements;
		this.uniquenessExtractors = uniquenessExtractors;
		this.sizeGenerator = sizeGenerator(minSize, maxSize, genSize, sizeDistribution);

		// This is a heuristic value.
		// The assumption is that with 5 times the number of possible values,
		// each possible value should be hit at least once with a high probability.
		this.maxAttempts = Math.min(10000, maxUniqueElements * 5);
	}

	@Override
	public Shrinkable<C> next(Random random) {
		int listSize = sizeGenerator.apply(random);
		List<Shrinkable<T>> listOfShrinkables = new ArrayList<>();

		// Raise probability for no duplicates even in large containers to approx 2 percent
		boolean noDuplicates = listSize >= 2
								   && listSize <= maxUniqueElements
								   && uniquenessExtractors.isEmpty()
								   && random.nextInt(100) <= 2;
		int sizeToShuffleIfExceeded = Integer.MAX_VALUE;

		boolean canUseSetForValues = uniquenessExtractors.isEmpty() || uniquenessExtractors.contains(FeatureExtractor.identity());
		Collection<T> existingValues = canUseSetForValues ? new LinkedHashSet<>() : new ArrayList<>();

		while (listOfShrinkables.size() < listSize) {
			try {
				Shrinkable<T> next = nextUntilAccepted(random, existingValues, elementGenerator::next, noDuplicates);
				listOfShrinkables.add(next);
			} catch (TooManyFilterMissesException tooManyFailedGenerationAttempts) {
				// Switch off noDuplicates to enable generation of elements to proceed
				if (noDuplicates) {
					// This should occur only rarely because usually the check against maxUniqueElements prevents it from happening.
					noDuplicates = false;
					sizeToShuffleIfExceeded = listOfShrinkables.size();

					// Resume generation
					continue;
				}
				if (listOfShrinkables.size() < minSize) {
					// Fail if minimum container size could not be reached
					throw tooManyFailedGenerationAttempts;
				}
				// Stop generation if minimum container size could be reached,
				// because uniqueness constraints - or an overestimated value for maxUniqueElements -
				// can reduce the achievable max size of a container.
				break;
			}
		}
		if (listOfShrinkables.size() > sizeToShuffleIfExceeded) {
			// If we started generating with no duplicates, and then realized we can't generate enough unique elements,
			// then the list becomes skewed: unique elements go first
			// We shuffle the list to allow other constellations (e.g. list unique-most elements starting with non-unique ones)
			Collections.shuffle(listOfShrinkables, random);
		}
		return createShrinkable.apply(listOfShrinkables);
	}

	private Shrinkable<T> nextUntilAccepted(
		Random random,
		Collection<T> existingValues,
		Function<Random, Shrinkable<T>> fetchShrinkable,
		boolean noDuplicates
	) {
		for (int i = 0; i < maxAttempts; i++) {
			Shrinkable<T> next = fetchShrinkable.apply(random);
			T value = next.value();
			if (noDuplicates && existingValues.contains(value)) {
				continue;
			}
			if (!checkSpecifiedUniqueness(existingValues, value)) {
				continue;
			}
			existingValues.add(value);
			return next;
		}
		String message = String.format("Trying to fulfill uniqueness constraint missed more than %s times.", maxAttempts);
		throw new TooManyFilterMissesException(message);
	}

	private boolean checkSpecifiedUniqueness(Collection<T> elements, T value) {
		return checkValueUniqueIn(uniquenessExtractors, value, elements);
	}

}
