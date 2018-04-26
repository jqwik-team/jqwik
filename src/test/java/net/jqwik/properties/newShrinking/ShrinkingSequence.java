package net.jqwik.properties.newShrinking;

public interface ShrinkingSequence<T> {
	boolean next(Runnable count);

	NShrinkable<T> current();
}
