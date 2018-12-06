package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;

public class DefaultCharacterArbitrary extends AbstractArbitraryBase implements CharacterArbitrary {

	public static final int MAX_ASCII_CODEPOINT = 0x007F;

	private char min = 0;
	private char max = 0;

	public DefaultCharacterArbitrary() {
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


	@Override
	public CharacterArbitrary between(char min, char max) {
		DefaultCharacterArbitrary clone = typedClone();
		clone.min = min;
		clone.max = max;
		return clone;
	}

	@Override
	public CharacterArbitrary ascii() {
		return between((char) Character.MIN_CODE_POINT, (char) MAX_ASCII_CODEPOINT);
	}

	@Override
	public CharacterArbitrary all() {
		return between(Character.MIN_VALUE, Character.MAX_VALUE);
	}

	@Override
	public CharacterArbitrary digit() {
		return between('0', '9');
	}

}
