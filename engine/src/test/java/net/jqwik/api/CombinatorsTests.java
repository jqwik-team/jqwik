package net.jqwik.api;

import java.util.*;
import java.util.function.*;

import net.jqwik.engine.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

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
	class ExhaustiveGeneration {

		@Example
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

		@Example
		void combineWithBuilder() {
			Arbitrary<Integer> numbers = Arbitraries.integers().between(1, 4);

			Supplier<ExhaustiveGenerationTests.AdditionBuilder> additionBuilderSupplier = ExhaustiveGenerationTests.AdditionBuilder::new;
			Arbitrary<Integer> sum = Combinators
				.withBuilder(additionBuilderSupplier)
				.use(numbers).in((b, n) -> b.addNumber(n))
				.use(numbers).in((b, n) -> b.addNumber(n))
				.build(ExhaustiveGenerationTests.AdditionBuilder::sum);

			assertThat(sum.exhaustive()).isPresent();

			ExhaustiveGenerator<Integer> generator = sum.exhaustive().get();
			assertThat(generator.maxCount()).isEqualTo(16);
			assertThat(generator).containsOnly(2, 3, 4, 5, 6, 7, 8);
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
