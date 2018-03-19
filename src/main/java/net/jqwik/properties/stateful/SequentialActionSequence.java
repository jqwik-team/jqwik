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

	List<Shrinkable<Action<M>>> sequenceToShrink() {
		if (hasRun)
			return runSequence;
		return candidateSequence;
	}

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
		if (hasRun) {
			runSequence.clear();
		}

		runSequence.clear();
		hasRun = true;
		try {
			for (Shrinkable<Action<M>> candidate : candidateSequence) {
				Action<M> action = candidate.value();
				if (action.precondition(model)) {
					runSequence.add(candidate);
					model = action.run(model);
				}
			}
		} catch (Throwable t) {
			String actionsString = extractValues(runSequence)
				.stream() //
				.map(action -> "    " + action.toString()) //
				.collect(Collectors.joining(System.lineSeparator()));
			String message = String.format(
				"Run failed with following actions:%s%s%s  model state: %s",
				System.lineSeparator(),
				actionsString,
				System.lineSeparator(),
				JqwikStringSupport.displayString(model)
			);
			throw new AssertionFailedError(message, t);
		}

		return model;
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

	private List<Action<M>> extractValues(List<Shrinkable<Action<M>>> shrinkables) {
		return shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
	}
}
