package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

public class ArbitraryMap<T, U> implements Arbitrary<U> {
	private final Arbitrary<T> self;
	private final Function<T, U> mapper;

	public ArbitraryMap(Arbitrary<T> self, Function<T, U> mapper) {
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
}
