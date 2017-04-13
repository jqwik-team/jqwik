package net.jqwik.properties;

import org.assertj.core.api.*;

import net.jqwik.api.*;

public class ArbitraryTests {

	@Example
	void filtering() {
		Arbitrary<Integer> count = new CountingArbitrary();
		Arbitrary<Integer> countEven = count.filter(i -> i % 2 == 0);

		Generator<Integer> generator = countEven.generator(1L, 1);

		Assertions.assertThat(generator.next()).isEqualTo(2);
		Assertions.assertThat(generator.next()).isEqualTo(4);
		Assertions.assertThat(generator.next()).isEqualTo(6);
		Assertions.assertThat(generator.next()).isEqualTo(7);
	}
}
