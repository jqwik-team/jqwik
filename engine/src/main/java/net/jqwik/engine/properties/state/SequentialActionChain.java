package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.jetbrains.annotations.*;
import org.opentest4j.*;

import net.jqwik.api.*;
import net.jqwik.api.state.*;
import net.jqwik.engine.support.*;

public class SequentialActionChain<T> implements ActionChain<T> {
	private final Chain<T> chain;

	private volatile T currentValue = null;
	private volatile RunningState currentRunning = RunningState.NOT_RUN;
	private final List<Consumer<T>> peekers = new ArrayList<>();
	private final List<Tuple.Tuple2<String, Consumer<T>>> invariants = new ArrayList<>();

	public SequentialActionChain(Chain<T> chain) {
		this.chain = chain;
	}

	@Override
	@NotNull
	public List<String> transformations() {
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
			callPeekers();
			checkInvariants();
		} catch (InvariantFailedError ife) {
			currentRunning = RunningState.FAILED;
			throw ife;
		} catch (TestAbortedException testAbortedException) {
			throw testAbortedException;
		} catch (Throwable t) {
			currentRunning = RunningState.FAILED;
			AssertionFailedError assertionFailedError = new AssertionFailedError(createErrorMessage("Run", t.getMessage()), t);
			assertionFailedError.setStackTrace(t.getStackTrace());
			throw assertionFailedError;
		}
	}

	private void callPeekers() {
		for (Consumer<T> peeker : peekers) {
			peeker.accept(currentValue);
		}
	}

	private void checkInvariants() {
		for (Tuple.Tuple2<String, Consumer<T>> tuple : invariants) {
			String label = tuple.get1();
			Consumer<T> invariant = tuple.get2();
			try {
				invariant.accept(currentValue);
			} catch (Throwable t) {
				throw new InvariantFailedError(createErrorMessage(label, t.getMessage()), t);
			}
		}
	}

	private String createErrorMessage(String name, String causeMessage) {
		String actionsString = transformations()
			.stream()
			.map(transformation -> "    " + transformation)
			.collect(Collectors.joining(System.lineSeparator()));
		return String.format(
			"%s failed after the following actions: [%s]%nfinal state: %s%n%s",
			name,
			actionsString.isEmpty() ? "" : String.format("%n%s  %n", actionsString),
			JqwikStringSupport.displayString(currentValue),
			causeMessage
		);
	}

	@Override
	@NotNull
	public ActionChain<T> withInvariant(@Nullable String label, Consumer<T> invariant) {
		String invariantLabel = label == null ? "Invariant" : String.format("Invariant '%s'", label);
		invariants.add(Tuple.of(invariantLabel, invariant));
		return this;
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
	public synchronized ActionChain<T> peek(@NotNull Consumer<T> peeker) {
		peekers.add(peeker);
		return this;
	}

	@Override
	public String toString() {
		if (running() == RunningState.NOT_RUN) {
			return String.format("ActionChain[%s]: %s max actions", running().name(), chain.maxTransformations());
		}
		String actionsString = JqwikStringSupport.displayString(transformations());
		return String.format("ActionChain[%s]: %s", running().name(), actionsString);
	}

	// This implementation is there to enable jqwik's after execution reporting
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SequentialActionChain<?> that = (SequentialActionChain<?>) o;
		return currentRunning == that.currentRunning;
	}

	@Override
	public int hashCode() {
		return Objects.hash(currentValue, currentRunning);
	}
}
