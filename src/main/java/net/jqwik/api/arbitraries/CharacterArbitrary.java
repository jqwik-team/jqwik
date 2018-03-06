package net.jqwik.api.arbitraries;

import net.jqwik.api.*;

/**
 * Fluent interface to configure the generation of Character and char values.
 */
public interface CharacterArbitrary extends Arbitrary<Character> {

	/**
	 * Allow all chars within {@code min} (included) and {@code max} (included) to show up in generated values.
	 */
	CharacterArbitrary between(char min, char max);

	/**
	 * Allow all ascii chars to show up in generated values.
	 */
	CharacterArbitrary ascii();

	/**
	 * Allow all unicode chars to show up in generated values.
	 */
	CharacterArbitrary all();

	/**
	 * Allow all numeric chars (digits) to show up in generated values.
	 */
	CharacterArbitrary digit();
}
