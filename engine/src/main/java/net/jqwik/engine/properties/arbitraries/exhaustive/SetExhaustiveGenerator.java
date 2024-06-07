package net.jqwik.engine.properties.arbitraries.exhaustive;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

import org.jspecify.annotations.*;

class SetExhaustiveGenerator<T extends @Nullable Object> implements ExhaustiveGenerator<Set<T>> {
	private final Arbitrary<T> elementArbitrary;
	private final long maxCount;
	private final int minSize;
	private final int maxSize;

	static Optional<Long> calculateMaxCount(Arbitrary<?> elementArbitrary, int minSize, int maxSize, long maxNumberOfSamples) {
		Optional<? extends ExhaustiveGenerator<?>> exhaustiveElement = elementArbitrary.exhaustive(maxNumberOfSamples);
		if (!exhaustiveElement.isPresent())
			return Optional.empty();

		long elementMaxCount = exhaustiveElement.get().maxCount();
		long sum = 0;
		for (int k = minSize; k <= maxSize; k++) {
			if (k == 0) { // empty set
				sum += 1;
				continue;
			}
			if (elementMaxCount < k) { // empty set
				continue;
			}
			if (elementMaxCount > 70) {
				// MathSupport.binomial() only works till 70
				return Optional.empty();
			}
			long choices = 0;
			try {
				choices = MathSupport.binomial(Math.toIntExact(elementMaxCount), k);
			} catch (ArithmeticException ae) {
				return Optional.empty();
			}
			if (choices < 0) {
				return Optional.empty();
			}
			sum += choices;
			if (sum > maxNumberOfSamples) { // Stop when break off point reached
				return Optional.empty();
			}
		}
		return Optional.of(sum);
	}

	SetExhaustiveGenerator(Arbitrary<T> elementArbitrary, long maxCount, int minSize, int maxSize) {
		this.elementArbitrary = elementArbitrary;
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.maxCount = maxCount;
	}

	@Override
	public Iterator<Set<T>> iterator() {
		return Combinatorics.setCombinations(elementArbitrary.exhaustive().get(), minSize, maxSize);
	}

	@Override
	public long maxCount() {
		return maxCount;
	}
}
