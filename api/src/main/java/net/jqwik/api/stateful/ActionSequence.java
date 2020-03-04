package net.jqwik.api.stateful;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * A sequence of Actions that can be run with a model of type {@code M}
 *
 * @param <M> The type of the model
 */
@API(status = MAINTAINED, since = "1.0")
public interface ActionSequence<M> {

	enum RunState {
		NOT_RUN, RUNNING, FAILED, SUCCEEDED
	}

	List<Action<M>> runActions();

	M run(M model);

	ActionSequence<M> withInvariant(Invariant<M> invariant);

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
