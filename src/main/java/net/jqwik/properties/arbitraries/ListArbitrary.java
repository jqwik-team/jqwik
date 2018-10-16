package net.jqwik.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.support.*;

public class ListArbitrary<T> extends DefaultCollectionArbitrary<T, List<T>> {

	public ListArbitrary(Arbitrary<T> elementArbitrary) {
		super(elementArbitrary);
	}

	@Override
	public RandomGenerator<List<T>> generator(int genSize) {
		return listGenerator(genSize);
	}

	@Override
	// TODO: Generalize and move to DefaultCollectionArbitrary
	public Optional<ExhaustiveGenerator<List<T>>> exhaustive() {
		Optional<ExhaustiveGenerator<T>> exhaustiveElement = elementArbitrary.exhaustive();
		if (!exhaustiveElement.isPresent())
			return Optional.empty();
		long maxCount = calculateMaxCount(exhaustiveElement.get().maxCount(), minSize, maxSize);
		if (maxCount > Integer.MAX_VALUE)
			return Optional.empty();

		ExhaustiveGenerator<List<T>> generator = new ExhaustiveGenerator<List<T>>() {
			@Override
			public Iterator<List<T>> iterator() {
				return Combinatorics.listCombinations(exhaustiveElement.get(), minSize, maxSize);
			}

			@Override
			public long maxCount() {
				return maxCount;
			}
		};

		return Optional.of(generator);
	}

	private long calculateMaxCount(long elementMaxCount, int minSize, int maxSize) {
		long sum = 0;
		for (int n = minSize; n <= maxSize; n++) {
			double choices = Math.pow(elementMaxCount, n);
			if (choices > Integer.MAX_VALUE) { // Stop when break off point reached
				return Long.MAX_VALUE;
			}
			sum += (long) choices;
		}
		return sum;
	}

}
