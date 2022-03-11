package net.jqwik.api.state;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * An action class represents a state transformation that can be preformed
 * on an object of type {@code S}.
 * Those objects can be either mutable, which means that the state changing method will return the same object,
 * or immutable, which requires the method to return another object that represents the transformed state.
 *
 * @param <S> Type of the object to transform through an action
 */
@API(status = EXPERIMENTAL, since = "1.7.0")
public interface Action<S> {

	/**
	 * If this method returns false, the action will not be performed.
	 *
	 * <p>
	 * Implementing this method will make the chain of actions harder to shrink.
	 * </p>
	 *
	 * @param state the current state
	 * @return true if the precondition holds
	 */
	default boolean precondition(S state) {
		return true;
	}

	/**
	 * Implement this method if you want to have the action behaviour depend on the previous state.
	 *
	 * <p>
	 * You have to implement either this method or {@link this#run(Object)}.
	 * </p>
	 * <p>
	 * Implementing this method will make the chain of actions harder to shrink.
	 * </p>
	 *
	 * @param state the current state
	 * @return an arbitrary of type {@linkplain Transformer Transformer<S>}.
	 */
	default Arbitrary<Transformer<S>> provideTransformer(S state) {
		return Arbitraries.just(
			new Transformer<S>() {
				@Override
				public S apply(S s) {
					return Action.this.run(s);
				}

				@Override
				public String transformation() {
					return Action.this.toString();
				}
			}
		);
	}

	/**
	 * Perform an action on state {@code S} and return the same state (if it has state)
	 * or a new one representing the new state.
	 *
	 * <p>
	 * You have to implement either this method or {@linkplain this#provideTransformer(Object)}.
	 * </p>
	 *
	 * @param state the current state
	 * @return the new state, which may or may not be the same object
	 */
	default S run(S state) {
		throw new UnsupportedOperationException("You have to override either Action.run(state) or Action.provideTransformer(state).");
	}
}
