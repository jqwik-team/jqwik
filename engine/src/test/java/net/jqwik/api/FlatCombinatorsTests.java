package net.jqwik.api;

import java.util.*;

import net.jqwik.engine.properties.*;

import static org.assertj.core.api.Assertions.*;

class FlatCombinatorsTests {

	private Random random = SourceOfRandomness.current();

	@Example
	void twoArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine2 = Combinators.combine(one(), two())
												 .flatAs((a, b) -> Arbitraries.constant(a + b));
		Shrinkable<Integer> value = generate(combine2);
		assertThat(value.value()).isEqualTo(3);
	}

	private Shrinkable<Integer> generate(Arbitrary<Integer> integerArbitrary) {
		return integerArbitrary.generator(1).next(random);
	}

	@Example
	void threeArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine3 = Combinators.combine(one(), two(), three())
												 .flatAs((a, b, c) -> Arbitraries.constant(a + b + c));
		Shrinkable<Integer> value = generate(combine3);
		assertThat(value.value()).isEqualTo(6);
	}

	@Example
	void fourArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine4 = Combinators.combine(one(), two(), three(), four())
												 .flatAs((a, b, c, d) -> Arbitraries.constant(a + b + c + d));
		Shrinkable<Integer> value = generate(combine4);
		assertThat(value.value()).isEqualTo(10);
	}

	@Example
	void fiveArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine5 = Combinators.combine(one(), two(), three(), four(), five()) //
												 .flatAs((a, b, c, d, e) -> Arbitraries.constant(a + b + c + d + e));
		Shrinkable<Integer> value = generate(combine5);
		assertThat(value.value()).isEqualTo(15);
	}

	@Example
	void sixArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine6 = Combinators.combine(one(), two(), three(), four(), five(), six()) //
												 .flatAs((a, b, c, d, e, f) -> Arbitraries.constant(a + b + c + d + e + f));
		Shrinkable<Integer> value = generate(combine6);
		assertThat(value.value()).isEqualTo(21);
	}

	@Example
	void sevenArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine7 = Combinators.combine(one(), two(), three(), four(), five(), six(), seven()) //
												 .flatAs((a, b, c, d, e, f, g) -> Arbitraries.constant(a + b + c + d + e + f + g));
		Shrinkable<Integer> value = generate(combine7);
		assertThat(value.value()).isEqualTo(28);
	}

	@Example
	void eightArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine8 = Combinators.combine(one(), two(), three(), four(), five(), six(), seven(), eight()) //
												 .flatAs((a, b, c, d, e, f, g, h) -> Arbitraries.constant(a + b + c + d + e + f + g + h));
		Shrinkable<Integer> value = generate(combine8);
		assertThat(value.value()).isEqualTo(36);
	}

	@Example
	void listOfArbitrariesCanBeCombined() {
		List<Arbitrary<Integer>> listOfArbitraries = Arrays.asList(one(), one(), two(), two(), three(), three());
		Arbitrary<Integer> combineList = Combinators.combine(listOfArbitraries) //
													.flatAs(list -> Arbitraries.constant(list.stream().mapToInt(e -> e).sum()));
		Shrinkable<Integer> value = generate(combineList);
		assertThat(value.value()).isEqualTo(12);
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
