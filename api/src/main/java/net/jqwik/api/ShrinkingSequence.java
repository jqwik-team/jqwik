package net.jqwik.api;

import java.util.function.*;

public interface ShrinkingSequence<T> {

	abstract class ShrinkingSequenceFacade {
		private static ShrinkingSequenceFacade implementation;

		static  {
			implementation = FacadeLoader.load(ShrinkingSequenceFacade.class);
		}

		public abstract <T> ShrinkingSequence<T> dontShrink(Shrinkable<T> shrinkable);
		public abstract <T> ShrinkingSequence<T> andThen(ShrinkingSequence<T> self, Function<Shrinkable<T>, ShrinkingSequence<T>> createFollowupSequence);
		public abstract <T, U> ShrinkingSequence<U> mapValue(ShrinkingSequence<T> self, Function<T, U> mapper);
		public abstract <T, U> ShrinkingSequence<U> map(ShrinkingSequence<T> self, Function<FalsificationResult<T>, FalsificationResult<U>> mapper);
	}

	static <T> ShrinkingSequence<T> dontShrink(Shrinkable<T> shrinkable) {
		return ShrinkingSequenceFacade.implementation.dontShrink(shrinkable);
	}

	boolean next(Runnable count, Consumer<FalsificationResult<T>> falsifiedReporter);

	FalsificationResult<T> current();

	void init(FalsificationResult<T> initialCurrent);

	default ShrinkingSequence<T> andThen(Function<Shrinkable<T>, ShrinkingSequence<T>> createFollowupSequence) {
		return ShrinkingSequenceFacade.implementation.andThen(this, createFollowupSequence);
	}

	// TODO: This feels strange. There _should_ be a way to replace all calls by calls to map().
	default <U> ShrinkingSequence<U> mapValue(Function<T, U> mapper) {
		return ShrinkingSequenceFacade.implementation.mapValue(this, mapper);
	}

	default <U> ShrinkingSequence<U> map(Function<FalsificationResult<T>, FalsificationResult<U>> mapper) {
		return ShrinkingSequenceFacade.implementation.map(this, mapper);
	}

}
