package net.jqwik.api;

import net.jqwik.properties.shrinking.*;

import java.util.function.*;

public interface ShrinkingSequence<T> {
	static <T> ShrinkingSequence<T> dontShrink(Shrinkable<T> shrinkable) {
		return new NullShrinkingSequence<>(shrinkable);
	}

	boolean next(Runnable count, Consumer<FalsificationResult<T>> falsifiedReporter);

	FalsificationResult<T> current();

	default ShrinkingSequence<T> andThen(Function<Shrinkable<T>, ShrinkingSequence<T>> createFollowupSequence) {
		return new NextShrinkingSequence<>(this, createFollowupSequence);
	}

	// TODO: This feels strange. There _should_ be a way to replace all calls by calls to map().
	default <U> ShrinkingSequence<U> mapValue(Function<T, U> mapper) {
		return new MappedValueShrinkingSequence<>(this, mapper);
	}

	default <U> ShrinkingSequence<U> map(Function<FalsificationResult<T>, FalsificationResult<U>> mapper) {
		return new MappedShrinkingSequence<>(this, mapper);
	}

}
