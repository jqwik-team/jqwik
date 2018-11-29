package net.jqwik.properties.stateful;

import java.io.*;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.support.*;

class FixedActionsFailedActionSequence<M> extends SequentialActionSequence<M> implements Externalizable {

	private List<Action<M>> listOfActions;

	FixedActionsFailedActionSequence(List<Action<M>> listOfActions) {
		super(createGenerator(listOfActions), listOfActions.size());
		this.runState = RunState.FAILED;
		this.listOfActions = listOfActions;
		this.sequence = listOfActions;
	}

	// Needed for deserialization
	public FixedActionsFailedActionSequence() {
		super(createGenerator(new ArrayList<>()), 1);
		this.listOfActions = new ArrayList<>();
	}

	@Override
	public String toString() {
		String actionsString = JqwikStringSupport.displayString(runActions());
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
		this.actionGenerator = createGenerator(this.listOfActions);
		this.intendedSize = this.listOfActions.size();
		this.sequence = this.listOfActions;
	}

	private static <M> ActionGenerator<M> createGenerator(List<Action<M>> listOfActions) {
		Iterator<Action<M>> iterator = listOfActions.iterator();
		return new ActionGenerator<M>() {
			private List<Shrinkable<Action<M>>> generated = new ArrayList<>();

			@Override
			public Action<M> next(M model) {
				if (iterator.hasNext()) {
					Action<M> action = iterator.next();
					generated.add(Shrinkable.unshrinkable(action));
				}
				throw new NoSuchElementException("No more actions available");
			}

			@Override
			public List<Shrinkable<Action<M>>> generated() {
				return generated;
			}
		};
	}

}
