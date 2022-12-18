package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.state.*;

class ShrinkableChainIteration<T> {
	final Shrinkable<Transformer<T>> shrinkable;
	final boolean accessState;
	final boolean changeState;

	private final Predicate<T> precondition;
	private final Transformer<T> cachedTransformer;

	ShrinkableChainIteration(
		Predicate<T> precondition,
		boolean accessState,
		Shrinkable<Transformer<T>> shrinkable
	) {
		// By default transformers are considered to change the state.
		this(precondition, accessState, true, shrinkable);
	}

	private ShrinkableChainIteration(
		Predicate<T> precondition,
		boolean accessState,
		boolean changeState,
		Shrinkable<Transformer<T>> shrinkable
	) {
		this.precondition = precondition;
		this.accessState = accessState;
		this.changeState = changeState;
		this.shrinkable = shrinkable;
		cachedTransformer = cacheTransformerIfItAccessesState(accessState, shrinkable);
	}

	private Transformer<T> cacheTransformerIfItAccessesState(boolean accessState, Shrinkable<Transformer<T>> shrinkable) {
		// Transformer method might access state, so we need to cache the value here
		// otherwise it might be evaluated with wrong state (e.g. after chain executes)
		return accessState ? shrinkable.value() : null;
	}

	@Override
	public String toString() {
		return String.format(
			"Iteration[accessState=%s, changeState=%s, transformation=%s]",
			accessState, changeState, transformer().transformation()
		);
	}

	boolean isEndOfChain() {
		return transformer().equals(Transformer.END_OF_CHAIN);
	}

	Optional<Predicate<T>> precondition() {
		return Optional.ofNullable(precondition);
	}

	ShrinkableChainIteration<T> withShrinkable(Shrinkable<Transformer<T>> shrinkable) {
		return new ShrinkableChainIteration<>(precondition, accessState, changeState, shrinkable);
	}

	ShrinkableChainIteration<T> withStateChange(boolean stateHasBeenChanged) {
		if (this.changeState == stateHasBeenChanged) {
			return this;
		}
		return new ShrinkableChainIteration<>(precondition, accessState, stateHasBeenChanged, shrinkable);
	}

	String transformation() {
		return transformer().transformation();
	}

	Transformer<T> transformer() {
		return cachedTransformer == null ? shrinkable.value() : cachedTransformer;
	}
}
