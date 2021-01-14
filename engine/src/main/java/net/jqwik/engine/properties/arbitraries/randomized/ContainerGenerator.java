package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.arbitraries.*;

class ContainerGenerator<T, C> implements RandomGenerator<C> {
	private final RandomGenerator<T> elementGenerator;
	private final Function<List<Shrinkable<T>>, Shrinkable<C>> createShrinkable;
	private final Function<Random, Integer> sizeGenerator;
	private final Collection<FeatureExtractor<T>> uniquenessExtractors;

	ContainerGenerator(
			RandomGenerator<T> elementGenerator,
			Function<List<Shrinkable<T>>, Shrinkable<C>> createShrinkable,
			Function<Random, Integer> sizeGenerator,
			Collection<FeatureExtractor<T>> uniquenessExtractors
	) {
		this.elementGenerator = elementGenerator;
		this.createShrinkable = createShrinkable;
		this.sizeGenerator = sizeGenerator;
		this.uniquenessExtractors = uniquenessExtractors;
	}

	@Override
	public Shrinkable<C> next(Random random) {
		int listSize = sizeGenerator.apply(random);
		List<Shrinkable<T>> list = new ArrayList<>();
		List<T> elements = new ArrayList<>();
		while (list.size() < listSize) {
			Shrinkable<T> next = nextUntilAccepted(random, elements, elementGenerator::next);
			list.add(next);
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
		return FeatureExtractor.checkUniquenessInElements(uniquenessExtractors, value, elements);
	}

}
