package net.jqwik.api;

import java.util.function.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = MAINTAINED, since = "1.0")
public interface ShrinkingSequence<T> {

	@API(status = INTERNAL)
	abstract class ShrinkingSequenceFacade {
		private static final ShrinkingSequenceFacade implementation;

		static  {
			implementation = FacadeLoader.load(ShrinkingSequenceFacade.class);
		}

		public abstract <T> ShrinkingSequence<T> dontShrink(Shrinkable<T> shrinkable);
		public abstract <T> ShrinkingSequence<T> andThen(ShrinkingSequence<T> self, Function<Shrinkable<T>, ShrinkingSequence<T>> createFollowupSequence);
		public abstract <T, U> ShrinkingSequence<U> mapValue(ShrinkingSequence<T> self, Function<T, U> mapper);
		public abstract <T, U> ShrinkingSequence<U> map(ShrinkingSequence<T> self, Function<FalsificationResult<T>, FalsificationResult<U>> mapper);
		public abstract <T> ShrinkingSequence<T> startWith(Shrinkable<T> startingShrinkable, Falsifier<T> falsifier);
	}

	static <T> ShrinkingSequence<T> dontShrink(Shrinkable<T> shrinkable) {
		return ShrinkingSequenceFacade.implementation.dontShrink(shrinkable);
	}

	@API(status = INTERNAL)
	static <T> ShrinkingSequence<T> startWith(Shrinkable<T> startingShrinkable, Falsifier<T> falsifier) {
		return ShrinkingSequenceFacade.implementation.startWith(startingShrinkable, falsifier);
	}

	boolean next(Runnable count, Consumer<FalsificationResult<T>> falsifiedReporter);

	FalsificationResult<T> current();

	@API(status = INTERNAL)
	void init(FalsificationResult<T> initialCurrent);

	default ShrinkingSequence<T> andThen(Function<Shrinkable<T>, ShrinkingSequence<T>> createFollowupSequence) {
		return ShrinkingSequenceFacade.implementation.andThen(this, createFollowupSequence);
	}

	default <U> ShrinkingSequence<U> mapValue(Function<T, U> mapper) {
		return ShrinkingSequenceFacade.implementation.mapValue(this, mapper);
	}

	default <U> ShrinkingSequence<U> map(Function<FalsificationResult<T>, FalsificationResult<U>> mapper) {
		return ShrinkingSequenceFacade.implementation.map(this, mapper);
	}

}
