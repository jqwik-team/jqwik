package net.jqwik.api.arbitraries;

import net.jqwik.api.*;

public interface CharacterArbitrary extends Arbitrary<Character> {

	CharacterArbitrary withChars(char[] allowedChars);

	CharacterArbitrary withChars(char from, char to);
}
