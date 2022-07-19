package net.jqwik.api.state;

import java.util.function.*;

import org.apiguardian.api.*;
import org.jetbrains.annotations.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * An action class represents a state transformation that can be performed
 * on an object of type {@code S}.
 * Those objects can be mutable, which means that the state changing method will return the same object,
 * or immutable, which requires the method to return another object that represents the transformed state.
 *
 * <p>
 * Do not implement this interface directly, but either {@linkplain Action.Dependent} or {@linkplain Action.Independent}.
 * Only those can be used to create an arbitrary for a {@linkplain ActionChain}.
 * </p>
 *
 * <p>
 * Mind that there is a another interface {@link net.jqwik.api.stateful.Action} which looks similar
 * but refers to jqwik's old and deprecated style of state-based property testing.
 * The two interfaces CANNOT be used interchangeably.
 * </p>
 *
 * @param <S> Type of the object to transform through an action
 * @see ActionChain
 * @see ActionChainArbitrary
 */
@API(status = EXPERIMENTAL, since = "1.7.0")
public interface Action<S> {

	/**
	 * Create an independent {@linkplain Action} with a constant transformer
	 */
	static <T> Action.Independent<T> just(Transformer<T> transformer) {
		return just((String) null, transformer);
	}

	/**
	 * Create an independent {@linkplain Action} with a description and a constant transformer
	 */
	static <T> Action.Independent<T> just(@Nullable String description, Transformer<T> transformer) {
		return () -> {
			Transformer<T> withDescription = description == null ? transformer : Transformer.transform(description, transformer);
			return Arbitraries.just(withDescription);
		};
	}

	/**
	 * Create an independent {@linkplain Action} with precondition and a constant transformer
	 */
	static <T> Action.Independent<T> just(Predicate<T> precondition, Transformer<T> transformer) {
		return just(null, precondition, transformer);
	}

	/**
	 * Create an independent {@linkplain Action} with description, precondition and a constant transformer
	 */
	static <T> Action.Independent<T> just(@Nullable String description, Predicate<T> precondition, Transformer<T> transformer) {
		// Do not merge implementation with Action.just(description, transformer) since dedicated implementation of precondition() changes shrinking behaviour
		return new Action.Independent<T>() {
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
	 * Implement this interface if you want to have the action's transforming behaviour depend on the previous state.
	 *
	 * <p>
	 * Implementing this interface instead of {@linkplain Independent} will make the chain of actions harder to shrink.
	 * </p>
	 */
	interface Dependent<S> extends Action<S> {
		/**
		 * Return an arbitrary for transformers that depends on the previous state.
		 *
		 * <p>
		 * In addition to performing a state transformation the mutator function
		 * can also check or assert post-conditions and invariants that should hold when doing the transformation.
		 * </p>
		 *
		 * @param state the current state
		 * @return an arbitrary of type {@linkplain Transformer Transformer<S>}.
		 */
		Arbitrary<Transformer<S>> transformer(S state);
	}

	/**
	 * Implement this interface if you want to have the action's transforming behaviour not to depend on previous state.
	 *
	 * <p>
	 * In addition to performing a state transformation the mutator function
	 * can also check or assert post-conditions and invariants that should hold when doing the transformation.
	 * </p>
	 */
	interface Independent<S> extends Action<S> {
		/**
		 * Return an arbitrary for transformers that does not depend on the previous state.
		 *
		 * <p>
		 * In addition to performing a state transformation the mutator function
		 * can also check or assert post-conditions and invariants that should hold when doing the transformation.
		 * </p>
		 *
		 * @return an arbitrary of type {@linkplain Transformer Transformer<S>}.
		 */
		Arbitrary<Transformer<S>> transformer();
	}

}
