package net.jqwik.api.state;

import java.util.function.*;

import net.jqwik.api.*;

import org.apiguardian.api.API;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

@API(status = EXPERIMENTAL, since = "1.7.0")
public interface ChainArbitrary<T> extends Arbitrary<Chain<T>> {

	/**
	 * Set the intended number of transformations of generated chains.
	 */
	ChainArbitrary<T> withMaxTransformations(int maxTransformations);

	/**
	 * Set supplier for the type specific {@linkplain ChangeDetector} which can make shrinking of chains more effective.
	 *
	 * @param detectorSupplier A function to create a new {@linkplain ChangeDetector} instance.
	 */
	ChainArbitrary<T> detectChangesWith(Supplier<ChangeDetector<T>> detectorSupplier);

}
