package net.jqwik.properties.stateful;

import java.util.*;
import java.util.stream.*;

import org.opentest4j.*;

import net.jqwik.api.stateful.*;
import net.jqwik.support.*;

public class NSequentialActionSequence <M> implements ActionSequence<M> {

	private final NActionGenerator<M> actionGenerator;
	private final int size;
	private final List<Action<M>> sequence = new ArrayList<>();
	private final List<Invariant<M>> invariants = new ArrayList<>();

	private RunState runState = RunState.NOT_RUN;
	private M state = null;

	public NSequentialActionSequence(NActionGenerator<M> actionGenerator, int size) {
		this.actionGenerator = actionGenerator;
		this.size = size;
	}

	@Override
	public synchronized List<Action<M>> runSequence() {
		return sequence;
	}

	@Override
	public synchronized M run(M model) {
		if (runState != RunState.NOT_RUN) {
			return state;
		}
		runState = RunState.RUNNING;
		state = model;
		for (int i = 0; i < size; i++) {
			Action<M> action = actionGenerator.next(state);
			sequence.add(action);
			try {
				state = action.run(state);
				checkInvariants();
			} catch (InvariantFailedError ife) {
				runState = RunState.FAILED;
				throw ife;
			} catch (Throwable t) {
				runState = RunState.FAILED;
				AssertionFailedError assertionFailedError = new AssertionFailedError(createErrorMessage("Run", t.getMessage()), t);
				assertionFailedError.setStackTrace(t.getStackTrace());
				throw assertionFailedError;
			}
		}
		runState = RunState.SUCCEEDED;
		return state;
	}

	private void checkInvariants() {
		try {
			for (Invariant<M> invariant : invariants) {
				invariant.check(state);
			}
		} catch (Throwable t) {
			throw new InvariantFailedError(createErrorMessage("Invariant", t.getMessage()), t);
		}
	}

	private String createErrorMessage(String name, String causeMessage) {
		String actionsString = sequence
			.stream() //
			.map(aTry -> "    " + aTry.toString()) //
			.collect(Collectors.joining(System.lineSeparator()));
		return String.format(
			"%s failed after following actions:%n%s%n  final state: %s%n%s",
			name,
			actionsString,
			JqwikStringSupport.displayString(state),
			causeMessage
		);
	}


	@Override
	public synchronized ActionSequence<M> withInvariant(Invariant<M> invariant) {
		invariants.add(invariant);
		return this;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public RunState runState() {
		return runState;
	}

	@Override
	public synchronized M state() {
		return state;
	}

	@Override
	public String toString() {
		String stateString = runState.name();
		String actionsString = JqwikStringSupport.displayString(sequence);
		return String.format("%s (%s) [%s]:%s", this.getClass().getSimpleName(), size, stateString, actionsString);
	}
}
