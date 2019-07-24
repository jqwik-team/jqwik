package net.jqwik.engine.properties.arbitraries.randomized;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.*;

public class UniqueGenerator<T> implements RandomGenerator<T> {
	private final RandomGenerator<T> toFilter;
	private final Set<T> usedValues = ConcurrentHashMap.newKeySet();

	public UniqueGenerator(RandomGenerator<T> toFilter) {
		this.toFilter = toFilter;
	}

	@Override
	public Shrinkable<T> next(Random random) {
		return nextUntilAccepted(random, r -> {
			Shrinkable<T> next = toFilter.next(r);
			return new UniqueShrinkable<>(next, usedValues);
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
				if (usedValues.contains(next.value())) {
					return Tuple.of(false, next);
				}
				usedValues.add(next.value());
				return Tuple.of(true, next);
			},
			maxMisses -> {
				String message = String.format("%s missed more than %s times.", toString(), maxMisses);
				return new TooManyFilterMissesException(message);
			}
		);
	}

}
