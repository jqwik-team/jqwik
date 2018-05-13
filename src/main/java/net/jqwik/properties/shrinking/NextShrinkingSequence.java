package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.function.*;

public class NextShrinkingSequence<T> implements ShrinkingSequence<T> {
	private final ShrinkingSequence<T> before;
	private final Function<Shrinkable<T>, ShrinkingSequence<T>> nextShrinkingStep;
	private ShrinkingSequence<T> nextSequence = null;

	public NextShrinkingSequence(ShrinkingSequence<T> before, Function<Shrinkable<T>, ShrinkingSequence<T>> nextShrinkingStep) {
		this.before = before;
		this.nextShrinkingStep = nextShrinkingStep;
	}

	@Override
	public boolean next(Runnable count, Consumer<FalsificationResult<T>> falsifiedReporter) {
		if (nextSequence == null) {
			if (before.next(count, falsifiedReporter)) {
				return true;
			} else {
				nextSequence = nextShrinkingStep.apply(before.current().shrinkable());
			}
		}
		return nextSequence.next(count, falsifiedReporter);
	}

	@Override
	public FalsificationResult<T> current() {
		if (nextSequence == null) {
			return before.current();
		}
		return nextSequence.current();
	}
}
