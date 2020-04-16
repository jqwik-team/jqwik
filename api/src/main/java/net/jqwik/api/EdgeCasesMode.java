package net.jqwik.api;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * The edge-cases mode determines if and when edge-cases will be explicitly generated.
 * It can be set in {@linkplain Property#edgeCases()} for any property method; default is {@linkplain #FIRST_AND_MIXIN}.
 *
 * @see Property
 */
@API(status = EXPERIMENTAL, since = "1.3.0")
public enum EdgeCasesMode {

	/**
	 * Generate edge cases first.
	 */
	FIRST,

	/**
	 * Mix edge cases into random generation.
	 */
	MIXIN,

	/**
	 * Generate edge cases first then mix them into random generation.
	 */
	FIRST_AND_MIXIN,

	/**
	 * Do not _explicitly_ generate edge cases. They might be generated randomly though.
	 */
	NONE

}
