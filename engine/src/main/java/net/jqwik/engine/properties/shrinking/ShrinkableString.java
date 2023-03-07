package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

public class ShrinkableString extends ShrinkableContainer<String, Character> {

	public ShrinkableString(List<Shrinkable<Character>> elements, int minSize, int maxSize, Arbitrary<Character> characterArbitrary) {
		super(elements, minSize, maxSize, Collections.emptySet(), characterArbitrary);
	}

	@Override
	String createValue(List<Shrinkable<Character>> shrinkables) {
		// Using loop instead of stream to make stack traces more readable
		StringBuilder builder = new StringBuilder(shrinkables.size());
		for (Shrinkable<Character> shrinkable : shrinkables) {
			builder.appendCodePoint(shrinkable.value());
		}
		return builder.toString();
	}

	@Override
	Shrinkable<String> createShrinkable(List<Shrinkable<Character>> shrunkElements) {
		return new ShrinkableString(shrunkElements, minSize, maxSize, elementArbitrary);
	}

	@Override
	public Stream<Shrinkable<String>> shrink() {
		if (elements.size() > 100) {
			return JqwikStreamSupport.concat(
					shrinkSizeAggressively(),
					shrinkSizeOfList(),
					shrinkElementsOneAfterTheOther(100)
			);
		}
		return JqwikStreamSupport.concat(
				shrinkSizeOfList(),
				shrinkElementsOneAfterTheOther(0),
				shrinkPairsOfElements(),
				sortElements()
		);
	}

	private static class CharacterCollector implements Collector<Character, StringBuilder, String> {
		@Override
		public Supplier<StringBuilder> supplier() {
			return StringBuilder::new;
		}

		@Override
		public BiConsumer<StringBuilder, Character> accumulator() {
			return StringBuilder::appendCodePoint;
		}

		@Override
		public BinaryOperator<StringBuilder> combiner() {
			return StringBuilder::append;
		}

		@Override
		public Function<StringBuilder, String> finisher() {
			return StringBuilder::toString;
		}

		@Override
		public Set<Collector.Characteristics> characteristics() {
			return Collections.emptySet();
		}

	}
}
