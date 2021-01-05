package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;
import net.jqwik.testing.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ShrinkingTestHelper.*;

@Group
@Label("ShrinkableSet")
class ShrinkableSetTests {

	@Example
	void creation() {
		Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(0, 1, 2, 3), 0);
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(4, 6));
		assertThat(shrinkable.value()).containsExactly(0, 1, 2, 3);
	}

	@Group
	class Shrinking {

		@Example
		void downAllTheWay() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(0, 1, 2), 0);

			Set<Integer> shrunkValue = shrinkToMinimal(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).hasSize(0);
		}

		@Example
		void downToMinSize() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(0, 1, 2, 3, 4), 2);

			Set<Integer> shrunkValue = shrinkToMinimal(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).containsExactly(0, 1);
		}

		@Example
		void downToNonEmpty() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(0, 1, 2, 3), 0);

			Set<Integer> shrunkValue = shrinkToMinimal(shrinkable, falsifier(Set::isEmpty), null);
			assertThat(shrunkValue).containsExactly(0);
		}

		@Example
		void alsoShrinkElements() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(2, 3, 4), 0);

			TestingFalsifier<Set<Integer>> falsifier = aSet -> aSet.size() <= 1;
			Set<Integer> shrunkValue = shrinkToMinimal(shrinkable, falsifier, null);
			assertThat(shrunkValue).containsExactly(0, 1);
		}

		@Example
		void shrinkingResultHasValueAndThrowable() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(2, 3, 4), 0);

			TestingFalsifier<Set<Integer>> falsifier = integers -> {
				if (integers.size() > 1) throw failAndCatch("my reason");
				return true;
			};
			ShrunkFalsifiedSample sample = shrink(shrinkable, falsifier, failAndCatch("original"));

			//noinspection unchecked
			assertThat((Set<Integer>) sample.parameters().get(0)).containsExactly(0, 1);
			assertThat(sample.falsifyingError()).isPresent();
			sample.falsifyingError().ifPresent(error -> {
				assertThat(error).isInstanceOf(AssertionError.class);
				assertThat(error).hasMessage("my reason");
			});
		}

		@Example
		void withFilterOnSetSize() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(1, 2, 3, 4), 0);

			Falsifier<Set<Integer>> falsifier = falsifier(Set::isEmpty);
			Falsifier<Set<Integer>> filteredFalsifier = falsifier.withFilter(aSet -> aSet.size() % 2 == 0);

			Set<Integer> shrunkValue = shrinkToMinimal(shrinkable, filteredFalsifier, null);
			assertThat(shrunkValue).containsExactly(0, 1);
		}

		@Example
		void withFilterOnSetContents() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(2, 5, 6), 0);

			Falsifier<Set<Integer>> falsifier = falsifier(Set::isEmpty);
			Falsifier<Set<Integer>> filteredFalsifier = falsifier.withFilter(aSet -> aSet.contains(2) || aSet.contains(4));
			Set<Integer> shrunkValue = shrinkToMinimal(shrinkable, filteredFalsifier, null);
			assertThat(shrunkValue).containsExactly(2);
		}

		@Example
		void shrinkPairsTogether() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(12, 13), 0);

			TestingFalsifier<Set<Integer>> falsifier =
				integers -> {
					Iterator<Integer> iterator = integers.iterator();
					Integer first = iterator.next();
					Integer second = iterator.next();
					return integers.size() != 2 || Math.abs(first - second) > 1;
				};

			Set<Integer> shrunkValue = shrinkToMinimal(shrinkable, falsifier, null);
			assertThat(shrunkValue).containsExactly(0, 1);
		}

		@Example
		void shrinkAllPairsTogether() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(11, 12, 13, 14), 4);

			TestingFalsifier<Set<Integer>> falsifier =
				integers -> {
					Optional<Integer> min = integers.stream().min(Integer::compareTo);
					Optional<Integer> max = integers.stream().max(Integer::compareTo);
					return Math.abs(max.get() - min.get()) > 4;
				};

			Set<Integer> shrunkValue = shrinkToMinimal(shrinkable, falsifier, null);
			assertThat(shrunkValue).containsExactly(0, 1, 2, 3);
		}

		@Example
		void bigSet() {
			Set<Shrinkable<Integer>> elementShrinkables = IntStream.range(0, 1000).mapToObj(OneStepShrinkable::new)
																   .collect(Collectors.toSet());
			Shrinkable<Set<Integer>> shrinkable = new ShrinkableSet<>(elementShrinkables, 5, 1000);

			Set<Integer> shrunkValue = shrinkToMinimal(shrinkable, falsifier(Set::isEmpty), null);
			assertThat(shrunkValue).containsExactly(0, 1, 2, 3, 4);
		}

	}

	private Shrinkable<Set<Integer>> createShrinkableSet(List<Integer> listValues, int minSize) {
		Set<Shrinkable<Integer>> elementShrinkables = listValues.stream().map(OneStepShrinkable::new).collect(Collectors.toSet());
		return new ShrinkableSet<>(elementShrinkables, minSize, listValues.size());
	}

}
