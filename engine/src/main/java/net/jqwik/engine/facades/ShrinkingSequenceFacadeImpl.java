package net.jqwik.engine.facades;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.*;

/**
 * Is loaded through reflection in api module
 */
public class ShrinkingSequenceFacadeImpl extends ShrinkingSequence.ShrinkingSequenceFacade {
	@Override
	public <T> ShrinkingSequence<T> dontShrink(Shrinkable<T> shrinkable) {
		return new NullShrinkingSequence<>(shrinkable);
	}

	@Override
	public <T> ShrinkingSequence<T> andThen(
		ShrinkingSequence<T> self, Function<Shrinkable<T>, ShrinkingSequence<T>> createFollowupSequence
	) {
		return new NextShrinkingSequence<>(self, createFollowupSequence);
	}

	@Override
	public <T, U> ShrinkingSequence<U> mapValue(ShrinkingSequence<T> self, Function<T, U> mapper) {
		return new MappedValueShrinkingSequence<>(self, mapper);
	}

	@Override
	public <T, U> ShrinkingSequence<U> map(
		ShrinkingSequence<T> self, Function<FalsificationResult<T>, FalsificationResult<U>> mapper
	) {
		return new MappedShrinkingSequence<>(self, mapper);
	}

	@Override
	public <T> ShrinkingSequence<T> startWith(Shrinkable<T> startingShrinkable, Falsifier<T> falsifier) {
		return new StartWithShrinkingSequence<>(startingShrinkable, falsifier);
	}

}
