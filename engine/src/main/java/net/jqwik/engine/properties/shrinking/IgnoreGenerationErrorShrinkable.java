package net.jqwik.engine.properties.shrinking;

import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.arbitraries.*;

public class IgnoreGenerationErrorShrinkable<T> implements Shrinkable<T> {
	private Shrinkable<T> shrinkable;

	public IgnoreGenerationErrorShrinkable(Shrinkable<T> shrinkable) {this.shrinkable = shrinkable;}

	@Override
	public T value() {
		return shrinkable.value();
	}

	@Override
	public ShrinkingSequence<T> shrink(Falsifier<T> falsifier) {
		ShrinkingSequence<T> shrinkingSequence = shrinkable.shrink(falsifier);
		return new IgnoreGenerationErrorSequence<>(shrinkingSequence);
	}

	@Override
	public ShrinkingDistance distance() {
		return shrinkable.distance();
	}

	private static class IgnoreGenerationErrorSequence<T> implements ShrinkingSequence<T> {

		private ShrinkingSequence<T> shrinkingSequence;
		private FalsificationResult<T> current;

		public IgnoreGenerationErrorSequence(ShrinkingSequence<T> shrinkingSequence) {this.shrinkingSequence = shrinkingSequence;}

		@Override
		public boolean next(Runnable count, Consumer<FalsificationResult<T>> falsifiedReporter) {
			// TODO: Catch generation errors
			try {
				boolean next = shrinkingSequence.next(count, falsifiedReporter);
				this.current = shrinkingSequence.current().map(IgnoreGenerationErrorShrinkable::new);
				return next;
			} catch (GenerationError generationError) {
				// TODO: What can I do here?
				//return next(count, falsifiedReporter);
				return false;
			}
		}

		@Override
		public FalsificationResult<T> current() {
			return current;
		}

		@Override
		public void init(FalsificationResult<T> initialCurrent) {
			this.shrinkingSequence.init(initialCurrent);
			this.current = initialCurrent.map(IgnoreGenerationErrorShrinkable::new);
		}
	}
}
