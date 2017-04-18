package net.jqwik.properties;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.*;
import org.assertj.core.api.*;

import java.util.*;

public class ArbitrariesTests {

	private Random random = new Random();

	@Example
	void fromGenerator() {
		Arbitrary<String> stringArbitrary = Arbitraries.fromGenerator(random -> Integer.toString(random.nextInt(10)));

		RandomGenerator<String> generator = stringArbitrary.generator(1L, 1);

		assertIntStringLessThan10(generator.next(random));
		assertIntStringLessThan10(generator.next(random));
		assertIntStringLessThan10(generator.next(random));
		assertIntStringLessThan10(generator.next(random));
	}

	@Example
	void ofValues() {
		Arbitrary<String> stringArbitrary = Arbitraries.of("1", "hallo", "test");

		RandomGenerator<String> generator = stringArbitrary.generator(1L, 1);

		Assertions.assertThat(generator.next(random)).isIn("1", "hallo", "test");
		Assertions.assertThat(generator.next(random)).isIn("1", "hallo", "test");
		Assertions.assertThat(generator.next(random)).isIn("1", "hallo", "test");
		Assertions.assertThat(generator.next(random)).isIn("1", "hallo", "test");
	}

	private void assertIntStringLessThan10(String next) {
		Assertions.assertThat(next).isInstanceOf(String.class);
		Assertions.assertThat(Integer.parseInt(next)).isLessThan(10);
	}
}
