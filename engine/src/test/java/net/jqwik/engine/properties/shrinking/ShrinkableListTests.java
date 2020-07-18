package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import static net.jqwik.api.ShrinkingTestHelper.*;

@Group
@Label("ShrinkableList")
class ShrinkableListTests {

	@SuppressWarnings("unchecked")
	private final Consumer<List<Integer>> valueReporter = mock(Consumer.class);

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

			List<Integer> shrunkValue = shrinkToEnd(shrinkable, alwaysFalsify(), valueReporter, null);
			assertThat(shrunkValue).isEqualTo(asList());

			verify(valueReporter).accept(asList(0, 1));
			verify(valueReporter).accept(asList(0));
			verify(valueReporter).accept(asList());
			verifyNoMoreInteractions(valueReporter);
		}

		@Example
		@Label("also report falsified elements")
		void withElementShrinking() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(3, 3, 3);

			List<Integer> shrunkValue = shrinkToEnd(shrinkable, falsifier(List::isEmpty), valueReporter, null);
			assertThat(shrunkValue).isEqualTo(asList(0));

			verify(valueReporter).accept(asList(3, 3));
			verify(valueReporter).accept(asList(3));
			verify(valueReporter).accept(asList(2));
			verify(valueReporter).accept(asList(1));
			verify(valueReporter).accept(asList(0));
			verifyNoMoreInteractions(valueReporter);
		}

	}

	@Group
	class Shrinking {

		@Example
		void downAllTheWay() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(0, 1, 2);

			List<Integer> shrunkValue = shrinkToEnd(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEmpty();
		}

		@Example
		void downToMinSize() {
			List<Shrinkable<Integer>> elementShrinkables =
				Arrays.stream(new Integer[]{0, 1, 2, 3, 4}).map(Shrinkable::unshrinkable).collect(Collectors.toList());
			Shrinkable<List<Integer>> shrinkable = new ShrinkableList<>(elementShrinkables, 2);

			List<Integer> shrunkValue = shrinkToEnd(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).hasSize(2);
		}

		@Example
		void downToOneElement() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(0, 1, 2);

			List<Integer> shrunkValue = shrinkToEnd(shrinkable, falsifier(List::isEmpty), null);
			assertThat(shrunkValue).hasSize(1);
		}

		@Example
		void alsoShrinkElements() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(1, 2, 3);

			List<Integer> shrunkValue = shrinkToEnd(shrinkable, falsifier(integers -> integers.size() <= 1), null);
			assertThat(shrunkValue).containsExactly(0, 0);
		}

		@Example
		void shrinkDuplicateElements() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(3, 3);

			TestingFalsifier<List<Integer>> falsifier =
				integers -> integers.size() != 2 || !integers.get(0).equals(integers.get(1));

			List<Integer> shrunkValue = shrinkToEnd(shrinkable, falsifier, null);
			assertThat(shrunkValue).containsExactly(0, 0);
		}

		@Example
		void shrinkingResultHasValueAndThrowable() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(1, 1, 1);

			TestingFalsifier<List<Integer>> falsifier = integers -> {
				if (integers.size() > 1) throw failAndCatch("my reason");
				return true;
			};

			ShrunkFalsifiedSample sample = shrink(shrinkable, falsifier, ignore -> {}, failAndCatch("original reason"));

			assertThat(sample.parameters()).containsExactly(asList(0, 0));
			assertThat(sample.falsifyingError()).isPresent();
			assertThat(sample.falsifyingError().get()).isInstanceOf(AssertionError.class);
			assertThat(sample.falsifyingError().get()).hasMessage("my reason");
		}

		@Example
		void shrinkSizeAgainAfterShrinkingElements() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(1, 0, 2, 1);

			TestingFalsifier<List<Integer>> falsifier = integers -> integers.size() == new HashSet<>(integers).size();
			List<Integer> shrunkValue = shrinkToEnd(shrinkable, falsifier, null);
			assertThat(shrunkValue).containsExactly(0, 0);
		}

		@Example
		void withFilterOnListSize() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(3, 3, 3, 3);

			TestingFalsifier<List<Integer>> falsifier = ignore -> false;
			Falsifier<List<Integer>> filteredFalsifier = falsifier.withFilter(
				elements -> elements.size() % 2 == 0
			);

			List<Integer> shrunkValue = shrinkToEnd(shrinkable, filteredFalsifier, null);
			assertThat(shrunkValue).isEqualTo(asList());
		}

		@Example
		void withFilterOnElementContents() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(3, 3, 3);

			TestingFalsifier<List<Integer>> falsifier = List::isEmpty;
			Falsifier<List<Integer>> filteredFalsifier = falsifier.withFilter(
				elements -> elements.stream().allMatch(i -> i % 2 == 1)
			);

			List<Integer> shrunkValue = shrinkToEnd(shrinkable, filteredFalsifier, null);
			assertThat(shrunkValue).isEqualTo(asList(1));
		}

		@Example
		void longList() {
			List<Shrinkable<Integer>> elementShrinkables =
				IntStream.range(1, 200)
						 .mapToObj(OneStepShrinkable::new)
						 .collect(Collectors.toList());
			Shrinkable<List<Integer>> shrinkable = new ShrinkableList<>(elementShrinkables, 0);

			List<Integer> shrunkValue = shrinkToEnd(shrinkable, falsifier(List::isEmpty), null);
			assertThat(shrunkValue).hasSize(1);
		}
	}

	private Shrinkable<List<Integer>> createShrinkableList(Integer... listValues) {
		List<Shrinkable<Integer>> elementShrinkables =
			Arrays.stream(listValues).map(OneStepShrinkable::new).collect(Collectors.toList());
		return new ShrinkableList<>(elementShrinkables, 0);
	}

}
