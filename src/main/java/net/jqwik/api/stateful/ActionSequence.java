package net.jqwik.api.stateful;

import java.util.*;

public interface ActionSequence<M> {
	List<Action<M>> sequence();

	M run(M model);

	ActionSequence<M> withInvariant(Invariant<M> invariant);

	int size();
}
