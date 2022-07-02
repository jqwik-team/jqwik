package net.jqwik.engine.properties.shrinking;

import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;
import net.jqwik.engine.support.*;
import net.jqwik.testing.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingFalsifier.*;

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

			Set<Integer> shrunkValue = shrink(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).hasSize(0);
		}

		@Example
		void downToMinSize() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(0, 1, 2, 3, 4), 2);

			Set<Integer> shrunkValue = shrink(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).containsExactly(0, 1);
		}

		@Example
		void downToNonEmpty() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(0, 1, 2, 3), 0);

			Set<Integer> shrunkValue = shrink(shrinkable, falsifier(Set::isEmpty), null);
			assertThat(shrunkValue).containsExactly(0);
		}

		@Example
		void alsoShrinkElements() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(2, 3, 4), 0);

			TestingFalsifier<Set<Integer>> falsifier = aSet -> aSet.size() <= 1;
			Set<Integer> shrunkValue = shrink(shrinkable, falsifier, null);
			assertThat(shrunkValue).containsExactly(0, 1);
		}

		@Example
		void shrinkingResultHasValueAndThrowable() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(2, 3, 4), 0);

			TestingFalsifier<Set<Integer>> falsifier = integers -> {
				if (integers.size() > 1) throw failAndCatch("my reason");
				return true;
			};
			ShrunkFalsifiedSample sample = shrinkToSample(shrinkable, falsifier, failAndCatch("original"));

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

			Set<Integer> shrunkValue = shrink(shrinkable, filteredFalsifier, null);
			assertThat(shrunkValue).containsExactly(0, 1);
		}

		@Example
		void withFilterOnSetContents() {
			Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(asList(2, 5, 6), 0);

			TestingFalsifier<Set<Integer>> falsifier = Set::isEmpty;
			Falsifier<Set<Integer>> filteredFalsifier = falsifier.withFilter(aSet -> aSet.contains(2) || aSet.contains(4));
			Set<Integer> shrunkValue = shrink(shrinkable, filteredFalsifier, null);
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

			Set<Integer> shrunkValue = shrink(shrinkable, falsifier, null);
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

			Set<Integer> shrunkValue = shrink(shrinkable, falsifier, null);
			assertThat(shrunkValue).containsExactly(0, 1, 2, 3);
		}

		@Example
		void bigSet() {
			Set<Shrinkable<Integer>> elementShrinkables = IntStream.range(0, 1000).mapToObj(OneStepShrinkable::new)
																   .collect(CollectorsSupport.toLinkedHashSet());
			Shrinkable<Set<Integer>> shrinkable = new ShrinkableSet<>(elementShrinkables, 5, 1000, Collections.emptySet());

			Set<Integer> shrunkValue = shrink(shrinkable, falsifier(Set::isEmpty), null);
			assertThat(shrunkValue).containsExactly(0, 1, 2, 3, 4);
		}

	}

	@Example
	void shrinkToTwoElements() {
		List<Integer> values = Arrays.asList(56, 4, 23, 2);
		Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(values, 2, i -> i % 5);

		Set<Integer> shrunkValue = shrink(shrinkable, TestingFalsifier.alwaysFalsify(), null);
		assertThat(shrunkValue).containsExactly(0, 1);
	}

	@Example
	void shrinkingNeverCreatesSetThatViolatesUniqueness() {
		List<Integer> values = Arrays.asList(56, 4, 23, 2, 95);
		Shrinkable<Set<Integer>> shrinkable = createShrinkableSet(values, 2, i -> i % 5);

		Predicate<Set<Integer>> condition = set -> isUniqueModulo(set, 5);
		assertWhileShrinking(shrinkable, condition);
	}

	private boolean isUniqueModulo(Set<Integer> list, int modulo) {
		List<Integer> moduloList = list.stream().map(i -> {
			if (i == null) {
				return null;
			}
			return i % modulo;
		}).collect(Collectors.toList());
		return new LinkedHashSet<>(moduloList).size() == list.size();
	}

	@SafeVarargs
	private final Shrinkable<Set<Integer>> createShrinkableSet(
			List<Integer> listValues,
			int min,
			FeatureExtractor<Integer>... extractors
	) {
		List<Shrinkable<Integer>> elementShrinkables =
				listValues.stream()
						  .map(i -> new ShrinkableBigInteger(
								  BigInteger.valueOf(i),
								  Range.of(BigInteger.ZERO, BigInteger.valueOf(100)),
								  BigInteger.valueOf(0)
						  ).map(BigInteger::intValueExact))
						  .collect(Collectors.toList());
		return new ShrinkableSet<>(elementShrinkables, min, listValues.size(), Arrays.asList(extractors));
	}

	private AssertionError failAndCatch(String message) {
		try {
			throw new AssertionError(message);
		} catch (AssertionError error) {
			return error;
		}
	}

}
