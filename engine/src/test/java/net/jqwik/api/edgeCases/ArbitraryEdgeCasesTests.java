package net.jqwik.api.edgeCases;

import java.util.ArrayList;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

@Group
class ArbitraryEdgeCasesTests {

	@Example
	void mapping() {
		Arbitrary<String> arbitrary = Arbitraries.integers().between(-10, 10).map(i -> Integer.toString(i));
		EdgeCases<String> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				"-10", "-9", "-2", "-1", "0", "1", "2", "9", "10"
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(9);
	}

	@Example
	void filtering() {
		Arbitrary<Integer> arbitrary = Arbitraries.integers().between(-10, 10).filter(i -> i % 2 == 0);
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				-10, -2, 0, 2, 10
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(5);
	}

	@Example
	void ignoringExceptions() {
		Arbitrary<Integer> arbitrary =
				Arbitraries.integers().between(-10, 10)
						   .map(i -> {
							   if (i % 2 != 0) {
								   throw new IllegalArgumentException("Only even numbers");
							   }
							   return i;
						   })
						   .ignoreException(IllegalArgumentException.class);
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				-10, -2, 0, 2, 10
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(5);
	}

	@Example
	void injectNull() {
		Arbitrary<Integer> arbitrary = Arbitraries.of(-10, 10).injectNull(0.1);
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				null, -10, 10
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(3);
	}

	@Example
	void fixGenSize() {
		Arbitrary<Integer> arbitrary = Arbitraries.integers().between(-10, 10).fixGenSize(100);
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				-10, -9, -2, -1, 0, 1, 2, 9, 10
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(9);
	}

	@Example
	void unique() {
		Arbitrary<Integer> arbitrary = Arbitraries.integers().between(-10, 10).unique();
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				-10, -9, -2, -1, 0, 1, 2, 9, 10
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(9);
	}

	@Example
	void flatMapping() {
		Arbitrary<Integer> arbitrary = Arbitraries.of(5, 10)
												  .flatMap(i -> Arbitraries.integers().between(i, i * 10));

		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				5, 6, 49, 50, 10, 11, 99, 100
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(8);
	}

	@Example
	void optionals() {
		Arbitrary<Optional<Integer>> arbitrary = Arbitraries.of(-10, 10).optional();
		EdgeCases<Optional<Integer>> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				Optional.empty(), Optional.of(-10), Optional.of(10)
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(3);
	}

	@Group
	class CollectionTypes {

		@Example
		void listEdgeCases() {
			Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
			Arbitrary<List<Integer>> arbitrary = ints.list();
			assertThat(values(arbitrary.edgeCases())).containsExactlyInAnyOrder(
					Collections.emptyList(),
					Collections.singletonList(-10),
					Collections.singletonList(10)
			);
			// make sure edge cases can be repeatedly generated
			assertThat(values(arbitrary.edgeCases())).hasSize(3);
		}

		@Example
		void listEdgeCasesWhenMinSize1() {
			IntegerArbitrary ints = Arbitraries.integers().between(-10, 10);
			Arbitrary<List<Integer>> arbitrary = ints.list().ofMinSize(1);
			assertThat(values(arbitrary.edgeCases())).containsExactlyInAnyOrder(
					Collections.singletonList(-10),
					Collections.singletonList(-9),
					Collections.singletonList(-2),
					Collections.singletonList(-1),
					Collections.singletonList(0),
					Collections.singletonList(1),
					Collections.singletonList(2),
					Collections.singletonList(9),
					Collections.singletonList(10)
			);
		}

		@Example
		void listEdgeCasesWhenMinSizeGreaterThan1() {
			IntegerArbitrary ints = Arbitraries.integers().between(-10, 10);
			Arbitrary<List<Integer>> arbitrary = ints.list().ofMinSize(2);
			assertThat(values(arbitrary.edgeCases())).isEmpty();
		}

		@Example
		void listEdgeCasesWhenFixedSize() {
			Arbitrary<Integer> ints = Arbitraries.of(10, 100);
			Arbitrary<List<Integer>> arbitrary = ints.list().ofSize(3);
			assertThat(values(arbitrary.edgeCases())).containsExactlyInAnyOrder(
					asList(10, 10, 10),
					asList(100, 100, 100)
			);
		}

