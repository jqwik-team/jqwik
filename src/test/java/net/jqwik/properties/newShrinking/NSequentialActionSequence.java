package net.jqwik.properties.newShrinking;

import net.jqwik.*;
import net.jqwik.api.stateful.*;
import net.jqwik.properties.stateful.*;
import net.jqwik.support.*;
import org.opentest4j.*;

import java.util.*;
import java.util.stream.*;

public class NSequentialActionSequence<M> implements ActionSequence<M> {

	private final List<Action<M>> candidateSequence;
	private final List<Try<M>> tries = new ArrayList<>();
	private final List<Invariant<M>> invariants = new ArrayList<>();

	private boolean hasRun = false;
	private M state = null;

	NSequentialActionSequence(List<Action<M>> candidateSequence) {
		this.candidateSequence = candidateSequence;
	}

	@Override
	public synchronized List<Action<M>> sequence() {
		if (!hasRun) {
			throw new JqwikException("Sequence has not run yet.");
		}
		return tries //
			.stream() //
			.filter(Try::preconditionValid) //
			.map(Try::action) //
			.collect(Collectors.toList());
	}

	@Override
	public synchronized M run(M model) {
		tries.clear();
		hasRun = true;
		try {
			state = tryAllCandidates(model);
			return state;
		} catch (InvariantFailedError ife) {
			throw ife;
		} catch (Throwable t) {
			AssertionFailedError assertionFailedError = new AssertionFailedError(createErrorMessage("Run"), t);
			assertionFailedError.setStackTrace(t.getStackTrace());
			throw assertionFailedError;
		}
	}

	@Override
	public ActionSequence<M> withInvariant(Invariant<M> invariant) {
		invariants.add(invariant);
		return this;
	}

	@Override
	public int size() {
		return candidateSequence.size();
	}

	@Override
	public M state() {
		if (!hasRun) {
			throw new JqwikException("Sequence has not run yet.");
		}

		return state;
	}

	@Override
	public String toString() {
		String stateString = "";
		List<Action<M>> actionsToShow;
		if (hasRun) {
			stateString = "(after run)";
			actionsToShow = sequence();
		} else {
			stateString = "(before run)";
			actionsToShow = candidateSequence;
		}
		String actionsString = JqwikStringSupport.displayString(actionsToShow);
		return String.format("%s %s:%s", this.getClass().getSimpleName(), stateString, actionsString);
	}

	private M tryAllCandidates(M model) {
		state = model;
		for (Action<M> candidate : candidateSequence) {
			tryNextCandidate(candidate);
		}
		return state;
	}

	private void tryNextCandidate(Action<M> candidate) {
		Try<M> aTry = new Try<M>(candidate);
		tries.add(aTry);
		state = aTry.run(state);
		if (aTry.preconditionValid()) {
			checkInvariants();
		}
	}

	private void checkInvariants() {
		try {
			for (Invariant<M> invariant : invariants) {
				invariant.check(state);
			}
		} catch (Throwable t) {
			throw new InvariantFailedError(createErrorMessage("Invariant"), t);
		}
	}

	private String createErrorMessage(String name) {
		String actionsString = tries
			.stream() //
			.map(aTry -> "    " + aTry.toString()) //
			.collect(Collectors.joining(System.lineSeparator()));
		return String.format(
			"%s failed after following actions:%n%s%n  final state: %s",
			name,
			actionsString,
			JqwikStringSupport.displayString(state)
		);
	}

	private static class Try<M> {

		private final Action<M> action;
		private boolean preconditionValid = false;

		public Try(Action<M> action) {
			this.action = action;
		}

		public M run(M model) {
			if (action.precondition(model)) {
				preconditionValid = true;
				return action.run(model);
			}
			return model;
		}

		public boolean preconditionValid() {
			return preconditionValid;
		}

		public Action<M> action() {
			return action;
		}

		@Override
		public String toString() {
			String precondition = preconditionValid ? "" : " (precondition failed)";
			return String.format("%s%s", action.toString(), precondition);
		}
	}

}
