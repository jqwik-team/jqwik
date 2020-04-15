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
			assertThat(values(arbitrary.edgeCases())).containsExactlyInAnyOrder(
				-10, -2, -1, 0, 1, 2, 10
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
