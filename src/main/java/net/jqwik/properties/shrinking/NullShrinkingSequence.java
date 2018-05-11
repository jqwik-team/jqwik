package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.function.*;

public class NullShrinkingSequence<T> implements ShrinkingSequence<T> {

	private final Shrinkable<T> shrinkable;

	public NullShrinkingSequence(Shrinkable<T> shrinkable) {
		this.shrinkable = shrinkable;
	}

	@Override
	public boolean next(Runnable count, Consumer<T> reportFalsified) {
		return false;
	}

	@Override
	public FalsificationResult<T> current() {
		return FalsificationResult.falsified(shrinkable);
	}

}
