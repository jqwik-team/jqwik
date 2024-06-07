package net.jqwik.engine.properties.arbitraries.exhaustive;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

class CombinedExhaustiveGenerator<R> implements ExhaustiveGenerator<R> {
	private final Long maxCount;
	private final List<Arbitrary<Object>> arbitraries;
	private final Function<? super List<?>, ? extends R> combinator;

	static Optional<Long> calculateMaxCount(List<Arbitrary<Object>> arbitraries, long maxNumberOfSamples) {
		long product = 1;
		for (Arbitrary<Object> arbitrary : arbitraries) {
			Optional<ExhaustiveGenerator<Object>> exhaustive = arbitrary.exhaustive(maxNumberOfSamples);
			if (!exhaustive.isPresent()) {
				return Optional.empty();
			}
			product *= exhaustive.get().maxCount();
			if (product > maxNumberOfSamples) {
				return Optional.empty();
			}
		}
		return Optional.of(product);
	}

	CombinedExhaustiveGenerator(Long maxCount, List<Arbitrary<Object>> arbitraries, Function<? super List<?>, ? extends R> combinator) {
		this.maxCount = maxCount;
		this.arbitraries = arbitraries;
		this.combinator = combinator;
	}

	@Override
	public long maxCount() {
		return maxCount;
	}

	@Override
	public Iterator<R> iterator() {
		List<Iterable<Object>> iterables = arbitraries
			.stream()
			.map(a -> (Iterable<Object>) a.exhaustive().get())
			.collect(Collectors.toList());
		Iterator<List<Object>> valuesIterator = Combinatorics.combine(iterables);

		return new Iterator<R>() {
			@Override
			public boolean hasNext() {
				return valuesIterator.hasNext();
			}

			@Override
			public R next() {
				List<Object> values = valuesIterator.next();
				return combinator.apply(values);
			}
		};
	}
}
