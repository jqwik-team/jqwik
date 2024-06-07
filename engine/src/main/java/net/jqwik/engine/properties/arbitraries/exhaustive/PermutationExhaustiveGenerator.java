package net.jqwik.engine.properties.arbitraries.exhaustive;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

import org.jspecify.annotations.*;

import static net.jqwik.engine.support.MathSupport.*;

class PermutationExhaustiveGenerator<T extends @Nullable Object> implements ExhaustiveGenerator<List<T>> {
	private final List<T> values;
	private final Long maxCount;

	public PermutationExhaustiveGenerator(List<T> values, Long maxCount) {
		this.values = values;
		this.maxCount = maxCount;
	}

	static Optional<Long> calculateMaxCount(List<?> values, long maxNumberOfSamples) {
		try {
			long choices = factorial(values.size());
			if (choices > maxNumberOfSamples || choices < 0) {
				return Optional.empty();
			}
			return Optional.of(choices);
		} catch (ArithmeticException ae) {
			return Optional.empty();
		}
	}

	@Override
	public long maxCount() {
		return maxCount;
	}

	@Override
	public Iterator<List<T>> iterator() {
		return Combinatorics.listPermutations(values);
	}
}
