package net.jqwik.api.stateful;

import java.util.*;

public interface ActionSequence<M> {
	List<Action<M>> runSequence();

	M run(M model);

	ActionSequence<M> withInvariant(Invariant<M> invariant);

	int size();

	M state();
}
