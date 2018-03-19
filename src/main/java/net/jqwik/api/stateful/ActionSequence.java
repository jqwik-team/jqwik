package net.jqwik.api.stateful;

import java.util.*;

public interface ActionSequence<M> {
	List<Action<M>> sequence();

	M run(M model);
}
