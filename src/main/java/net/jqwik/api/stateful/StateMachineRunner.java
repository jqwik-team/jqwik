package net.jqwik.api.stateful;

import net.jqwik.api.*;

import java.util.*;

public interface StateMachineRunner<M> {
	List<Shrinkable<Action<M>>> runSequence();

	void run();
}
