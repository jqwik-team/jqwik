package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

public class CharacterRange implements Arbitrary<Character> {
	private final char min;
	private final char max;

	public CharacterRange(char min, char max) {
		this.min = min;
		this.max = max;
	}

	@Override
	public RandomGenerator<Character> generator(int genSize) {
		return RandomGenerators.chars(min, max);
	}

	@Override
	public Optional<ExhaustiveGenerator<Character>> exhaustive() {
		long maxCount = max + 1 - min;
		return ExhaustiveGenerators
				   .fromIterable(() -> IntStream.range(min, max + 1).iterator(), maxCount)
				   .map(optionalGenerator -> optionalGenerator.map(anInt -> (char) (int) anInt));
	}
}
