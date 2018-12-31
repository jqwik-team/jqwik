package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure the generation of Character and char values.
 */
@API(status = MAINTAINED, since = "1.0")
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
