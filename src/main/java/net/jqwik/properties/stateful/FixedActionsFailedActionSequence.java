package net.jqwik.properties.stateful;

import java.io.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.support.*;

class FixedActionsFailedActionSequence<M> extends AbstractActionSequence<M> implements Externalizable {

	private List<Action<M>> listOfActions;

	FixedActionsFailedActionSequence(List<Action<M>> listOfActions) {
		super(listOfActions);
		this.runState = RunState.FAILED;
		this.listOfActions = listOfActions;
		this.sequence = listOfActions;
	}

	// Needed for deserialization
	public FixedActionsFailedActionSequence() {
		super(new ArrayList<>());
		this.listOfActions = new ArrayList<>();
	}

	@Override
	public String toString() {
		String actionsString = JqwikStringSupport.displayString(actions);
		return String.format("ActionSequence: %s", actionsString);
	}

	@Override
	public synchronized M run(M model) {
		runState = RunState.NOT_RUN;
		sequence.clear();
		return super.run(model);
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(listOfActions);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.listOfActions = (List<Action<M>>) in.readObject();
		this.runState = RunState.FAILED;
		this.actions = this.listOfActions;
		this.sequence = this.listOfActions;
	}

	private static <M> ActionGenerator createGenerator(List<Action<M>> listOfActions) {
		Iterator<Action<M>> iterator = listOfActions.iterator();
		return new ActionGenerator() {
			private List<Shrinkable<Action>> generated = new ArrayList<>();

			@Override
			public Action next(Object model) {
				if (iterator.hasNext()) {
					Action<M> action = iterator.next();
					generated.add(Shrinkable.unshrinkable(action));
				}
				throw new NoSuchElementException("No more actions available");
			}

			@Override
			public List<Shrinkable<Action>> generated() {
				return generated;
			}
		};
	}

}
