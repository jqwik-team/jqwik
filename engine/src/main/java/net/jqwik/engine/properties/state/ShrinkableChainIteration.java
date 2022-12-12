package net.jqwik.engine.properties.state;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.state.*;

class ShrinkableChainIteration<T> {
	// Larger values might improve shrink quality, however, they increase the shrink space, so it might increase shrink duration
	private final static int NUM_SAMPLES_IN_EAGER_CHAIN_SHRINK = Integer.getInteger("jqwik.eagerChainShrinkSamples", 2);

	static class ShrinkableWithEagerValue<T> implements Shrinkable<T> {
		protected final Shrinkable<T> base;
		private final ShrinkingDistance distance;
		final T value;

		ShrinkableWithEagerValue(Shrinkable<T> base) {
			this.base = base;
			this.distance = base.distance();
			this.value = base.value();
		}

		@Override
		public T value() {
			return value;
		}

		@Override
		public Stream<Shrinkable<T>> shrink() {
			return base.shrink();
		}

		@Override
		public ShrinkingDistance distance() {
			return distance;
		}
	}

	static class EagerShrinkable<T> extends ShrinkableWithEagerValue<T> {
		private final List<Shrinkable<T>> shrinkResults;

		EagerShrinkable(Shrinkable<T> base, int numSamples) {
			super(base);
			this.shrinkResults =
				base.shrink()
					.sorted(Comparator.comparing(Shrinkable::distance))
					.limit(numSamples)
					.map(ShrinkableWithEagerValue::new)
					.collect(Collectors.toList());
		}

		@Override
		public Stream<Shrinkable<T>> shrink() {
			return shrinkResults.stream();
		}
	}

	final Shrinkable<Transformer<T>> shrinkable;
	private final Predicate<T> precondition;
	final boolean accessState;
	final boolean changeState;

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
		// When the shrinkable does not access state, we could just use it as is for ".value()", and ".shrink()"
		// If we get LazyShrinkable here, it means we are in a shrinking phase, so we know ".shrink()" will be called only
		// in case the subsequent execution fails. So we can just keep LazyShrinkable as is
		// Otherwise, we need to eagerly evaluate the shrinkables to since the state might change by appyling subsequent transformers,
		// so we won't be able to access the state anymore.
		// See https://github.com/jlink/jqwik/issues/428
		if (!accessState || shrinkable instanceof ShrinkableChainIteration.ShrinkableWithEagerValue) {
			this.shrinkable = shrinkable;
		} else {
			this.shrinkable = new EagerShrinkable<>(shrinkable, NUM_SAMPLES_IN_EAGER_CHAIN_SHRINK);
		}
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
		return shrinkable.value();
	}
}
