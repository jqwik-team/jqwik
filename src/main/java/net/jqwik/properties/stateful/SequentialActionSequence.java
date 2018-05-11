package net.jqwik.properties.stateful;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.properties.arbitraries.*;
import net.jqwik.support.*;
import org.opentest4j.*;

import java.util.*;
import java.util.stream.*;

public class SequentialActionSequence<M> implements ActionSequence<M> {

	public static <M> ActionSequenceArbitrary<M> fromActions(Arbitrary<Action<M>> actionArbitrary) {
		return new DefaultActionSequenceArbitrary<>(actionArbitrary);
	}

	private final List<Action<M>> candidateSequence;
	private final List<Try<M>> tries = new ArrayList<>();
	private final List<Invariant<M>> invariants = new ArrayList<>();

	private boolean hasRun = false;
	private M state = null;

	public SequentialActionSequence(List<Action<M>> candidateSequence) {
		this.candidateSequence = candidateSequence;
	}

	@Override
	public synchronized List<Action<M>> runSequence() {
		assertHasRun();
		return tries //
			.stream() //
			.filter(Try::preconditionValid) //
			.map(Try::action) //
			.collect(Collectors.toList());
	}

	private void assertHasRun() {
		if (!hasRun) {
			throw new JqwikException("Sequence has not run yet.");
		}
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
		assertHasRun();
		return state;
	}

	@Override
	public String toString() {
		String stateString = "";
		List<Action<M>> actionsToShow;
		if (hasRun) {
			stateString = "(after run)";
			actionsToShow = runSequence();
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

	private static class DefaultActionSequenceArbitrary<M> extends AbstractArbitraryBase implements ActionSequenceArbitrary<M> {

		private final Arbitrary<Action<M>> actionArbitrary;
		private int size = 0;

		DefaultActionSequenceArbitrary(Arbitrary<Action<M>> actionArbitrary) {
			this.actionArbitrary = actionArbitrary;
		}

		@Override
		public DefaultActionSequenceArbitrary<M> ofSize(int size) {
			DefaultActionSequenceArbitrary<M> clone = typedClone();
			clone.size = size;
			return clone;
		}

		@Override
		public RandomGenerator<ActionSequence<M>> generator(int genSize) {
			final int numberOfActions = //
				size != 0 ? size //
					: (int) Math.max(Math.round(Math.sqrt(genSize)), 10);
			RandomGenerator<Action<M>> actionGenerator = actionArbitrary.generator(genSize);
			return new ActionSequenceGenerator<>(actionGenerator, numberOfActions);
		}

	}
}
