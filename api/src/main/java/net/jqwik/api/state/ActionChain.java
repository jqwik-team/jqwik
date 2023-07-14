package net.jqwik.api.state;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * A chain of transforming Actions that can be run for values of type {@code S}.
 * Since the next action is usually created on demand, the current {@code runState}
 * of a chain can be queried.
 *
 * <p>
 * By default any action chain instance is not thread safe,
 * i.e. you should not try to invoke {@linkplain #run()} concurrently.
 * </p>
 *
 * @param <S> The type of the object going through state transformations
 */
@API(status = EXPERIMENTAL, since = "1.7.0")
public interface ActionChain<S> {

	@API(status = INTERNAL)
	abstract class ActionChainFacade {
		static final ActionChainFacade implementation;

		static {
			implementation = FacadeLoader.load(ActionChainFacade.class);
		}

		public abstract <T> ActionChainArbitrary<T> startWith(Supplier<? extends T> initialSupplier);
	}

	/**
	 * Create arbitrary for a {@linkplain ActionChain chain} with a certain initial state.
	 *
	 * @param initialSupplier function to create the initial state object
	 * @param <T>             The type of state to be transformed through the chain.
	 * @return new arbitrary instance
	 */
	static <T> ActionChainArbitrary<T> startWith(Supplier<? extends T> initialSupplier) {
		return ActionChainFacade.implementation.startWith(initialSupplier);
	}

	enum RunningState {
		NOT_RUN, RUNNING, FAILED, SUCCEEDED
	}

	/**
	 * Return list of all applied transformations.
	 *
	 * <p>
	 * For a chain that has not been run this list is always empty.
	 * </p>
	 *
	 * @return list of describing strings
	 */
	List<String> transformations();

	/**
	 * Return list of all used transformer instances.
	 *
	 * <p>
	 * Checking transformer instances - e.g. if they are of a certain implementation type -
	 * only makes sense if the transformer's description string is NOT set explicitly,
	 * e.g. in an Action's description() method.
	 * </p>
	 *
	 * <p>
	 * For a chain that has not been run this list is always empty.
	 * </p>
	 *
	 * @return list of transformer instances
	 */
	@API(status = EXPERIMENTAL, since = "1.7.1")
	List<Transformer<S>> transformers();

	/**
	 * Run the list through all transformations provided by the actions to create it.
	 * Stop when either the maximum number of transformations is reached or if a
	 * {@linkplain Transformer#END_OF_CHAIN} is being applied.
	 *
	 * @return the last resulting state of running through transformations
	 */
	S run();

	/**
	 * Add an unlabelled invariant to a sequence.
	 *
	 * @param invariant will be checked after each successful action
	 * @return the same chain instance
	 * @see #withInvariant(String, Consumer)
	 */
	default ActionChain<S> withInvariant(Consumer<S> invariant) {
		return withInvariant(null, invariant);
	}

	/**
	 * Add a labelled invariant to a sequence.
	 *
	 * @param label     will show up in error messages when the invariant fails
	 * @param invariant will be checked after each successful action
	 * @return the same chain instance
	 * @see #withInvariant(Consumer)
	 */
	ActionChain<S> withInvariant(@Nullable String label, Consumer<S> invariant);

	/**
	 * The final state value after running an action chain.
	 *
	 * @return state or {@linkplain Optional#empty()} if chain has not been run
	 */
	Optional<S> finalState();

	/**
	 * An action chain can be in different running states: NOT_RUN, RUNNING, FAILED, SUCEEDED
	 *
	 * @return a {@linkplain RunningState state} object
	 */
	RunningState running();

	/**
	 * Observe the state transformations of a running chain by adding a peeker to an action chain.
	 * The {@code peeker} will be called after each successful transformation
	 * but before checking invariants.
	 *
	 * <p>
	 * There can be more than one peeker.
	 * </p>
	 *
	 * @param peeker A consumer of a state object
	 * @return the same chain instance
	 */
	ActionChain<S> peek(Consumer<S> peeker);

}
