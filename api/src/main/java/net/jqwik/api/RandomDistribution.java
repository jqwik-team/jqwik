package net.jqwik.api;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Determines a numerical arbitrary's distribution of randomly generated values
 * across the allowed range.
 *
 * <p>
 *     The generation of an arbitrary's edge cases is not influenced by this parameter.
 * </p>
 *
 * @see net.jqwik.api.arbitraries.BigIntegerArbitrary
 * @see net.jqwik.api.arbitraries.IntegerArbitrary
 * @see net.jqwik.api.arbitraries.LongArbitrary
 * @see net.jqwik.api.arbitraries.ShortArbitrary
 * @see net.jqwik.api.arbitraries.ByteArbitrary
 * @see net.jqwik.api.arbitraries.BigDecimalArbitrary
 * @see net.jqwik.api.arbitraries.DoubleArbitrary
 * @see net.jqwik.api.arbitraries.FloatArbitrary
 */
@API(status = EXPERIMENTAL, since = "1.3.0")
public enum RandomDistribution {

	/**
	 * Values closer to the shrinking target of a numerical range have higher
	 * probability of being generated.
	 */
	BIASED,

	/**
	 * All values within the allowed range have the same probability of being generated.
	 */
	UNIFORM
}
