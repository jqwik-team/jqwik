package net.jqwik.properties.arbitraries.exhaustive;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.support.*;

class ListExhaustiveGenerator<T> implements ExhaustiveGenerator<List<T>> {
	private final Arbitrary<T> elementArbitrary;
	private final Long maxCount;
	private final int minSize;
	private final int maxSize;

	static Optional<Long> calculateMaxCount(Arbitrary<?> elementArbitrary, int minSize, int maxSize) {
		Optional<? extends ExhaustiveGenerator<?>> exhaustiveElement = elementArbitrary.exhaustive();
		if (!exhaustiveElement.isPresent())
			return Optional.empty();

		long elementMaxCount = exhaustiveElement.get().maxCount();
		long sum = 0;
		for (int n = minSize; n <= maxSize; n++) {
			double choices = Math.pow(elementMaxCount, n);
			if (choices > Integer.MAX_VALUE) { // Stop when break off point reached
				return Optional.empty();
			}
			sum += (long) choices;
		}
		return Optional.of(sum);
	}

	ListExhaustiveGenerator(Arbitrary<T> elementArbitrary, Long maxCount, int minSize, int maxSize) {
		this.elementArbitrary = elementArbitrary;
		this.maxCount = maxCount;
		this.minSize = minSize;
		this.maxSize = maxSize;
	}

	@Override
	public Iterator<List<T>> iterator() {
		return Combinatorics
			.listCombinations(elementArbitrary.exhaustive().get(), minSize, maxSize);
	}

	@Override
	public long maxCount() {
		return maxCount;
	}
}
