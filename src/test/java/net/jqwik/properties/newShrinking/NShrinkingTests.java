package net.jqwik.properties.newShrinking;

import net.jqwik.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.*;

@Group
class NShrinkingTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;

	@Group
	class ShrinkableWithSimpleSequence {

		@Example
		void shrinkDownAllTheWay() {
			NShrinkable<Integer> shrinkable = new DecrementingIntegerShrinkable(5);
			assertThat(shrinkable.value()).isEqualTo(5);
			assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(5));

			NShrinkingSequence<Integer> sequence = new NShrinkingSequence<>(shrinkable, anInt -> false);
			assertThat(sequence.currentBest()).isEqualTo(shrinkable);

			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.currentBest().value()).isEqualTo(4);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.currentBest().value()).isEqualTo(3);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.currentBest().value()).isEqualTo(2);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.currentBest().value()).isEqualTo(1);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.currentBest().value()).isEqualTo(0);
			assertThat(sequence.next(count)).isFalse();
			assertThat(sequence.currentBest().value()).isEqualTo(0);

			assertThat(counter.get()).isEqualTo(5);
		}

		@Example
		void shrinkDownSomeWay() {
			NShrinkable<Integer> shrinkable = new DecrementingIntegerShrinkable(5);
			assertThat(shrinkable.value()).isEqualTo(5);
			assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(5));

			NShrinkingSequence<Integer> sequence = new NShrinkingSequence<>(shrinkable, anInt -> anInt < 2);

			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.currentBest().value()).isEqualTo(4);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.currentBest().value()).isEqualTo(3);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.currentBest().value()).isEqualTo(2);
			assertThat(sequence.next(count)).isFalse();
			assertThat(sequence.currentBest().value()).isEqualTo(2);

			assertThat(counter.get()).isEqualTo(3);
		}

		@Example
		void shrinkDownWithFilter() {
			NShrinkable<Integer> shrinkable = new DecrementingIntegerShrinkable(10);

			Falsifier<Integer> falsifier = anInt -> anInt < 6;
			Predicate<Integer> onlyEvenNumbers = anInt -> anInt % 2 == 0;
			NShrinkingSequence<Integer> sequence = new NShrinkingSequence<>(shrinkable, falsifier.withFilter(onlyEvenNumbers));

			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.currentBest().value()).isEqualTo(10);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.currentBest().value()).isEqualTo(8);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.currentBest().value()).isEqualTo(8);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.currentBest().value()).isEqualTo(6);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.currentBest().value()).isEqualTo(6);
			assertThat(sequence.next(count)).isFalse();

			assertThat(counter.get()).isEqualTo(5);
		}

	}

	@Group
	class ShrinkableWithFullCoverage {
		@Example
		void shrinkDownAllTheWay() {
			NShrinkable<Integer> shrinkable = new FullIntegerShrinkable(5);

			NShrinkingSequence<Integer> sequence = new NShrinkingSequence<>(shrinkable, anInt -> false);
			assertThat(sequence.currentBest()).isEqualTo(shrinkable);

			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.currentBest().value()).isEqualTo(0);
			assertThat(sequence.next(count)).isFalse();
			assertThat(sequence.currentBest().value()).isEqualTo(0);

			assertThat(counter.get()).isEqualTo(1);
		}

		@Example
		void shrinkDownSomeWay() {
			NShrinkable<Integer> shrinkable = new FullIntegerShrinkable(5);
			assertThat(shrinkable.value()).isEqualTo(5);
			assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(5));

			NShrinkingSequence<Integer> sequence = new NShrinkingSequence<>(shrinkable, anInt -> anInt < 2);

			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.currentBest().value()).isEqualTo(2);
			assertThat(sequence.next(count)).isFalse();
			assertThat(sequence.currentBest().value()).isEqualTo(2);

			assertThat(counter.get()).isEqualTo(1);
		}

		@Example
		void shrinkDownWithFilter() {
			NShrinkable<Integer> shrinkable = new FullIntegerShrinkable(10);

			Falsifier<Integer> falsifier = anInt -> anInt < 6;
			Predicate<Integer> onlyEvenNumbers = anInt -> anInt % 2 == 0;
			NShrinkingSequence<Integer> sequence = new NShrinkingSequence<>(shrinkable, falsifier.withFilter(onlyEvenNumbers));

			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.currentBest().value()).isEqualTo(6);
			assertThat(sequence.next(count)).isFalse();

			assertThat(counter.get()).isEqualTo(2);
		}

	}

	private static class DecrementingIntegerShrinkable extends NShrinkableValue<Integer> {
		DecrementingIntegerShrinkable(int integer) {
			super(integer);
		}

		@Override
		public Set<NShrinkable<Integer>> shrink() {
			if (value() == 0)
				return Collections.emptySet();
			return Collections.singleton(new DecrementingIntegerShrinkable(value() - 1));
		}

		@Override
		public ShrinkingDistance distance() {
			return ShrinkingDistance.of(value());
		}
	}

	private static class FullIntegerShrinkable extends NShrinkableValue<Integer> {
		FullIntegerShrinkable(int integer) {
			super(integer);
		}

		@Override
		public Set<NShrinkable<Integer>> shrink() {
			return IntStream.range(0, value()).mapToObj(FullIntegerShrinkable::new).collect(Collectors.toSet());
		}

		@Override
		public ShrinkingDistance distance() {
			return ShrinkingDistance.of(value());
		}
	}
}
