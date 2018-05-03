package net.jqwik.properties.newShrinking;

import net.jqwik.api.*;
import net.jqwik.properties.newShrinking.ShrinkableTypesForTest.*;
import org.mockito.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Group
@Label("ShrinkableSet")
class ShrinkableSetTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;
	private Consumer<Set<Integer>> reporter = mock(Consumer.class);

	@Example
	void creation() {
		NShrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(0, 1, 2, 3), 0);
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(4, 6));
		assertThat(shrinkable.value()).containsExactly(0, 1, 2, 3);
	}


	@Example
	@Label("report all falsified on the way")
	void reportFalsified() {
		NShrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(4, 0, 1, 2), 0);

		ShrinkingSequence<Set<Integer>> sequence = shrinkable.shrink(Set::isEmpty);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).containsExactly(0, 1, 2);
		verifyLastReporterCall(0, 1, 2);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).containsExactly(0, 1);
		verifyLastReporterCall(0, 1);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).containsExactly(0);
		verifyLastReporterCall(0);

		assertThat(sequence.next(count, reporter)).isFalse();
		verifyNoMoreInteractions(reporter);
	}

	@SuppressWarnings("unchecked")
	private void verifyLastReporterCall(Object... elements) {
		ArgumentCaptor<Set> setCaptor = ArgumentCaptor.forClass(Set.class);
		verify(reporter).accept(setCaptor.capture());
		assertThat(setCaptor.getValue()).containsExactly(elements);
		Mockito.clearInvocations(reporter);
	}


	@Group
	class Shrinking {

		@Example
		void downAllTheWay() {
			NShrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(0, 1, 2), 0);

			ShrinkingSequence<Set<Integer>> sequence = shrinkable.shrink(aSet -> false);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(2);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(1);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(0);
			assertThat(sequence.next(count, reporter)).isFalse();

			assertThat(counter.get()).isEqualTo(3);
		}

		@Example
		void downToMinSize() {
			NShrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(0, 1, 2, 3, 4), 2);

			ShrinkingSequence<Set<Integer>> sequence = shrinkable.shrink(aSet -> true);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(4);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(3);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(2);
			assertThat(sequence.next(count, reporter)).isFalse();

			assertThat(counter.get()).isEqualTo(3);
		}

//		@Example
//		void downToNonEmpty() {
//			NShrinkable<Set<Integer>> shrinkable = createShrinkableSet("abcd", 0);
//
//			ShrinkingSequence<Set<Integer>> sequence = shrinkable.shrink(String::isEmpty);
//
//			assertThat(sequence.next(count, ignore -> {
//			})).isTrue();
//			assertThat(sequence.current().value().size()).isEqualTo(3);
//			assertThat(sequence.next(count, ignore -> {
//			})).isTrue();
//			assertThat(sequence.current().value().size()).isEqualTo(2);
//			assertThat(sequence.next(count, ignore -> {
//			})).isTrue();
//			assertThat(sequence.current().value().size()).isEqualTo(1);
//			assertThat(sequence.next(count, ignore -> {
//			})).isFalse();
//
//			assertThat(counter.get()).isEqualTo(3);
//		}
//
//		@Example
//		void alsoShrinkElements() {
//
//			NShrinkable<Set<Integer>> shrinkable = createShrinkableSet("bbb", 0);
//
//			ShrinkingSequence<Set<Integer>> sequence = shrinkable.shrink(aSet -> aSet.size() <= 1);
//
//			assertThat(sequence.next(count, ignore -> {
//			})).isTrue();
//			assertThat(sequence.current().value()).isEqualTo("bb");
//			assertThat(sequence.next(count, ignore -> {
//			})).isTrue();
//			assertThat(sequence.current().value().size()).isEqualTo(2);
//			assertThat(sequence.next(count, ignore -> {
//			})).isTrue();
//			assertThat(sequence.current().value().size()).isEqualTo(2);
//			assertThat(sequence.next(count, ignore -> {
//			})).isFalse();
//			assertThat(sequence.current().value()).isEqualTo("aa");
//
//			assertThat(counter.get()).isEqualTo(3);
//		}
//
//		@Example
//		void withFilterOnStringLength() {
//			NShrinkable<Set<Integer>> shrinkable = createShrinkableSet("cccc", 0);
//
//			Falsifier<String> falsifier = ignore -> false;
//			Falsifier<String> filteredFalsifier = falsifier.withFilter(aSet -> aSet.size() % 2 == 0);
//
//			ShrinkingSequence<Set<Integer>> sequence = shrinkable.shrink(filteredFalsifier);
//
//			assertThat(sequence.next(count, ignore -> {
//			})).isTrue();
//			assertThat(sequence.current().value()).isEqualTo("cccc");
//
//			assertThat(sequence.next(count, ignore -> {
//			})).isTrue();
//			assertThat(sequence.current().value()).isEqualTo("cc");
//
//			assertThat(sequence.next(count, ignore -> {
//			})).isTrue();
//			assertThat(sequence.current().value()).isEqualTo("cc");
//
//			assertThat(sequence.next(count, ignore -> {
//			})).isTrue();
//			assertThat(sequence.current().value()).isEqualTo("");
//
//			assertThat(sequence.next(count, ignore -> {
//			})).isFalse();
//
//			assertThat(counter.get()).isEqualTo(4);
//		}
//
//		@Example
//		void withFilterOnStringContents() {
//			NShrinkable<Set<Integer>> shrinkable = createShrinkableSet("ddd", 0);
//
//			Falsifier<String> falsifier = String::isEmpty;
//			Falsifier<String> filteredFalsifier = falsifier //
//				.withFilter(aSet -> aSet.startsWith("d") || aSet.startsWith("b"));
//			ShrinkingSequence<Set<Integer>> sequence = shrinkable.shrink(filteredFalsifier);
//
//			while (sequence.next(count, reporter)) {
//			}
//			assertThat(sequence.current().value()).isEqualTo("b");
//
//			assertThat(counter.get()).isEqualTo(6);
//		}

	}


	private NShrinkable<Set<Integer>> createShrinkableSet(List<Integer> listValues, int minSize) {
		Set<NShrinkable<Integer>> elementShrinkables = listValues.stream().map(OneStepShrinkable::new).collect(Collectors.toSet());
		return new ShrinkableSet<>(elementShrinkables, minSize);
	}

}
