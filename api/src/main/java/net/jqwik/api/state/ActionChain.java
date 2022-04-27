package net.jqwik.api.state;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;
import org.jetbrains.annotations.*;

import static org.apiguardian.api.API.Status.*;

/**
 * A chain of transforming Actions that can be run for values of type {@code S}.
 * Since the next action is usually created on demand, the current {@code runState}
 * of a chain can be queried.
 *
 * <p>
 *     By default any action chain instance is not thread safe,
 *     i.e. you should not try to invoke {@linkplain #run()} concurrently.
 * </p>
 *
 * @param <S> The type of the object going through state transformations
 */
@API(status = EXPERIMENTAL, since = "1.7.0")
public interface ActionChain<S> {

	enum RunningState {
		NOT_RUN, RUNNING, FAILED, SUCCEEDED
	}

	/**
	 * Return list of all applied transformations.
	 *
	 * <p>
	 *     For a chain that has not been run this list is always empty.
	 * </p>
	 *
	 * @return list of describing strings
	 */
	List<String> transformations();

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
