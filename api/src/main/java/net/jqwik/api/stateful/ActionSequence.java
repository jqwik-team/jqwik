package net.jqwik.api.stateful;

import java.util.*;

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
}
