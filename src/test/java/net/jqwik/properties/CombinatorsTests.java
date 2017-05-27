package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.properties.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

class CombinatorsTests {

	private Random random = new Random();

	@Example
	void twoArbitrariesCanBeCombined() {
		NArbitrary<Integer> combine2 = NCombinators.combine(one(), two()).as((a, b) -> a + b);
		NShrinkable<Integer> value = generate(combine2);
		assertThat(value.value()).isEqualTo(3);
	}

	private NShrinkable<Integer> generate(NArbitrary<Integer> integerArbitrary) {
		return integerArbitrary.generator(1).next(random);
	}

	@Example
	void threeArbitrariesCanBeCombined() {
		NArbitrary<Integer> combine3 = NCombinators.combine(one(), two(), three()).as((a, b, c) -> a + b + c);
		NShrinkable<Integer> value = generate(combine3);
		assertThat(value.value()).isEqualTo(6);
	}

	@Example
	void fourArbitrariesCanBeCombined() {
		NArbitrary<Integer> combine4 = NCombinators.combine(one(), two(), three(), four()).as((a, b, c, d) -> a + b + c + d);
		NShrinkable<Integer> value = generate(combine4);
		assertThat(value.value()).isEqualTo(10);
	}

	NArbitrary<Integer> one() {
		return NArbitraries.of(1);
	}

	NArbitrary<Integer> two() {
		return NArbitraries.of(2);
	}

	NArbitrary<Integer> three() {
		return NArbitraries.of(3);
	}

	NArbitrary<Integer> four() {
		return NArbitraries.of(4);
	}
}
