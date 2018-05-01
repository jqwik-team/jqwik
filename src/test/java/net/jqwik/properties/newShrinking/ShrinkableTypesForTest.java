package net.jqwik.properties.newShrinking;

import net.jqwik.api.*;

import java.util.*;
import java.util.stream.*;

@Group
class ShrinkableTypesForTest {

	static class OneStepShrinkable extends AbstractShrinkable<Integer> {
		OneStepShrinkable(int integer) {
			super(integer);
		}

		@Override
		public Set<NShrinkable<Integer>> shrinkCandidatesFor(NShrinkable<Integer> shrinkable) {
			if (shrinkable.value() == 0)
				return Collections.emptySet();
			return Collections.singleton(new OneStepShrinkable(shrinkable.value() - 1));
		}

		@Override
		public ShrinkingDistance distance() {
			return ShrinkingDistance.of(value());
		}
	}

	static class FullShrinkable extends AbstractShrinkable<Integer> {
		FullShrinkable(int integer) {
			super(integer);
		}

		@Override
		public Set<NShrinkable<Integer>> shrinkCandidatesFor(NShrinkable<Integer> shrinkable) {
			return IntStream.range(0, shrinkable.value()).mapToObj(FullShrinkable::new).collect(Collectors.toSet());
		}

		@Override
		public ShrinkingDistance distance() {
			return ShrinkingDistance.of(value());
		}
	}

	static class PartialShrinkable extends AbstractShrinkable<Integer> {
		PartialShrinkable(int integer) {
			super(integer);
		}

		@Override
		public Set<NShrinkable<Integer>> shrinkCandidatesFor(NShrinkable<Integer> shrinkable) {
			Integer value = shrinkable.value();
			Set<NShrinkable<Integer>> shrinks = new HashSet<>();
			if (value > 0) shrinks.add(new PartialShrinkable(value - 1));
			if (value > 1) shrinks.add(new PartialShrinkable(value - 2));
			return shrinks;
		}

		@Override
		public ShrinkingDistance distance() {
			return ShrinkingDistance.of(value());
		}
	}
}
