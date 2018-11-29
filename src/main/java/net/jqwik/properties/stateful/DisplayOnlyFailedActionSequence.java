package net.jqwik.properties.stateful;

import java.io.*;
import java.util.*;

import net.jqwik.api.stateful.*;
import net.jqwik.support.*;

class DisplayOnlyFailedActionSequence<M> implements ActionSequence<M>, Serializable {

	private final List<Action<M>> listOfActions;

	DisplayOnlyFailedActionSequence(List<Action<M>> listOfActions) {
		// Make sure listOfActions is serializable
		this.listOfActions = Collections.unmodifiableList(listOfActions);
	}

	@Override
	public List<Action<M>> runActions() {
		return listOfActions;
	}

	@Override
	public M run(M model) {
		throw new UnsupportedOperationException("This is a display only action sequence");
	}

	@Override
	public ActionSequence<M> withInvariant(Invariant<M> invariant) {
		throw new UnsupportedOperationException("This is a display only action sequence");
	}

	@Override
	public M finalModel() {
		throw new UnsupportedOperationException("This is a display only action sequence");
	}

	@Override
	public RunState runState() {
		return RunState.FAILED;
	}

	@Override
	public String toString() {
		String actionsString = JqwikStringSupport.displayString(listOfActions);
		return String.format("ActionSequence[%s]: %s", runState().name(), actionsString);
	}

}