		@Example
		void listEdgeCasesAreGeneratedFreshlyOnEachCallToIterator() {
			IntegerArbitrary ints = Arbitraries.integers().between(-1, 1);
			Arbitrary<List<Integer>> arbitrary = ints.list();
			EdgeCases<List<Integer>> edgeCases = arbitrary.edgeCases();

			for (Shrinkable<List<Integer>> listShrinkable : edgeCases) {
				listShrinkable.value().add(42);
			}

			Set<List<Integer>> values = values(edgeCases);
			assertThat(values).containsExactlyInAnyOrder(
					Collections.emptyList(),
					Collections.singletonList(-1),
					Collections.singletonList(0),
					Collections.singletonList(1)
			);
		}

		@Example
		void setEdgeCases() {
			IntegerArbitrary ints = Arbitraries.integers().between(-10, 10);
			Arbitrary<Set<Integer>> arbitrary = ints.set();
			assertThat(values(arbitrary.edgeCases())).containsExactlyInAnyOrder(
					Collections.emptySet(),
					Collections.singleton(-10),
					Collections.singleton(-9),
					Collections.singleton(-2),
					Collections.singleton(-1),
					Collections.singleton(0),
					Collections.singleton(1),
					Collections.singleton(2),
					Collections.singleton(9),
					Collections.singleton(10)
			);
			assertThat(values(arbitrary.edgeCases())).hasSize(10);
		}

		@Example
		void setEdgeCasesWithMinSize1() {
			IntegerArbitrary ints = Arbitraries.integers().between(-10, 10);
			Arbitrary<Set<Integer>> arbitrary = ints.set().ofMinSize(1);
			assertThat(values(arbitrary.edgeCases())).containsExactlyInAnyOrder(
					Collections.singleton(-10),
					Collections.singleton(-9),
					Collections.singleton(-2),
					Collections.singleton(-1),
					Collections.singleton(0),
					Collections.singleton(1),
					Collections.singleton(2),
					Collections.singleton(9),
					Collections.singleton(10)
			);
		}

		@Example
		void streamEdgeCases() {
			Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
			Arbitrary<Stream<Integer>> arbitrary = ints.stream();
			Set<Stream<Integer>> streams = values(arbitrary.edgeCases());
			Set<List<Integer>> lists = streams.stream().map(stream -> stream.collect(Collectors.toList())).collect(Collectors.toSet());
			assertThat(lists).containsExactlyInAnyOrder(
					Collections.emptyList(),
					Collections.singletonList(-10),
					Collections.singletonList(10)
			);
			assertThat(values(arbitrary.edgeCases())).hasSize(3);
		}

		@Example
		void iteratorEdgeCases() {
			Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
			Arbitrary<Iterator<Integer>> arbitrary = ints.iterator();
			Set<Iterator<Integer>> iterators = values(arbitrary.edgeCases());
			Set<List<Integer>> lists =
					iterators.stream()
							 .map(iterator -> {
								 List<Integer> list = new ArrayList<>();
								 while (iterator.hasNext()) { list.add(iterator.next()); }
								 return list;
							 })
							 .collect(Collectors.toSet());
			assertThat(lists).containsExactlyInAnyOrder(
					Collections.emptyList(),
					Collections.singletonList(-10),
					Collections.singletonList(10)
			);
			assertThat(values(arbitrary.edgeCases())).hasSize(3);
		}

		@Example
		void arraysAreCombinationsOfElementsUpToMaxLength() {
			Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
			StreamableArbitrary<Integer, Integer[]> arbitrary = ints.array(Integer[].class);
			assertThat(values(arbitrary.edgeCases())).containsExactlyInAnyOrder(
					new Integer[]{},
					new Integer[]{-10},
					new Integer[]{10}
			);
			assertThat(values(arbitrary.edgeCases())).hasSize(3);
		}

		@Example
		void tupleEdgeCases() {
			Arbitrary<Integer> ints = Arbitraries.just(42);
			assertThat(values(ints.tuple1().edgeCases()))
					.containsExactlyInAnyOrder(Tuple.of(42));
			assertThat(values(ints.tuple2().edgeCases()))
					.containsExactlyInAnyOrder(Tuple.of(42, 42));
			assertThat(values(ints.tuple3().edgeCases()))
					.containsExactlyInAnyOrder(Tuple.of(42, 42, 42));
			assertThat(values(ints.tuple4().edgeCases()))
					.containsExactlyInAnyOrder(Tuple.of(42, 42, 42, 42));
		}

	}

	@Group
	class GenericConfiguration {

		@Example
		void noEdgeCases() {
			Arbitrary<String> arbitrary =
					Arbitraries
							.of("one", "two", "three")
							.edgeCases(edgeCasesConfig -> edgeCasesConfig.none());

			EdgeCases<String> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).isEmpty();

