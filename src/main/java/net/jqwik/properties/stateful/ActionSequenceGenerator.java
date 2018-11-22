package net.jqwik.properties.stateful;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

class ActionSequenceGenerator<M> implements RandomGenerator<ActionSequence<M>> {
	private final int genSize;
	private final int numberOfActions;
	private final Arbitrary<Action<M>> actionArbitrary;

	ActionSequenceGenerator(Arbitrary<Action<M>> actionArbitrary, int genSize, int numberOfActions) {
		this.actionArbitrary = actionArbitrary;
		this.genSize = genSize;
		this.numberOfActions = numberOfActions;
	}

	@Override
	public Shrinkable<ActionSequence<M>> next(Random random) {
		ActionGenerator<M> actionGenerator = new RandomActionGenerator<>(actionArbitrary, genSize, random);
		return new ShrinkableActionSequence<>(actionGenerator, numberOfActions, ShrinkingDistance.of(numberOfActions));
	}

}
