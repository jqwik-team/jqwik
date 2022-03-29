package net.jqwik.engine.properties.state;

import net.jqwik.api.*;
import net.jqwik.api.state.*;

class ShrinkableChainIteration<T> {
	final long randomSeed;
	final boolean stateHasBeenAccessed;
	final Shrinkable<Transformer<T>> shrinkable;
	final boolean stateHasBeenChanged;

	ShrinkableChainIteration(
		long randomSeed,
		boolean stateHasBeenAccessed,
		Shrinkable<Transformer<T>> shrinkable
	) {
		// By default transformers are considered to change the state.
		this(randomSeed, stateHasBeenAccessed, shrinkable, true);
	}

	private ShrinkableChainIteration(
		long randomSeed,
		boolean stateHasBeenAccessed,
		Shrinkable<Transformer<T>> shrinkable,
		boolean stateHasBeenChanged
	) {
		this.randomSeed = randomSeed;
		this.stateHasBeenAccessed = stateHasBeenAccessed;
		this.stateHasBeenChanged = stateHasBeenChanged;
		this.shrinkable = shrinkable;
	}

	@Override
	public String toString() {
		return String.format(
			"Iteration[accessState=%s, changeState=%s, transformation=%s]",
			stateHasBeenAccessed, stateHasBeenChanged, shrinkable.value().transformation()
		);
	}

	ShrinkableChainIteration<T> withShrinkable(Shrinkable<Transformer<T>> shrinkable) {
		return new ShrinkableChainIteration<>(randomSeed, stateHasBeenAccessed, shrinkable, stateHasBeenChanged);
	}

	ShrinkableChainIteration<T> withoutStateChange() {
		return new ShrinkableChainIteration<>(randomSeed, stateHasBeenAccessed, shrinkable, false);
	}
}
