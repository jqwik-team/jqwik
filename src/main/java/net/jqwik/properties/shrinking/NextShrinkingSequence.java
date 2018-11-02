package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.function.*;

public class NextShrinkingSequence<T> implements ShrinkingSequence<T> {
	private final ShrinkingSequence<T> before;
	private final Function<Shrinkable<T>, ShrinkingSequence<T>> nextShrinkingStep;
	private ShrinkingSequence<T> nextSequence = null;
	private FalsificationResult<T> current = null;

	public NextShrinkingSequence(ShrinkingSequence<T> before, Function<Shrinkable<T>, ShrinkingSequence<T>> nextShrinkingStep) {
		this.before = before;
		this.nextShrinkingStep = nextShrinkingStep;
		this.current = before.current();
	}

	@Override
	public boolean next(Runnable count, Consumer<FalsificationResult<T>> falsifiedReporter) {
		if (nextSequence == null) {
			if (before.next(count, falsifiedReporter)) {
				current = before.current();
				return true;
			} else {
				nextSequence = nextShrinkingStep.apply(current.shrinkable());
			}
		}
		boolean next = nextSequence.next(count, falsifiedReporter);
		if (next) {
			current = nextSequence.current();
		}
		return next;
	}

	@Override
	public FalsificationResult<T> current() {
		return current;
	}
}
