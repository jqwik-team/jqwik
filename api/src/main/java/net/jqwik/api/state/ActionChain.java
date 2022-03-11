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

	enum RunState {
		NOT_RUN, RUNNING, FAILED, SUCCEEDED
	}

	List<String> runActions();

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

	S finalValue();

	RunState runState();

	/**
	 * Peek into the model of a running chain.
	 * The {@code modelPeeker} will be called after each successful action
	 * but before checking invariants.
	 *
	 * <p>
	 * There can be more than one peeker.
	 * </p>
	 *
	 * @param peeker
	 * @return the same chain instance
	 */
	ActionChain<S> peek(Consumer<S> peeker);

}
