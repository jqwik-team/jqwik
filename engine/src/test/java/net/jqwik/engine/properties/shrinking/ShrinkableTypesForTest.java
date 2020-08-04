package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

@Group
public class ShrinkableTypesForTest {

	public static class OneStepShrinkable extends AbstractValueShrinkable<Integer> {
		private final int minimum;

		public OneStepShrinkable(int integer) {
			this(integer, 0);
		}

		OneStepShrinkable(int integer, int minimum) {
			super(integer);
			this.minimum = minimum;
		}

		@Override
		public Stream<Shrinkable<Integer>> shrink() {
			if (this.value() == minimum)
				return Stream.empty();
			return Stream.of(new OneStepShrinkable(this.value() - 1, minimum));
		}

		@Override
		public Optional<Shrinkable<Integer>> grow(Shrinkable<?> before, Shrinkable<?> after) {
			Object beforeValue = before.value();
			Object afterValue = after.value();
			if (beforeValue instanceof Integer && afterValue instanceof Integer) {
				int diff = (int) beforeValue - (int) afterValue;
				int grownValue = value() + diff;
				if (grownValue >= minimum) {
					return Optional.of(new OneStepShrinkable(grownValue, minimum));
				}
			}
			return Optional.empty();
		}

		@Override
		public ShrinkingDistance distance() {
			return ShrinkingDistance.of(value() - minimum);
		}

	}

	public static class FullShrinkable extends AbstractValueShrinkable<Integer> {
		FullShrinkable(int integer) {
			super(integer);
		}

		@Override
		public Stream<Shrinkable<Integer>> shrink() {
			return IntStream.range(0, this.value()).mapToObj(FullShrinkable::new);
		}

		@Override
		public ShrinkingDistance distance() {
			return ShrinkingDistance.of(value());
		}

	}

	public static class PartialShrinkable extends AbstractValueShrinkable<Integer> {
		PartialShrinkable(int integer) {
			super(integer);
		}

		@Override
		public Stream<Shrinkable<Integer>> shrink() {
			Integer value = this.value();
			List<Stream<Shrinkable<Integer>>> shrinks = new ArrayList<>();
			if (value > 0) shrinks.add(Stream.of(new PartialShrinkable(value - 1)));
			if (value > 1) shrinks.add(Stream.of(new PartialShrinkable(value - 2)));
			return JqwikStreamSupport.concat(shrinks);
		}

		@Override
		public ShrinkingDistance distance() {
			return ShrinkingDistance.of(value());
		}
	}


	public static class ShrinkToLarger extends AbstractValueShrinkable<Integer> {

		ShrinkToLarger(int integer) {
			super(integer);
		}

		@Override
		public Stream<Shrinkable<Integer>> shrink() {
			return Stream.of(new ShrinkToLarger(this.value() + 1));
		}

		@Override
		public ShrinkingDistance distance() {
			return ShrinkingDistance.of(value());
		}

	}

	public static class ShrinkWithFixedDistance extends AbstractValueShrinkable<Integer> {

		ShrinkWithFixedDistance(int integer) {
			super(integer);
		}

		@Override
		public Stream<Shrinkable<Integer>> shrink() {
			if (this.value() == 0) {
				return Stream.empty();
			}
			return Stream.of(new ShrinkWithFixedDistance(this.value() - 1));
		}

		@Override
		public ShrinkingDistance distance() {
			return ShrinkingDistance.of(42);
		}

	}
}
