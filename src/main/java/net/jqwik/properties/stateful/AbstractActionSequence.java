package net.jqwik.properties.stateful;

import java.util.*;
import java.util.stream.*;

import org.opentest4j.*;

import net.jqwik.*;
import net.jqwik.api.stateful.*;
import net.jqwik.support.*;

// TODO: Remove duplication with SequentialActionSequence
abstract class AbstractActionSequence<M> implements ActionSequence<M> {

	protected Iterable<Action<M>> actions;
	protected List<Action<M>> sequence = new ArrayList<>();
	private final List<Invariant<M>> invariants = new ArrayList<>();

	protected RunState runState = RunState.NOT_RUN;
	private M currentModel = null;

	protected AbstractActionSequence(Iterable<Action<M>> actions) {
		this.actions = actions;
	}

	@Override
	public synchronized List<Action<M>> runActions() {
		return sequence;
	}

	@Override
	public synchronized M run(M model) {
		if (runState != RunState.NOT_RUN) {
			return currentModel;
		}
		runState = RunState.RUNNING;
		currentModel = model;

		for (Action<M> action : actions) {
			sequence.add(action);
			try {
				currentModel = action.run(currentModel);
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
		if (sequence.isEmpty()) {
			throw new JqwikException("Could not generated a single action. At least 1 is required.");
		}
		runState = RunState.SUCCEEDED;
		return currentModel;
	}

	private void checkInvariants() {
		try {
			for (Invariant<M> invariant : invariants) {
				invariant.check(currentModel);
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
			"%s failed after following actions:%n%s%n  final currentModel: %s%n%s",
			name,
			actionsString,
			JqwikStringSupport.displayString(currentModel),
			causeMessage
		);
	}

	@Override
	public synchronized ActionSequence<M> withInvariant(Invariant<M> invariant) {
		invariants.add(invariant);
		return this;
	}

	@Override
	public RunState runState() {
		return runState;
	}

	@Override
	public synchronized M finalModel() {
		return currentModel;
	}
}
