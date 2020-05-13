package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Group
@Label("ShrinkableSet")
class ShrinkableSetTests {

	private final AtomicInteger counter = new AtomicInteger(0);
	private final Runnable count = counter::incrementAndGet;

	@SuppressWarnings("unchecked")
	private final Consumer<Set<Integer>> valueReporter = mock(Consumer.class);
	private final Consumer<FalsificationResult<Set<Integer>>> reporter = result -> valueReporter.accept(result.value());

	@Example
	void creation() {
		Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(0, 1, 2, 3), 0);
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(4, 6));
		assertThat(shrinkable.value()).containsExactly(0, 1, 2, 3);
	}


	@Example
	@Label("report all falsified on the way")
	void reportFalsified() {
		Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(4, 0, 1, 2), 0);

		ShrinkingSequence<Set<Integer>> sequence = shrinkable.shrink((TestingFalsifier<Set<Integer>>) Set::isEmpty);

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
		verifyNoMoreInteractions(valueReporter);
	}

	@SuppressWarnings("unchecked")
	private void verifyLastReporterCall(Object... elements) {
		ArgumentCaptor<Set> setCaptor = ArgumentCaptor.forClass(Set.class);
		verify(valueReporter).accept(setCaptor.capture());
		assertThat(setCaptor.getValue()).containsExactly(elements);
		Mockito.clearInvocations(valueReporter);
	}


	@Group
	class Shrinking {

		@Example
		void downAllTheWay() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(0, 1, 2), 0);

			ShrinkingSequence<Set<Integer>> sequence = shrinkable.shrink((TestingFalsifier<Set<Integer>>) aSet -> false);

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
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(0, 1, 2, 3, 4), 2);

			ShrinkingSequence<Set<Integer>> sequence = shrinkable.shrink((TestingFalsifier<Set<Integer>>) aSet -> false);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(4);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(3);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().size()).isEqualTo(2);
			assertThat(sequence.current().value()).containsExactly(0, 1);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).containsExactly(0, 1);

			while(sequence.next(count, reporter));
			assertThat(sequence.current().value()).containsExactly(0, 1);
		}

		@Example
		void downToNonEmpty() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(0, 1, 2, 3), 0);

			ShrinkingSequence<Set<Integer>> sequence = shrinkable.shrink((TestingFalsifier<Set<Integer>>) Set::isEmpty);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).containsExactly(0, 1, 2);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).containsExactly(0, 1);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).containsExactly(0);
			assertThat(sequence.next(count, reporter)).isFalse();

			assertThat(counter.get()).isEqualTo(3);
		}

		@Example
		void alsoShrinkElements() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(2, 3, 4), 0);

			ShrinkingSequence<Set<Integer>> sequence = shrinkable.shrink((TestingFalsifier<Set<Integer>>) aSet -> aSet.size() <= 1);
			while (sequence.next(count, reporter));
			assertThat(sequence.current().value()).containsExactly(0, 1);
		}

		@Example
		void shrinkingResultHasValueAndThrowable() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(2, 3, 4), 0);

			ShrinkingSequence<Set<Integer>> sequence = shrinkable.shrink((TestingFalsifier<Set<Integer>>) integers -> {
				if (integers.size() > 1) throw new IllegalArgumentException("my reason");
				return true;
			});

			while(sequence.next(count, reporter)) {}

			assertThat(sequence.current().value()).containsExactly(0, 1);
			assertThat(sequence.current().throwable()).isPresent();
			assertThat(sequence.current().throwable().get()).isInstanceOf(IllegalArgumentException.class);
			assertThat(sequence.current().throwable().get()).hasMessage("my reason");
		}


		@Example
		void withFilterOnSetSize() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(1, 2, 3, 4), 0);

			TestingFalsifier<Set<Integer>> falsifier = ignore -> false;
			Falsifier<Set<Integer>> filteredFalsifier = falsifier.withFilter(aSet -> aSet.size() % 2 == 0);

			ShrinkingSequence<Set<Integer>> sequence = shrinkable.shrink(filteredFalsifier);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).containsExactly(1, 2, 3, 4);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).containsExactly(1, 2);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).containsExactly(1, 2);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEmpty();
			assertThat(sequence.next(count, reporter)).isFalse();

			assertThat(counter.get()).isEqualTo(4);
		}

		@Example
		void withFilterOnSetContents() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(2, 5, 6), 0);

			TestingFalsifier<Set<Integer>> falsifier = Set::isEmpty;
			Falsifier<Set<Integer>> filteredFalsifier = falsifier.withFilter(aSet -> aSet.contains(2) || aSet.contains(4));
			ShrinkingSequence<Set<Integer>> sequence = shrinkable.shrink(filteredFalsifier);

			while (sequence.next(count, reporter));
			assertThat(sequence.current().value()).containsExactly(2);
		}

		@Example
		void bigSet() {
			Set<Shrinkable<Integer>> elementShrinkables = IntStream.range(0, 1000).mapToObj(OneStepShrinkable::new).collect(Collectors.toSet());
			Shrinkable<Set<Integer>> shrinkable = new ShrinkableSet<>(elementShrinkables, 5);

			ShrinkingSequence<Set<Integer>> sequence = shrinkable.shrink((TestingFalsifier<Set<Integer>>) Set::isEmpty);

			while (sequence.next(count, reporter));
			assertThat(sequence.current().value()).hasSize(5);
		}

	}


	private Shrinkable<Set<Integer>> createShrinkableSet(List<Integer> listValues, int minSize) {
		Set<Shrinkable<Integer>> elementShrinkables = listValues.stream().map(OneStepShrinkable::new).collect(Collectors.toSet());
		return new ShrinkableSet<>(elementShrinkables, minSize);
	}

}
