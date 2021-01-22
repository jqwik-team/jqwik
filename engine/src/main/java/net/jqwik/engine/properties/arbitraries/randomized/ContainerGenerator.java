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
	private final int cutoffSize;
	private final Collection<FeatureExtractor<T>> uniquenessExtractors;

	private Function<Random, Integer> sizeGenerator;

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

	ContainerGenerator(
			RandomGenerator<T> elementGenerator,
			Function<List<Shrinkable<T>>, Shrinkable<C>> createShrinkable,
			int minSize,
			int maxSize,
			int cutoffSize,
			Collection<FeatureExtractor<T>> uniquenessExtractors
	) {
		this.elementGenerator = elementGenerator;
		this.createShrinkable = createShrinkable;
		this.minSize = minSize;
		this.cutoffSize = cutoffSize;
		this.uniquenessExtractors = uniquenessExtractors;
		this.sizeGenerator = sizeGenerator(minSize, maxSize, cutoffSize);
	}

	@Override
	public Shrinkable<C> next(Random random) {
		int listSize = sizeGenerator.apply(random);
		List<Shrinkable<T>> list = new ArrayList<>();
		List<T> elements = new ArrayList<>();
		while (list.size() < listSize) {
			try {
				Shrinkable<T> next = nextUntilAccepted(random, elements, elementGenerator::next);
				list.add(next);
			} catch (TooManyFilterMissesException tooManyFilterMissesException) {
				// Ignore if list.size() >= minSize, because uniqueness constraints influence possible max size
				if (list.size() < minSize) {
					throw tooManyFilterMissesException;
				} else {
					listSize = list.size();
					sizeGenerator = sizeGenerator(minSize, listSize, cutoffSize);
				}
			}

		}
		return createShrinkable.apply(list);
	}

	private Shrinkable<T> nextUntilAccepted(Random random, List<T> elements, Function<Random, Shrinkable<T>> fetchShrinkable) {
		Shrinkable<T> accepted = MaxTriesLoop.loop(
				() -> true,
				next -> {
					next = fetchShrinkable.apply(random);
					T value = next.value();
					if (checkUniqueness(elements, value)) {
						elements.add(value);
						return Tuple.of(true, next);
					}
					return Tuple.of(false, next);
				},
				(maxMisses) -> {
					String message = String.format("Trying to fulfill uniqueness constraint missed more than %s times.", maxMisses);
					return new TooManyFilterMissesException(message);
				}
		);
		return accepted;
	}

	private boolean checkUniqueness(List<T> elements, T value) {
		return checkValueUniqueIn(uniquenessExtractors, value, elements);
	}

}
