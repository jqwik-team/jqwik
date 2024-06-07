package net.jqwik.engine.properties.stateful;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.jspecify.annotations.*;
import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.stateful.*;
import net.jqwik.engine.support.*;

class SequentialActionSequence<M extends @Nullable Object> implements ActionSequence<M> {

	protected ActionGenerator<M> actionGenerator;
	protected int intendedSize;
	protected final List<Action<M>> sequence = new ArrayList<>();
	private final List<Tuple2<String, Invariant<M>>> invariants = new ArrayList<>();
	private final List<Consumer<M>> peekers = new ArrayList<>();

	protected RunState runState = RunState.NOT_RUN;
	private @Nullable M currentModel = null;

	SequentialActionSequence(ActionGenerator<M> actionGenerator, int intendedSize) {
		if (intendedSize < 1) {
			throw new IllegalArgumentException("The intended size of an ActionSequence must not be 0");
		}
		this.actionGenerator = actionGenerator;
		this.intendedSize = intendedSize;
	}

	@Override
	public synchronized List<Action<M>> runActions() {
		return sequence;
	}

	@Override
	public synchronized M run(M model) {
		currentModel = model;
		if (runState == RunState.NOT_RUN) {
			initialRun();
		} else {
			repeatedRun();
		}
		if (sequence.isEmpty()) {
			throw new JqwikException("Sequences without actions are invalid");
		}
		runState = RunState.SUCCEEDED;
		return currentModel;
	}

	private void initialRun() {
		runState = RunState.RUNNING;
		for (int i = 0; i < intendedSize; i++) {
			Action<M> action;
			try {
				action = actionGenerator.next(currentModel);
			} catch (NoSuchElementException nsee) {
				break;
			}
			sequence.add(action);
			runAction(action);
		}
	}

	private void repeatedRun() {
		runState = RunState.RUNNING;
		for (Action<M> action : new ArrayList<>(sequence)) {
			if (action.precondition(currentModel)) {
				runAction(action);
			} else {
				throw new TestAbortedException("Precondition violated on repeated run");
			}
		}
	}

	private void runAction(final Action<M> action) {
		try {
			currentModel = action.run(currentModel);
			callModelPeekers();
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

	private void callModelPeekers() {
		for (Consumer<M> peeker : peekers) {
			peeker.accept(currentModel);
		}
	}

	private void checkInvariants() {
		for (Tuple2<String, Invariant<M>> tuple : invariants) {
			String label = tuple.get1();
			Invariant<M> invariant = tuple.get2();
			try {
				invariant.check(currentModel);
			} catch (Throwable t) {
				String invariantLabel = label == null ? "Invariant" : String.format("Invariant '%s'", label);
				throw new InvariantFailedError(createErrorMessage(invariantLabel, t.getMessage()), t);
			}
		}
	}

	private String createErrorMessage(String name, String causeMessage) {
		String actionsString = sequence
								   .stream()
								   .map(aTry -> "    " + aTry.toString())
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
	public synchronized ActionSequence<M> withInvariant(String label, Invariant<M> invariant) {
		invariants.add(Tuple.of(label, invariant));
		return this;
	}

	@Override
	public synchronized ActionSequence<M> peek(Consumer<M> modelPeeker) {
		peekers.add(modelPeeker);
		return this;
	}

	@Override
	public RunState runState() {
		return runState;
	}

	@Override
	public int size() {
		if (runState == RunState.NOT_RUN) {
			return intendedSize;
		} else {
			return actionGenerator.generated().size();
		}
	}

	@Override
	public synchronized M finalModel() {
		return currentModel;
	}

	@Override
	public String toString() {
		if (runState == RunState.NOT_RUN) {
			return String.format("ActionSequence[%s]: %s actions intended", runState.name(), intendedSize);
		}
		String actionsString = JqwikStringSupport.displayString(sequence);
		return String.format("ActionSequence[%s]: %s", runState.name(), actionsString);
	}
}
