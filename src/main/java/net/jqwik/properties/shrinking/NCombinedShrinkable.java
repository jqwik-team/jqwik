package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class NCombinedShrinkable<T> implements NShrinkable<T> {
	private final List<NShrinkable<Object>> shrinkables;
	private final Function<List<Object>, T> combinator;

	public NCombinedShrinkable(List<NShrinkable<Object>> shrinkables, Function<List<Object>, T> combinator) {
		this.shrinkables = shrinkables;
		this.combinator = combinator;
	}

	@Override
	public T value() {
		return combinator.apply(toValues(shrinkables));
	}

	private List<Object> toValues(List<NShrinkable<Object>> shrinkables) {
		return shrinkables.stream().map(NShrinkable::value).collect(Collectors.toList());
	}

	@Override
	public ShrinkingSequence<T> shrink(Falsifier<T> falsifier) {
		return new CombinedShrinkingSequence(falsifier);
	}

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.combine(shrinkables);
	}

	private class CombinedShrinkingSequence implements ShrinkingSequence<T> {

		final private ElementsShrinkingSequence<Object> elementsSequence;

		private CombinedShrinkingSequence(Falsifier<T> falsifier) {
			Falsifier<List<Object>> combinedFalsifier = elements -> falsifier.test(combinator.apply(elements));
			elementsSequence = new ElementsShrinkingSequence<>( //
				shrinkables, null, //
				combinedFalsifier, //
				ShrinkingDistance::combine //
			);
		}

		@Override
		public boolean next(Runnable count, Consumer<T> falsifiedReporter) {
			Consumer<List<Object>> combinedReporter = elements -> falsifiedReporter.accept(combinator.apply(elements));
			return elementsSequence.next(count, combinedReporter);
		}

		@Override
		public FalsificationResult<T> current() {
			return elementsSequence.current().map(combinator);
		}
	}
}
