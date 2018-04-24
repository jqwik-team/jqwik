package net.jqwik.properties.newShrinking;

import net.jqwik.api.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class NShrinkingTests {

	@Example
	void simpleIntegerShrinkable() {
		NShrinkable<Integer> shrinkable = new SimpleIntegerShrinkable(10);

		assertThat(shrinkable.value()).isEqualTo(10);
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(10));

		Falsifier<Integer> falsifier = anInt -> false;

		NShrinker<Integer> shrinker = new NShrinker<>(shrinkable, falsifier);
		assertThat(shrinker.currentBest()).isEqualTo(shrinkable);

		assertThat(shrinker.next()).isTrue();
		assertThat(shrinker.currentBest().value()).isEqualTo(9);
	}

	private static class SimpleIntegerShrinkable extends NShrinkableValue<Integer> {
		SimpleIntegerShrinkable(int integer) {
			super(integer);
		}

		@Override
		public Set<NShrinkable<Integer>> shrink() {
			if (value() == 0)
				return Collections.emptySet();
			return Collections.singleton(new SimpleIntegerShrinkable(value() - 1));
		}

		@Override
		public ShrinkingDistance distance() {
			return ShrinkingDistance.of(value());
		}
	}
}
