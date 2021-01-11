package net.jqwik.api.edgeCases;

import java.util.ArrayList;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ArbitraryTestHelper.*;
import static net.jqwik.testing.TestingSupport.*;

@Group
class ArbitraryEdgeCasesTests {

	@Example
	void mapping() {
		Arbitrary<String> arbitrary = Arbitraries.integers().between(-10, 10).map(i -> Integer.toString(i));
		EdgeCases<String> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCases(edgeCases)).containsExactlyInAnyOrder(
				"-10", "-9", "-2", "-1", "0", "1", "2", "9", "10"
		);
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCases(edgeCases)).hasSize(9);
	}

	@Example
	void filtering() {
		Arbitrary<Integer> arbitrary = Arbitraries.integers().between(-10, 10).filter(i -> i % 2 == 0);
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCases(edgeCases)).containsExactlyInAnyOrder(
				-10, -2, 0, 2, 10
		);
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCases(edgeCases)).hasSize(5);
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
		assertThat(collectEdgeCases(edgeCases)).containsExactlyInAnyOrder(
				-10, -2, 0, 2, 10
		);
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCases(edgeCases)).hasSize(5);
	}

	@Example
	void injectNull() {
		Arbitrary<Integer> arbitrary = Arbitraries.of(-10, 10).injectNull(0.1);
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCases(edgeCases)).containsExactlyInAnyOrder(
				null, -10, 10
		);
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCases(edgeCases)).hasSize(3);
	}

	@Example
	void fixGenSize() {
		Arbitrary<Integer> arbitrary = Arbitraries.integers().between(-10, 10).fixGenSize(100);
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCases(edgeCases)).containsExactlyInAnyOrder(
				-10, -9, -2, -1, 0, 1, 2, 9, 10
		);
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCases(edgeCases)).hasSize(9);
	}

	@Example
	void unique() {
		Arbitrary<Integer> arbitrary = Arbitraries.integers().between(-10, 10).unique();
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCases(edgeCases)).containsExactlyInAnyOrder(
				-10, -9, -2, -1, 0, 1, 2, 9, 10
		);
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCases(edgeCases)).hasSize(9);
	}

	@Example
	void flatMapping() {
		Arbitrary<Integer> arbitrary = Arbitraries.of(5, 10)
												  .flatMap(i -> Arbitraries.integers().between(i, i * 10));

		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCases(edgeCases)).containsExactlyInAnyOrder(
				5, 6, 49, 50, 10, 11, 99, 100
		);
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCases(edgeCases)).hasSize(8);
	}

	@Example
	void optionals() {
		Arbitrary<Optional<Integer>> arbitrary = Arbitraries.of(-10, 10).optional();
		EdgeCases<Optional<Integer>> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCases(edgeCases)).containsExactlyInAnyOrder(
				Optional.empty(), Optional.of(-10), Optional.of(10)
		);
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCases(edgeCases)).hasSize(3);
	}

	@Group
	class CollectionTypes {


		@Example
		void setEdgeCases() {
			IntegerArbitrary ints = Arbitraries.integers().between(-10, 10);
			Arbitrary<Set<Integer>> arbitrary = ints.set();
			assertThat(collectEdgeCases(arbitrary.edgeCases())).containsExactlyInAnyOrder(
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
			assertThat(collectEdgeCases(arbitrary.edgeCases())).hasSize(10);
		}

		@Example
		void setEdgeCasesWithMinSize1() {
			IntegerArbitrary ints = Arbitraries.integers().between(-10, 10);
			Arbitrary<Set<Integer>> arbitrary = ints.set().ofMinSize(1);
			assertThat(collectEdgeCases(arbitrary.edgeCases())).containsExactlyInAnyOrder(
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
			Set<Stream<Integer>> streams = collectEdgeCases(arbitrary.edgeCases());
			Set<List<Integer>> lists = streams.stream().map(stream -> stream.collect(Collectors.toList())).collect(Collectors.toSet());
			assertThat(lists).containsExactlyInAnyOrder(
					Collections.emptyList(),
					Collections.singletonList(-10),
					Collections.singletonList(10)
			);
			assertThat(collectEdgeCases(arbitrary.edgeCases())).hasSize(3);
		}

		@Example
		void arraysAreCombinationsOfElementsUpToMaxLength() {
			Arbitrary<Integer> ints = Arbitraries.of(-10, 10);
			StreamableArbitrary<Integer, Integer[]> arbitrary = ints.array(Integer[].class);
			assertThat(collectEdgeCases(arbitrary.edgeCases())).containsExactlyInAnyOrder(
					new Integer[]{},
					new Integer[]{-10},
					new Integer[]{10}
			);
			assertThat(collectEdgeCases(arbitrary.edgeCases())).hasSize(3);
		}

		@Example
		void tupleEdgeCases() {
			Arbitrary<Integer> ints = Arbitraries.just(42);
			assertThat(collectEdgeCases(ints.tuple1().edgeCases()))
					.containsExactlyInAnyOrder(Tuple.of(42));
			assertThat(collectEdgeCases(ints.tuple2().edgeCases()))
					.containsExactlyInAnyOrder(Tuple.of(42, 42));
			assertThat(collectEdgeCases(ints.tuple3().edgeCases()))
					.containsExactlyInAnyOrder(Tuple.of(42, 42, 42));
			assertThat(collectEdgeCases(ints.tuple4().edgeCases()))
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
			assertThat(collectEdgeCases(edgeCases)).isEmpty();

			// Random value generation still works
			assertAllGenerated(arbitrary.generator(1000), s -> {
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
			assertThat(collectEdgeCases(edgeCases)).containsExactlyInAnyOrder("three");
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
			assertThat(collectEdgeCases(edgeCases)).containsExactlyInAnyOrder("one", "two", "three", "four");
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
			assertThat(collectEdgeCases(edgeCases)).containsExactlyInAnyOrder("two", "three");
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
			assertThat(collectEdgeCases(edgeCases)).containsExactlyInAnyOrder("one");
		}

	}

}
