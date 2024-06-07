package net.jqwik.api.stateful;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

/**
 * A sequence of Actions that can be run with a model of type {@code M}
 *
 * @param <M> The type of the model
 */
@API(status = MAINTAINED, since = "1.0")
public interface ActionSequence<M extends @Nullable Object> {

	enum RunState {
		NOT_RUN, RUNNING, FAILED, SUCCEEDED
	}

	List<Action<M>> runActions();

	M run(M model);

	@API(status = EXPERIMENTAL, since = "1.3.3")
	int size();

	/**
	 * Add an unlabelled invariant to a sequence.
	 *
	 * @param invariant will be checked after each successful invocation of {@linkplain #run(Object)}
	 * @return the same sequence instance
	 *
	 * @see	#withInvariant(String, Invariant)
	 */
	default ActionSequence<M> withInvariant(Invariant<M> invariant) {
		return withInvariant(null, invariant);
	}

	/**
	 * Add a labelled invariant to a sequence.
	 *
	 * @param label will show up in error messages when the invariant fails
	 * @param invariant will be checked after each successful invocation of {@linkplain #run(Object)}
	 * @return the same sequence instance
	 *
	 * @see	#withInvariant(Invariant)
	 */
	@API(status = MAINTAINED, since = "1.4.0")
	ActionSequence<M> withInvariant(@Nullable String label, Invariant<M> invariant);

	M finalModel();

	RunState runState();

	/**
	 * Peek into the model of a running sequence.
	 * The {@code modelPeeker} will be called after each successful
	 * invocation of {@linkplain #run(Object)} but before checking invariants.
	 *
	 * @param modelPeeker
	 * @return the same sequence instance
	 */
	@API(status = EXPERIMENTAL, since = "1.2.5")
	ActionSequence<M> peek(Consumer<M> modelPeeker);

}
