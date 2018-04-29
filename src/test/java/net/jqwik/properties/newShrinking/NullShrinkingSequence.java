package net.jqwik.properties.newShrinking;

import java.util.function.*;

class NullShrinkingSequence<T> implements ShrinkingSequence<T> {

	private final NShrinkable<T> shrinkable;

	NullShrinkingSequence(NShrinkable<T> shrinkable) {
		this.shrinkable = shrinkable;
	}

	@Override
	public boolean next(Runnable count, Consumer<T> reportFalsified) {
		return false;
	}

	@Override
	public NShrinkable<T> current() {
		return shrinkable;
	}

}
