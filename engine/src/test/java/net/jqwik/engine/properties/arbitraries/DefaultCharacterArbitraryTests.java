package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.edgeCases.*;
import net.jqwik.testing.*;

import static net.jqwik.testing.TestingSupport.*;

class DefaultCharacterArbitraryTests implements GenericGenerationProperties, GenericEdgeCasesProperties {

	@Override
	public Arbitrary<Arbitrary<?>> arbitraries() {
		return Arbitraries.of(
			new DefaultCharacterArbitrary(),
			new DefaultCharacterArbitrary().whitespace(),
			new DefaultCharacterArbitrary().with('a', 'b', 'c'),
			new DefaultCharacterArbitrary().alpha().numeric()
		);
	}

	CharacterArbitrary arbitrary = new DefaultCharacterArbitrary();

	@Example
	void perDefaultNoNoncharactersAndNoPrivateUseCharactersAreCreated(@ForAll JqwikRandom random) {
		checkAllGenerated(
			this.arbitrary.generator(1000, true),
			random,
			c -> {
				if (DefaultCharacterArbitrary.isNoncharacter(c))
					return false;
				return !DefaultCharacterArbitrary.isPrivateUseCharacter(c);
			}
		);

		TestingSupport.checkAtLeastOneGenerated(
			this.arbitrary.generator(1000, true),
			random,
			c -> c <= '\u1000'
		);
		TestingSupport.checkAtLeastOneGenerated(
			this.arbitrary.generator(1000, true),
			random,
			c -> c >= '\uF000'
		);
	}

	@Example
	void edgeCasesAreGenerated(@ForAll JqwikRandom random) {
		TestingSupport.checkAtLeastOneGenerated(
			this.arbitrary.generator(1000, true),
			random,
			c -> c == Character.MIN_VALUE
		);
	}

	@Example
	void allOverridesAnythingBefore(@ForAll JqwikRandom random) {
		CharacterArbitrary all = this.arbitrary.ascii().all();
		checkAllGenerated(
			all.generator(1000, true),
			random,
			c -> c >= Character.MIN_VALUE && c <= Character.MAX_VALUE
		);
		TestingSupport.checkAtLeastOneGenerated(
			all.generator(1000, true),
			random,
			c -> c <= '\u1000'
		);
		TestingSupport.checkAtLeastOneGenerated(
			all.generator(1000, true),
			random,
			c -> c >= '\uF000'
		);
	}

	@Example
	void chars(@ForAll JqwikRandom random) {
		final List<Character> chars = Arrays.asList('a', 'b', 'c', '1', '2', '.');
		CharacterArbitrary all = this.arbitrary.with('a', 'b', 'c', '1', '2', '.');
		checkAllGenerated(
			all.generator(1000, true),
			random,
			chars::contains
		);
		assertAtLeastOneGeneratedOf(
			all.generator(1000, true),
			random,
			'a', 'b', 'c', '1', '2', '.'
		);
	}

	@Example
	void charsFromCharSequence(@ForAll JqwikRandom random) {
		final List<Character> chars = Arrays.asList('a', 'b', 'c', '1', '2', '.');
		CharacterArbitrary all = this.arbitrary.with("abc12.");
		checkAllGenerated(
			all.generator(1000, true),
			random,
			chars::contains
		);
		assertAtLeastOneGeneratedOf(
			all.generator(1000, true),
			random,
			'a', 'b', 'c', '1', '2', '.'
		);
	}

	@Example
	void range(@ForAll JqwikRandom random) {
		char min = '\u0010';
		char max = '\u0030';
		CharacterArbitrary all = this.arbitrary.range(min, max);
		checkAllGenerated(
			all.generator(1000, true),
			random,
			c -> c >= min && c <= max
		);
		assertAtLeastOneGeneratedOf(
			all.generator(1000, true),
			random,
			min, max
		);
	}

	@Example
	void digit(@ForAll JqwikRandom random) {
		CharacterArbitrary all = this.arbitrary.numeric();
		checkAllGenerated(
			all.generator(1000, true),
			random,
			c -> c >= '0' && c <= '9'
		);
		assertAtLeastOneGeneratedOf(
			all.generator(1000, true),
			random,
			'0', '9'
		);
	}

	@Example
	void ascii(@ForAll JqwikRandom random) {
		CharacterArbitrary all = this.arbitrary.ascii();
		checkAllGenerated(
			all.generator(1000, true),
			random,
			c -> c >= 0 && c <= 127
		);
		assertAtLeastOneGeneratedOf(
			all.generator(1000, true),
			random,
			(char) 10, (char) 126
		);
	}

	@Example
	void alpha(@ForAll JqwikRandom random) {
		CharacterArbitrary all = this.arbitrary.alpha();
		checkAllGenerated(
			all.generator(1000, true),
			random,
			this::isAlpha
		);
	}

	private boolean isAlpha(char c) {
		if (c >= 'a' && c <= 'z') return true;
		return c >= 'A' && c <= 'Z';
	}

	@Example
	void addUpRangesAndChars(@ForAll JqwikRandom random) {
		char min1 = '\u0010';
		char max1 = '\u0030';
		char min2 = '\u0110';
		char max2 = '\u0130';
		char min3 = '\u1010';
		char max3 = '\u1030';

		final List<Character> chars = Arrays.asList('a', 'b', 'c', '1', '2', '.');

		CharacterArbitrary all = this.arbitrary
			.range(min1, max1)
			.range(min2, max2)
			.range(min3, max3)
			.with('a', 'b', 'c', '1', '2', '.');

		checkAllGenerated(
			all.generator(1000, true),
			random,
			c -> (c >= min1 && c <= max1) ||
					 (c >= min2 && c <= max2) ||
					 (c >= min3 && c <= max3) ||
					 chars.contains(c)
		);

		assertAtLeastOneGeneratedOf(
			all.generator(1000, true),
			random,
			min1, max1, min2, max2, min3, max3,
			'a', 'b', 'c', '1', '2', '.'
		);
	}

	@Example
	void whitespace(@ForAll JqwikRandom random) {
		CharacterArbitrary all = this.arbitrary.whitespace();
		checkAllGenerated(
			all.generator(1000, true),
			random,
			Character::isWhitespace
		);
		assertAtLeastOneGeneratedOf(
			all.generator(1000, true),
			random,
			toCharacterArray(DefaultCharacterArbitrary.WHITESPACE_CHARS)
		);
	}

	private Character[] toCharacterArray(char[] chars) {
		Character[] result = new Character[chars.length];
		for (int i = 0; i < chars.length; i++) {
			result[i] = chars[i];
		}
		return result;
	}
}
