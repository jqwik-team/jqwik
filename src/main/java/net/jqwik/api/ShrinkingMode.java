package net.jqwik.api;

/**
 * The shrinking mode defines the shrinking behaviour of a property.
 * It can be set in {@linkplain Property#shrinking()} for any property method; default is {@linkplain #BOUNDED}.
 *
 * @see Property
 *
 */
public enum ShrinkingMode {
	/**
	 * No shrinking for falsified values.
	 */
	OFF,

	/**
	 * Shrinking is tried to a depth of 1000 steps maximum per value.
	 * If shrinking has not finished by then, the best found value is taken and
	 * bounded shrinking is reported through JUnit's reporting mechanism.
	 */
	BOUNDED,

	/**
	 * Shrinking continues until no smaller value can be found that also falsifies the property.
	 * This might take very long or not end at all in rare cases.
	 */
	FULL
}
