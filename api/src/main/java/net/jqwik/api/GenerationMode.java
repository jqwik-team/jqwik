package net.jqwik.api;

/**
 * The generation mode defines the generation behaviour of a property.
 * It can be set in {@linkplain Property#generation()} for any property method; default is {@linkplain #AUTO}.
 *
 * @see Property
 * @see FromData
 */
public enum GenerationMode {
	/**
	 * Use randomized value generation.
	 */
	RANDOMIZED,

	/**
	 * Use exhaustive generation. Only possible if all used arbitraries can
	 * provide exhaustive generators.
	 */
	EXHAUSTIVE,

	/**
	 * Use data specified in {@linkplain FromData} annotation
	 */
	DATA_DRIVEN,

	/**
	 * Let jqwik choose which generation mode it prefers:
	 * <ul>
	 *     <li>If all arbitraries provide exhaustive generators and
	 *     if the multiplication of all maxCount() values is &le;
	 *     {@linkplain Property#tries()} use {@linkplain #EXHAUSTIVE}</li>

	 *     <li>If the property has a {@linkplain FromData} annotation
	 *     use {@linkplain #DATA_DRIVEN}</li>

	 *     <li>In all other cases use {@linkplain #RANDOMIZED}</li>
	 * </ul>
	 */
	AUTO
}
