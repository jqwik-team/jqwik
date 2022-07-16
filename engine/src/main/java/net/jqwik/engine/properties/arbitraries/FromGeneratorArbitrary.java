package net.jqwik.engine.properties.arbitraries;

import net.jqwik.api.*;
import net.jqwik.api.support.*;

public class FromGeneratorArbitrary<T> implements Arbitrary<T> {

	private final RandomGenerator<T> generator;

	public FromGeneratorArbitrary(RandomGenerator<T> generator) {
		this.generator = generator;
	}

	@Override
	public RandomGenerator<T> generator(final int genSize) {
		return generator;
	}

	@Override
	public EdgeCases<T> edgeCases(int maxEdgeCases) {
		return EdgeCases.none();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FromGeneratorArbitrary<?> that = (FromGeneratorArbitrary<?>) o;
		return LambdaSupport.areEqual(generator, that.generator);
	}

	@Override
	public int hashCode() {
		return generator.getClass().hashCode();
	}
}
