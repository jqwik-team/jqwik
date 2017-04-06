package net.jqwik.execution.properties;

import javaslang.test.*;
import net.jqwik.api.*;
import net.jqwik.api.properties.*;
import org.assertj.core.api.*;

import java.util.*;

class CombinatorsTests {

	@Example
	void twoArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine2 = Combinators.combine(one(), two()).as((a, b) -> a + b);
		int value = combine2.apply(1).apply(new Random());
		Assertions.assertThat(value).isEqualTo(3);
	}

	@Example
	void threeArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine3 = Combinators.combine(one(), two(), three()).as((a, b, c) -> a + b + c);
		int value = combine3.apply(1).apply(new Random());
		Assertions.assertThat(value).isEqualTo(6);
	}

	@Example
	void fourArbitrariesCanBeCombined() {
		Arbitrary<Integer> combine4 = Combinators.combine(one(), two(), three(), four()).as((a, b, c, d) -> a + b + c + d);
		int value = combine4.apply(1).apply(new Random());
		Assertions.assertThat(value).isEqualTo(10);
	}

	Arbitrary<Integer> one() {
		return Generator.of(1);
	}
	Arbitrary<Integer> two() {
		return Generator.of(2);
	}
	Arbitrary<Integer> three() {
		return Generator.of(3);
	}
	Arbitrary<Integer> four() {
		return Generator.of(4);
	}
}
