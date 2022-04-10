package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.state.*;

class ShrinkableChainIteration<T> {
	// TODO: Remove randomSeed
	final long randomSeed;
	final boolean stateHasBeenAccessed_OLD;
	final Shrinkable<Transformer<T>> shrinkable;
	private final Predicate<T> precondition;
	final boolean accessState;
	final boolean changeState;

	ShrinkableChainIteration(
		long randomSeed,
		boolean stateHasBeenAccessed_OLD,
		Predicate<T> precondition,
		boolean accessState,
		Shrinkable<Transformer<T>> shrinkable
	) {
		// By default transformers are considered to change the state.
		this(randomSeed, stateHasBeenAccessed_OLD, precondition, accessState, shrinkable, true);
	}

	private ShrinkableChainIteration(
		long randomSeed,
		boolean stateHasBeenAccessed_OLD,
		Predicate<T> precondition,
		boolean accessState,
		Shrinkable<Transformer<T>> shrinkable,
		boolean changeState
	) {
		this.randomSeed = randomSeed;
		this.stateHasBeenAccessed_OLD = stateHasBeenAccessed_OLD;
		this.precondition = precondition;
		this.accessState = accessState;
		this.changeState = changeState;
		this.shrinkable = shrinkable;
	}

	@Override
	public String toString() {
		return String.format(
			"Iteration[accessState=%s, changeState=%s, transformation=%s]",
			stateHasBeenAccessed_OLD, changeState, shrinkable.value().transformation()
		);
	}

	boolean isEndOfChain() {
		return shrinkable.value().equals(Transformer.END_OF_CHAIN);
	}

	Optional<Predicate<T>> precondition() {
		return Optional.ofNullable(precondition);
	}

	ShrinkableChainIteration<T> withShrinkable(Shrinkable<Transformer<T>> shrinkable) {
		return new ShrinkableChainIteration<>(randomSeed, stateHasBeenAccessed_OLD, precondition, accessState, shrinkable, changeState);
	}

	ShrinkableChainIteration<T> withStateChange(boolean stateHasBeenChanged) {
		if (this.changeState == stateHasBeenChanged) {
			return this;
		}
		return new ShrinkableChainIteration<>(randomSeed, stateHasBeenAccessed_OLD,  precondition, accessState, shrinkable, stateHasBeenChanged);
	}
}
