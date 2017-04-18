package net.jqwik.properties;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

public class ArbitraryTests {

	private Random random = new Random();

	@Example
	void filtering() {
		Arbitrary<Integer> count = new CountingArbitrary();
		Arbitrary<Integer> countEven = count.filter(i -> i % 2 == 0);

		RandomGenerator<Integer> generator = countEven.generator(1);

		Assertions.assertThat(generator.next(random)).isEqualTo(2);
		Assertions.assertThat(generator.next(random)).isEqualTo(4);
		Assertions.assertThat(generator.next(random)).isEqualTo(6);
		Assertions.assertThat(generator.next(random)).isEqualTo(8);
	}

	@Example
	void mapping() {
		Arbitrary<Integer> count = new CountingArbitrary();
		Arbitrary<String> countStrings = count.map(i -> "i=" + i);

		RandomGenerator<String> generator = countStrings.generator(1);

		Assertions.assertThat(generator.next(random)).isEqualTo("i=1");
		Assertions.assertThat(generator.next(random)).isEqualTo("i=2");
		Assertions.assertThat(generator.next(random)).isEqualTo("i=3");
	}

	@Example
	void withNullInjectsNullValues() {
		Arbitrary<Integer> count = new CountingArbitrary();
		Arbitrary<Integer> withNull = count.injectNull(0.5);

		RandomGenerator<Integer> generator = withNull.generator(1);
		for (int i = 0; i < 1000; i++) {
			Integer value = generator.next(random);
			if (value == null)
				return;
		}

		Assertions.fail("Null should have been generated");
	}


}
