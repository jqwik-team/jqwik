package net.jqwik.api;

import java.util.ArrayList;
import java.util.*;
import java.util.function.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

@Disabled
class CombinatorsEdgeCasesTests {

	@Example
	void combine2arbitraries() {
		Arbitrary<Integer> a1020 = Arbitraries.of(10, 20);
		Arbitrary<Integer> a12 = Arbitraries.of(1, 2);
		Arbitrary<Integer> plus = Combinators
									  .combine(a1020, a12)
									  .as((i1, i2) -> i1 + i2);
	}

	@Example
	void combine3arbitraries() {
		Arbitrary<Integer> a100200 = Arbitraries.of(100, 200);
		Arbitrary<Integer> a1020 = Arbitraries.of(10, 20);
		Arbitrary<Integer> a12 = Arbitraries.of(1, 2);
		Arbitrary<Integer> plus = Combinators
									  .combine(a100200, a1020, a12)
									  .as((i1, i2, i3) -> i1 + i2 + i3);
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
	}

	@Example
	void combineWithBuilder() {
		Arbitrary<Integer> numbers = Arbitraries.integers().between(1, 4);

		Supplier<AdditionBuilder> additionBuilderSupplier = AdditionBuilder::new;
		Arbitrary<Integer> sum = Combinators
									 .withBuilder(additionBuilderSupplier)
									 .use(numbers).in((b, n) -> b.addNumber(n))
									 .use(numbers).in((b, n) -> b.addNumber(n))
									 .build(AdditionBuilder::sum);
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
