package net.jqwik.properties.stateful;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;
import net.jqwik.properties.arbitraries.*;

import java.util.*;
import java.util.stream.*;

public class StateMachineGenerator<M> implements RandomGenerator<StateMachineRunner<M>> {
	private final StateMachine<M> stateMachine;
	private final int genSize;
	private final int numberOfActions;

	public StateMachineGenerator(StateMachine<M> stateMachine, int genSize) {
		this.stateMachine = stateMachine;
		this.genSize = genSize;
		this.numberOfActions = calculateNumberOfActions(genSize);
	}

	private int calculateNumberOfActions(int genSize) {
		return (int) Math.max(Math.round(Math.sqrt(genSize)), 10);
	}

	@Override
	public Shrinkable<StateMachineRunner<M>> next(Random random) {
		List<Arbitrary<Action<M>>> arbitraries = stateMachine.actions();
		List<RandomGenerator<Action<M>>> generators = arbitraries.stream().map(arbitrary -> arbitrary.generator(genSize)).collect(Collectors.toList());
		List<Shrinkable<Action<M>>> candidateActions = generateCandidates(generators, numberOfActions, random);
		SequentialStateMachineRunner<M> stateMachineRunner = new SequentialStateMachineRunner<>(stateMachine, candidateActions);
		return new ShrinkableValue<>(stateMachineRunner, new StateMachineRunnerShrinkCandidates<>());
	}

	private List<Shrinkable<Action<M>>> generateCandidates(List<RandomGenerator<Action<M>>> generators, int numberOfActions, Random random) {
		List<Shrinkable<Action<M>>> candidates = new ArrayList<>();
		for (int i = 0; i < numberOfActions; i++) {
			Shrinkable<Action<M>> next = RandomGenerators
					.chooseValue(generators, random)
					.sampleRandomly(random);
			candidates.add(next);
		}
		return candidates;
	}

}
