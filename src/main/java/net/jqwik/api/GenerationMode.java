package net.jqwik.api;

/**
 * The generation mode defines the generation behaviour of a property.
 * It can be set in {@linkplain Property#generation()} for any property method; default is {@linkplain #AUTO}.
 *
 * @see Property
 */
public enum GenerationMode {
	/**
	 * Use randomized value generation.
	 */
	RANDOMIZED,

	/**
	 * Use exhaustive generation. Only possible if all used arbitraries can
	 * provide exhaustive generators. Allow data-driven generation
	 * if the property also has a {@linkplain Data} annotation
	 */
	EXHAUSTIVE,

	/**
	 * Let jqwik choose which generation mode it prefers:
	 * <ul>
	 *     <li>If all arbitraries provide exhaustive generators and
	 *     if the multiplication of all maxCount() values is <=
	 *     {@linkplain Property#tries()} use {@linkplain #EXHAUSTIVE}</li>

	 *     <li>In all other cases use {@linkplain #RANDOMIZED} but
	 *     allow data-driven generation if the property also has a
	 *     {@linkplain Data} annotation</li>
	 * </ul>
	 */
	AUTO
}
