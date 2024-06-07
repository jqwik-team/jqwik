package net.jqwik.engine.properties.arbitraries.exhaustive;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

import org.jspecify.annotations.*;

public class FlatMappedExhaustiveGenerator<U extends @Nullable Object, T extends @Nullable Object> implements ExhaustiveGenerator<U> {
	private final ExhaustiveGenerator<T> baseGenerator;
	private final long maxCount;
	private final Function<? super T, ? extends Arbitrary<U>> mapper;

	public static <T, U> Optional<Long> calculateMaxCounts(
		ExhaustiveGenerator<T> baseGenerator,
		Function<? super T, ? extends Arbitrary<U>> mapper,
		long maxNumberOfSamples
	) {
		long choices = 0;
		for (T baseValue : baseGenerator) {
			Optional<ExhaustiveGenerator<U>> exhaustive = mapper.apply(baseValue).exhaustive(maxNumberOfSamples);
			if (!exhaustive.isPresent()) {
				return Optional.empty();
			}
			choices += exhaustive.get().maxCount();
			if (choices > maxNumberOfSamples) {
				return Optional.empty();
			}
		}
		return Optional.of(choices);
	}

	public FlatMappedExhaustiveGenerator(ExhaustiveGenerator<T> baseGenerator, long maxCount, Function<? super T, ? extends Arbitrary<U>> mapper) {
		this.baseGenerator = baseGenerator;
		this.maxCount = maxCount;
		this.mapper = mapper;
	}

	@Override
	public long maxCount() {
		return maxCount;
	}

	@Override
	public Iterator<U> iterator() {
		List<Iterable<U>> iterators =
			StreamSupport.stream(baseGenerator.spliterator(), false)
				.map(baseValue -> (Iterable<U>) mapper.apply(baseValue).exhaustive().get())
				.collect(Collectors.toList());

		return Combinatorics.concat(iterators);
	}
}
