package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.mockito.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import static net.jqwik.api.ShrinkingTestHelper.*;

@Group
@Label("ShrinkableSet")
class ShrinkableSetTests {

	@SuppressWarnings("unchecked")
	private final Consumer<Set<Integer>> valueReporter = mock(Consumer.class);

	@Example
	void creation() {
		Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(0, 1, 2, 3), 0);
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(4, 6));
		assertThat(shrinkable.value()).containsExactly(0, 1, 2, 3);
	}

	@Example
	@Label("report all falsified on the way")
	void reportFalsified() {

		//noinspection unchecked
		Mockito.clearInvocations(valueReporter);

		Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(4, 0, 1, 2), 0);

		Set<Integer> shrunkValue = shrinkToEnd(shrinkable, falsifier(Set::isEmpty), valueReporter, null);
		assertThat(shrunkValue).containsExactly(0);

		ArgumentCaptor<Set<Integer>> setCaptor = ArgumentCaptor.forClass(Set.class);
		verify(valueReporter, atLeastOnce()).accept(setCaptor.capture());
		assertThat(setCaptor.getAllValues()).containsOnly(
			asSet(0, 1, 2),
			asSet(0, 1),
			asSet(0)
		);
		verifyNoMoreInteractions(valueReporter);
	}

	private Set<Integer> asSet(int... ints) {
		return Arrays
				   .stream(ints)
				   .boxed()
				   .collect(Collectors.toCollection(HashSet::new));
	}

	@Group
	class Shrinking {

		@Example
		void downAllTheWay() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(0, 1, 2), 0);

			Set<Integer> shrunkValue = shrinkToEnd(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).hasSize(0);
		}

		@Example
		void downToMinSize() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(0, 1, 2, 3, 4), 2);

			Set<Integer> shrunkValue = shrinkToEnd(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).containsExactly(0, 1);
		}

		@Example
		void downToNonEmpty() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(0, 1, 2, 3), 0);

			Set<Integer> shrunkValue = shrinkToEnd(shrinkable, falsifier(Set::isEmpty), null);
			assertThat(shrunkValue).containsExactly(0);
		}

		@Example
		void alsoShrinkElements() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(2, 3, 4), 0);

			TestingFalsifier<Set<Integer>> falsifier = aSet -> aSet.size() <= 1;
			Set<Integer> shrunkValue = shrinkToEnd(shrinkable, falsifier, null);
			assertThat(shrunkValue).containsExactly(0, 1);
		}

		@Example
		void shrinkingResultHasValueAndThrowable() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(2, 3, 4), 0);

			TestingFalsifier<Set<Integer>> falsifier = integers -> {
				if (integers.size() > 1) throw failAndCatch("my reason");
				return true;
			};
			PropertyShrinkingResult result = shrink(shrinkable, falsifier, valueReporter, failAndCatch("original"));

			//noinspection unchecked
			assertThat((Set<Integer>) result.sample().get(0)).containsExactly(0, 1);
			assertThat(result.throwable()).isPresent();
			assertThat(result.throwable().get()).isInstanceOf(AssertionError.class);
			assertThat(result.throwable().get()).hasMessage("my reason");
		}

		@Example
		void withFilterOnSetSize() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(1, 2, 3, 4), 0);

			Falsifier<Set<Integer>> falsifier = falsifier(Set::isEmpty);
			Falsifier<Set<Integer>> filteredFalsifier = falsifier.withFilter(aSet -> aSet.size() % 2 == 0);

			Set<Integer> shrunkValue = shrinkToEnd(shrinkable, filteredFalsifier, null);
			assertThat(shrunkValue).containsExactly(0, 1);
		}

		@Example
		void withFilterOnSetContents() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(2, 5, 6), 0);

			Falsifier<Set<Integer>> falsifier = falsifier(Set::isEmpty);
			Falsifier<Set<Integer>> filteredFalsifier = falsifier.withFilter(aSet -> aSet.contains(2) || aSet.contains(4));
			Set<Integer> shrunkValue = shrinkToEnd(shrinkable, filteredFalsifier, null);
			assertThat(shrunkValue).containsExactly(2);
		}

		@Example
		void bigSet() {
			Set<Shrinkable<Integer>> elementShrinkables = IntStream.range(0, 1000).mapToObj(OneStepShrinkable::new)
																   .collect(Collectors.toSet());
			Shrinkable<Set<Integer>> shrinkable = new ShrinkableSet<>(elementShrinkables, 5);

			Set<Integer> shrunkValue = shrinkToEnd(shrinkable, falsifier(Set::isEmpty), null);
			assertThat(shrunkValue).containsExactly(0, 1, 2, 3, 4);
		}

	}

	private Shrinkable<Set<Integer>> createShrinkableSet(List<Integer> listValues, int minSize) {
		Set<Shrinkable<Integer>> elementShrinkables = listValues.stream().map(OneStepShrinkable::new).collect(Collectors.toSet());
		return new ShrinkableSet<>(elementShrinkables, minSize);
	}

}
