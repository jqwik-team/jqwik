package net.jqwik.properties;

import org.assertj.core.api.*;

import net.jqwik.api.*;

import java.util.*;

class CombinatorsTests {

	private Random random = new Random();

	@Example
	void twoArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine2 = Combinators.combine(one(), two()).as((a, b) -> a + b);
		int value = generate(combine2);
		Assertions.assertThat(value).isEqualTo(3);
	}

	private int generate(Arbitrary<Integer> integerArbitrary) {
		return integerArbitrary.generator(1).next(random);
	}

	@Example
	void threeArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine3 = Combinators.combine(one(), two(), three()).as((a, b, c) -> a + b + c);
		int value = generate(combine3);
		Assertions.assertThat(value).isEqualTo(6);
	}

	@Example
	void fourArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine4 = Combinators.combine(one(), two(), three(), four()).as((a, b, c, d) -> a + b + c + d);
		int value = generate(combine4);
		Assertions.assertThat(value).isEqualTo(10);
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
}
