package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

//TODO: Make it a SelfConfiguringArbitrary
public class IgnoreGenerationExceptions<T> implements Arbitrary<T> {

	private Arbitrary<T> innerArbitrary;

	public IgnoreGenerationExceptions(Arbitrary<T> innerArbitrary) {this.innerArbitrary = innerArbitrary;}

	@Override
	public RandomGenerator<T> generator(int genSize) {
		RandomGenerator<T> innerGenerator = innerArbitrary.generator(genSize);
		return random -> generateNext(random, innerGenerator::next);
	}

	@Override
	public Optional<ExhaustiveGenerator<T>> exhaustive() {
		//TODO: Ignore exceptions in exhaustively generated values
		return Optional.empty();
	}

	private Shrinkable<T> generateNext(Random random, Function<Random, Shrinkable<T>> generator) {
		try {
			return generator.apply(random);
		} catch (Throwable throwable) {
			return generateNext(random, generator);
		}
	}
}
