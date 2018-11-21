package net.jqwik.properties.stateful;

import java.util.*;

import net.jqwik.api.stateful.*;

public class NSequentialActionSequence <M> implements ActionSequence<M> {

	private final NActionGenerator<M> actionGenerator;
	private final int numberOfActions;

	public NSequentialActionSequence(NActionGenerator<M> actionGenerator, int numberOfActions) {
		this.actionGenerator = actionGenerator;
		this.numberOfActions = numberOfActions;
	}

	@Override
	public List<Action<M>> runSequence() {
		return null;
	}

	@Override
	public M run(M model) {
		return null;
	}

	@Override
	public ActionSequence<M> withInvariant(Invariant<M> invariant) {
		return null;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public M state() {
		return null;
	}
}
