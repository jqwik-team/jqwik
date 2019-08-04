package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;

class GenericArbitrary implements Arbitrary<Object> {

	private final Arbitrary wrapped;

	GenericArbitrary(Arbitrary<?> wrapped) {
		this.wrapped = wrapped;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RandomGenerator<Object> generator(int genSize) {
		return wrapped.generator(genSize);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Optional<ExhaustiveGenerator<Object>> exhaustive(long maxNumberOfSamples) {
		return wrapped.exhaustive(maxNumberOfSamples);
	}
}
