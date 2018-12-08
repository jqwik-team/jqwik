package net.jqwik.engine.properties.stateful;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

class ActionSequenceGenerator<M> implements RandomGenerator<ActionSequence<M>> {
	private final int genSize;
	private final int minSize;
	private final int maxSize;
	private final Arbitrary<Action<M>> actionArbitrary;

	ActionSequenceGenerator(Arbitrary<Action<M>> actionArbitrary, int genSize, int minSize, int maxSize) {
		this.actionArbitrary = actionArbitrary;
		this.genSize = genSize;
		this.minSize = minSize;
		this.maxSize = maxSize;
	}

	@Override
	public Shrinkable<ActionSequence<M>> next(Random random) {
		ActionGenerator<M> actionGenerator = new RandomActionGenerator<>(actionArbitrary, genSize, random);
		return new ShrinkableActionSequence<>(actionGenerator, minSize, maxSize, ShrinkingDistance.of(maxSize));
	}

}
