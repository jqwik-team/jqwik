package net.jqwik.api.stateful;

import java.util.*;

/**
 * A sequence of Actions that can be run with a model of type {@code M}
 *
 * @param <M> The type of the model
 */
public interface ActionSequence<M> {

	enum RunState {
		NOT_RUN, RUNNING, FAILED, SUCCEEDED
	}

	List<Action<M>> runActions();

	M run(M model);

	ActionSequence<M> withInvariant(Invariant<M> invariant);

	M finalModel();

	RunState runState();
}
