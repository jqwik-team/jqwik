package net.jqwik.engine.properties.shrinking;

import java.util.ArrayList;
import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ShrinkingTestHelper.*;

class ArbitraryShrinkingTests {

	@Property(tries = 10)
	void values(@ForAll Random random) {
		Arbitrary<Integer> arbitrary = Arbitraries.of(1, 2, 3);
		assertAllValuesAreShrunkTo(1, arbitrary, random);
	}

	@Property(tries = 10)
	void filtered(@ForAll Random random) {
		Arbitrary<Integer> arbitrary =
			Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).filter(i -> i % 2 == 0);
		assertAllValuesAreShrunkTo(2, arbitrary, random);
	}

	@Property(tries = 10)
	void ignoringException(@ForAll Random random) {
		Arbitrary<Integer> arbitrary =
			Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
					   .map(i -> {
						   if (i % 2 != 0) {
							   throw new IllegalArgumentException("Only even numbers");
						   }
						   return i;
					   })
					   .ignoreException(IllegalArgumentException.class);
		assertAllValuesAreShrunkTo(2, arbitrary, random);
	}

	@Property(tries = 10)
	void dontShrink(@ForAll Random random) {
		Arbitrary<Integer> arbitrary =
			Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).dontShrink();

		Shrinkable<Integer> shrinkable = arbitrary.generator(10).next(random);
		Falsifier<Integer> falsifier = ignore -> TryExecutionResult.falsified(null);
		int shrunkValue = shrinkToEnd(shrinkable, falsifier, null);
		assertThat(shrunkValue).isEqualTo(shrinkable.value());
	}

	@Property(tries = 10)
	void unique(@ForAll Random random) {
		Arbitrary<Integer> arbitrary =
			Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).unique();
		assertAllValuesAreShrunkTo(1, arbitrary, random);
	}

	@Property(tries = 10)
	void uniqueInSet(@ForAll Random random) {
		Arbitrary<Set<Integer>> arbitrary =
			Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).unique().set().ofSize(3);
		assertAllValuesAreShrunkTo(new HashSet<>(asList(1, 2, 3)), arbitrary, random);
	}

	@Property(tries = 10)
	void mapped(@ForAll Random random) {
		Arbitrary<String> arbitrary =
			Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).map(String::valueOf);
		assertAllValuesAreShrunkTo("1", arbitrary, random);
	}

	@Property(tries = 10)
	void flatMapped(@ForAll Random random) {
		Arbitrary<Integer> arbitrary =
			Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
					   .flatMap(i -> Arbitraries.of(i));
		assertAllValuesAreShrunkTo(1, arbitrary, random);
	}

	@Property(tries = 10)
	void flatMappedToString(@ForAll Random random) {
		Arbitrary<String> arbitrary =
			Arbitraries.integers().between(1, 10)
					   .flatMap(i -> Arbitraries.strings().withCharRange('a', 'z').ofLength(i));
		assertAllValuesAreShrunkTo("a", arbitrary, random);
	}

	@Provide
	Arbitrary<String> stringsOfLength1to10() {
		return Arbitraries.integers().between(1, 10)
						  .flatMap(i -> Arbitraries.strings().withCharRange('a', 'z').ofLength(i));

	}

	@Property(tries = 10)
	void lazy(@ForAll Random random) {
		Arbitrary<Integer> arbitrary =
			Arbitraries.lazy(() -> Arbitraries.of(1, 2, 3, 4, 5, 6));
		assertAllValuesAreShrunkTo(1, arbitrary, random);
	}

	@Property(tries = 10)
	boolean forType(@ForAll Random random) {
		Arbitrary<Counter> arbitrary = Arbitraries.forType(Counter.class);
		Counter value = shrinkToEnd(arbitrary, random);

		// 0:1, 1:0, 0:-1 or -1:0
		return Math.abs(value.n1 + value.n2) == 1;
	}

	@Property(tries = 10)
	void collectedListShrinksElementsAndSize(@ForAll Random random) {
		Arbitrary<Integer> integersShrunkTowardMax =
			Arbitraries
				.integers()
				.between(1, 3)
				.map(i -> 4 - i);

		Arbitrary<List<Integer>> collected = integersShrunkTowardMax.collect(list -> sum(list) >= 12);
		RandomGenerator<List<Integer>> generator = collected.generator(10);

		Shrinkable<List<Integer>> shrinkable = generator.next(random);
		TestingFalsifier<List<Integer>> falsifier = ignore1 -> false;
		List<Integer> shrunkValue = shrinkToEnd(shrinkable, falsifier, null);
		assertThat(shrunkValue).containsExactly(3, 3, 3, 3);
	}

	private int sum(List<Integer> list) {
		return list.stream().mapToInt(i -> i).sum();
	}

	@Group
	class Maps {

		@Property(tries = 10)
		boolean mapIsShrunkToEmptyMap(@ForAll Random random) {
			Arbitrary<Integer> keys = Arbitraries.integers().between(-10, 10);
			Arbitrary<String> values = Arbitraries.strings().alpha().ofLength(1);

			SizableArbitrary<Map<Integer, String>> arbitrary = Arbitraries.maps(keys, values).ofMaxSize(10);

			return shrinkToEnd(arbitrary, random).isEmpty();
		}

		@Property(tries = 10)
		void mapIsShrunkToSmallestValue(@ForAll Random random) {
			Arbitrary<Integer> keys = Arbitraries.integers().between(-10, 10);
			Arbitrary<String> values = Arbitraries.strings().withCharRange('A', 'Z').ofLength(1);

			SizableArbitrary<Map<Integer, String>> arbitrary = Arbitraries.maps(keys, values).ofMaxSize(10);

			TestingFalsifier<Map<Integer, String>> sumOfKeysLessThan2 = map -> map.keySet().size() < 2;
			Map<Integer, String> map = ShrinkingTestHelper.falsifyThenShrink(arbitrary, random, sumOfKeysLessThan2);

			assertThat(map).hasSize(2);
			assertThat(map.keySet()).containsAnyOf(0, 1, -1);
			assertThat(map.values()).containsOnly("A");
		}

	}

	@Group
	class MutableObjectShrinking {

		@Property
		@ExpectFailure(failureType = AssertionError.class, checkResult = ShrinkToMutable10.class)
		void mutableIsReset(
			@ForAll int before,
			@ForAll("mutable") Mutable mutable,
			@ForAll int after
		) {
			assertThat(mutable.otherValues()).isEmpty();
			mutable.addOtherValue(42);

			// Fails and invokes shrinking process
			assertThat(mutable.initValue).isLessThan(10);
		}

		private class ShrinkToMutable10 extends ShrinkToChecker {
			@Override
			public Iterable<?> shrunkValues() {
				return asList(0, new Mutable(10, asList(42)), 0);
			}
		}

		// TODO: Reimplement shrinking so that it handles mutable objects correctly
		@Disabled("Requires that shrinkables are properly reset before reuse during shrinking")
		//@Property(edgeCases = EdgeCasesMode.NONE)
		@ExpectFailure(failureType = AssertionError.class, checkResult = ShrinkToListOfMutable10.class)
		@Report(Reporting.FALSIFIED)
		void mutablesInListAreReset(
			@ForAll int before,
			@ForAll("listOfMutables") List<Mutable> list,
			@ForAll int after
		) {
			list.forEach(mutable -> {
				if (mutable.otherValues.size() != 1) {
					System.out.println("Wrong number of other values: " + mutable);
				}
			});
			list.forEach(mutable -> mutable.addOtherValue(mutable.initValue));

			// Fails and invokes shrinking process
			assertThat(list).allMatch(mutable -> mutable.initValue < 10);
		}

		private class ShrinkToListOfMutable10 extends ShrinkToChecker {
			@Override
			public Iterable<?> shrunkValues() {
				return asList(0, asList(new Mutable(10, asList(10, 10))), 0);
			}
		}

		@Provide
		Arbitrary<List<Mutable>> listOfMutables() {
			return mutable()
					   .list().ofMinSize(1)
					   .flatMapEach((all, each) -> {
						   return Arbitraries.of(all)
											 .map(other -> {
												 // each.otherValues.clear();
												 each.addOtherValue(other.initValue);
												 return each;
											 });
					   });
		}

		@Provide
		Arbitrary<Mutable> mutable() {
			return Arbitraries.integers().between(1, 10000).map(Mutable::new);
		}

	}

	private static class Mutable {
		final int initValue;
		private final List<Integer> otherValues;

		Mutable(int initValue) {
			this(initValue, new ArrayList<>());
		}

		Mutable(int initValue, List<Integer> otherValues) {
			this.initValue = initValue;
			this.otherValues = otherValues;
		}

		void addOtherValue(int otherValue) {
			this.otherValues.add(otherValue);
		}

		List<Integer> otherValues() {
			return otherValues;
		}

		@Override
		public String toString() {
			return String.format("Mutable(%s, %s)", initValue, otherValues);
		}

		@Override
		public boolean equals(final Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Mutable mutable = (Mutable) o;

			if (initValue != mutable.initValue) return false;
			return otherValues.equals(mutable.otherValues);
		}

		@Override
		public int hashCode() {
			int result = initValue;
			result = 31 * result + otherValues.hashCode();
			return result;
		}
	}

	private static class Counter {
		public int n1, n2;

		public Counter(int n1, int n2) {
			if (n1 == n2) {
				throw new IllegalArgumentException("Numbers must not be equal");
			}
			this.n1 = n1;
			this.n2 = n2;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Counter counter = (Counter) o;

			if (n1 != counter.n1) return false;
			return n2 == counter.n2;
		}

		@Override
		public int hashCode() {
			int result = n1;
			result = 31 * result + n2;
			return result;
		}

		@Override
		public String toString() {
			return n1 + ":" + n2;
		}
	}
}
