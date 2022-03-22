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

	static <T> Action<T> just(Transformer<T> transformer) {
		return just(transformer, transformer.transformation());
	}

	static <T> Action<T> just(Transformer<T> transformer, String description) {
		return new Action<T>() {
			@Override
			public Arbitrary<Transformer<T>> transformer() {
				return Arbitraries.just(transformer);
			}

			@Override
			public String toString() {
				return description;
			}
		};
	}

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
	 * Implement this method if you want to have the action's transforming behaviour depend on the previous state.
	 *
	 * <p>
	 * You have to implement either this method or {@link this#transformer()}.
	 * </p>
	 * <p>
	 * Implementing this method instead of {@linkplain #transformer()} will make the chain of actions harder to shrink.
	 * </p>
	 *
	 * @param state the current state
	 * @return an arbitrary of type {@linkplain Transformer Transformer<S>}.
	 */
	default Arbitrary<Transformer<S>> transformer(S state) {
		throw new UnsupportedOperationException("You have to override either Action.run(state) or Action.provideTransformer(state).");
	}

	/**
	 * Implement this method if you want to have the action's transforming behaviour not to depend on previous state.
	 *
	 * <p>
	 * You have to implement either this method or {@linkplain this#transformer(Object)}.
	 * </p>
	 *
	 * @return an arbitrary of type {@linkplain Transformer Transformer<S>}.
	 */
	default Arbitrary<Transformer<S>> transformer() {
		throw new UnsupportedOperationException("You have to override either Action.run(state) or Action.provideTransformer(state).");
	}
}
