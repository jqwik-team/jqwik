package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import static net.jqwik.api.ArbitraryTestHelper.*;

class DefaultCharacterArbitraryTests {

	CharacterArbitrary arbitrary = new DefaultCharacterArbitrary();

	@Example
	void perDefaultNoNoncharactersAndNoPrivateUseCharactersAreCreated() {
		assertAllGenerated(this.arbitrary.generator(1000, true), c -> {
			if (DefaultCharacterArbitrary.isNoncharacter(c))
				return false;
			return !DefaultCharacterArbitrary.isPrivateUseCharacter(c);
		});

		assertAtLeastOneGenerated(this.arbitrary.generator(1000, true), c -> c <= '\u1000');
		assertAtLeastOneGenerated(this.arbitrary.generator(1000, true), c -> c >= '\uF000');
	}

	@Example
	void edgeCasesAreGenerated() {
		assertAtLeastOneGenerated(this.arbitrary.generator(1000, true), c -> c == Character.MIN_VALUE);
	}

	@Example
	void allOverridesAnythingBefore() {
		CharacterArbitrary all = this.arbitrary.ascii().all();
		assertAllGenerated(all.generator(1000, true), c -> c >= Character.MIN_VALUE && c <= Character.MAX_VALUE);
		assertAtLeastOneGenerated(all.generator(1000, true), c -> c <= '\u1000');
		assertAtLeastOneGenerated(all.generator(1000, true), c -> c >= '\uF000');
	}

	@Example
	void chars() {
		final List<Character> chars = Arrays.asList('a', 'b', 'c', '1', '2', '.');
		CharacterArbitrary all = this.arbitrary.with('a', 'b', 'c', '1', '2', '.');
		assertAllGenerated(all.generator(1000, true), chars::contains);
		assertAtLeastOneGeneratedOf(all.generator(1000, true), 'a', 'b', 'c', '1', '2', '.');
	}

	@Example
	void charsFromCharSequence() {
		final List<Character> chars = Arrays.asList('a', 'b', 'c', '1', '2', '.');
		CharacterArbitrary all = this.arbitrary.with("abc12.");
		assertAllGenerated(all.generator(1000, true), chars::contains);
		assertAtLeastOneGeneratedOf(all.generator(1000, true), 'a', 'b', 'c', '1', '2', '.');
	}

	@Example
	void range() {
		char min = '\u0010';
		char max = '\u0030';
		CharacterArbitrary all = this.arbitrary.range(min, max);
		assertAllGenerated(all.generator(1000, true), c -> c >= min && c <= max);
		assertAtLeastOneGeneratedOf(all.generator(1000, true), min, max);
	}

	@Example
	void digit() {
		CharacterArbitrary all = this.arbitrary.numeric();
		assertAllGenerated(all.generator(1000, true), c -> c >= '0' && c <= '9');
		assertAtLeastOneGeneratedOf(all.generator(1000, true), '0', '9');
	}

	@Example
	void ascii() {
		CharacterArbitrary all = this.arbitrary.ascii();
		assertAllGenerated(all.generator(1000, true), c -> c >= 0 && c <= 127);
		assertAtLeastOneGeneratedOf(all.generator(1000, true), (char) 10, (char) 126);
	}

	@Example
	void alpha() {
		CharacterArbitrary all = this.arbitrary.alpha();
		assertAllGenerated(all.generator(1000, true), this::isAlpha);
	}

	private boolean isAlpha(char c) {
		if (c >= 'a' && c <= 'z') return true;
		return c >= 'A' && c <= 'Z';
	}

	@Example
	void addUpRangesAndChars() {
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

		assertAllGenerated(
			all.generator(1000, true),
			c -> (c >= min1 && c <= max1) ||
					 (c >= min2 && c <= max2) ||
					 (c >= min3 && c <= max3) ||
					 chars.contains(c)
		);

		assertAtLeastOneGeneratedOf(all.generator(1000, true),
									min1, max1, min2, max2, min3, max3,
									'a', 'b', 'c', '1', '2', '.'
		);
	}

	@Example
	void whitespace() {
		CharacterArbitrary all = this.arbitrary.whitespace();
		assertAllGenerated(all.generator(1000, true), (Predicate<Character>) Character::isWhitespace);
		assertAtLeastOneGeneratedOf(all.generator(1000, true), toCharacterArray(DefaultCharacterArbitrary.WHITESPACE_CHARS));
	}

	private Character[] toCharacterArray(char[] chars) {
		Character[] result = new Character[chars.length];
		for (int i = 0; i < chars.length; i++) {
			result[i] = chars[i];
		}
		return result;
	}
}
