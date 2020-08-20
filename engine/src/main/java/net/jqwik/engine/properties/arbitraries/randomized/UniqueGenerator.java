package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.*;

public class UniqueGenerator<T> implements RandomGenerator<T> {
	private final RandomGenerator<T> toFilter;
	private final Set<T> usedValues = Collections.synchronizedSet(new HashSet<>());

	public UniqueGenerator(RandomGenerator<T> toFilter) {
		this.toFilter = toFilter;
	}

	@Override
	public Shrinkable<T> next(Random random) {
		return nextUntilAccepted(random, r -> {
			Shrinkable<T> next = toFilter.next(r);
			return new UniqueShrinkable<>(next, this::shrink);
		});
	}

	@Override
	public String toString() {
		return String.format("Unique [%s]", toFilter);
	}

	private Shrinkable<T> nextUntilAccepted(Random random, Function<Random, Shrinkable<T>> fetchShrinkable) {
		return MaxTriesLoop.loop(
			() -> true,
			next -> {
				next = fetchShrinkable.apply(random);
				T value = next.value();
				if (usedValues.contains(value)) {
					return Tuple.of(false, next);
				}
				usedValues.add(value);
				return Tuple.of(true, next);
			},
			maxMisses -> {
				String message = String.format("%s missed more than %s times.", toString(), maxMisses);
				return new TooManyFilterMissesException(message);
			}
		);
	}

	private Stream<Shrinkable<T>> shrink(UniqueShrinkable<T> current) {
		return current.toFilter.shrink().filter(s -> {
			return !usedValues.contains(s.value());
		}).map(s -> {
			// TODO: In theory the set of used values should only contain those in the current try
			// but currently it contains all values tried in this shrinking
			usedValues.add(s.value());
			return new UniqueShrinkable<>(s, this::shrink);
		});
	}

}
