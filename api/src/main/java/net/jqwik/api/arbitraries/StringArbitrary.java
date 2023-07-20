package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Fluent interface to configure arbitraries that generate String values.
 */
@API(status = MAINTAINED, since = "1.0")
public interface StringArbitrary extends Arbitrary<String> {

	/**
	 * Set the maximum allowed length {@code maxLength} (included) of generated strings.
	 *
	 * @throws IllegalArgumentException if maxLength &lt; 0 or maxLength &lt; min length
	 */
	StringArbitrary ofMaxLength(int maxLength);

	/**
	 * Set the minimum allowed length {@code minLength} (included) of generated strings.
	 * This will also set the max length of the string if {@code minLength} is larger than the current max length.
	 *
	 * @throws IllegalArgumentException if minLength &lt; 0
	 */
	StringArbitrary ofMinLength(int minLength);

	/**
	 * Fix the length to {@code length} of generated strings.
	 *
	 * @throws IllegalArgumentException if length &lt; 0
	 */
	default StringArbitrary ofLength(int length) {
		return ofMinLength(length).ofMaxLength(length);
	}

	/**
	 * Allow all chars in {@code chars} to show up in generated strings.
	 *
	 * Can be combined with other methods that allow chars.
	 */
	StringArbitrary withChars(char... chars);

	/**
	 * Allow all chars in {@code chars} to show up in generated strings.
	 *
	 * Can be combined with other methods that allow chars.
	 */
	@API(status = MAINTAINED, since = "1.2.1")
	StringArbitrary withChars(CharSequence chars);

	/**
	 * Allow all chars within {@code from} (included) and {@code to} (included) to show up in generated strings.
	 *
	 * Can be combined with other methods that allow chars.
	 */
	StringArbitrary withCharRange(char from, char to);

	/**
	 * Allow all ascii chars to show up in generated strings.
	 *
	 * Can be combined with other methods that allow chars.
	 */
	StringArbitrary ascii();

	/**
	 * Allow all alpha chars to show up in generated strings.
	 *
	 * Can be combined with other methods that allow chars.
	 */
	StringArbitrary alpha();

	/**
	 * Allow all numeric chars (digits) to show up in generated strings.
	 *
	 * Can be combined with other methods that allow chars.
	 */
	StringArbitrary numeric();

	/**
	 * Allow all chars that will return {@code true} for
	 * {@link Character#isWhitespace(char)}.
	 *
	 * Can be combined with other methods that allow chars.
	 */
	StringArbitrary whitespace();

	/**
	 * Allow all unicode chars even noncharacters and private use characters
	 * but only in plane 0 (aka Basic Multilingual Plane)
	 */
	StringArbitrary all();

	/**
	 * Exclude all {@code charsToExclude} from generated strings
	 *
	 * @param charsToExclude chars to exclude
	 * @return new instance of arbitrary
	 */
	@API(status = MAINTAINED, since = "1.5.1")
	StringArbitrary excludeChars(char ... charsToExclude);

	/**
	 * Set random distribution {@code distribution} of length of generated string.
	 * The distribution's center is the minimum length of the generated list.
	 */
	@API(status = EXPERIMENTAL, since = "1.5.3")
	StringArbitrary withLengthDistribution(RandomDistribution lengthDistribution);

	/**
	 * Set the probability for repeating chars within the string to an approximate probability value.
	 *
	 * @param repeatProbability Must be between 0 (included) and 1 (excluded)
	 */
	@API(status = MAINTAINED, since = "1.5.3")
	StringArbitrary repeatChars(double repeatProbability);

	/**
	 * Prevent character from having duplicates within the generated string.
	 */
	@API(status = EXPERIMENTAL, since = "1.8.0")
	StringArbitrary uniqueChars();
}
