package net.jqwik.api.arbitraries;

public interface CharacterArbitrary extends NullableArbitrary<Character> {

	CharacterArbitrary withChars(char[] allowedChars);

	CharacterArbitrary withChars(char from, char to);
}
