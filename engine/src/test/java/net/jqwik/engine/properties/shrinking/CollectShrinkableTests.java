package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Group
@Label("CollectShrinkable")
class CollectShrinkableTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;

	@SuppressWarnings("unchecked")
	private Consumer<List<Integer>> valueReporter = mock(Consumer.class);
	private Consumer<FalsificationResult<List<Integer>>> reporter = result -> valueReporter.accept(result.value());

	@Example
	void creation() {
		Shrinkable<Integer> shrinkable3 = new OneStepShrinkable(3);
		Shrinkable<Integer> shrinkable2 = new OneStepShrinkable(2);
		Shrinkable<Integer> shrinkable1 = new OneStepShrinkable(1);

		List<Shrinkable<Integer>> shrinkables = Arrays.asList(shrinkable3, shrinkable2, shrinkable1);

		Predicate<List<Integer>> untilIsIgnoredHere = l -> true;
		CollectShrinkable<Integer> shrinkable = new CollectShrinkable<>(shrinkables, untilIsIgnoredHere);

		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(3, 6));
		assertThat(shrinkable.value()).containsExactly(3, 2, 1);
	}

	@Example
	void shrinkingWithoutSizeReduction() {
		Shrinkable<Integer> shrinkable4 = new OneStepShrinkable(3);
		Shrinkable<Integer> shrinkable1 = new OneStepShrinkable(1);

		List<Shrinkable<Integer>> shrinkables = Arrays.asList(shrinkable4, shrinkable1);

		Predicate<List<Integer>> untilSizeAtLeast2 = l -> l.size() >= 2;
		Shrinkable<List<Integer>> shrinkable = new CollectShrinkable<>(shrinkables, untilSizeAtLeast2);

		ShrinkingSequence<List<Integer>> sequence = shrinkable.shrink(ignore -> false);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).containsExactly(2, 1);
		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).containsExactly(1, 1);
		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).containsExactly(0, 1);
		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).containsExactly(0, 0);
		assertThat(sequence.next(count, reporter)).isFalse();

		assertThat(counter.get()).isEqualTo(4);
	}

	@Example
	void shrinkingWithoutSizeReductionButNotAllShrinkingStepsWork() {
		Shrinkable<Integer> shrinkable4 = new FullShrinkable(5);
		Shrinkable<Integer> shrinkable1 = new FullShrinkable(1);

		List<Shrinkable<Integer>> shrinkables = Arrays.asList(shrinkable4, shrinkable1);

		Predicate<List<Integer>> untilSizeAtLeast2 = l -> l.size() >= 2;
		Shrinkable<List<Integer>> shrinkable = new CollectShrinkable<>(shrinkables, untilSizeAtLeast2);

		Falsifier<List<Integer>> sumMustNotBeEven = listOfInts -> {
			int sum = listOfInts.stream().mapToInt(i -> i).sum();
			return sum % 2 != 0;
		};
		ShrinkingSequence<List<Integer>> sequence = shrinkable.shrink(sumMustNotBeEven);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).containsExactly(1, 1);
		assertThat(sequence.next(count, reporter)).isFalse();

		assertThat(counter.get()).isEqualTo(1);
	}

	@Example
	void shrinkingWithSizeReduction() {
		Shrinkable<Integer> shrinkable1 = new OneStepShrinkable(2).map(i -> 3 - i);

		List<Shrinkable<Integer>> shrinkables = Arrays.asList(shrinkable1, shrinkable1, shrinkable1, shrinkable1, shrinkable1, shrinkable1);

		Predicate<List<Integer>> sumAtLeast6 = l -> {
			int sum = l.stream().mapToInt(i -> i).sum();
			return sum >= 6;
		};
		Shrinkable<List<Integer>> shrinkable = new CollectShrinkable<>(shrinkables, sumAtLeast6);

		ShrinkingSequence<List<Integer>> sequence = shrinkable.shrink(ignore -> false);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).containsExactly(2, 1, 1, 1, 1);
		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).containsExactly(3, 1, 1, 1);
		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).containsExactly(3, 2, 1);
		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).containsExactly(3, 3);
		assertThat(sequence.next(count, reporter)).isFalse();

		assertThat(counter.get()).isEqualTo(4);
	}


	@Example
	void reportFalsifier() {
		Shrinkable<Integer> shrinkable3 = new OneStepShrinkable(3);

		List<Shrinkable<Integer>> shrinkables = Arrays.asList(shrinkable3);

		Predicate<List<Integer>> untilNotEmpty = l -> !l.isEmpty();
		Shrinkable<List<Integer>> shrinkable = new CollectShrinkable<>(shrinkables, untilNotEmpty);


		ShrinkingSequence<List<Integer>> sequence = shrinkable.shrink(ignore -> false);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).containsExactly(2);
		verify(valueReporter).accept(Arrays.asList(2));
		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).containsExactly(1);
		verify(valueReporter).accept(Arrays.asList(1));
		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).containsExactly(0);
		verify(valueReporter).accept(Arrays.asList(0));
		assertThat(sequence.next(count, reporter)).isFalse();

		verifyNoMoreInteractions(valueReporter);
	}

}
