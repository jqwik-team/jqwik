package net.jqwik.properties.newShrinking;

class NullShrinkingSequence<T> implements ShrinkingSequence<T> {

	private final NShrinkable<T> shrinkable;

	NullShrinkingSequence(NShrinkable<T> shrinkable) {
		this.shrinkable = shrinkable;
	}

	@Override
	public boolean next(Runnable count) {
		return false;
	}

	@Override
	public NShrinkable<T> current() {
		return shrinkable;
	}

}
