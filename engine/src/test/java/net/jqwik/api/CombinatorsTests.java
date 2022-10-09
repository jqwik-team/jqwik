package net.jqwik.api;

import java.util.ArrayList;
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.edgeCases.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingSupport.*;

class CombinatorsTests {

	private final Random random = SourceOfRandomness.current();

	@Example
	void twoArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine2 = Combinators.combine(one(), two()).as((a, b) -> a + b);
		Shrinkable<Integer> value = generate(combine2);
		assertThat(value.value()).isEqualTo(3);
	}

	@Example
	void threeArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine3 = Combinators.combine(one(), two(), three()).as((a, b, c) -> a + b + c);
		Shrinkable<Integer> value = generate(combine3);
		assertThat(value.value()).isEqualTo(6);
	}

	@Example
	void fourArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine4 = Combinators.combine(one(), two(), three(), four()).as((a, b, c, d) -> a + b + c + d);
		Shrinkable<Integer> value = generate(combine4);
		assertThat(value.value()).isEqualTo(10);
	}

	@Example
	void fiveArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine5 = Combinators.combine(one(), two(), three(), four(), five()) //
												 .as((a, b, c, d, e) -> a + b + c + d + e);
		Shrinkable<Integer> value = generate(combine5);
		assertThat(value.value()).isEqualTo(15);
	}

	@Example
	void sixArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine6 = Combinators.combine(one(), two(), three(), four(), five(), six()) //
												 .as((a, b, c, d, e, f) -> a + b + c + d + e + f);
		Shrinkable<Integer> value = generate(combine6);
		assertThat(value.value()).isEqualTo(21);
	}

	@Example
	void sevenArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine7 = Combinators.combine(one(), two(), three(), four(), five(), six(), seven()) //
												 .as((a, b, c, d, e, f, g) -> a + b + c + d + e + f + g);
		Shrinkable<Integer> value = generate(combine7);
		assertThat(value.value()).isEqualTo(28);
	}

	@Example
	void eightArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine8 = Combinators.combine(one(), two(), three(), four(), five(), six(), seven(), eight()) //
												 .as((a, b, c, d, e, f, g, h) -> a + b + c + d + e + f + g + h);
		Shrinkable<Integer> value = generate(combine8);
		assertThat(value.value()).isEqualTo(36);
	}

	@Example
	void listOfArbitrariesCanBeCombined() {
		List<Arbitrary<Integer>> listOfArbitraries = Arrays.asList(one(), one(), two(), two(), three(), three());
		Arbitrary<Integer> combineList = Combinators.combine(listOfArbitraries) //
													.as(list -> list.stream().mapToInt(e -> e).sum());
		Shrinkable<Integer> value = generate(combineList);
		assertThat(value.value()).isEqualTo(12);
	}

	@Group
	@Label("combine(..).filter(..)")
	class Filtering {

		Arbitrary<Integer> oneToThree() {
			return Arbitraries.integers().between(1, 3);
		}

		@Example
		void twoArbitraries(@ForAll Random random) {
			Arbitrary<Tuple.Tuple2<Integer, Integer>> combine2 =
				Combinators.combine(oneToThree(), oneToThree())
						   .filter((a, b) -> !a.equals(b))
						   .as(Tuple::of);

			assertAllGenerated(combine2.generator(1000), random, tuple -> {
				assertThat(tuple.get1())
					.describedAs("combination %s", tuple)
					.isNotEqualTo(tuple.get2());
			});
		}

		@Example
		void doubleFilters(@ForAll Random random) {
			Arbitrary<Tuple.Tuple2<Integer, Integer>> combine2 =
				Combinators.combine(oneToThree(), oneToThree())
						   .filter((a, b) -> a + b != 2)
						   .filter((a, b) -> a + b != 6)
						   .as(Tuple::of);

			assertAllGenerated(combine2.generator(1000), random, tuple -> {
				assertThat(tuple.get1() + tuple.get2())
					.isNotEqualTo(2);
				assertThat(tuple.get1() + tuple.get2())
					.isNotEqualTo(6);
			});
		}

	}

	@Group
	class GenerationTests implements GenericGenerationProperties {
		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> plus = Combinators.combine(a1, a2).as((i1, i2) -> i1 + i2);
			return Arbitraries.of(plus);
		}

	}

	@Group
	@SuppressWarnings("Convert2MethodRef")
	class EdgeCasesGeneration implements GenericEdgeCasesProperties {

		@Override
		public Arbitrary<Arbitrary<?>> arbitraries() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> plus = Combinators.combine(a1, a2).as((i1, i2) -> i1 + i2);
			return Arbitraries.of(plus);
		}

		@Example
		void combine2arbitraries() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1, a2)
										  .as((i1, i2) -> i1 + i2);

			EdgeCases<Integer> edgeCases = plus.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases))
				.containsExactlyInAnyOrder(11, 21, 12, 22);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(4);
		}

		@Example
		void noEdgeCasesWhenOneOfCombinedArbitrariesHasNone() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20).withoutEdgeCases();
			Arbitrary<Integer> plus = Combinators
										  .combine(a1, a2)
										  .as((i1, i2) -> i1 + i2);

			EdgeCases<Integer> edgeCases = plus.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases)).isEmpty();
		}

		@Example
		void combinationCanBeShrunk() {
			Arbitrary<Integer> a1 = Arbitraries.integers().between(-10, 10);
			Arbitrary<Integer> a2 = Arbitraries.integers().between(-100, 100);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1, a2)
										  .as((i1, i2) -> i1 + i2);
			EdgeCases<Integer> edgeCases = plus.edgeCases();

			Shrinkable<Integer> firstEdgeCase = edgeCases.iterator().next();

			Falsifier<Integer> falsifier = ignore -> TryExecutionResult.falsified(null);
			int shrunkValue = shrink(firstEdgeCase, falsifier, null);
			assertThat(shrunkValue).isEqualTo(0);
		}

		@Example
		void combine3arbitraries() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a3 = Arbitraries.of(100, 200);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1, a2, a3)
										  .as((i1, i2, i3) -> i1 + i2 + i3);

			EdgeCases<Integer> edgeCases = plus.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases))
				.containsExactlyInAnyOrder(111, 112, 211, 121, 221, 212, 122, 222);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(8);
		}

		@Example
		void combine4arbitraries() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a3 = Arbitraries.of(100, 200);
			Arbitrary<Integer> a4 = Arbitraries.of(1000, 2000);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1, a2, a3, a4)
										  .as((i1, i2, i3, i4) -> i1 + i2 + i3 + i4);

			EdgeCases<Integer> edgeCases = plus.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases))
				.contains(1111, 1112, 1121, 1211, 2111, 2222);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(16);
		}

		@Example
		void combine5arbitraries() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a3 = Arbitraries.of(100, 200);
			Arbitrary<Integer> a4 = Arbitraries.of(1000, 2000);
			Arbitrary<Integer> a5 = Arbitraries.of(10000, 20000);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1, a2, a3, a4, a5)
										  .as((i1, i2, i3, i4, i5) -> i1 + i2 + i3 + i4 + i5);

			EdgeCases<Integer> edgeCases = plus.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases))
				.contains(11111, 11112, 11121, 11211, 12111, 21111, 22222);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(32);
		}

		@Example
		void combine6arbitraries() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a3 = Arbitraries.of(100, 200);
			Arbitrary<Integer> a4 = Arbitraries.of(1000, 2000);
			Arbitrary<Integer> a5 = Arbitraries.of(10000, 20000);
			Arbitrary<Integer> a6 = Arbitraries.of(100000, 200000);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1, a2, a3, a4, a5, a6)
										  .as((i1, i2, i3, i4, i5, i6) -> i1 + i2 + i3 + i4 + i5 + i6);

			EdgeCases<Integer> edgeCases = plus.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases))
				.contains(111111, 111112, 111121, 111211, 112111, 121111, 211111, 222222);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(64);
		}

		@Example
		void combine7arbitraries() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a3 = Arbitraries.of(100, 200);
			Arbitrary<Integer> a4 = Arbitraries.of(1000, 2000);
			Arbitrary<Integer> a5 = Arbitraries.of(10000, 20000);
			Arbitrary<Integer> a6 = Arbitraries.of(100000, 200000);
			Arbitrary<Integer> a7 = Arbitraries.of(1000000, 2000000);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1, a2, a3, a4, a5, a6, a7)
										  .as((i1, i2, i3, i4, i5, i6, i7) -> i1 + i2 + i3 + i4 + i5 + i6 + i7);

			EdgeCases<Integer> edgeCases = plus.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases))
				.contains(1111111, 1111112, 1111121, 1111211, 1112111, 1121111, 1211111, 2111111, 2222222);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(128);
		}

		@Example
		void combine8arbitraries() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a3 = Arbitraries.of(100, 200);
			Arbitrary<Integer> a4 = Arbitraries.of(1000, 2000);
			Arbitrary<Integer> a5 = Arbitraries.of(10000, 20000);
			Arbitrary<Integer> a6 = Arbitraries.of(100000, 200000);
			Arbitrary<Integer> a7 = Arbitraries.of(1000000, 2000000);
			Arbitrary<Integer> a8 = Arbitraries.of(10000000, 20000000);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1, a2, a3, a4, a5, a6, a7, a8)
										  .as((i1, i2, i3, i4, i5, i6, i7, i8) -> i1 + i2 + i3 + i4 + i5 + i6 + i7 + i8);

			EdgeCases<Integer> edgeCases = plus.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases))
				.contains(11111111, 11111112, 11111121, 11111211, 11112111, 11121111, 11211111, 12111111, 21111111, 22222222);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(256);
		}

		@Example
		void combineArbitraryList() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2, 3);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a3 = Arbitraries.of(100, 200);
			Arbitrary<Integer> plus = Combinators
										  .combine(asList(a1, a2, a3))
										  .as(params -> params.stream().mapToInt(i -> i).sum());

			EdgeCases<Integer> edgeCases = plus.edgeCases();
			assertThat(collectEdgeCaseValues(edgeCases))
				.containsExactlyInAnyOrder(111, 113, 121, 123, 211, 213, 221, 223);
			// make sure edge cases can be repeatedly generated
			assertThat(collectEdgeCaseValues(edgeCases)).hasSize(8);
		}

		class AdditionBuilder {

			private final List<Integer> numbers = new ArrayList<>();

			AdditionBuilder addNumber(int number) {
				numbers.add(number);
				return this;
			}

			int sum() {
				return numbers.stream().mapToInt(n -> n).sum();
			}
		}
	}

	@Group
	class ExhaustiveGeneration {

		// @Example
		// Failing because exhaustive combination does not recreate each object every time.
		// Fixing this would require some fundamental change in exhaustive generation
		void combineStatefulObjects() {
			Arbitrary<StringBuilder> stringBuilders = Arbitraries.create(StringBuilder::new);
			Arbitrary<String> strings = Arbitraries.of("a", "b", "c");
			Arbitrary<String> append = Combinators
										   .combine(stringBuilders, strings)
										   .as((b, s) -> {
											   b.append(s);
											   return b.toString();
										   });

			assertThat(append.exhaustive()).isPresent();

			ExhaustiveGenerator<String> generator = append.exhaustive().get();
			assertThat(generator.maxCount()).isEqualTo(3);
			assertThat(generator).containsExactly("a", "b", "c");
		}

		@Example
		void combine2arbitraries() {
			Arbitrary<Integer> a1020 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a12 = Arbitraries.of(1, 2);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1020, a12)
										  .as((i1, i2) -> i1 + i2);

			assertThat(plus.exhaustive()).isPresent();

			ExhaustiveGenerator<Integer> generator = plus.exhaustive().get();
			assertThat(generator.maxCount()).isEqualTo(4);
			assertThat(generator).containsExactly(11, 12, 21, 22);
		}

		@Example
		void combine3arbitraries() {
			Arbitrary<Integer> a100200 = Arbitraries.of(100, 200);
			Arbitrary<Integer> a1020 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a12 = Arbitraries.of(1, 2);
			Arbitrary<Integer> plus = Combinators
										  .combine(a100200, a1020, a12)
										  .as((i1, i2, i3) -> i1 + i2 + i3);

			assertThat(plus.exhaustive()).isPresent();

			ExhaustiveGenerator<Integer> generator = plus.exhaustive().get();
			assertThat(generator.maxCount()).isEqualTo(8);
			assertThat(generator).containsExactly(111, 112, 121, 122, 211, 212, 221, 222);
		}

		@Example
		void combine4arbitraries() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a3 = Arbitraries.of(100, 200);
			Arbitrary<Integer> a4 = Arbitraries.of(1000, 2000);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1, a2, a3, a4)
										  .as((i1, i2, i3, i4) -> i1 + i2 + i3 + i4);

			assertThat(plus.exhaustive()).isPresent();

			ExhaustiveGenerator<Integer> generator = plus.exhaustive().get();
			assertThat(generator.maxCount()).isEqualTo(16);
			assertThat(generator).hasSize(16);
			assertThat(generator).contains(1111, 2222);
		}

		@Example
		void combine5arbitraries() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a3 = Arbitraries.of(100, 200);
			Arbitrary<Integer> a4 = Arbitraries.of(1000, 2000);
			Arbitrary<Integer> a5 = Arbitraries.of(10000, 20000);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1, a2, a3, a4, a5)
										  .as((i1, i2, i3, i4, i5) -> i1 + i2 + i3 + i4 + i5);

			assertThat(plus.exhaustive()).isPresent();

			ExhaustiveGenerator<Integer> generator = plus.exhaustive().get();
			assertThat(generator.maxCount()).isEqualTo(32);
			assertThat(generator).hasSize(32);
			assertThat(generator).contains(11111, 22222);
		}

		@Example
		void combine6arbitraries() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a3 = Arbitraries.of(100, 200);
			Arbitrary<Integer> a4 = Arbitraries.of(1000, 2000);
			Arbitrary<Integer> a5 = Arbitraries.of(10000, 20000);
			Arbitrary<Integer> a6 = Arbitraries.of(100000, 200000);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1, a2, a3, a4, a5, a6)
										  .as((i1, i2, i3, i4, i5, i6) -> i1 + i2 + i3 + i4 + i5 + i6);

			assertThat(plus.exhaustive()).isPresent();

			ExhaustiveGenerator<Integer> generator = plus.exhaustive().get();
			assertThat(generator.maxCount()).isEqualTo(64);
			assertThat(generator).hasSize(64);
			assertThat(generator).contains(111111, 222222);
		}

		@Example
		void combine7arbitraries() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a3 = Arbitraries.of(100, 200);
			Arbitrary<Integer> a4 = Arbitraries.of(1000, 2000);
			Arbitrary<Integer> a5 = Arbitraries.of(10000, 20000);
			Arbitrary<Integer> a6 = Arbitraries.of(100000, 200000);
			Arbitrary<Integer> a7 = Arbitraries.of(1000000, 2000000);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1, a2, a3, a4, a5, a6, a7)
										  .as((i1, i2, i3, i4, i5, i6, i7) -> i1 + i2 + i3 + i4 + i5 + i6 + i7);

			assertThat(plus.exhaustive()).isPresent();

			ExhaustiveGenerator<Integer> generator = plus.exhaustive().get();
			assertThat(generator.maxCount()).isEqualTo(128);
			assertThat(generator).hasSize(128);
			assertThat(generator).contains(1111111, 2222222);
		}

		@Example
		void combine8arbitraries() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a3 = Arbitraries.of(100, 200);
			Arbitrary<Integer> a4 = Arbitraries.of(1000, 2000);
			Arbitrary<Integer> a5 = Arbitraries.of(10000, 20000);
			Arbitrary<Integer> a6 = Arbitraries.of(100000, 200000);
			Arbitrary<Integer> a7 = Arbitraries.of(1000000, 2000000);
			Arbitrary<Integer> a8 = Arbitraries.of(10000000, 20000000);
			Arbitrary<Integer> plus = Combinators
										  .combine(a1, a2, a3, a4, a5, a6, a7, a8)
										  .as((i1, i2, i3, i4, i5, i6, i7, i8) -> i1 + i2 + i3 + i4 + i5 + i6 + i7 + i8);

			assertThat(plus.exhaustive()).isPresent();

			ExhaustiveGenerator<Integer> generator = plus.exhaustive().get();
			assertThat(generator.maxCount()).isEqualTo(256);
			assertThat(generator).hasSize(256);
			assertThat(generator).contains(11111111, 22222222);
		}

		@Example
		void combineArbitraryList() {
			Arbitrary<Integer> a1 = Arbitraries.of(1, 2, 3);
			Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
			Arbitrary<Integer> a3 = Arbitraries.of(100, 200);
			Arbitrary<Integer> plus = Combinators
										  .combine(asList(a1, a2, a3))
										  .as(params -> params.stream().mapToInt(i -> i).sum());

			assertThat(plus.exhaustive()).isPresent();

			ExhaustiveGenerator<Integer> generator = plus.exhaustive().get();
			assertThat(generator.maxCount()).isEqualTo(12);
			assertThat(generator).containsOnly(111, 112, 113, 121, 122, 123, 211, 212, 213, 221, 222, 223);
		}
	}

	private Shrinkable<Integer> generate(Arbitrary<Integer> integerArbitrary) {
		return integerArbitrary.generator(1, true).next(random);
	}

	Arbitrary<Integer> one() {
		return Arbitraries.of(1);
	}

	Arbitrary<Integer> two() {
		return Arbitraries.of(2);
	}

	Arbitrary<Integer> three() {
		return Arbitraries.of(3);
	}

	Arbitrary<Integer> four() {
		return Arbitraries.of(4);
	}

	Arbitrary<Integer> five() {
		return Arbitraries.of(5);
	}

	Arbitrary<Integer> six() {
		return Arbitraries.of(6);
	}

	Arbitrary<Integer> seven() {
		return Arbitraries.of(7);
	}

	Arbitrary<Integer> eight() {
		return Arbitraries.of(8);
	}

}
