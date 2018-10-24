package net.jqwik.properties.stateful;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

import java.util.*;

class ActionSequenceGenerator<M> implements RandomGenerator<ActionSequence<M>> {
	private final RandomGenerator<Action<M>> actionGenerator;
	private final int numberOfActions;

	ActionSequenceGenerator(RandomGenerator<Action<M>> actionGenerator, int numberOfActions) {
		this.actionGenerator = actionGenerator;
		this.numberOfActions = numberOfActions;
	}

	@Override
	public Shrinkable<ActionSequence<M>> next(Random random) {
		List<Shrinkable<Action<M>>> candidateActions = generateCandidates(numberOfActions, random);
		return new ShrinkableActionSequence<>(candidateActions);
	}

	private List<Shrinkable<Action<M>>> generateCandidates(int numberOfActions, Random random) {
		List<Shrinkable<Action<M>>> candidates = new ArrayList<>();
		for (int i = 0; i < numberOfActions; i++) {
			candidates.add(actionGenerator.next(random));
		}
		return candidates;
	}

}
