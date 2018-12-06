package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

@Group
public class ShrinkableTypesForTest {

	public static class OneStepShrinkable extends AbstractShrinkable<Integer> {
		private final int minimum;

		public OneStepShrinkable(int integer) {
			this(integer, 0);
		}
		OneStepShrinkable(int integer, int minimum) {
			super(integer);
			this.minimum = minimum;
		}

		@Override
		public Set<Shrinkable<Integer>> shrinkCandidatesFor(Shrinkable<Integer> shrinkable) {
			if (shrinkable.value() == minimum)
				return Collections.emptySet();
			return Collections.singleton(new OneStepShrinkable(shrinkable.value() - 1, minimum));
		}

		@Override
		public ShrinkingDistance distance() {
			return ShrinkingDistance.of(value() - minimum);
		}
	}

	public static class FullShrinkable extends AbstractShrinkable<Integer> {
		FullShrinkable(int integer) {
			super(integer);
		}

		@Override
		public Set<Shrinkable<Integer>> shrinkCandidatesFor(Shrinkable<Integer> shrinkable) {
			return IntStream.range(0, shrinkable.value()).mapToObj(FullShrinkable::new).collect(Collectors.toSet());
		}

		@Override
		public ShrinkingDistance distance() {
			return ShrinkingDistance.of(value());
		}
	}

	public static class PartialShrinkable extends AbstractShrinkable<Integer> {
		PartialShrinkable(int integer) {
			super(integer);
		}

		@Override
		public Set<Shrinkable<Integer>> shrinkCandidatesFor(Shrinkable<Integer> shrinkable) {
			Integer value = shrinkable.value();
			Set<Shrinkable<Integer>> shrinks = new HashSet<>();
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
