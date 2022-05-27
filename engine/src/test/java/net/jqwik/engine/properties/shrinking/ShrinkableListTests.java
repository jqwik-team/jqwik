package net.jqwik.engine.properties.shrinking;

import java.math.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;
import net.jqwik.testing.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingFalsifier.*;

@Group
@Label("ShrinkableList")
class ShrinkableListTests {

	@Example
	void creation() {
		Shrinkable<List<Integer>> shrinkable = createShrinkableList(0, 1, 2, 3);
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(4, 6));
		assertThat(shrinkable.value()).isEqualTo(asList(0, 1, 2, 3));
	}

	@Group
	class Shrinking {

		@Example
		void downAllTheWay() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(0, 1, 2);

			List<Integer> shrunkValue = shrink(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEmpty();
		}

		@Example
		void downToMinSize() {
			List<Shrinkable<Integer>> elementShrinkables =
					Arrays.stream(new Integer[]{0, 1, 2, 3, 4}).map(Shrinkable::unshrinkable).collect(Collectors.toList());
			Shrinkable<List<Integer>> shrinkable = new ShrinkableList<>(elementShrinkables, 2, 5);

			List<Integer> shrunkValue = shrink(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).hasSize(2);
		}

		@Example
		void downToOneElement() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(0, 1, 2);

			List<Integer> shrunkValue = shrink(shrinkable, falsifier(List::isEmpty), null);
			assertThat(shrunkValue).hasSize(1);
		}

		@Example
		void alsoShrinkElements() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(1, 2, 3);

			List<Integer> shrunkValue = shrink(shrinkable, falsifier(integers -> integers.size() <= 1), null);
			assertThat(shrunkValue).containsExactly(0, 0);
		}

		@Example
		void shrinkPairsTogether() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(3, 3);

			TestingFalsifier<List<Integer>> falsifier =
					integers -> integers.size() != 2 || !integers.get(0).equals(integers.get(1));

			List<Integer> shrunkValue = shrink(shrinkable, falsifier, null);
			assertThat(shrunkValue).containsExactly(0, 0);
		}

		@Property
		void shrinkAnyPairTogether(
				@ForAll @IntRange(max = 5) int index1,
				@ForAll @IntRange(max = 5) int index2
		) {
			Assume.that(index1 != index2);

			List<Shrinkable<Integer>> elementShrinkables =
					Arrays.stream(new Integer[]{10, 10, 10, 10, 10, 10, 10, 10, 10}).map(OneStepShrinkable::new)
						  .collect(Collectors.toList());
			Shrinkable<List<Integer>> shrinkable = new ShrinkableList<>(elementShrinkables, 6, 10);

			TestingFalsifier<List<Integer>> falsifier = integers -> {
				int int1 = integers.get(index1);
				int int2 = integers.get(index2);
				return int1 < 7 || int1 != int2;
			};

			List<Integer> shrunkValue = shrink(shrinkable, falsifier, null);

			List<Integer> expectedList = asList(0, 0, 0, 0, 0, 0);
			expectedList.set(index1, 7);
			expectedList.set(index2, 7);

			assertThat(shrunkValue).isEqualTo(expectedList);
		}

		@Example
		void shrinkToFullSortedList() {
			List<Shrinkable<Integer>> elementShrinkables = asList(
					Shrinkable.unshrinkable(4, ShrinkingDistance.of(4)),
					Shrinkable.unshrinkable(3, ShrinkingDistance.of(3)),
					Shrinkable.unshrinkable(1, ShrinkingDistance.of(1)),
					Shrinkable.unshrinkable(2, ShrinkingDistance.of(2))
			);
			Shrinkable<List<Integer>> shrinkable = new ShrinkableList<>(elementShrinkables, 0, 4);

			TestingFalsifier<List<Integer>> falsifier =
					integers -> {
						int sum = integers.stream().mapToInt(i -> i).sum();
						return sum < 10 || integers.size() < 4;
					};

			List<Integer> shrunkValue = shrink(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo(asList(1, 2, 3, 4));
		}

		@Example
		void shrinkToPartiallySortedList() {
			List<Shrinkable<Integer>> elementShrinkables = asList(
					Shrinkable.unshrinkable(4, ShrinkingDistance.of(4)),
					Shrinkable.unshrinkable(3, ShrinkingDistance.of(3)),
					Shrinkable.unshrinkable(1, ShrinkingDistance.of(1)),
					Shrinkable.unshrinkable(2, ShrinkingDistance.of(2))
			);
			Shrinkable<List<Integer>> shrinkable = new ShrinkableList<>(elementShrinkables, 0, 4);

			TestingFalsifier<List<Integer>> falsifier =
					integers -> {
						int sum = integers.stream().mapToInt(i -> i).sum();
						return sum < 10 || integers.size() < 4 || integers.get(3) != 2;
					};

			List<Integer> shrunkValue = shrink(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo(asList(1, 3, 4, 2));
		}

		@Example
		void shrinkingResultHasValueAndThrowable() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(1, 1, 1);

			TestingFalsifier<List<Integer>> falsifier = integers -> {
				if (integers.size() > 1) throw failAndCatch("my reason");
				return true;
			};

			ShrunkFalsifiedSample sample = shrinkToSample(shrinkable, falsifier, failAndCatch("original reason"));

			assertThat(sample.parameters()).containsExactly(asList(0, 0));
			assertThat(sample.falsifyingError()).isPresent();
			assertThat(sample.falsifyingError()).containsInstanceOf(AssertionError.class);
			assertThat(sample.falsifyingError().get()).hasMessage("my reason");
		}

		@Example
		void shrinkSizeAgainAfterShrinkingElements() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(1, 0, 2, 1);

			TestingFalsifier<List<Integer>> falsifier = integers -> integers.size() == new LinkedHashSet<>(integers).size();
			List<Integer> shrunkValue = shrink(shrinkable, falsifier, null);
			assertThat(shrunkValue).containsExactly(0, 0);
		}

		@Example
		void withFilterOnListSize() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(3, 3, 3, 3);

			TestingFalsifier<List<Integer>> falsifier = ignore -> false;
			Falsifier<List<Integer>> filteredFalsifier = falsifier.withFilter(
					elements -> elements.size() % 2 == 0
			);

			List<Integer> shrunkValue = shrink(shrinkable, filteredFalsifier, null);
			assertThat(shrunkValue).isEqualTo(asList());
		}

		@Example
		void withFilterOnElementContents() {
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(3, 3, 3);

			TestingFalsifier<List<Integer>> falsifier = List::isEmpty;
			Falsifier<List<Integer>> filteredFalsifier = falsifier.withFilter(
					elements -> elements.stream().allMatch(i -> i % 2 == 1)
			);

			List<Integer> shrunkValue = shrink(shrinkable, filteredFalsifier, null);
			assertThat(shrunkValue).isEqualTo(asList(1));
		}

		@Example
		void longList() {
			List<Shrinkable<Integer>> elementShrinkables =
					IntStream.range(1, 200)
							 .mapToObj(OneStepShrinkable::new)
							 .collect(Collectors.toList());
			Shrinkable<List<Integer>> shrinkable = new ShrinkableList<>(elementShrinkables, 0, 200);

			List<Integer> shrunkValue = shrink(shrinkable, falsifier(List::isEmpty), null);
			assertThat(shrunkValue).hasSize(1);
		}
	}

	@Group
	class UniquenessShrinking {

		@Example
		void shrinkToTwoElements() {
			List<Integer> values = Arrays.asList(56, 4, 3, 1);
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(values, 2, 3, i -> i);

			List<Integer> shrunkValue = shrink(shrinkable, TestingFalsifier.alwaysFalsify(), null);
			assertThat(shrunkValue).containsExactly(0, 1);
		}

		@Example
		void shrinkToTwoElementsWithFalsifier() {
			List<Integer> values = Arrays.asList(6, 23, 11, 99, 7, 44);
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(values, 2, 6, i -> i);

			TestingFalsifier<List<Integer>> oneNumberMustBeAbove5 = list -> list.stream().anyMatch(i -> i <= 5);
			List<Integer> shrunkValue = shrink(shrinkable, oneNumberMustBeAbove5, null);
			assertThat(shrunkValue).containsExactly(6, 7);
		}

		@Example
		void shrinkToManyElements() {
			List<Integer> values = Arrays.asList(1, 2, 3, 23, 77, 1, 0, 99, 7, 81, 55, 66);
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(values, 5, 10, i -> i);

			List<Integer> shrunkValue = shrink(shrinkable, TestingFalsifier.alwaysFalsify(), null);
			assertThat(shrunkValue).containsExactly(0, 1, 2, 3, 4);
		}

		@Example
		void shrinkWithMoreThanOneUniquenessExtractor() {
			List<Integer> values = Arrays.asList(2, 6, 23, 77, 0, 99, 7, 81, 55, 66);
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(
					values, 3, 10,
					i -> i,
					i -> (i % 2 == 0) ? i : i - 1 // Only one of 0|1, 2|3, 4|5 etc.
			);

			List<Integer> shrunkValue = shrink(shrinkable, TestingFalsifier.alwaysFalsify(), null);
			assertThat(shrunkValue).containsExactly(0, 2, 4);
		}

		@Example
		void shrinkingNeverCreatesListThatViolatesUniqueness() {
			List<Integer> values = Arrays.asList(56, 4, 23, 2, 95);
			Shrinkable<List<Integer>> shrinkable = createShrinkableList(values, 2, 5, i -> i);

			Predicate<List<Integer>> condition = list -> new LinkedHashSet<>(list).size() == list.size();
			assertWhileShrinking(shrinkable, condition);
		}


	}

	@Group
	class SumShrinking {

		@Example
		void shrinkSumOfPairToLastValue() {
			List<Shrinkable<Integer>> elementShrinkables =
					Arrays.stream(new Integer[]{17, 8}).map(OneStepShrinkable::new).collect(Collectors.toList());
			Shrinkable<List<Integer>> shrinkable = new ShrinkableList<>(elementShrinkables, 2, 2);

			TestingFalsifier<List<Integer>> falsifier =
					integers -> {
						int sum = integers.stream().mapToInt(i -> i).sum();
						return sum < 20;
					};

			List<Integer> shrunkValue = shrink(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo(asList(0, 20));
		}

		@Example
		void shrinkSumOfListTowardsEnd() {
			List<Shrinkable<Integer>> elementShrinkables =
					Arrays.stream(new Integer[]{10, 8, 5, 9}).map(OneStepShrinkable::new).collect(Collectors.toList());
			Shrinkable<List<Integer>> shrinkable = new ShrinkableList<>(elementShrinkables, 4, 4)
														   .filter(list -> list.stream().allMatch(i -> i <= 10));

			TestingFalsifier<List<Integer>> falsifier =
					integers -> {
						int sum = integers.stream().mapToInt(i -> i).sum();
						return sum < 21;
					};

			List<Integer> shrunkValue = shrink(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo(asList(0, 1, 10, 10));
		}

		@Example
		void sumOfFilteredValues() {
			Predicate<Integer> filter = i -> i % 2 == 0;
			List<Shrinkable<Integer>> elementShrinkables =
					Arrays.stream(new Integer[]{12, 8, 6, 4})
						  .map(OneStepShrinkable::new)
						  .map(s -> new FilteredShrinkable<>(s, filter))
						  .collect(Collectors.toList());
			Shrinkable<List<Integer>> shrinkable = new ShrinkableList<>(elementShrinkables, 4, 4)
														   .filter(list -> list.stream().allMatch(i -> i <= 10));

			TestingFalsifier<List<Integer>> falsifier =
					integers -> {
						int sum = integers.stream().mapToInt(i -> i).sum();
						return sum < 21;
					};

			List<Integer> shrunkValue = shrink(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo(asList(0, 2, 10, 10));
		}

		@Property(tries = 100)
		void sumOfIntegers(@ForAll Random random) {
			ListArbitrary<Integer> integerLists = Arbitraries.integers().between(0, 10).list().ofSize(4);

			TestingFalsifier<List<Integer>> falsifier =
					integers -> {
						int sum = integers.stream().mapToInt(i -> i).sum();
						return sum < 21;
					};

			List<Integer> shrunkValue = falsifyThenShrink(integerLists, random, falsifier);
			assertThat(shrunkValue).isEqualTo(asList(0, 1, 10, 10));
		}

		@Property(tries = 100)
		void sumOfIntegersWithShrinkingTarget(@ForAll Random random) {
			ListArbitrary<Integer> integerLists = Arbitraries.integers().between(0, 20).shrinkTowards(10).list().ofSize(4);

			TestingFalsifier<List<Integer>> falsifier =
					integers -> {
						int sum = integers.stream().mapToInt(i -> i).sum();
						return sum < 43;
					};

			List<Integer> shrunkValue = falsifyThenShrink(integerLists, random, falsifier);
			assertThat(shrunkValue).isEqualTo(asList(10, 10, 10, 13));
		}

		@Property(tries = 100)
		void sumOfShorts(@ForAll Random random) {
			ListArbitrary<Short> integerLists = Arbitraries.shorts().between((short) 0, (short) 10).list().ofSize(4);

			TestingFalsifier<List<Short>> falsifier =
					integers -> {
						int sum = integers.stream().mapToInt(i -> i).sum();
						return sum < 21;
					};

			List<Short> shrunkValue = falsifyThenShrink(integerLists, random, falsifier);
			assertThat(shrunkValue).isEqualTo(asList((short) 0, (short) 1, (short) 10, (short) 10));
		}

		@Property(tries = 100)
		void sumOfBytes(@ForAll Random random) {
			ListArbitrary<Byte> integerLists = Arbitraries.bytes().between((byte) 0, (byte) 10).list().ofSize(4);

			TestingFalsifier<List<Byte>> falsifier =
					integers -> {
						int sum = integers.stream().mapToInt(i -> i).sum();
						return sum < 21;
					};

			List<Byte> shrunkValue = falsifyThenShrink(integerLists, random, falsifier);
			assertThat(shrunkValue).isEqualTo(asList((byte) 0, (byte) 1, (byte) 10, (byte) 10));
		}

		@Property(tries = 100)
		void sumOfLongs(@ForAll Random random) {
			ListArbitrary<Long> integerLists = Arbitraries.longs().between(0, 10).list().ofSize(4);

			TestingFalsifier<List<Long>> falsifier =
					integers -> {
						long sum = integers.stream().mapToLong(i -> i).sum();
						return sum < 21L;
					};

			List<Long> shrunkValue = falsifyThenShrink(integerLists, random, falsifier);
			assertThat(shrunkValue).isEqualTo(asList(0L, 1L, 10L, 10L));
		}

		@Property(tries = 100)
		void sumOfIntegersAcrossLists(@ForAll Random random) {
			ListArbitrary<List<Integer>> listOfLists =
					Arbitraries.integers().between(0, 10)
							   .list().ofMaxSize(5)
							   .list().ofMaxSize(5);

			TestingFalsifier<List<List<Integer>>> falsifier =
					lol -> {
						int sum = lol.stream()
									 .flatMap(Collection::stream)
									 .mapToInt(i -> i).sum();
						return sum < 21;
					};

			List<List<Integer>> shrunkValue = falsifyThenShrink(listOfLists, random, falsifier);
			assertThat(shrunkValue).isEqualTo(asList(asList(1, 10, 10)));
		}

		@Property(tries = 100)
		void sumOfIntegersAcrossSets(@ForAll Random random) {
			Arbitrary<List<Set<Integer>>> listOfSets =
					Arbitraries.integers().between(0, 10)
							   .set().ofMaxSize(10)
							   .list().ofMaxSize(5)
							   .filter(lol -> {
								   List<Integer> allElements = lol.stream().flatMap(Collection::stream).collect(Collectors.toList());
								   return (allElements.size() == new LinkedHashSet<>(allElements).size());
							   });

			TestingFalsifier<List<Set<Integer>>> falsifier =
					lol -> {
						int sum = lol.stream()
									 .flatMap(Collection::stream)
									 .mapToInt(i -> i).sum();
						return sum < 21;
					};

			List<Set<Integer>> shrunkValue = falsifyThenShrink(listOfSets, random, falsifier);
			assertThat(shrunkValue).hasSize(1);
			// TODO: An even better shrinker should result in:
			// assertThat(shrunkValue).isEqualTo(asList(new HashSet<>(asList(2, 9, 10))));
		}

	}

	@SuppressWarnings("unchecked")
	private Shrinkable<List<Integer>> createShrinkableList(Integer... listValues) {
		return createShrinkableList(Arrays.asList(listValues), 0, listValues.length);
	}

	@SafeVarargs
	private final Shrinkable<List<Integer>> createShrinkableList(
			List<Integer> listValues,
			int min,
			int max,
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
		return new ShrinkableList<>(elementShrinkables, min, max, Arrays.asList(extractors));
	}

	private AssertionError failAndCatch(String message) {
		try {
			throw new AssertionError(message);
		} catch (AssertionError error) {
			return error;
		}
	}

}
