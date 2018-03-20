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

	private final List<Shrinkable<Action<M>>> candidateSequence;
	private final List<Shrinkable<Action<M>>> runSequence = new ArrayList<>();
	private final List<Invariant<M>> invariants = new ArrayList<>();

	private boolean hasRun = false;

	SequentialActionSequence(List<Shrinkable<Action<M>>> candidateSequence) {
		this.candidateSequence = candidateSequence;
	}

	@Override
	public List<Action<M>> sequence() {
		if (!hasRun) {
			throw new JqwikException("Sequence has not run yet.");
		}
		return extractValues(runSequence);
	}

	@Override
	public synchronized M run(M model) {
		runSequence.clear();
		hasRun = true;
		try {
			model = tryAllCandidates(model);
			return model;
		} catch (InvariantFailedError ife) {
			throw ife;
		} catch (Throwable t) {
			AssertionFailedError assertionFailedError = new AssertionFailedError(createErrorMessage(model, "Run"), t);
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
	public String toString() {
		String stateString = "";
		List<Shrinkable<Action<M>>> actionsToShow = runSequence;
		if (!hasRun) {
			stateString = "(before run)";
			actionsToShow = candidateSequence;
		}
		String actionsString = JqwikStringSupport.displayString(extractValues(actionsToShow));
		return String.format("%s%s:%s", this.getClass().getSimpleName(), stateString, actionsString);
	}

	List<Shrinkable<Action<M>>> sequenceToShrink() {
		if (hasRun)
			return runSequence;
		return candidateSequence;
	}

	private M tryAllCandidates(M model) {
		for (Shrinkable<Action<M>> candidate : candidateSequence) {
			model = tryNextCandidate(model, candidate);
		}
		return model;
	}

	private M tryNextCandidate(M model, Shrinkable<Action<M>> candidate) {
		Action<M> action = candidate.value();
		if (action.precondition(model)) {
			runSequence.add(candidate);
			model = action.run(model);
			checkInvariants(model);
		}
		return model;
	}

	private void checkInvariants(M model) {
			try {
				for (Invariant<M> invariant : invariants) {
					invariant.check(model);
				}
			} catch (Throwable t) {
				String name = String.format("Invariant");
				throw new InvariantFailedError(createErrorMessage(model, name), t);
			}
	}

	private String createErrorMessage(M model, String name) {
		String actionsString = extractValues(runSequence)
			.stream() //
			.map(action -> "    " + action.toString()) //
			.collect(Collectors.joining(System.lineSeparator()));
		return String.format(
			"%s failed after following actions:%s%s%s  model state: %s",
			name,
			System.lineSeparator(),
			actionsString,
			System.lineSeparator(),
			JqwikStringSupport.displayString(model)
		);
	}

	private List<Action<M>> extractValues(List<Shrinkable<Action<M>>> shrinkables) {
		return shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
	}

	private static class DefaultActionSequenceArbitrary<M> extends AbstractArbitraryBase implements ActionSequenceArbitrary<M> {

		private final Arbitrary<Action<M>> actionArbitrary;
		private int size = 0;

		DefaultActionSequenceArbitrary(Arbitrary<Action<M>> actionArbitrary) {this.actionArbitrary = actionArbitrary;}

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
