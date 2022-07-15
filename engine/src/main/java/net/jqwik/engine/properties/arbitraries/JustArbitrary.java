package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;

public class JustArbitrary<T> implements Arbitrary<T> {

	private final T value;

	public JustArbitrary(T value) {
		this.value = value;
	}

	@Override
	public <U> Arbitrary<U> flatMap(Function<T, Arbitrary<U>> mapper) {
		// Optimization: just(value).flatMap(mapper) -> mapper(value)
		return mapper.apply(value);
	}

	@Override
	public <U> Arbitrary<U> map(Function<T, U> mapper) {
		// Optimization: just(value).map(mapper) -> just(mapper(value))
		return new JustArbitrary<>(mapper.apply(value));
	}

	@Override
	public RandomGenerator<T> generator(int tries) {
		return random -> Shrinkable.unshrinkable(value);
	}

	@Override
	public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
		return ExhaustiveGenerators.choose(Arrays.asList(value), maxNumberOfSamples);
	}

	@Override
	public EdgeCases<T> edgeCases(int maxEdgeCases1) {
		return maxEdgeCases1 <= 0
				   ? EdgeCases.none()
				   : EdgeCases.fromSupplier(() -> Shrinkable.unshrinkable(value));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		JustArbitrary<?> that = (JustArbitrary<?>) o;
		return Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		Objects.hash(value);
		return value != null ? value.hashCode() : 0;
	}
}
