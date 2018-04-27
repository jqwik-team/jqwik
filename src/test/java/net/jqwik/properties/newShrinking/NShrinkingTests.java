package net.jqwik.properties.newShrinking;

import net.jqwik.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.*;

@Group
class NShrinkingTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;

	@Group
	class ShrinkableWithOneStepShrinking {

		@Example
		void shrinkDownAllTheWay() {
			NShrinkable<Integer> shrinkable = new OneStepShrinkable(5);
			assertThat(shrinkable.value()).isEqualTo(5);
			assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(5));

			ShrinkingSequence<Integer> sequence = shrinkable.shrink(anInt -> false);
			assertThat(sequence.current()).isEqualTo(shrinkable);

			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(4);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(3);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(2);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(1);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(0);
			assertThat(sequence.next(count)).isFalse();
			assertThat(sequence.current().value()).isEqualTo(0);

			assertThat(counter.get()).isEqualTo(5);
		}

		@Example
		void shrinkDownSomeWay() {
			NShrinkable<Integer> shrinkable = new OneStepShrinkable(5);
			assertThat(shrinkable.value()).isEqualTo(5);
			assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(5));

			ShrinkingSequence<Integer> sequence = shrinkable.shrink(anInt -> anInt < 2);

			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(4);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(3);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(2);
			assertThat(sequence.next(count)).isFalse();
			assertThat(sequence.current().value()).isEqualTo(2);

			assertThat(counter.get()).isEqualTo(3);
		}

		@Example
		void shrinkDownWithFilter() {
			NShrinkable<Integer> shrinkable = new OneStepShrinkable(10);

			Falsifier<Integer> falsifier = anInt -> anInt < 6;
			Predicate<Integer> onlyEvenNumbers = anInt -> anInt % 2 == 0;
			ShrinkingSequence<Integer> sequence = shrinkable.shrink(falsifier.withFilter(onlyEvenNumbers));

			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(10);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(8);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(8);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(6);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(6);
			assertThat(sequence.next(count)).isFalse();

			assertThat(counter.get()).isEqualTo(5);
		}

	}

	@Group
	class ShrinkableWithFullShrinking {
		@Example
		void shrinkDownAllTheWay() {
			NShrinkable<Integer> shrinkable = new FullShrinkable(5);

			ShrinkingSequence<Integer> sequence = shrinkable.shrink(anInt -> false);
			assertThat(sequence.current()).isEqualTo(shrinkable);

			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(0);
			assertThat(sequence.next(count)).isFalse();
			assertThat(sequence.current().value()).isEqualTo(0);

			assertThat(counter.get()).isEqualTo(1);
		}

		@Example
		void shrinkDownSomeWay() {
			NShrinkable<Integer> shrinkable = new FullShrinkable(5);
			assertThat(shrinkable.value()).isEqualTo(5);
			assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(5));

			ShrinkingSequence<Integer> sequence = shrinkable.shrink(anInt -> anInt < 2);

			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(2);
			assertThat(sequence.next(count)).isFalse();
			assertThat(sequence.current().value()).isEqualTo(2);

			assertThat(counter.get()).isEqualTo(1);
		}

		@Example
		void shrinkDownWithFilter() {
			NShrinkable<Integer> shrinkable = new FullShrinkable(10);

			Falsifier<Integer> falsifier = anInt -> anInt < 6;
			Predicate<Integer> onlyEvenNumbers = anInt -> anInt % 2 == 0;
			ShrinkingSequence<Integer> sequence = shrinkable.shrink(falsifier.withFilter(onlyEvenNumbers));

			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(6);
			assertThat(sequence.next(count)).isFalse();

			assertThat(counter.get()).isEqualTo(2);
		}

	}

	@Group
	class ShrinkableWithPartialShrinking {
		@Example
		void shrinkDownAllTheWay() {
			NShrinkable<Integer> shrinkable = new PartialShrinkable(5);

			ShrinkingSequence<Integer> sequence = shrinkable.shrink(anInt -> false);
			assertThat(sequence.current()).isEqualTo(shrinkable);

			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(3);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(1);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(0);
			assertThat(sequence.next(count)).isFalse();
			assertThat(sequence.current().value()).isEqualTo(0);

			assertThat(counter.get()).isEqualTo(3);
		}

		@Example
		void shrinkDownSomeWay() {
			NShrinkable<Integer> shrinkable = new PartialShrinkable(5);

			ShrinkingSequence<Integer> sequence = shrinkable.shrink(anInt -> anInt < 2);

			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(3);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(2);
			assertThat(sequence.next(count)).isFalse();
			assertThat(sequence.current().value()).isEqualTo(2);

			assertThat(counter.get()).isEqualTo(2);
		}

		@Example
		void shrinkDownWithFilter() {
			NShrinkable<Integer> shrinkable = new PartialShrinkable(10);

			Falsifier<Integer> falsifier = anInt -> anInt < 6;
			Predicate<Integer> onlyEvenNumbers = anInt -> anInt % 2 == 0;
			ShrinkingSequence<Integer> sequence = shrinkable.shrink(falsifier.withFilter(onlyEvenNumbers));

			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(8);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(6);
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.next(count)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(6);
			assertThat(sequence.next(count)).isFalse();

			assertThat(counter.get()).isEqualTo(5);
		}

	}

	@Group
	class ListShrinkable {

		@Example
		void shrinkDownAllTheWay() {
			List<NShrinkable<Integer>> elementShrinkables = asList( //
				new OneStepShrinkable(0), //
				new OneStepShrinkable(1), //
				new OneStepShrinkable(2) //
			);
			NShrinkable<List<Integer>> shrinkable = new NListShrinkable<>(elementShrinkables);
			assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(3, 3));
			assertThat(shrinkable.value()).isEqualTo(asList(0, 1, 2));

			ShrinkingSequence<List<Integer>> sequence = shrinkable.shrink(aList -> false);
			assertThat(sequence.current()).isEqualTo(shrinkable);

//			assertThat(sequence.next(count)).isTrue();
//			assertThat(sequence.current().value().size()).isEqualTo(2);
//			assertThat(sequence.next(count)).isTrue();
//			assertThat(sequence.current().value().size()).isEqualTo(1);
//			assertThat(sequence.next(count)).isTrue();
//			assertThat(sequence.current().value().size()).isEqualTo(0);
//			assertThat(sequence.next(count)).isFalse();
//
//			assertThat(counter.get()).isEqualTo(3);
		}

	}

	private static class OneStepShrinkable extends NShrinkableValue<Integer> {
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

	private static class FullShrinkable extends NShrinkableValue<Integer> {
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

	private static class PartialShrinkable extends NShrinkableValue<Integer> {
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
