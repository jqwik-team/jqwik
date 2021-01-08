package net.jqwik.api.edgeCases;

import java.util.ArrayList;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;

@SuppressWarnings("Convert2MethodRef")
class CombinatorsEdgeCasesTests {

	@Example
	void combine2arbitraries() {
		Arbitrary<Integer> a1 = Arbitraries.of(1, 2);
		Arbitrary<Integer> a2 = Arbitraries.of(10, 20);
		Arbitrary<Integer> plus = Combinators
									  .combine(a1, a2)
									  .as((i1, i2) -> i1 + i2);

		EdgeCases<Integer> edgeCases = plus.edgeCases();
		assertThat(values(edgeCases))
			.containsExactlyInAnyOrder(11, 21, 12, 22);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(4);

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
		assertThat(values(edgeCases))
			.containsExactlyInAnyOrder(111, 112, 211, 121, 221, 212, 122, 222);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(8);
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
		assertThat(values(edgeCases))
			.contains(1111, 1112, 1121, 1211, 2111, 2222);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(16);
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
		assertThat(values(edgeCases))
			.contains(11111, 11112, 11121, 11211, 12111, 21111, 22222);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(32);
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
		assertThat(values(edgeCases))
			.contains(111111, 111112, 111121, 111211, 112111, 121111, 211111, 222222);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(64);
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
		assertThat(values(edgeCases))
			.contains(1111111, 1111112, 1111121, 1111211, 1112111, 1121111, 1211111, 2111111, 2222222);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(128);
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
		assertThat(values(edgeCases))
			.contains(11111111, 11111112, 11111121, 11111211, 11112111, 11121111, 11211111, 12111111, 21111111, 22222222);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(256);
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
		assertThat(values(edgeCases))
			.containsExactlyInAnyOrder(111, 113, 121, 123, 211, 213, 221, 223);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(8);
	}

	@Example
	void combineWithBuilder() {
		Arbitrary<Integer> numbers = Arbitraries.integers().between(10, 100);

		Supplier<AdditionBuilder> additionBuilderSupplier = AdditionBuilder::new;
		Arbitrary<Integer> sum = Combinators
									 .withBuilder(additionBuilderSupplier)
									 .use(numbers).in((b, n) -> b.addNumber(n))
									 .use(numbers).in((b, n) -> b.addNumber(n))
									 .build(AdditionBuilder::sum);

		EdgeCases<Integer> edgeCases = sum.edgeCases();
		assertThat(values(edgeCases))
			.containsExactlyInAnyOrder(20, 21, 22, 198, 199, 200, 109, 110, 111);
		// make sure edge cases can be repeatedly generated
		assertThat(values(edgeCases)).hasSize(9);
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

	private <T> Set<T> values(EdgeCases<T> edgeCases) {
		Set<T> values = new HashSet<>();
		for (Shrinkable<T> edgeCase : edgeCases) {
			values.add(edgeCase.value());
		}
		return values;
	}
}
