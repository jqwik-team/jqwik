package net.jqwik.api.state;

import java.util.function.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

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
public interface Action<S extends @Nullable Object> {

	/**
	 * Create an unconditioned {@linkplain ActionBuilder}.
	 */
	static <T extends @Nullable Object> ActionBuilder<T> builder() {
		return new ActionBuilder<T>();
	}

	/**
	 * Create an {@linkplain ActionBuilder} with a precondition.
	 */
	static <T extends @Nullable Object> ActionBuilder<T> when(Predicate<? super T> precondition) {
		return new ActionBuilder<T>().when(precondition);
	}

	/**
	 * Convenience method to create an independent {@linkplain Action} with a constant transformer
	 */
	static <T extends @Nullable Object> Action.Independent<T> just(Transformer<T> transformer) {
		return Action.<T>builder().just(transformer);
	}

	/**
	 * Convenience method to create an independent {@linkplain Action} with a description and a constant transformer
	 */
	static <T extends @Nullable Object> Action.Independent<T> just(String description, Transformer<T> transformer) {
		return Action.<T>builder().describeAs(description).just(transformer);
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
	@FunctionalInterface
	interface Dependent<S extends @Nullable Object> extends Action<S> {
		/**
		 * Return an arbitrary for transformers that depends on the previous state.
		 *
		 * <p>
		 * In addition to performing a state transformation the transformin function
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
	 *
	 * @see JustMutate
	 * @see JustTransform
	 */
	@FunctionalInterface
	interface Independent<S extends @Nullable Object> extends Action<S> {
		/**
		 * Return an arbitrary for transformers that does not depend on the previous state.
		 *
		 * <p>
		 * In addition to performing a state transformation the transforming function
		 * can also check or assert post-conditions and invariants that should hold when doing the transformation.
		 * </p>
		 *
		 * @return an arbitrary of type {@linkplain Transformer Transformer<S>}.
		 */
		Arbitrary<Transformer<S>> transformer();
	}

	/**
	 * Subclass if you want to implement an {@linkplain Action.Independent independent action} that simply transforms the given state.
	 *
	 * <p>
	 *     If you rather want to declaratively specify an action  start with {@linkplain Action#just(Transformer)},
	 *     {@linkplain Action#when(Predicate)} or {@linkplain Action#builder()}.
	 * </p>
	 *
	 * <p>
	 *     For actions whose state transformation depends on the state,
	 *     you have to implement either {@linkplain Action.Dependent} or {@linkplain Action.Independent}.
	 * </p>
	 */
	abstract class JustTransform<S extends @Nullable Object> implements Action.Independent<S> {

		@Override
		public Arbitrary<Transformer<S>> transformer() {
			return Arbitraries.just(Transformer.transform(
				description(),
				this::transform
			));
		}

		public abstract S transform(S state);

		String description() {
			return getClass().getSimpleName();
		}
	}

	/**
	 * Subclass if you want to implement an {@linkplain Action.Independent independent action} that simply mutates the given state.
	 *
	 * <p>
	 *     If you rather want to declaratively specify an action start with {@linkplain Action#just(Transformer)},
	 *     {@linkplain Action#when(Predicate)} or {@linkplain Action#builder()}.
	 * </p>
	 *
	 * <p>
	 *     For actions whose state transformation depends on the state,
	 *     you have to implement either {@linkplain Action.Dependent} or {@linkplain Action.Independent}.
	 * </p>
	 */
	abstract class JustMutate<S extends @Nullable Object> implements Action.Independent<S> {

		@Override
		public Arbitrary<Transformer<S>> transformer() {
			return Arbitraries.just(Transformer.mutate(
				description(),
				this::mutate
			));
		}

		abstract public void mutate(S state);

		public String description() {
			return getClass().getSimpleName();
		}
	}

}
