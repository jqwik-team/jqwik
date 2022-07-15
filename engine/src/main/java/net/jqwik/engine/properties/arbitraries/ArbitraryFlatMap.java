package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;

public class ArbitraryFlatMap<T, U> implements Arbitrary<U> {
	private final Arbitrary<T> self;
	private final Function<T, Arbitrary<U>> mapper;

	public ArbitraryFlatMap(Arbitrary<T> self, Function<T, Arbitrary<U>> mapper) {
		this.self = self;
		this.mapper = mapper;
	}

	@Override
	public RandomGenerator<U> generator(int genSize) {
		return self.generator(genSize).flatMap(mapper, genSize, false);
	}

	@Override
	public RandomGenerator<U> generatorWithEmbeddedEdgeCases(int genSize) {
		return self.generatorWithEmbeddedEdgeCases(genSize).flatMap(mapper, genSize, true);
	}

	@Override
	public Optional<ExhaustiveGenerator<U>> exhaustive(long maxNumberOfSamples) {
		return self.exhaustive(maxNumberOfSamples)
				   .flatMap(generator -> ExhaustiveGenerators.flatMap(generator, mapper, maxNumberOfSamples));
	}

	@Override
	public EdgeCases<U> edgeCases(int maxEdgeCases) {
		return EdgeCasesSupport.flatMapArbitrary(self.edgeCases(maxEdgeCases), mapper, maxEdgeCases);
	}

	@Override
	public boolean isGeneratorMemoizable() {
		return self.isGeneratorMemoizable();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ArbitraryFlatMap<?, ?> that = (ArbitraryFlatMap<?, ?>) o;
		if (!self.equals(that.self)) return false;
		return LambdaSupport.areEqual(mapper, that.mapper);
	}

	@Override
	public int hashCode() {
		return HashCodeSupport.hash(self, mapper.getClass());
	}
}
