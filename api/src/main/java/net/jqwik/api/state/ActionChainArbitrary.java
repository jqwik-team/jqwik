package net.jqwik.api.state;

import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.7.0")
public interface ActionChainArbitrary<S> extends Arbitrary<ActionChain<S>> {

	/**
	 * @deprecated Use {@link #withAction(Action)} instead. Will soon be removed.
	 */
	@API(status = DEPRECATED)
	default ActionChainArbitrary<S> addAction(Action<S> action) {
		return withAction(1, action);
	}

	/**
	 * @deprecated Use {@link #withAction(int, Action)} instead. Will soon be removed.
	 */
	@API(status = DEPRECATED, since = "1.7.2")
	default ActionChainArbitrary<S> addAction(int weight, Action<S> action) {
		return withAction(weight, action);
	}

	/**
	 * Allow an additional action with default weight of 1.
	 *
	 * @param action Instance of {@linkplain Action.Dependent} or {@linkplain Action.Independent}
	 * @return new arbitrary instance
	 */
	@API(status = EXPERIMENTAL, since = "1.7.2")
	default ActionChainArbitrary<S> withAction(Action<S> action) {
		return withAction(1, action);
	}

	/**
	 * Allow an additional action to be generated.
	 *
	 * @param weight Determines the relative probability of an action to be chosen.
	 * @param action Instance of {@linkplain Action.Dependent} or {@linkplain Action.Independent}
	 * @return new arbitrary instance
	 */
	@API(status = EXPERIMENTAL, since = "1.7.2")
	ActionChainArbitrary<S> withAction(int weight, Action<S> action);

	/**
	 * Set the intended number of transformations of generated chains.
	 *
	 * <p>
	 *     Setting {@code maxTransformations} to {@code -1} creates a potentially infinite chain.
	 *     Such a chain will only end when a {@linkplain Transformer#endOfChain()} is applied.
	 * </p>
	 *
	 * @return new arbitrary instance
	 */
	ActionChainArbitrary<S> withMaxTransformations(int maxSize);

	/**
	 * Create a potentially infinite chain.
	 * Such a chain will only end when a {@linkplain Transformer#endOfChain()} is applied.
	 *
	 * @return new arbitrary instance
	 */
	default ActionChainArbitrary<S> infinite() {
		return withMaxTransformations(-1);
	}

	/**
	 * Set supplier for the type specific {@linkplain ChangeDetector} which can make shrinking of action chains more effective.
	 *
	 * @param detectorSupplier A function to create a new {@linkplain ChangeDetector} instance.
	 *
	 * @return new arbitrary instance
	 */
	ActionChainArbitrary<S> improveShrinkingWith(Supplier<ChangeDetector<S>> detectorSupplier);
}
