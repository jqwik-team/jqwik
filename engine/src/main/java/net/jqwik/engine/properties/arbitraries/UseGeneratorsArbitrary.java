package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

abstract class UseGeneratorsArbitrary<T extends @Nullable Object> implements Arbitrary<T> {

	private final RandomGenerator<T> randomGenerator;
	private final Function<Long, Optional<ExhaustiveGenerator<T>>> exhaustiveGeneratorFunction;
	private final Function<Integer, EdgeCases<T>> edgeCasesSupplier;

	public UseGeneratorsArbitrary(
		RandomGenerator<T> randomGenerator,
		Function<Long, Optional<ExhaustiveGenerator<T>>> exhaustiveGeneratorFunction,
		Function<Integer, EdgeCases<T>> edgeCasesSupplier
	) {
		this.randomGenerator = randomGenerator;
		this.exhaustiveGeneratorFunction = exhaustiveGeneratorFunction;
		this.edgeCasesSupplier = edgeCasesSupplier;
	}

	@Override
	public RandomGenerator<T> generator(int tries) {
		return randomGenerator;
	}

	@Override
	public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
		return exhaustiveGeneratorFunction.apply(maxNumberOfSamples);
	}

	@Override
	public EdgeCases<T> edgeCases(int maxEdgeCases) {
		return maxEdgeCases <= 0
				   ? EdgeCases.none()
				   : edgeCasesSupplier.apply(maxEdgeCases);
	}

}
