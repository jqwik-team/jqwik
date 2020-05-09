package net.jqwik.api;

import java.math.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Determines how generated numerical values are generated and distributed
 * across the allowed range and a center withing this range.
 *
 * <p>
 * Since all random numeric value generation is going back to
 * {@linkplain BigDecimal} generation this interfaces uses only values
 * of type {@linkplain BigDecimal}.
 * </p>
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
public interface RandomDistribution {

	/**
	 * A distribution that generates values closer to the center of a numerical range
	 * with a higher probability. The bigger the range the stronger the bias.
	 */
	static RandomDistribution biased() {
		return new RandomDistribution() {
			@Override
			public int hashCode() {
				return 0;
			}
		};
	}

	/**
	 * A distribution that generates values across the allowed range
	 * with a uniform probability distribution.
	 */
	static RandomDistribution uniform() {
		return new RandomDistribution() {
			@Override
			public int hashCode() {
				return 42;
			}
		};
	}

}
