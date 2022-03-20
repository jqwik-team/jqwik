package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.jetbrains.annotations.*;
import org.opentest4j.*;

import net.jqwik.api.state.*;
import net.jqwik.engine.support.*;

public class SequentialActionChain<T> implements ActionChain<T> {
	private final Chain<T> chain;

	private volatile T currentValue = null;
	private volatile RunningState currentRunning = RunningState.NOT_RUN;

	public SequentialActionChain(Chain<T> chain) {
		this.chain = chain;
	}

	@Override
	@NotNull
	public List<String> runActions() {
		return chain.transformations();
	}

	@Override
	@NotNull
	public synchronized T run() {
		currentRunning = RunningState.RUNNING;
		for (Iterator<T> iterator = chain.iterator(); iterator.hasNext(); ) {
			nextAction(iterator);
		}
		currentRunning = RunningState.SUCCEEDED;
		return currentValue;
	}

	private void nextAction(Iterator<T> iterator) {
		try {
			T state = iterator.next();
			currentValue = state;
			// callModelPeekers();
			// checkInvariants();
			// } catch (InvariantFailedError ife) {
			// 	currentRunning = RunningState.FAILED;
			// 	throw ife;
		} catch (Throwable t) {
			currentRunning = RunningState.FAILED;
			AssertionFailedError assertionFailedError = new AssertionFailedError(createErrorMessage("Run", t.getMessage()), t);
			assertionFailedError.setStackTrace(t.getStackTrace());
			throw assertionFailedError;
		}
	}

	// private void callModelPeekers() {
	// 	for (Consumer<M> peeker : peekers) {
	// 		peeker.accept(currentModel);
	// 	}
	// }
	//
	// private void checkInvariants() {
	// 	for (Tuple.Tuple2<String, Invariant<M>> tuple : invariants) {
	// 		String label = tuple.get1();
	// 		Invariant<M> invariant = tuple.get2();
	// 		try {
	// 			invariant.check(currentModel);
	// 		} catch (Throwable t) {
	// 			String invariantLabel = label == null ? "Invariant" : String.format("Invariant '%s'", label);
	// 			throw new InvariantFailedError(createErrorMessage(invariantLabel, t.getMessage()), t);
	// 		}
	// 	}
	// }

	private String createErrorMessage(String name, String causeMessage) {
		String actionsString = runActions()
			.stream()
			.map(aTry -> "    " + aTry)
			.collect(Collectors.joining(System.lineSeparator()));
		return String.format(
			"%s failed after following actions:%n%s%n  final state: %s%n%s",
			name,
			actionsString,
			JqwikStringSupport.displayString(currentValue),
			causeMessage
		);
	}

	@Override
	@NotNull
	public ActionChain<T> withInvariant(@Nullable String label, Consumer<T> invariant) {
		return null;
	}

	@Override
	@NotNull
	public synchronized Optional<T> finalState() {
		return Optional.ofNullable(currentValue);
	}

	@Override
	@NotNull
	public ActionChain.RunningState running() {
		return currentRunning;
	}

	@Override
	@NotNull
	public ActionChain<T> peek(@NotNull Consumer<T> peeker) {
		return null;
	}
}
