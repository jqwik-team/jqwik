package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.support.*;

import org.jspecify.annotations.*;

public class ArbitraryMap<T extends @Nullable Object, U extends @Nullable Object> implements Arbitrary<U> {
	private final Arbitrary<T> self;
	private final Function<? super T, ? extends U> mapper;

	public ArbitraryMap(Arbitrary<T> self, Function<? super T, ? extends U> mapper) {
		this.self = self;
		this.mapper = mapper;
	}

	@Override
	public RandomGenerator<U> generator(int genSize) {
		return self.generator(genSize).map(mapper);
	}

	@Override
	public RandomGenerator<U> generatorWithEmbeddedEdgeCases(int genSize) {
		return self.generatorWithEmbeddedEdgeCases(genSize).map(mapper);
	}

	@Override
	public Optional<ExhaustiveGenerator<U>> exhaustive(long maxNumberOfSamples) {
		return self.exhaustive(maxNumberOfSamples)
				   .map(generator -> generator.map(mapper));
	}

	@Override
	public EdgeCases<U> edgeCases(int maxEdgeCases) {
		return EdgeCasesSupport.map(self.edgeCases(maxEdgeCases), mapper);
	}

	@Override
	public boolean isGeneratorMemoizable() {
		return self.isGeneratorMemoizable();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ArbitraryMap<?, ?> that = (ArbitraryMap<?, ?>) o;
		if (!self.equals(that.self)) return false;
		return LambdaSupport.areEqual(mapper, that.mapper);
	}

	@Override
	public int hashCode() {
		return HashCodeSupport.hash(self, mapper.getClass());
	}
}