			// Random value generation still works
			ArbitraryTestHelper.assertAllGenerated(arbitrary.generator(1000), s -> {
				assertThat(s).isIn("one", "two", "three");
			});
		}

		@Example
		void filter() {
			Arbitrary<String> arbitrary =
					Arbitraries
							.of("one", "two", "three")
							.edgeCases(edgeCasesConfig -> {
								edgeCasesConfig.filter(s -> s.contains("t"));
								edgeCasesConfig.filter(s -> s.contains("e"));
							});

			EdgeCases<String> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder("three");
		}

		@Example
		void addingEdgeCases() {
			Arbitrary<String> arbitrary =
					Arbitraries
							.of("one", "two", "three")
							.edgeCases(edgeCasesConfig -> {
								edgeCasesConfig.add("two");
								edgeCasesConfig.add("four");
							});

			EdgeCases<String> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder("one", "two", "three", "four");
		}

		@Example
		void combineFilterAndAdd() {
			Arbitrary<String> arbitrary =
					Arbitraries
							.of("one", "two", "three")
							.edgeCases(edgeCasesConfig -> {
								edgeCasesConfig.filter(s -> s.contains("t"));
								edgeCasesConfig.add("two");
							});

			EdgeCases<String> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder("two", "three");
		}

		@Example
		void includeOnly() {
			Arbitrary<String> arbitrary =
					Arbitraries
							.of("one", "two", "three")
							.edgeCases(edgeCasesConfig -> {
								edgeCasesConfig.includeOnly("one", "four");
							});

			EdgeCases<String> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder("one");
		}

	}


	@Group
	class NumberConfiguration {

		@Example
		void noEdgeCases() {
			Arbitrary<Integer> arbitrary =
					Arbitraries
							.integers()
							.between(-100, 100)
					.edgeCases(edgeCasesConfig -> edgeCasesConfig.none());

			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).isEmpty();

			// Random value generation still works
			ArbitraryTestHelper.assertAllGenerated(arbitrary.generator(1000), i -> {
				assertThat(i).isBetween(-100, 100);
			});
		}

		@Example
		void filter() {
			Arbitrary<Integer> arbitrary =
					Arbitraries
							.integers()
							.between(-100, 100)
					.edgeCases(edgeCasesConfig -> {
						edgeCasesConfig.filter(i -> i % 2 == 0);
						edgeCasesConfig.filter(i -> i >= 0);
					});

			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(0, 2, 100);
		}

		@Example
		void addingEdgeCases() {
			Arbitrary<Integer> arbitrary =
					Arbitraries
							.integers()
							.between(-100, 100)
					.edgeCases(edgeCasesConfig -> {
						edgeCasesConfig.add(42);
						edgeCasesConfig.add(41);
					});

			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(-1, 0, -2, 1, 2, -99, -100, 99, 100, 41, 42);
		}

		@Example
		void addedIntegerEdgeCasesAreShrinkable() {
			Arbitrary<Integer> arbitrary =
					Arbitraries
							.integers()
							.between(-100, 100)
					.edgeCases(edgeCasesConfig -> {
						edgeCasesConfig.includeOnly();
						edgeCasesConfig.add(42);
					});

			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(edgeCases.size()).isEqualTo(1);

			Shrinkable<Integer> integerShrinkable = edgeCases.suppliers().get(0).get();
			assertThat(integerShrinkable.shrink()).isNotEmpty();
		}

		@Example
		void combineFilterAndAdd() {
			Arbitrary<Integer> arbitrary =
					Arbitraries
							.integers()
							.between(-100, 100)
					.edgeCases(edgeCasesConfig -> {
						edgeCasesConfig.filter(i -> i < 0);
						edgeCasesConfig.add(42);
						edgeCasesConfig.add(41);
					});

			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(-1, -2, -99, -100, 41, 42);

		}

		@Example
		void includeOnly() {
			Arbitrary<Integer> arbitrary =
					Arbitraries
							.integers()
							.between(-100, 100)
					.edgeCases(edgeCasesConfig -> edgeCasesConfig.includeOnly(-100, 100));

			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(-100, 100);
		}

	}

	private <T> Set<T> values(EdgeCases<T> edgeCases) {
		Set<T> values = new HashSet<>();
		for (Shrinkable<T> edgeCase : edgeCases) {
			values.add(edgeCase.value());
		}
		return values;
	}
}
