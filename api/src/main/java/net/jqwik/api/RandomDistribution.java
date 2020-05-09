package net.jqwik.api;

import java.math.*;
import java.util.*;

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
 * The generation of an arbitrary's edge cases is not influenced by the distribution.
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

	@API(status = INTERNAL)
	abstract class RandomDistributionFacade {
		private static final RandomDistribution.RandomDistributionFacade implementation;

		static {
			implementation = FacadeLoader.load(RandomDistribution.RandomDistributionFacade.class);
		}

		public abstract RandomDistribution biased();

		public abstract RandomDistribution uniform();
	}

	/**
	 * Generator for BigInteger values which are behind all generated numeric values in jqwik.
	 */
	interface RandomNumericGenerator {

		/**
		 * Generate next random number within the specified range given on creation of the generator.
		 *
		 * @param random A random value to use for random generation
		 *
		 * @return an instance of BigInteger. Never {@code null}.
		 */
		BigInteger next(Random random);
	}

	/**
	 * A distribution that generates values closer to the center of a numerical range
	 * with a higher probability. The bigger the range the stronger the bias.
	 */
	static RandomDistribution biased() {
		return RandomDistributionFacade.implementation.biased();
	}

	/**
	 * A distribution that generates values across the allowed range
	 * with a uniform probability distribution.
	 */
	static RandomDistribution uniform() {
		return RandomDistributionFacade.implementation.uniform();
	}

	/**
	 * Return a generator that will generate value with the desired distribution
	 *
	 * @param genSize The approximate number of values to generate. Can be influenced by callers.
	 * @param min The minimum allowed value (included)
	 * @param max The maximum allowed value (included)
	 * @param center The center for the distribution. Must be within min and max.
	 *
	 * @return generator for randomly generated BigInteger values
	 */
	RandomNumericGenerator createGenerator(int genSize, BigInteger min, BigInteger max, BigInteger center);
}
