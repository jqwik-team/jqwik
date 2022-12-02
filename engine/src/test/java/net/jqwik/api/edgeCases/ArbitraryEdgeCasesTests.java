package net.jqwik.api.edgeCases;

import java.util.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@Group
class ArbitraryEdgeCasesTests implements GenericEdgeCasesProperties {

	@Override
	public Arbitrary<Arbitrary<?>> arbitraries() {
		return Arbitraries.of(
			mappingArbitrary(),
			filteringArbitrary(),
			ignoringExceptionArbitrary(),
			Arbitraries.of(-10, 10).injectNull(0.1),
			Arbitraries.integers().between(-10, 10).fixGenSize(100),
			Arbitraries.of(-10, 10).optional(),
			Arbitraries.just(42).tuple5(),
			flatMappingArbitrary()
		);
	}

	@Example
	void mapping() {
		Arbitrary<String> arbitrary = mappingArbitrary();
		EdgeCases<String> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
				"-10", "-9", "-2", "-1", "0", "1", "2", "9", "10"
		);
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCaseValues(edgeCases)).hasSize(9);
	}

	private Arbitrary<String> mappingArbitrary() {
		return Arbitraries.integers().between(-10, 10).map(i -> Integer.toString(i));
	}

	@Example
	void filtering() {
		Arbitrary<Integer> arbitrary = filteringArbitrary();
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
				-10, -2, 0, 2, 10
		);
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCaseValues(edgeCases)).hasSize(5);
	}

	private Arbitrary<Integer> filteringArbitrary() {
		return Arbitraries.integers().between(-10, 10).filter(i -> i % 2 == 0);
	}

	private Arbitrary<Integer> ignoringExceptionArbitrary() {
		Arbitrary<Integer> arbitrary =
				Arbitraries.integers().between(-10, 10)
						   .map(i -> {
							   if (i % 2 != 0) {
								   throw new IllegalArgumentException("Only even numbers");
							   }
							   return i;
						   })
						   .ignoreException(IllegalArgumentException.class);
		return arbitrary;
	}

	@Example
	void injectNull() {
		Arbitrary<Integer> arbitrary = Arbitraries.of(-10, 10).injectNull(0.1);
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
				null, -10, 10
		);
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCaseValues(edgeCases)).hasSize(3);
	}

	@Example
	void fixGenSize() {
		Arbitrary<Integer> arbitrary = Arbitraries.integers().between(-10, 10).fixGenSize(100);
		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
				-10, -9, -2, -1, 0, 1, 2, 9, 10
		);
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCaseValues(edgeCases)).hasSize(9);
	}

	@Example
	void flatMapping() {
		Arbitrary<Integer> arbitrary = flatMappingArbitrary();

		EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
				5, 6, 49, 50, 10, 11, 99, 100
		);
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCaseValues(edgeCases)).hasSize(8);
	}

	private Arbitrary<Integer> flatMappingArbitrary() {
		return Arbitraries.of(5, 10)
						  .flatMap(i -> Arbitraries.integers().between(i, i * 10));
	}

	@Example
	void optionals() {
		Arbitrary<Optional<Integer>> arbitrary = Arbitraries.of(-10, 10).optional();
		EdgeCases<Optional<Integer>> edgeCases = arbitrary.edgeCases();
		assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(
				Optional.empty(), Optional.of(-10), Optional.of(10)
		);
		// make sure edge cases can be repeatedly generated
		assertThat(collectEdgeCaseValues(edgeCases)).hasSize(3);
	}

	@Example
	void tupleEdgeCases() {
		Arbitrary<Integer> ints = Arbitraries.just(42);
		assertThat(collectEdgeCaseValues(ints.tuple1().edgeCases()))
				.containsExactlyInAnyOrder(Tuple.of(42));
		assertThat(collectEdgeCaseValues(ints.tuple2().edgeCases()))
				.containsExactlyInAnyOrder(Tuple.of(42, 42));
		assertThat(collectEdgeCaseValues(ints.tuple3().edgeCases()))
				.containsExactlyInAnyOrder(Tuple.of(42, 42, 42));
		assertThat(collectEdgeCaseValues(ints.tuple4().edgeCases()))
				.containsExactlyInAnyOrder(Tuple.of(42, 42, 42, 42));
	}

	@Group
	class GenericConfiguration {

		@Example
		void noEdgeCases(@ForAll Random random) {
			Arbitrary<String> arbitrary =
				Arbitraries
					.of("one", "two", "three")
					.edgeCases(edgeCasesConfig -> edgeCasesConfig.none());

			EdgeCases<String> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).isEmpty();

			// Random value generation still works
			assertAllGenerated(
				arbitrary.generator(1000, true),
				random,
				s -> {
					assertThat(s).isIn("one", "two", "three");
				}
			);
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
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder("three");
		}

		@Example
		void addingEdgeCases() {
			Arbitrary<String> arbitrary =
					Arbitraries
							.of("one", "two", "three")
							.edgeCases(edgeCasesConfig -> {
								edgeCasesConfig.add("two");
								edgeCasesConfig.add("four");
								edgeCasesConfig.add("seven", "eight");
							});

			EdgeCases<String> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder("one", "two", "three", "four", "seven", "eight");
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
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder("two", "three");
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
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder("one");
		}

	}

}
