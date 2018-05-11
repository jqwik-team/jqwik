package net.jqwik.api;

import net.jqwik.properties.shrinking.*;

import java.util.function.*;

public interface ShrinkingSequence<T> {
	static <T> ShrinkingSequence<T> dontShrink(Shrinkable<T> shrinkable) {
		return new NullShrinkingSequence<>(shrinkable);
	}

	boolean next(Runnable count, Consumer<T> reportFalsified);

	FalsificationResult<T> current();

	default ShrinkingSequence<T> andThen(Function<Shrinkable<T>, ShrinkingSequence<T>> createFollowupSequence) {
		return new NextShrinkingSequence<>(this, createFollowupSequence);
	}

	default <U> ShrinkingSequence<U> map(Function<T, U> mapper) {
		return new MappedShrinkingSequence<>(this, mapper);
	}

}
