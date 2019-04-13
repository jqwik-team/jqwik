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
	 * Allow all unicode chars to show up in generated values.
	 */
	CharacterArbitrary all();

	/**
	 * Allow all chars within {@code min} (included) and {@code max} (included) to show up in generated values.
	 *
	 * @deprecated Use {@link #range(char, char)} instead.
	 */
	@Deprecated
	@API(status = DEPRECATED)
	default CharacterArbitrary between(char min, char max) {
		return this.range(min, max);
	}

	/**
	 * Allow all chars in {@code chars} show up in generated values.
	 *
	 * Adds to all already allowed chars.
	 */
	@API(status = MAINTAINED, since = "1.1.3")
	CharacterArbitrary with(char... allowedChars);

	/**
	 * Allow all chars within {@code min} (included) and {@code max} (included) to show up in generated values.
	 *
	 * Adds to all already allowed chars.
	 */
	@API(status = MAINTAINED, since = "1.1.3")
	CharacterArbitrary range(char min, char max);

	/**
	 * Allow all ascii chars to show up in generated values.
	 *
	 * Adds to all already allowed chars.
	 */
	CharacterArbitrary ascii();

	/**
	 * Allow all numeric chars (digits) to show up in generated values.
	 *
	 * Adds to all already allowed chars.
	 */
	CharacterArbitrary digit();
}
