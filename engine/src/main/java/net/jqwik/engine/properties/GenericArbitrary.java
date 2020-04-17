package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;

class GenericArbitrary implements Arbitrary<Object> {

	private final Arbitrary<Object> wrapped;

	@SuppressWarnings("unchecked")
	GenericArbitrary(Arbitrary<?> wrapped) {
		this.wrapped = (Arbitrary<Object>) wrapped;
	}

	@Override
	public RandomGenerator<Object> generator(int genSize) {
		return wrapped.generator(genSize);
	}

	@Override
	public Optional<ExhaustiveGenerator<Object>> exhaustive(long maxNumberOfSamples) {
		return wrapped.exhaustive(maxNumberOfSamples);
	}

	@Override
	public EdgeCases<Object> edgeCases() {
		return wrapped.edgeCases();
	}
}
