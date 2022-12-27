package net.jqwik.engine.properties.stateful;

import net.jqwik.api.*;
import net.jqwik.api.JqwikRandom;
import net.jqwik.api.stateful.*;

class ActionSequenceGenerator<M> implements RandomGenerator<ActionSequence<M>> {
	private final int genSize;
	private final int maxSize;
	private final Arbitrary<Action<M>> actionArbitrary;

	ActionSequenceGenerator(Arbitrary<Action<M>> actionArbitrary, int genSize, int maxSize) {
		this.actionArbitrary = actionArbitrary;
		this.genSize = genSize;
		this.maxSize = maxSize;
	}

	@Override
	public Shrinkable<ActionSequence<M>> next(JqwikRandom random) {
		ActionGenerator<M> actionGenerator = new RandomActionGenerator<>(actionArbitrary, genSize, random);
		return new ShrinkableActionSequence<>(actionGenerator, maxSize, ShrinkingDistance.of(maxSize));
	}

}
