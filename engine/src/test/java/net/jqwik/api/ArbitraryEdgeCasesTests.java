package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

@Group
class ArbitraryEdgeCasesTests {

	@Example
	void mapping() {
		Arbitrary<String> arbitrary = Arbitraries.integers().between(-10, 10).map(i -> Integer.toString(i));
		EdgeCases<String> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
			"-10", "-2", "-1", "0", "1", "2", "10"
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(7);
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
	void injectNull() {
		Arbitrary<Integer> arbitrary = Arbitraries.integers().between(-10, 10).injectNull(0.1);
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
			-10, -2, -1, 0, 1, 2, 10, null
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(8);
	}

	@Example
	void fixGenSize() {
		Arbitrary<Integer> arbitrary = Arbitraries.integers().between(-10, 10).fixGenSize(100);
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
			-10, -2, -1, 0, 1, 2, 10
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(7);
	}

	@Example
	void unique() {
		Arbitrary<Integer> arbitrary = Arbitraries.integers().between(-10, 10).unique();
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
			-10, -2, -1, 0, 1, 2, 10
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(7);
	}

	@Example
	void flatMapping() {
		Arbitrary<Integer> arbitrary = Arbitraries.integers()
												  .between(5, 10)
												  .flatMap(i -> Arbitraries.integers().between(i, i * 10));

		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
			5, 50, 10, 100
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(4);
	}

	@Example
	void optionals() {
		Arbitrary<Optional<Integer>> arbitrary = Arbitraries.integers().between(-10, 10).optional();
		EdgeCases<Optional<Integer>> edgeCases = arbitrary.edgeCases();
		assertThat(values(edgeCases)).containsExactlyInAnyOrder(
			Optional.empty(), Optional.of(-10), Optional.of(-2), Optional.of(-1), Optional.of(0), Optional.of(1), Optional.of(2), Optional
																																	  .of(10)
		);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(8);
	}

	@Group
	class CollectionTypes {

		@Example
		void listEdgeCases() {
			IntegerArbitrary ints = new DefaultIntegerArbitrary().between(-10, 10);
			Arbitrary<List<Integer>> arbitrary = ints.list();
			assertThat(values(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				Collections.emptyList(),
				Collections.singletonList(-10),
				Collections.singletonList(-2),
				Collections.singletonList(-1),
				Collections.singletonList(0),
				Collections.singletonList(1),
				Collections.singletonList(2),
				Collections.singletonList(10)
			);
			// make sure edge cases can be repeatedly generated
			assertThat(values(arbitrary.edgeCases())).hasSize(8);
		}

		@Example
		void listEdgeCasesWhenMinSize1() {
			IntegerArbitrary ints = new DefaultIntegerArbitrary().between(-10, 10);
			Arbitrary<List<Integer>> arbitrary = ints.list().ofMinSize(1);
			assertThat(values(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				Collections.singletonList(-10),
				Collections.singletonList(-2),
				Collections.singletonList(-1),
				Collections.singletonList(0),
				Collections.singletonList(1),
				Collections.singletonList(2),
				Collections.singletonList(10)
			);
		}

		@Example
		void listEdgeCasesWhenMinSizeGreaterThan1() {
			IntegerArbitrary ints = new DefaultIntegerArbitrary().between(-10, 10);
			Arbitrary<List<Integer>> arbitrary = ints.list().ofMinSize(2);
			assertThat(values(arbitrary.edgeCases())).isEmpty();
		}

		@Example
		void listEdgeCasesAreGeneratedFreshlyOnEachCallToIterator() {
			IntegerArbitrary ints = new DefaultIntegerArbitrary().between(-1, 1);
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
			IntegerArbitrary ints = new DefaultIntegerArbitrary().between(-10, 10);
			Arbitrary<Set<Integer>> arbitrary = ints.set();
			assertThat(values(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				Collections.emptySet(),
				Collections.singleton(-10),
				Collections.singleton(-2),
				Collections.singleton(-1),
				Collections.singleton(0),
				Collections.singleton(1),
				Collections.singleton(2),
				Collections.singleton(10)
			);
			assertThat(values(arbitrary.edgeCases())).hasSize(8);
		}

		@Example
		void setEdgeCasesWithMinSize1() {
			IntegerArbitrary ints = new DefaultIntegerArbitrary().between(-10, 10);
			Arbitrary<Set<Integer>> arbitrary = ints.set().ofMinSize(1);
			assertThat(values(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				Collections.singleton(-10),
				Collections.singleton(-2),
				Collections.singleton(-1),
				Collections.singleton(0),
				Collections.singleton(1),
				Collections.singleton(2),
				Collections.singleton(10)
			);
		}

		@Example
		void streamEdgeCases() {
			IntegerArbitrary ints = new DefaultIntegerArbitrary().between(-10, 10);
			Arbitrary<Stream<Integer>> arbitrary = ints.stream();
			Set<Stream<Integer>> streams = values(arbitrary.edgeCases());
			Set<List<Integer>> lists = streams.stream().map(stream -> stream.collect(Collectors.toList())).collect(Collectors.toSet());
			assertThat(lists).containsExactlyInAnyOrder(
				Collections.emptyList(),
				Collections.singletonList(-10),
				Collections.singletonList(-2),
				Collections.singletonList(-1),
				Collections.singletonList(0),
				Collections.singletonList(1),
				Collections.singletonList(2),
				Collections.singletonList(10)
			);
			assertThat(values(arbitrary.edgeCases())).hasSize(8);
		}

		@Example
		void iteratorEdgeCases() {
			IntegerArbitrary ints = new DefaultIntegerArbitrary().between(-10, 10);
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
				Collections.singletonList(-2),
				Collections.singletonList(-1),
				Collections.singletonList(0),
				Collections.singletonList(1),
				Collections.singletonList(2),
				Collections.singletonList(10)
			);
			assertThat(values(arbitrary.edgeCases())).hasSize(8);
		}

		@Example
		void arraysAreCombinationsOfElementsUpToMaxLength() {
			IntegerArbitrary ints = new DefaultIntegerArbitrary().between(-10, 10);
			StreamableArbitrary<Integer, Integer[]> arbitrary = ints.array(Integer[].class);
			assertThat(values(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				new Integer[]{},
				new Integer[]{-10},
				new Integer[]{-2},
				new Integer[]{-1},
				new Integer[]{0},
				new Integer[]{1},
				new Integer[]{2},
				new Integer[]{10}
			);
			assertThat(values(arbitrary.edgeCases())).hasSize(8);
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
