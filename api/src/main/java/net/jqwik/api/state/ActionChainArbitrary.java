package net.jqwik.api.state;

import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.7.0")
public interface ActionChainArbitrary<S> extends Arbitrary<ActionChain<S>> {

	/**
	 * Set the intended number of transformations of generated chains.
	 */
	ActionChainArbitrary<S> withMaxTransformations(int maxSize);

	/**
	 * Set supplier for the type specific {@linkplain ChangeDetector} which can make shrinking of action chains more effective.
	 *
	 * @param detectorSupplier A function to create a new {@linkplain ChangeDetector} instance.
	 */
	ActionChainArbitrary<S> detectChangesWith(Supplier<ChangeDetector<S>> detectorSupplier);

}
