package net.jqwik.properties.newShrinking;

import java.util.function.*;

public interface ShrinkingSequence<T> {
	static <T> ShrinkingSequence<T> dontShrink(NShrinkable<T> shrinkable) {
		return new NullShrinkingSequence<>(shrinkable);
	}

	boolean next(Runnable count);

	NShrinkable<T> current();

	default ShrinkingSequence<T> andThen(Function<NShrinkable<T>, ShrinkingSequence<T>> nextShrinkingStep) {
		return new NextShrinkingSequence<>(this, nextShrinkingStep);
	}

}
