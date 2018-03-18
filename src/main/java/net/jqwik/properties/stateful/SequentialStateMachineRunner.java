package net.jqwik.properties.stateful;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.support.*;
import org.opentest4j.*;

import java.util.*;
import java.util.stream.*;

public class SequentialStateMachineRunner<M> implements StateMachineRunner<M> {

	public static <M> Arbitrary<StateMachineRunner<M>> arbitrary(Class<? extends StateMachine<M>> stateMachineClass) {
		StateMachine<M> stateMachine = JqwikReflectionSupport.newInstanceWithDefaultConstructor(stateMachineClass);
		return genSize -> new StateMachineGenerator<>(stateMachine, genSize);
	}

	private final StateMachine<M> stateMachine;
	private final List<Shrinkable<Action<M>>> candidateSequence;
	private final List<Shrinkable<Action<M>>> runSequence = new ArrayList<>();

	private boolean hasRun = false;

	SequentialStateMachineRunner(StateMachine<M> stateMachine, List<Shrinkable<Action<M>>> candidateSequence) {
		this.stateMachine = stateMachine;
		this.candidateSequence = candidateSequence;
	}

	public StateMachine<M> getStateMachine() {
		return stateMachine;
	}

	@Override
	public List<Shrinkable<Action<M>>> runSequence() {
		if (!hasRun) {
			throw new JqwikException(String.format("State machine %s has not run yet.", stateMachine));
		}
		return runSequence;
	}

	@Override
	public synchronized void run() {
		if (hasRun) {
			runSequence.clear();
		}

		M model = stateMachine.createModel();
		runSequence.clear();
		hasRun = true;
		try {
			for (Shrinkable<Action<M>> candidate : candidateSequence) {
				Action<M> action = candidate.value();
				if (action.precondition(model)) {
					runSequence.add(candidate);
					action.run(model);
				}
			}
		} catch (Throwable t) {
			String actionsString = extractValues(runSequence)
				.stream() //
				.map(action -> "   " + action.toString()) //
				.collect(Collectors.joining(System.lineSeparator()));
			String message = String.format(
				"State machine [%s] failed with following actions:%s%s",
				stateMachine.getClass().getSimpleName(),
				System.lineSeparator(), actionsString
			);
			throw new AssertionFailedError(message, t);
		}
	}

	@Override
	public String toString() {
		String stateString = "";
		List<Shrinkable<Action<M>>> actionsToShow = runSequence;
		if (!hasRun) {
			stateString = "(not yet run)";
			actionsToShow = candidateSequence;
		}
		String actionsString = JqwikStringSupport.displayString(extractValues(actionsToShow));
		return String.format("%s%s:%s", this.getClass().getSimpleName(), stateString, actionsString);
	}

	private List<Action> extractValues(List<Shrinkable<Action<M>>> shrinkables) {
		return shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
	}
}
