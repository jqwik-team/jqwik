package net.jqwik.api.stateful;

import java.util.*;

public interface ActionSequence<M> {

	enum RunState {
		NOT_RUN, RUNNING, FAILED, SUCCEEDED
	}

	List<Action<M>> runSequence();

	M run(M model);

	ActionSequence<M> withInvariant(Invariant<M> invariant);

	int size();

	M state();

	// TODO: Remove default imple as soon as old implementation has gone
	default RunState runState() {
		return RunState.NOT_RUN;
	}
}
