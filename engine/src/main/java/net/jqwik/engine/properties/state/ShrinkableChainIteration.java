package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.state.*;

class ShrinkableChainIteration<T> {
	// TODO: Remove randomSeed
	final long randomSeed;
	final Shrinkable<Transformer<T>> shrinkable;
	private final Predicate<T> precondition;
	final boolean accessState;
	final boolean changeState;

	ShrinkableChainIteration(
		long randomSeed,
		Predicate<T> precondition,
		boolean accessState,
		Shrinkable<Transformer<T>> shrinkable
	) {
		// By default transformers are considered to change the state.
		this(randomSeed, precondition, accessState, true, shrinkable);
	}

	private ShrinkableChainIteration(
		long randomSeed,
		Predicate<T> precondition,
		boolean accessState,
		boolean changeState, Shrinkable<Transformer<T>> shrinkable
	) {
		this.randomSeed = randomSeed;
		this.precondition = precondition;
		this.accessState = accessState;
		this.changeState = changeState;
		this.shrinkable = shrinkable;
	}

	@Override
	public String toString() {
		return String.format(
			"Iteration[accessState=%s, changeState=%s, transformation=%s]",
			accessState, changeState, shrinkable.value().transformation()
		);
	}

	boolean isEndOfChain() {
		return shrinkable.value().equals(Transformer.END_OF_CHAIN);
	}

	Optional<Predicate<T>> precondition() {
		return Optional.ofNullable(precondition);
	}

	ShrinkableChainIteration<T> withShrinkable(Shrinkable<Transformer<T>> shrinkable) {
		return new ShrinkableChainIteration<>(randomSeed, precondition, accessState, changeState, shrinkable);
	}

	ShrinkableChainIteration<T> withStateChange(boolean stateHasBeenChanged) {
		if (this.changeState == stateHasBeenChanged) {
			return this;
		}
		return new ShrinkableChainIteration<>(randomSeed, precondition, accessState, stateHasBeenChanged, shrinkable);
	}
}
