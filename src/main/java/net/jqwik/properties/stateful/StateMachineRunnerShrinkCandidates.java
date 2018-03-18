package net.jqwik.properties.stateful;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.properties.arbitraries.*;

import java.util.*;
import java.util.stream.*;

public class StateMachineRunnerShrinkCandidates<M> implements ShrinkCandidates<StateMachineRunner<M>> {

	@Override
	public Set<StateMachineRunner<M>> nextCandidates(StateMachineRunner<M> value) {
		Set<StateMachineRunner<M>> shrunkSequence = shrinkSequence(value);
		// TODO: Shrink actions if sequence cannot be shrunk any more
		return shrunkSequence;
	}

	private Set<StateMachineRunner<M>> shrinkSequence(StateMachineRunner<M> stateMachineRunner) {
		SequentialStateMachineRunner<M> sequentialRunner = (SequentialStateMachineRunner<M>) stateMachineRunner;

		Set<List<Shrinkable<Action<M>>>> setOfSequences = shrinkActions(sequentialRunner.runSequence());
		return setOfSequences.stream() //
							 .map(seq -> (StateMachineRunner<M>) new SequentialStateMachineRunner<>(sequentialRunner.getStateMachine(), seq)) //
							 .collect(Collectors.toSet());
	}

	private Set<List<Shrinkable<Action<M>>>> shrinkActions(List<Shrinkable<Action<M>>> sequence) {
		if (sequence.size() <= 1) {
			return Collections.emptySet();
		}
		Set<List<Shrinkable<Action<M>>>> setOfSequences = new HashSet<>();
		for (int i = 0; i < sequence.size(); i++) {
			ArrayList<Shrinkable<Action<M>>> newCandidate = new ArrayList<>(sequence);
			newCandidate.remove(i);
			setOfSequences.add(newCandidate);
		}
		return setOfSequences;
	}

	@Override
	public int distance(StateMachineRunner<M> stateMachineRunner) {
		return stateMachineRunner.runSequence().stream().mapToInt(Shrinkable::distance).sum();
	}
}
