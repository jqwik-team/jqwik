package net.jqwik.engine;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.*;

public abstract class ArbitraryDecorator<T>  extends AbstractArbitraryBase implements Arbitrary<T> {

	abstract protected Arbitrary<T> arbitrary();

	@Override
	public RandomGenerator<T> generator(int genSize) {
		return arbitrary().generator(genSize);
	}

	@Override
	public boolean isUnique() {
		return arbitrary().isUnique();
	}

	@Override
	public Optional<ExhaustiveGenerator<T>> exhaustive(long maxNumberOfSamples) {
		return arbitrary().exhaustive(maxNumberOfSamples);
	}

	@Override
	public EdgeCases<T> edgeCases() {
		return arbitrary().edgeCases();
	}

	@Override
	public String toString() {
		return String.format("Decorated:%s", super.toString());
	}
}
