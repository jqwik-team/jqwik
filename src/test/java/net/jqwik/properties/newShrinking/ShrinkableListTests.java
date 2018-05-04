package net.jqwik.properties.newShrinking;

import net.jqwik.api.*;
import net.jqwik.properties.newShrinking.ShrinkableTypesForTest.*;
import org.assertj.core.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Group
@Label("ShrinkableList")
class ShrinkableListTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;
	private Consumer<List<Integer>> reporter = mock(Consumer.class);

	@Example
	void creation() {
		NShrinkable<List<Integer>> shrinkable = createShrinkableList(0, 1, 2, 3);
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(4, 6));
		assertThat(shrinkable.value()).isEqualTo(asList(0, 1, 2, 3));
	}

	@Group
	class ReportFalsified {

		@Example
		@Label("report all falsified on the way")
		void downAllTheWay() {
			NShrinkable<List<Integer>> shrinkable = createShrinkableList(0, 1, 2);

			ShrinkingSequence<List<Integer>> sequence = shrinkable.shrink(aList -> false);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(0, 1));
			verify(reporter).accept(asList(0, 1));

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(0));
			verify(reporter).accept(asList(0));

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList());
			verify(reporter).accept(asList());

			assertThat(sequence.next(count, reporter)).isFalse();
			verifyNoMoreInteractions(reporter);
		}

		@Example
		@Label("also report falsified elements")
		void withElementShrinking() {
			NShrinkable<List<Integer>> shrinkable = createShrinkableList(3, 3, 3);

			ShrinkingSequence<List<Integer>> sequence = shrinkable.shrink(List::isEmpty);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(3, 3));
			verify(reporter).accept(asList(3, 3));

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(3));
			verify(reporter).accept(asList(3));

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(2));
			verify(reporter).accept(asList(2));

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(1));
			verify(reporter).accept(asList(1));

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(0));
			verify(reporter).accept(asList(0));

			assertThat(sequence.next(count, reporter)).isFalse();
			verifyNoMoreInteractions(reporter);
		}

	}

	@Group
	class Shrinking {

		@Example
		void downAllTheWay() {
			NShrinkable<List<Integer>> shrinkable = createShrinkableList(0, 1, 2);

			ShrinkingSequence<List<Integer>> sequence = shrinkable.shrink(aList -> false);

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
		void downToMinsize() {
			List<NShrinkable<Integer>> elementShrinkables =
				Arrays.stream(new Integer[]{0, 1, 2, 3, 4}).map(NShrinkable::unshrinkable).collect(Collectors.toList());
			NShrinkable<List<Integer>> shrinkable = new ShrinkableList<>(elementShrinkables, 2);

			ShrinkingSequence<List<Integer>> sequence = shrinkable.shrink(aList -> false);

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
			NShrinkable<List<Integer>> shrinkable = createShrinkableList(0, 1, 2);

			ShrinkingSequence<List<Integer>> sequence = shrinkable.shrink(List::isEmpty);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(2);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(1);
			assertThat(sequence.next(count, reporter)).isFalse();

			Assertions.assertThat(counter.get()).isEqualTo(2);
		}

		@Example
		void alsoShrinkElements() {
			NShrinkable<List<Integer>> shrinkable = createShrinkableList(1, 1, 1);

			ShrinkingSequence<List<Integer>> sequence = shrinkable.shrink(integers -> integers.size() <= 1);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo(asList(1, 1));
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(2);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(2);
			assertThat(sequence.next(count, reporter)).isFalse();
			assertThat(sequence.current().value()).isEqualTo(asList(0, 0));

			Assertions.assertThat(counter.get()).isEqualTo(3);
		}

		@Example
		void withFilterOnListSize() {
			NShrinkable<List<Integer>> shrinkable = createShrinkableList(3, 3, 3, 3);

			Falsifier<List<Integer>> falsifier = ignore -> false;
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
			NShrinkable<List<Integer>> shrinkable = createShrinkableList(3, 3, 3);

			Falsifier<List<Integer>> falsifier = List::isEmpty;
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
			List<NShrinkable<Integer>> elementShrinkables =
				IntStream.range(1, 200)
						 .mapToObj(OneStepShrinkable::new)
						 .collect(Collectors.toList());
			NShrinkable<List<Integer>> shrinkable = new ShrinkableList<>(elementShrinkables, 0);

			Falsifier<List<Integer>> falsifier = List::isEmpty;
			ShrinkingSequence<List<Integer>> sequence = shrinkable.shrink(falsifier);

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

	private NShrinkable<List<Integer>> createShrinkableList(Integer... listValues) {
		List<NShrinkable<Integer>> elementShrinkables =
			Arrays.stream(listValues).map(OneStepShrinkable::new).collect(Collectors.toList());
		return new ShrinkableList<>(elementShrinkables, 0);
	}

}
