package net.jqwik.api;

import net.jqwik.properties.*;

import static org.assertj.core.api.Assertions.*;

import java.util.*;

class CombinatorsTests {

	private Random random = SourceOfRandomness.current();

	@Example
	void twoArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine2 = Combinators.combine(one(), two()).as((a, b) -> a + b);
		Shrinkable<Integer> value = generate(combine2);
		assertThat(value.value()).isEqualTo(3);
	}

	private Shrinkable<Integer> generate(Arbitrary<Integer> integerArbitrary) {
		return integerArbitrary.generator(1).next(random);
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
