package net.jqwik.engine.properties.shrinking;

import java.util.function.*;

import net.jqwik.api.*;

public class NullShrinkingSequence<T> implements ShrinkingSequence<T> {

	private FalsificationResult<T> current;

	public NullShrinkingSequence(Shrinkable<T> shrinkable) {
		this.current = FalsificationResult.notFalsified(shrinkable);
	}

	@Override
	public boolean next(Runnable count, Consumer<FalsificationResult<T>> falsifiedReporter) {
		return false;
	}

	@Override
	public void init(FalsificationResult<T> initialCurrent) {
		current = initialCurrent;
	}

	@Override
	public FalsificationResult<T> current() {
		return current;
	}

}
