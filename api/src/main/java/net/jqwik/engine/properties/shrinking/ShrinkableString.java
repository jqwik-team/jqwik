package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

public class ShrinkableString extends ShrinkableContainer<String, Character> {

	private final int minSize;

	public ShrinkableString(List<Shrinkable<Character>> elements, int minSize) {
		super(elements, minSize);
		this.minSize = minSize;
	}

	@Override
	Collector<Character, ?, String> containerCollector() {
		return new CharacterCollector();
	}

	@Override
	Shrinkable<String> createShrinkable(List<Shrinkable<Character>> shrunkElements) {
		return new ShrinkableString(shrunkElements, minSize);
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
