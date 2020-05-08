package net.jqwik.api.arbitraries;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.3.0")
public interface NumericalArbitrary<T, A extends NumericalArbitrary<T, A>> extends Arbitrary<T> {

	/**
	 * Set the {@code distribution} to use when generating random values.
	 * Default is {@linkplain RandomDistribution#BIASED}.
	 *
	 * @param distribution The distribution to use when generating random values
	 */
	A withDistribution(RandomDistribution distribution);

}
