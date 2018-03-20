package net.jqwik.properties.stateful;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.support.*;
import org.opentest4j.*;

import java.util.*;
import java.util.stream.*;

public class SequentialActionSequence<M> implements ActionSequence<M> {

	public static <M> Arbitrary<ActionSequence<M>> fromActions(Arbitrary<Action<M>> actionArbitrary) {
		return genSize -> {
			RandomGenerator<Action<M>> actionGenerator = actionArbitrary.generator(genSize);
			return new ActionSequenceGenerator<>(actionGenerator, genSize);
		};
	}

	private final List<Shrinkable<Action<M>>> candidateSequence;
	private final List<Shrinkable<Action<M>>> runSequence = new ArrayList<>();
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
			throw new AssertionFailedError(createErrorMessage(model, "Run"), t);
		}
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
			checkInvariant(model);
		}
		return model;
	}

	private void checkInvariant(M model) {
		if (model instanceof Invariant) {
			Invariant invariant = (Invariant) model;
			try {
				if (!invariant.invariant()) {
					String name = String.format("Invariant in %s", model.getClass().getSimpleName());
					throw new InvariantFailedError(createErrorMessage(model, name));
				}
			} catch (InvariantFailedError ife) {
				throw ife;
			} catch (Throwable t) {
				String name = String.format("Invariant in %s", model.getClass().getSimpleName());
				throw new InvariantFailedError(createErrorMessage(model, name), t);
			}
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
}
