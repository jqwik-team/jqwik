package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.function.*;

public class NextShrinkingSequence<T> implements ShrinkingSequence<T> {
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
			if (before.next(count, reportFalsified)) {
				return true;
			} else {
				nextSequence = nextShrinkingStep.apply(before.current().shrinkable());
			}
		}
		return nextSequence.next(count, reportFalsified);
	}

	@Override
	public FalsificationResult<T> current() {
		if (nextSequence == null) {
			return before.current();
		}
		return nextSequence.current();
	}
}
