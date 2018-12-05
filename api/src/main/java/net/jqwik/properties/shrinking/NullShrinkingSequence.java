package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.function.*;

public class NullShrinkingSequence<T> implements ShrinkingSequence<T> {

	private final Shrinkable<T> shrinkable;

	public NullShrinkingSequence(Shrinkable<T> shrinkable) {
		this.shrinkable = shrinkable;
	}

	@Override
	public boolean next(Runnable count, Consumer<FalsificationResult<T>> falsifiedReporter) {
		return false;
	}

	@Override
	public void init(FalsificationResult<T> initialCurrent) {
	}

	@Override
	public FalsificationResult<T> current() {
		return FalsificationResult.falsified(shrinkable);
	}

}
