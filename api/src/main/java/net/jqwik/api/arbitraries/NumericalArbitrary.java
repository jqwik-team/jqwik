package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = MAINTAINED, since = "1.8.0")
public interface NumericalArbitrary<T, A extends NumericalArbitrary<T, A>> extends Arbitrary<T> {

	/**
	 * Set the {@linkplain RandomDistribution distribution} to use when generating random numerical values.
	 *
	 * <p>
	 * jqwik currently offers two built-in distributions:
	 * <ul>
	 *     <li>{@linkplain RandomDistribution#biased()} is the default</li>
	 *     <li>{@linkplain RandomDistribution#uniform()} creates a uniform probability distribution</li>
	 * </ul>
	 *
	 * @param distribution The distribution to use when generating random values
	 * @return a random distribution instance
	 */
	A withDistribution(RandomDistribution distribution);

}
