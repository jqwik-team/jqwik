package net.jqwik.engine.properties.state;

import net.jqwik.api.*;
import net.jqwik.api.state.*;

class ShrinkableChainIteration<T> {
	final long randomSeed;
	final boolean stateHasBeenAccessed;
	final Shrinkable<Transformer<T>> shrinkable;

	ShrinkableChainIteration(
		long randomSeed,
		boolean stateHasBeenAccessed,
		Shrinkable<Transformer<T>> shrinkable
	) {
		this.randomSeed = randomSeed;
		this.stateHasBeenAccessed = stateHasBeenAccessed;
		this.shrinkable = shrinkable;
	}

	@Override
	public String toString() {
		return String.format("Iteration[accessState=%s, transformation=%s]", stateHasBeenAccessed, shrinkable.value().transformation());
	}
}
