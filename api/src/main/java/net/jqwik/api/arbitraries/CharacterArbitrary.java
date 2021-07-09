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
	 *
	 * <p>
	 * Resets previous settings.
	 * </p>
	 *
	 * @return new instance of arbitrary
	 */
	CharacterArbitrary all();

	/**
	 * Allow all chars in {@code allowedChars} show up in generated values.
	 *
	 * <p>
	 * Adds to all already allowed chars.
	 * </p>
	 *
	 * @param allowedChars chars allowed
	 * @return new instance of arbitrary
	 */
	@API(status = MAINTAINED, since = "1.1.3")
	CharacterArbitrary with(char... allowedChars);

	/**
	 * Allow all chars in {@code allowedChars} show up in generated values.
	 *
	 * <p>
	 * Adds to all already allowed chars.
	 * </p>
	 *
	 * @param allowedChars as String or other CharSequence
	 * @return new instance of arbitrary
	 */
	@API(status = MAINTAINED, since = "1.2.1")
	CharacterArbitrary with(CharSequence allowedChars);

	/**
	 * Allow all chars within {@code min} (included) and {@code max} (included) to show up in generated values.
	 *
	 * <p>
	 * Adds to all already allowed chars.
	 * </p>
	 *
	 * @param min min char value
	 * @param max max char value
	 * @return new instance of arbitrary
	 */
	@API(status = MAINTAINED, since = "1.1.3")
	CharacterArbitrary range(char min, char max);

	/**
	 * Allow all ascii chars to show up in generated values.
	 *
	 * <p>
	 * Adds to all already allowed chars.
	 * </p>
	 *
	 * @return new instance of arbitrary
	 */
	CharacterArbitrary ascii();

	/**
	 * Allow all numeric chars (digits) to show up in generated values.
	 *
	 * <p>
	 * Adds to all already allowed chars.
	 * </p>
	 *
	 * @return new instance of arbitrary
	 *
	 * @deprecated Use {@linkplain #numeric()} instead. Will be removed in 1.7.0.
	 */
	@Deprecated
	default CharacterArbitrary digit() {
		return numeric();
	}

	/**
	 * Allow all numeric chars to show up in generated values.
	 *
	 * <p>
	 * Adds to all already allowed chars.
	 * </p>
	 *
	 * @return new instance of arbitrary
	 */
	@API(status = MAINTAINED, since = "1.5.3")
	CharacterArbitrary numeric();

	/**
	 * Allow all whitespace chars to show up in generated values.
	 *
	 * <p>
	 * Adds to all already allowed chars.
	 * </p>
	 *
	 * @return new instance of arbitrary
	 */
	@API(status = MAINTAINED, since = "1.1.3")
	CharacterArbitrary whitespace();

	/**
	 * Allow all alpha chars to show up in generated strings.
	 *
	 * <p>
	 * Adds to all already allowed chars.
	 * </p>
	 *
	 * @return new instance of arbitrary
	 */
	@API(status = MAINTAINED, since = "1.5.3")
	CharacterArbitrary alpha();
}
