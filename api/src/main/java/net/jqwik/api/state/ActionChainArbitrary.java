package net.jqwik.api.state;

import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.7.0")
public interface ActionChainArbitrary<S> extends Arbitrary<ActionChain<S>> {

	/**
	 * Set the intended number of transformations of generated chains.
	 *
	 * <p>
	 *     Setting {@code maxTransformations} to {@code -1} creates a potentially infinite chain.
	 *     Such a chain will only end when a {@linkplain Transformer#endOfChain()} is applied.
	 * </p>
	 */
	ActionChainArbitrary<S> withMaxTransformations(int maxSize);

	/**
	 * Create a potentially infinite chain.
	 * Such a chain will only end when a {@linkplain Transformer#endOfChain()} is applied.
	 */
	default ActionChainArbitrary<S> infinite() {
		return withMaxTransformations(-1);
	}

	/**
	 * Set supplier for the type specific {@linkplain ChangeDetector} which can make shrinking of action chains more effective.
	 *
	 * @param detectorSupplier A function to create a new {@linkplain ChangeDetector} instance.
	 */
	ActionChainArbitrary<S> improveShrinkingWith(Supplier<ChangeDetector<S>> detectorSupplier);

}
