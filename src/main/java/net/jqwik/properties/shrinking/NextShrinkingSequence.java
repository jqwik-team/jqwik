package net.jqwik.properties.shrinking;

import java.util.function.*;

import net.jqwik.api.*;

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
	public void init(FalsificationResult<T> initialCurrent) {
		if (current == null) {
			current = initialCurrent;
		} else {
			current = FalsificationResult.falsified(current.shrinkable(), initialCurrent.throwable().orElse(null));
		}
		this.before.init(initialCurrent);
	}

	@Override
	public boolean next(Runnable count, Consumer<FalsificationResult<T>> falsifiedReporter) {
		if (nextSequence == null) {
			if (before.next(count, falsifiedReporter)) {
				current = before.current();
				return true;
			} else {
				nextSequence = nextShrinkingStep.apply(current.shrinkable());
				nextSequence.init(current);
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

	@Override
	public String toString() {
		return String.format("Next [%s, %s]", before, nextSequence);
	}
}
