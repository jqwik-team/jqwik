package net.jqwik.properties.newShrinking;

import java.util.function.*;

class NextShrinkingSequence<T> implements ShrinkingSequence<T> {
	private final ShrinkingSequence<T> before;
	private final Function<NShrinkable<T>, ShrinkingSequence<T>> nextShrinkingStep;
	private ShrinkingSequence<T> nextSequence = null;

	public NextShrinkingSequence(ShrinkingSequence<T> before, Function<NShrinkable<T>, ShrinkingSequence<T>> nextShrinkingStep) {
		this.before = before;
		this.nextShrinkingStep = nextShrinkingStep;
	}

	@Override
	public boolean next(Runnable count, Consumer<T> reportFalsified) {
		if (nextSequence == null) {
			if (before.next(count, ignore -> {})) {
				return true;
			} else {
				nextSequence = nextShrinkingStep.apply(before.current());
			}
		}
		return nextSequence.next(count, ignore -> {});
	}

	@Override
	public NShrinkable<T> current() {
		if (nextSequence == null) {
			return before.current();
		}
		return nextSequence.current();
	}
}
