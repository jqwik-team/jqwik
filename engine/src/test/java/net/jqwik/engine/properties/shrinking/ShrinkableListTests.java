package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Group
@Label("ShrinkableList")
class ShrinkableListTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;

	@SuppressWarnings("unchecked")
	private Consumer<List<Integer>> valueReporter = mock(Consumer.class);
	private Consumer<FalsificationResult<List<Integer>>> reporter = result -> valueReporter.accept(result.value());

	@Example
	void creation() {
		Shrinkable<List<Integer>> shrinkable = createShrinkableList(0, 1, 2, 3);
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(4, 6));
		assertThat(shrinkable.value()).isEqualTo(asList(0, 1, 2, 3));
	}

	@Group
	class ReportFalsified {

		@Example
		@Label("report all falsified on the way")
		void downAllTheWay() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(0, 1, 2);

			ShrinkingSequence<List<Integer>> sequence = shrinkable.shrinkWithCondition(aList -> false);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(0, 1));
			verify(valueReporter).accept(asList(0, 1));

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(0));
			verify(valueReporter).accept(asList(0));

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList());
			verify(valueReporter).accept(asList());

			assertThat(sequence.next(count, reporter)).isFalse();
			verifyNoMoreInteractions(valueReporter);
		}

		@Example
		@Label("also report falsified elements")
		void withElementShrinking() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(3, 3, 3);

			ShrinkingSequence<List<Integer>> sequence = shrinkable.shrinkWithCondition(List::isEmpty);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(3, 3));
			verify(valueReporter).accept(asList(3, 3));

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(3));
			verify(valueReporter).accept(asList(3));

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(2));
			verify(valueReporter).accept(asList(2));

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(1));
			verify(valueReporter).accept(asList(1));

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(0));
			verify(valueReporter).accept(asList(0));

			assertThat(sequence.next(count, reporter)).isFalse();
			verifyNoMoreInteractions(valueReporter);
		}

	}

	@Group
	class Shrinking {

		@Example
		void downAllTheWay() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(0, 1, 2);

			ShrinkingSequence<List<Integer>> sequence = shrinkable.shrinkWithCondition(aList -> false);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(2);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(1);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(0);
			assertThat(sequence.next(count, reporter)).isFalse();

			Assertions.assertThat(counter.get()).isEqualTo(3);
		}

		@Example
		void downToMinSize() {
			List<Shrinkable<Integer>> elementShrinkables =
				Arrays.stream(new Integer[]{0, 1, 2, 3, 4}).map(Shrinkable::unshrinkable).collect(Collectors.toList());
			Shrinkable<List<Integer>> shrinkable = new ShrinkableList<>(elementShrinkables, 2);

			ShrinkingSequence<List<Integer>> sequence = shrinkable.shrinkWithCondition(aList -> false);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(4);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(3);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(2);
			assertThat(sequence.next(count, reporter)).isFalse();

			Assertions.assertThat(counter.get()).isEqualTo(3);
		}

		@Example
		void downToOneElement() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(0, 1, 2);

			ShrinkingSequence<List<Integer>> sequence = shrinkable.shrinkWithCondition(List::isEmpty);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(2);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(1);
			assertThat(sequence.next(count, reporter)).isFalse();

			Assertions.assertThat(counter.get()).isEqualTo(2);
		}

		@Example
		void alsoShrinkElements() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(1, 2, 3);

			ShrinkingSequence<List<Integer>> sequence = shrinkable.shrinkWithCondition(integers -> integers.size() <= 1);
			while(sequence.next(count, reporter));
			assertThat(sequence.current().value()).isEqualTo(asList(0, 0));
		}

		@Example
		void shrinkDuplicateElements() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(3, 3);

			ShrinkingSequence<List<Integer>> sequence =
				shrinkable.shrinkWithCondition(integers -> integers.size() != 2 ||
												  !integers.get(0).equals(integers.get(1)));
			while(sequence.next(count, reporter));
			assertThat(sequence.current().value()).isEqualTo(asList(0, 0));
		}

		@Example
		void shrinkingResultHasValueAndThrowable() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(1, 1, 1);

			ShrinkingSequence<List<Integer>> sequence = shrinkable.shrinkWithCondition(integers -> {
				if (integers.size() > 1) throw new IllegalArgumentException("my reason");
				return true;
			});

			while(sequence.next(count, reporter)) {}

			assertThat(sequence.current().value()).isEqualTo(asList(0, 0));
			assertThat(sequence.current().throwable()).isPresent();
			assertThat(sequence.current().throwable().get()).isInstanceOf(IllegalArgumentException.class);
			assertThat(sequence.current().throwable().get()).hasMessage("my reason");
		}

		@Example
		void shrinkSizeAgainAfterShrinkingElements() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(1, 0, 2, 1);

			ShrinkingSequence<List<Integer>> sequence =
				shrinkable.shrinkWithCondition(integers -> integers.size() == new HashSet<>(integers).size());
			while (sequence.next(count, reporter));

			Assertions.assertThat(sequence.current().value()).isEqualTo(asList(0, 0));
		}

		@Example
		void withFilterOnListSize() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(3, 3, 3, 3);

			LegacyFalsifier<List<Integer>> falsifier = ignore -> false;
			Falsifier<List<Integer>> filteredFalsifier = falsifier.withFilter(
				elements -> elements.size() % 2 == 0);
			ShrinkingSequence<List<Integer>> sequence = shrinkable.shrink(filteredFalsifier);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(3, 3, 3, 3));

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(3, 3));

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(3, 3));

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList());

			assertThat(sequence.next(count, reporter)).isFalse();

			Assertions.assertThat(counter.get()).isEqualTo(4);
		}

		@Example
		void withFilterOnElementContents() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(3, 3, 3);

			LegacyFalsifier<List<Integer>> falsifier = List::isEmpty;
			Falsifier<List<Integer>> filteredFalsifier = falsifier.withFilter(
				elements -> elements.stream().allMatch(i -> i % 2 == 1));
			ShrinkingSequence<List<Integer>> sequence = shrinkable.shrink(filteredFalsifier);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(3, 3));
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(3));
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(3));
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(1));
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(1));
			assertThat(sequence.next(count, reporter)).isFalse();

			Assertions.assertThat(counter.get()).isEqualTo(5);
		}

		@Example
		void longList() {
			List<Shrinkable<Integer>> elementShrinkables =
				IntStream.range(1, 200)
						 .mapToObj(OneStepShrinkable::new)
						 .collect(Collectors.toList());
			Shrinkable<List<Integer>> shrinkable = new ShrinkableList<>(elementShrinkables, 0);

			ShrinkingSequence<List<Integer>> sequence = shrinkable.shrinkWithCondition(List::isEmpty);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).hasSize(100);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).hasSize(50);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).hasSize(25);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).hasSize(13);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).hasSize(9);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).hasSize(8);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).hasSize(7);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).hasSize(6);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).hasSize(5);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).hasSize(4);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).hasSize(3);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).hasSize(2);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).hasSize(1);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).hasSize(1);
			assertThat(sequence.next(count, reporter)).isFalse();

			Assertions.assertThat(counter.get()).isEqualTo(14);
		}
	}

	private Shrinkable<List<Integer>> createShrinkableList(Integer... listValues) {
		List<Shrinkable<Integer>> elementShrinkables =
			Arrays.stream(listValues).map(OneStepShrinkable::new).collect(Collectors.toList());
		return new ShrinkableList<>(elementShrinkables, 0);
	}

}
