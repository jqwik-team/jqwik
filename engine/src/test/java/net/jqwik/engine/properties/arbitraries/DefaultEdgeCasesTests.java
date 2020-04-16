package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;

import static org.assertj.core.api.Assertions.*;

@Group
@Label("Default Edge Cases")
class DefaultEdgeCasesTests {

	@Group
	class Integrals {

		@Example
		void intEdgeCases() {
			IntegerArbitrary arbitrary = new DefaultIntegerArbitrary().between(-10, 10);
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				-10, -2, -1, 0, 1, 2, 10
			);
			// make sure edge cases can be repeatedly generated
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				-10, -2, -1, 0, 1, 2, 10
			);
		}

		@Example
		void intOnlyPositive() {
			IntegerArbitrary arbitrary = new DefaultIntegerArbitrary().between(5, 100);
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				5, 100
			);
		}

		@Example
		void intWithShrinkTarget() {
			IntegerArbitrary arbitrary = new DefaultIntegerArbitrary().between(5, 100).shrinkTowards(42);
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(values(edgeCases)).containsExactlyInAnyOrder(
				5, 42, 100
			);
		}

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
			// make sure edge cases can be repeatedly generated
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
