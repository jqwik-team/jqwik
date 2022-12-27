package net.jqwik.api;

import java.util.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.api.edgeCases.*;
import net.jqwik.api.statistics.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingFalsifier.*;
import static net.jqwik.testing.TestingSupport.*;

class ArbitrariesRecursiveTests {

	@Example
	void minMaxDepthRecursion(@ForAll JqwikRandom random) {
		Arbitrary<List<Integer>> lists = Arbitraries.recursive(
			() -> Arbitraries.create(ArrayList::new),
			list -> {
				Arbitrary<Integer> ints = Arbitraries.integers();
				return Combinators.combine(list, ints).as((l, i) -> {
					l.add(i);
					return l;
				});
			},
			5, 10
		);

		assertAllGenerated(lists, random, result -> {
			assertThat(result).hasSizeBetween(5, 10);
		});
	}

	@Example
	void fixedDepthRecursion(@ForAll JqwikRandom random) {
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

	@Example
	void zeroDepthRecursion(@ForAll JqwikRandom random) {
		Arbitrary<List<Integer>> lists = Arbitraries.recursive(
			() -> Arbitraries.create(ArrayList::new),
			list -> {
				Arbitrary<Integer> ints = Arbitraries.integers();
				return Combinators.combine(list, ints).as((l, i) -> {
					l.add(i);
					return l;
				});
			},
			0
		);

		assertAllGenerated(lists, random, result -> {
			assertThat(result).isEmpty();
		});
	}

	@Property(tries = 100, generation = GenerationMode.RANDOMIZED)
	@StatisticsReport(onFailureOnly = true)
	void recursionDepthDistribution(@ForAll("recursiveValue") int value) {
		Statistics.label("recursion depth")
				  .collect(value)
				  .coverage(checker -> {
					  checker.check(0).count(c -> c >= 1);
					  checker.check(1).count(c -> c >= 1);
					  checker.check(2).count(c -> c >= 1);
					  checker.check(3).count(c -> c >= 1);
					  checker.check(4).count(c -> c >= 1);
					  checker.check(5).count(c -> c >= 1);
					  checker.check(6).count(c -> c >= 1);
					  checker.check(7).count(c -> c >= 1);
					  checker.check(8).count(c -> c >= 1);
					  checker.check(9).count(c -> c >= 1);
				  });
	}

	@Provide
	Arbitrary<Integer> recursiveValue() {
		return Arbitraries.recursive(() -> Arbitraries.just(0), ints -> ints.map(i -> i + 1), 0, 9);
	}

	@Group
	class ExhaustiveGeneration {

		@Example
		void fixedDepthRecursion() {
			Optional<ExhaustiveGenerator<Integer>> optionalGenerator =
				fixedDepthRecursiveIntArbitrary().exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Integer> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(2);
			assertThat(generator).containsExactly(8, 13);
		}

		@Example
		void minMaxDepthRecursion() {
			Optional<ExhaustiveGenerator<Integer>> optionalGenerator =
				minMaxDepthRecursiveIntArbitrary().exhaustive();
			assertThat(optionalGenerator).isPresent();

			ExhaustiveGenerator<Integer> generator = optionalGenerator.get();
			assertThat(generator.maxCount()).isEqualTo(6);
			assertThat(generator).containsExactlyInAnyOrder(7, 8, 9, 12, 13, 14);
		}

		@Example
		void tooManyCombinations() {
			Arbitrary<Integer> base = Arbitraries.integers();
			Optional<ExhaustiveGenerator<Integer>> optionalGenerator =
				Arbitraries.recursive(() -> base, ints -> ints.map(i -> i + 1), 1, 1000).exhaustive();
			assertThat(optionalGenerator).isNotPresent();
		}

	}

	@Group
	class GenerationTests implements GenericGenerationProperties {
		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			return Arbitraries.of(
				fixedDepthRecursiveIntArbitrary(),
				minMaxDepthRecursiveIntArbitrary()
			);
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
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactly(8, 13);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(2);
		}

		@Example
		void minMaxDepthRecursion() {
			Arbitrary<Integer> arbitrary = minMaxDepthRecursiveIntArbitrary();
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).containsExactlyInAnyOrder(7, 9, 12, 14);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(4);
		}

		@Example
		void noEdgeCasesForRecursionDepthAbove100() {
			Arbitrary<Integer> base = Arbitraries.of(5, 10);
			Arbitrary<Integer> arbitrary = Arbitraries.recursive(() -> base, ints -> ints.map(i -> i + 1), 101);
			EdgeCases<Integer> edgeCases = arbitrary.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).isEmpty();
		}

	}

	@Group
	@PropertyDefaults(tries = 20)
	class ShrinkingTests {

		@Property
		void fixedDepthRecursion(@ForAll JqwikRandom random) {
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

		@Property
		void minMaxDepthRecursion(@ForAll JqwikRandom random) {
			Arbitrary<Integer> base = Arbitraries.integers().between(1, 10);
			Arbitrary<Integer> integer = Arbitraries.recursive(
				() -> Arbitraries.just(0),
				anInt -> Combinators.combine(anInt, base).as(Integer::sum),
				5, 10
			);

			RandomGenerator<Integer> generator = integer.generator(10, true);
			Shrinkable<Integer> shrinkable = generator.next(random);
			Integer shrunkValue = shrink(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEqualTo(5);
		}

		@Property
		void complexShrinking(@ForAll JqwikRandom random) {
			Arbitrary<String> sentences = sentences(20);

			TestingFalsifier<String> max10words = sentence -> sentence.split(" ").length <= 10;
			String shrunkSentence = falsifyThenShrink(sentences, random, max10words);
			assertThat(shrunkSentence).isEqualTo("AA AA AA AA AA AA AA AA AA AA AA.");
		}

	}

	private Arbitrary<Integer> fixedDepthRecursiveIntArbitrary() {
		Arbitrary<Integer> base = Arbitraries.of(5, 10);
		return Arbitraries.recursive(() -> base, ints -> ints.map(i -> i + 1), 3);
	}

	private Arbitrary<Integer> minMaxDepthRecursiveIntArbitrary() {
		Arbitrary<Integer> base = Arbitraries.of(5, 10);
		return Arbitraries.recursive(() -> base, ints -> ints.map(i -> i + 1), 2, 4);
	}

	private Arbitrary<String> sentences(int maxWords) {
		Arbitrary<String> lastWord = word().map(w -> w + ".");

		return Arbitraries.recursive(
			() -> lastWord,
			this::prependWord,
			0, maxWords - 1
		);
	}

	private StringArbitrary word() {
		return Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(10);
	}

	private Arbitrary<String> prependWord(Arbitrary<String> sentence) {
		return Combinators.combine(word(), sentence).as((w, s) -> w + " " + s);
	}

}
