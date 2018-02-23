package net.jqwik.api.arbitraries;

/**
 * Fluent interface to configure arbitraries that generate String values.
 */
public interface StringArbitrary extends NullableArbitrary<String> {

	/**
	 * Set the maximum allowed length {@code maxLength} (included) of generated strings.
	 */
	StringArbitrary ofMaxLength(int maxLength);

	/**
	 * Set the minimum allowed length {@code mixLength} (included) of generated strings.
	 */
	StringArbitrary ofMinLength(int minLength);

	/**
	 * Fix the length to {@code length} of generated strings.
	 */
	default StringArbitrary ofLength(int length) {
		return ofMinLength(length).ofMaxLength(length);
	}

	/**
	 * Allow all chars in {@code chars} to show up in generated strings.
	 *
	 * Can be combined with other methods that allow chars.
	 */
	StringArbitrary withChars(char[] chars);

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
}
