package net.jqwik.engine.properties.shrinking;

import java.util.concurrent.atomic.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Group
@Label("Deep Search Shrinking")
class DeepSearchShrinkingTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;

	@SuppressWarnings("unchecked")
	private Consumer<Integer> valueReporter = mock(Consumer.class);
	private Consumer<FalsificationResult<Integer>> reporter = result -> valueReporter.accept(result.value());

	@Group
	class ReportFalsified {

		@Example
		@Label("without filter report all current values on the way")
		void withoutFilter() {
			Shrinkable<Integer> shrinkable = new OneStepShrinkable(4);

			ShrinkingSequence<Integer> sequence = shrinkable.shrink(anInt -> anInt < 2);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(3);
			verify(valueReporter).accept(3);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(2);
			verify(valueReporter).accept(2);

			assertThat(sequence.next(count, reporter)).isFalse();
			assertThat(sequence.current().value()).isEqualTo(2);
			verifyNoMoreInteractions(valueReporter);
		}

		@Example
		@Label("with filter report only simpler values that match filter")
		void withFilter() {
			Shrinkable<Integer> shrinkable = new OneStepShrinkable(10);

			Falsifier<Integer> falsifier = anInt -> anInt < 6;
			Predicate<Integer> onlyEvenNumbers = anInt -> anInt % 2 == 0;
			ShrinkingSequence<Integer> sequence = shrinkable.shrink(falsifier.withFilter(onlyEvenNumbers));

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(10);
			// 10 is not a new value
			verify(valueReporter, never()).accept(10);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(8);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(8);
			verify(valueReporter, times(1)).accept(8);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(6);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(6);
			verify(valueReporter, times(1)).accept(6);

			assertThat(sequence.next(count, reporter)).isFalse();
			verifyNoMoreInteractions(valueReporter);

		}

	}

	@Group
	class ShrinkableWithOneStepShrinking {

		@Example
		void shrinkDownAllTheWay() {
			Shrinkable<Integer> shrinkable = new OneStepShrinkable(5);
			assertThat(shrinkable.value()).isEqualTo(5);
			assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(5));

			ShrinkingSequence<Integer> sequence = shrinkable.shrink(anInt -> false);
			assertThat(sequence.current().shrinkable()).isEqualTo(shrinkable);

			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(4);
			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(3);
			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(2);
			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(1);
			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(0);
			assertThat(sequence.next(count, ignore -> {})).isFalse();
			assertThat(sequence.current().value()).isEqualTo(0);

			assertThat(counter.get()).isEqualTo(5);
		}

		@Example
		void shrinkDownSomeWay() {
			Shrinkable<Integer> shrinkable = new OneStepShrinkable(5);
			assertThat(shrinkable.value()).isEqualTo(5);
			assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(5));

			ShrinkingSequence<Integer> sequence = shrinkable.shrink(anInt -> anInt < 2);

			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(4);
			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(3);
			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(2);
			assertThat(sequence.next(count, ignore -> {})).isFalse();
			assertThat(sequence.current().value()).isEqualTo(2);

			assertThat(counter.get()).isEqualTo(3);
		}

		@Example
		void shrinkDownWithFilter() {
			Shrinkable<Integer> shrinkable = new OneStepShrinkable(10);

			Falsifier<Integer> falsifier = anInt -> anInt < 6;
			Predicate<Integer> onlyEvenNumbers = anInt -> anInt % 2 == 0;
			ShrinkingSequence<Integer> sequence = shrinkable.shrink(falsifier.withFilter(onlyEvenNumbers));

			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(10);
			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(8);
			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(8);
			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(6);
			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(6);
			assertThat(sequence.next(count, ignore -> {})).isFalse();

			assertThat(counter.get()).isEqualTo(5);
		}

	}

	@Group
	class ShrinkableWithFullShrinking {
		@Example
		void shrinkDownAllTheWay() {
			Shrinkable<Integer> shrinkable = new FullShrinkable(5);

			ShrinkingSequence<Integer> sequence = shrinkable.shrink(anInt -> false);
			assertThat(sequence.current().shrinkable()).isEqualTo(shrinkable);

			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(0);
			assertThat(sequence.next(count, ignore -> {})).isFalse();
			assertThat(sequence.current().value()).isEqualTo(0);

			assertThat(counter.get()).isEqualTo(1);
		}

		@Example
		void shrinkDownSomeWay() {
			Shrinkable<Integer> shrinkable = new FullShrinkable(5);
			assertThat(shrinkable.value()).isEqualTo(5);
			assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(5));

			ShrinkingSequence<Integer> sequence = shrinkable.shrink(anInt -> anInt < 2);

			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(2);
			assertThat(sequence.next(count, ignore -> {})).isFalse();
			assertThat(sequence.current().value()).isEqualTo(2);

			assertThat(counter.get()).isEqualTo(1);
		}

		@Example
		void shrinkDownWithFilter() {
			Shrinkable<Integer> shrinkable = new FullShrinkable(10);

			Falsifier<Integer> falsifier = anInt -> anInt < 6;
			Predicate<Integer> onlyEvenNumbers = anInt -> anInt % 2 == 0;
			ShrinkingSequence<Integer> sequence = shrinkable.shrink(falsifier.withFilter(onlyEvenNumbers));

			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(6);
			assertThat(sequence.next(count, ignore -> {})).isFalse();

			assertThat(counter.get()).isEqualTo(2);
		}

	}

	@Group
	class ShrinkableWithPartialShrinking {
		@Example
		void shrinkDownAllTheWay() {
			Shrinkable<Integer> shrinkable = new PartialShrinkable(5);

			ShrinkingSequence<Integer> sequence = shrinkable.shrink(anInt -> false);
			assertThat(sequence.current().shrinkable()).isEqualTo(shrinkable);

			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(3);
			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(1);
			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(0);
			assertThat(sequence.next(count, ignore -> {})).isFalse();
			assertThat(sequence.current().value()).isEqualTo(0);

			assertThat(counter.get()).isEqualTo(3);
		}

		@Example
		void shrinkDownSomeWay() {
			Shrinkable<Integer> shrinkable = new PartialShrinkable(5);

			ShrinkingSequence<Integer> sequence = shrinkable.shrink(anInt -> anInt < 2);

			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(3);
			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(2);
			assertThat(sequence.next(count, ignore -> {})).isFalse();
			assertThat(sequence.current().value()).isEqualTo(2);

			assertThat(counter.get()).isEqualTo(2);
		}

		@Example
		void shrinkDownWithFilter() {
			Shrinkable<Integer> shrinkable = new PartialShrinkable(10);

			Falsifier<Integer> falsifier = anInt -> anInt < 6;
			Predicate<Integer> onlyEvenNumbers = anInt -> anInt % 2 == 0;
			ShrinkingSequence<Integer> sequence = shrinkable.shrink(falsifier.withFilter(onlyEvenNumbers));

			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(8);
			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(6);
			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.next(count, ignore -> {})).isTrue();
			assertThat(sequence.current().value()).isEqualTo(6);
			assertThat(sequence.next(count, ignore -> {})).isFalse();

			assertThat(counter.get()).isEqualTo(5);
		}

	}

}
