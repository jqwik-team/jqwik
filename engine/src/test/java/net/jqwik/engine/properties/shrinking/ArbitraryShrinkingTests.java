package net.jqwik.engine.properties.shrinking;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;

import static net.jqwik.engine.properties.ArbitraryTestHelper.*;

class ArbitraryShrinkingTests {

	@Property(tries = 10)
	void values(@ForAll Random random) {
		Arbitrary<Integer> arbitrary = Arbitraries.of(1, 2, 3);
		assertAllValuesAreShrunkTo(1, arbitrary, random);
	}

	@Property(tries = 10)
	void withExamples(@ForAll Random random) {
		Arbitrary<Integer> arbitrary = Arbitraries.of(1, 2, 3) //
			.withSamples(100, 200, 300);
		assertAllValuesAreShrunkTo(100, arbitrary, random);
	}

	@Property(tries = 10)
	void filtered(@ForAll Random random) {
		Arbitrary<Integer> arbitrary = Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) //
			.filter(i -> i % 2 == 0);
		assertAllValuesAreShrunkTo(2, arbitrary, random);
	}

	@Property(tries = 10)
	void unique(@ForAll Random random) {
		Arbitrary<Integer> arbitrary = Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) //
			.unique();
		assertAllValuesAreShrunkTo(1, arbitrary, random);
	}

	@Property(tries = 10)
	void uniqueInSet(@ForAll Random random) {
		Arbitrary<Set<Integer>> arbitrary = Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) //
			.unique().set().ofSize(3);
		assertAllValuesAreShrunkTo(new HashSet<>(Arrays.asList(1, 2, 3)), arbitrary, random);
	}

	@Property(tries = 10)
	void mapped(@ForAll Random random) {
		Arbitrary<String> arbitrary = Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) //
			.map(String::valueOf);
		assertAllValuesAreShrunkTo("1", arbitrary, random);
	}

	@Property(tries = 10)
	void flatMapped(@ForAll Random random) {
		Arbitrary<Integer> arbitrary = Arbitraries.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) //
			.flatMap(i -> Arbitraries.of(i));
		assertAllValuesAreShrunkTo(1, arbitrary, random);
	}

	@Property(tries = 10)
	void lazy(@ForAll Random random) {
		Arbitrary<Integer> arbitrary = Arbitraries.lazy( () -> Arbitraries.of(1, 2, 3, 4, 5, 6));
		assertAllValuesAreShrunkTo(1, arbitrary, random);
	}

	@Property(tries = 10)
	void forType(@ForAll Random random) {
		Arbitrary<Counter> arbitrary = Arbitraries.forType(Counter.class);
		assertAllValuesAreShrunkTo(new Counter(1, 1), arbitrary, random);
	}


	private static class Counter {
		public int n1, n2;

		public Counter(int n1, int n2) {
			if (n1 == 0 || n2 == 0) {
				throw new IllegalArgumentException("Numbers must not be 0");
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
