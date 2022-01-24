package net.jqwik.api;

import java.util.*;

import net.jqwik.api.edgeCases.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingFalsifier.*;
import static net.jqwik.testing.TestingSupport.*;

class ArbitrariesRecursiveTests {

	@Example
	void fixedDepthRecursion(@ForAll Random random) {
		Arbitrary<List<Integer>> lists = Arbitraries.recursive(
			() -> Arbitraries.create(ArrayList::new),
			list -> {
				Arbitrary<Integer> ints = Arbitraries.integers();
				return Combinators.combine(list, ints).as((l, i) -> {
					l.add(i);
					return l;
				});
			},
			10
		);

		assertAllGenerated(lists, random, result -> {
			assertThat(result).hasSize(10);
		});
	}

	private Arbitrary<Integer> fixedDepthRecursiveIntArbitrary() {
		Arbitrary<Integer> base = Arbitraries.integers().between(5, 10);
		return Arbitraries.recursive(() -> base, ints -> ints.map(i -> i + 1), 3);
	}

	@Group
	class ExhaustiveGeneration {

		@Example
		void fixedDepthRecursion() {
			Optional<ExhaustiveGenerator<Integer>> optionalGenerator =
				fixedDepthRecursiveIntArbitrary().exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Integer> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(6);
			assertThat(generator).containsExactly(8, 9, 10, 11, 12, 13);
		}

	}

	@Group
	class EdgeCasesTests implements GenericEdgeCasesProperties {

		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			return Arbitraries.of(
				fixedDepthRecursiveIntArbitrary()
			);
		}

		@Example
		void fixedDepthRecursion() {
			Arbitrary<Integer> arbitrary = fixedDepthRecursiveIntArbitrary();
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactly(8, 9, 12, 13);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(4);
		}

	}

	@Group
	class ShrinkingTests {

		@Property(tries = 10)
		void fixedDepthRecursion(@ForAll Random random) {
			Arbitrary<Integer> base = Arbitraries.integers().between(0, 10);
			Arbitrary<Integer> integer = Arbitraries.recursive(
				() -> base,
				anInt -> Combinators.combine(anInt, base).as(Integer::sum),
				10
			);

			RandomGenerator<Integer> generator = integer.generator(10, true);
			Shrinkable<Integer> shrinkable = generator.next(random);
			Integer shrunkValue = shrink(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEqualTo(0);
		}

	}

}
