package net.jqwik.properties.arbitraries.exhaustive;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

public class FlatMappedExhaustiveGenerator<U, T> implements ExhaustiveGenerator<U> {
	private final List<T> baseValues;
	private final long maxCount;
	private final Function<T, Arbitrary<U>> mapper;

	public static <T> List<T> baseValuesFor(ExhaustiveGenerator<T> base) {
		return StreamSupport.stream(base.spliterator(), false)
			.collect(Collectors.toList());
	}

	public FlatMappedExhaustiveGenerator(List<T> baseValues, long maxCount, Function<T, Arbitrary<U>> mapper) {this.baseValues = baseValues;
		this.maxCount = maxCount;
		this.mapper = mapper;
	}

	public static <T, U> Optional<Long> calculateMaxCounts(List<T> baseValues, Function<T, Arbitrary<U>> mapper) {
		long choices = 0;
		for (T baseValue : baseValues) {
			Optional<ExhaustiveGenerator<U>> exhaustive = mapper.apply(baseValue).exhaustive();
			if (!exhaustive.isPresent()) {
				return Optional.empty();
			}
			choices += exhaustive.get().maxCount();
			if (choices > ExhaustiveGenerators.MAXIMUM_ACCEPTED_MAX_COUNT) {
				return Optional.empty();
			}
		}
		return Optional.of(choices);
	}

	@Override
	public long maxCount() {
		return maxCount;
	}

	@Override
	public Iterator<U> iterator() {
		return null;
	}
}
