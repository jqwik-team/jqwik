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
@API(status = MAINTAINED, since = "1.3.0")
public interface RandomDistribution {

	@API(status = INTERNAL)
	abstract class RandomDistributionFacade {
		private static final RandomDistribution.RandomDistributionFacade implementation;

		static {
			implementation = FacadeLoader.load(RandomDistribution.RandomDistributionFacade.class);
		}

		public abstract RandomDistribution biased();

		public abstract RandomDistribution uniform();

		public abstract RandomDistribution gaussian(double borderSigma);
	}

	/**
	 * Generator for BigInteger values which are behind all generated numeric values in jqwik.
	 */
	@API(status = INTERNAL)
	interface RandomNumericGenerator {

		/**
		 * Generate next random number within the specified range given on creation of the generator.
		 *
		 * @param random A random value to use for random generation
		 *
		 * @return an instance of BigInteger. Never {@code null}.
		 */
		BigInteger next(JqwikRandom random);
	}

	/**
	 * A distribution that generates values closer to the center of a numerical range
	 * with a higher probability. The bigger the range the stronger the bias.
	 *
	 * @return a random distribution instance
	 */
	static RandomDistribution biased() {
		return RandomDistributionFacade.implementation.biased();
	}

	/**
	 * A distribution that generates values across the allowed range
	 * with a uniform probability distribution.
	 *
	 * @return a random distribution instance
	 */
	static RandomDistribution uniform() {
		return RandomDistributionFacade.implementation.uniform();
	}

	/**
	 * A distribution that generates values with (potentially asymmetric) gaussian distribution
	 * the mean of which is the specified center and the probability at the borders is
	 * approximately {@code borderSigma} times standard deviation.
	 *
	 * <p>
	 * Gaussian generation is approximately 10 times slower than {@linkplain #biased()} or {@linkplain #uniform()}
	 * generation. But still, except in rare cases this will not make a noticeable difference in the runtime
	 * of your properties.
	 *
	 * @param borderSigma The approximate factor of standard deviation at the border(s)
	 * @return a random distribution instance
	 */
	static RandomDistribution gaussian(double borderSigma) {
		return RandomDistributionFacade.implementation.gaussian(borderSigma);
	}

	/**
	 * A gaussian distribution with {@code borderSigma} of 3,
	 * i.e. approximately 99.7% of values are within the borders.
	 *
	 * @see #gaussian(double)
	 *
	 * @return a random distribution instance
	 */
	static RandomDistribution gaussian() {
		return RandomDistributionFacade.implementation.gaussian(3);
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
	@API(status = INTERNAL)
	RandomNumericGenerator createGenerator(int genSize, BigInteger min, BigInteger max, BigInteger center);
}
