package net.jqwik.api.stateful;

import java.util.*;

public interface ActionSequence<M> {
	List<Action<M>> sequence();

	void run(M model);
}
