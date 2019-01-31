package net.jqwik.engine.properties.shrinking;

import org.opentest4j.*;

import net.jqwik.api.*;

public class IgnoreGenerationErrorShrinkable<T> implements Shrinkable<T> {
	private Shrinkable<T> shrinkable;

	public IgnoreGenerationErrorShrinkable(Shrinkable<T> shrinkable) {this.shrinkable = shrinkable;}

	@Override
	public T value() {
		return shrinkable.value();
	}

	@Override
	public ShrinkingSequence<T> shrink(Falsifier<T> falsifier) {
		Falsifier<T> ignoreErrorsFalsifier = t -> {
			try {
				return falsifier.test(t);
			} catch (TestAbortedException tae) {
				throw tae;
			} catch (Throwable ignore) {
				throw new TestAbortedException();
			}
		};
		return shrinkable.shrink(ignoreErrorsFalsifier);
	}

	@Override
	public ShrinkingDistance distance() {
		return shrinkable.distance();
	}
}
