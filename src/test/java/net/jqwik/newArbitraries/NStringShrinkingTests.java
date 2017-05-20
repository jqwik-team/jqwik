package net.jqwik.newArbitraries;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

class NStringShrinkingTests {

	private NStringShrinker shrinker = new NStringShrinker();

	@Example
	void distanceIsLength() {
		assertThat(shrinker.distance("")).isEqualTo(0);
		assertThat(shrinker.distance("aaa")).isEqualTo(3);
		assertThat(shrinker.distance("abc")).isEqualTo(3);
	}

	@Example
	void emptyStringIsNotShrunk() {
		assertThat(shrinker.shrink("")).isEmpty();
	}

	@Example
	void stringOfLength1IsShrunkToEmpty() {
		assertThat(shrinker.shrink("a")).containsExactly("");
	}

	@Example
	void longerStringsAreShrunkFromBothSides() {
		assertThat(shrinker.shrink("abcba")).containsExactly("abcb", "bcba");
	}
}
