package net.jqwik.api.arbitraries;

public interface CharacterArbitrary extends NullableArbitrary<Character> {

	CharacterArbitrary between(char min, char max);

	CharacterArbitrary ascii();

	CharacterArbitrary all();

	CharacterArbitrary digit();
}
