package net.jqwik.properties;

import org.assertj.core.api.*;

import net.jqwik.api.*;

import java.util.*;

public class ArbitraryTests {

	private Random random = new Random();

	@Example
	void filtering() {
		Arbitrary<Integer> count = new CountingArbitrary();
		Arbitrary<Integer> countEven = count.filter(i -> i % 2 == 0);

		RandomGenerator<Integer> generator = countEven.generator(1L, 1);

		Assertions.assertThat(generator.next(random)).isEqualTo(2);
		Assertions.assertThat(generator.next(random)).isEqualTo(4);
		Assertions.assertThat(generator.next(random)).isEqualTo(6);
		Assertions.assertThat(generator.next(random)).isEqualTo(8);
	}
}
