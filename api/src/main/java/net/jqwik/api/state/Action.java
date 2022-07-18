package net.jqwik.api.state;

import java.util.function.*;

import org.apiguardian.api.*;
import org.jetbrains.annotations.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * An action class represents a state transformation that can be preformed
 * on an object of type {@code S}.
 * Those objects can be either mutable, which means that the state changing method will return the same object,
 * or immutable, which requires the method to return another object that represents the transformed state.
 *
 * <p>
 *     Mind that there is a another interface {@link net.jqwik.api.stateful.Action} which looks similar
 *     but refers to jqwik's old and deprecated style of state-based property testing.
 *     The two interfaces CANNOT be used interchangeably.
 * </p>
 *
 * @param <S> Type of the object to transform through an action
 */
@API(status = EXPERIMENTAL, since = "1.7.0")
public interface Action<S> {

	/**
	 * Create an {@linkplain Action} without generated parts
	 */
	static <T> Action<T> just(Transformer<T> transformer) {
		return just((String) null, transformer);
	}

	/**
	 * Create an {@linkplain Action} without generated parts
	 */
	static <T> Action<T> just(@Nullable String description, Transformer<T> transformer) {
		return new Action<T>() {
			@Override
			public Arbitrary<Transformer<T>> transformer() {
				Transformer<T> withDescription = description == null ? transformer : Transformer.transform(description, transformer);
				return Arbitraries.just(withDescription);
			}
		};
	}

	/**
	 * Create an {@linkplain Action} without generated parts
	 */
	static <T> Action<T> just(Predicate<T> precondition, Transformer<T> transformer) {
		return just(null, precondition, transformer);
	}

	/**
	 * Create an {@linkplain Action} without generated parts
	 */
	static <T> Action<T> just(@Nullable String description, Predicate<T> precondition, Transformer<T> transformer) {
		// Do not merge implementation with Action.just(description, transformer) since dedicated implementation of precondition() changes shrinking behaviour
		return new Action<T>() {
			@Override
			public Arbitrary<Transformer<T>> transformer() {
				Transformer<T> withDescription = description == null ? transformer : Transformer.transform(description, transformer);
				return Arbitraries.just(withDescription);
			}

			@Override
			public boolean precondition(T state) {
				return precondition.test(state);
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
	 * You have to implement either this method or {@linkplain #transformer()}.
	 *
	 * <p>
	 *     In addition to performing a state transformation the mutator function
	 *     can also check or assert post-conditions and invariants that should hold when doing the transformation.
	 * </p>
	 * <p>
	 * Implementing this method instead of {@linkplain #transformer()} will make the chain of actions harder to shrink.
	 * </p>
	 *
	 * @param state the current state
	 * @return an arbitrary of type {@linkplain Transformer Transformer<S>}.
	 */
	default Arbitrary<Transformer<S>> transformer(S state) {
		throw new UnsupportedOperationException("You have to override either Action.transformer() or Action.transformer(state).");
	}

	/**
	 * Implement this method if you want to have the action's transforming behaviour not to depend on previous state.
	 * You have to implement either this method or {@linkplain #transformer(Object)}.
	 *
	 * <p>
	 *     In addition to performing a state transformation the mutator function
	 *     can also check or assert post-conditions and invariants that should hold when doing the transformation.
	 * </p>
	 *
	 * @return an arbitrary of type {@linkplain Transformer Transformer<S>}.
	 */
	default Arbitrary<Transformer<S>> transformer() {
		throw new UnsupportedOperationException("You have to override either Action.transformer() or Action.transformer(state).");
	}
}
