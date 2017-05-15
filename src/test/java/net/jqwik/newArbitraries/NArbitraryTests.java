package net.jqwik.newArbitraries;

import static org.assertj.core.api.Assertions.*;

import java.util.*;

import net.jqwik.api.*;

class NArbitraryTests {

	private Random random = new Random();

	@Example
	void generateInteger() {
		NArbitrary<Integer> arbitrary = new NArbitraryWheel<>(1, 2, 3, 4, 5);
		NShrinkableGenerator<Integer> generator = arbitrary.generator(10);

		assertThat(generator.next(random)).isEqualTo(1);
		assertThat(generator.next(random)).isEqualTo(2);
		assertThat(generator.next(random)).isEqualTo(3);
		assertThat(generator.next(random)).isEqualTo(4);
		assertThat(generator.next(random)).isEqualTo(5);
		assertThat(generator.next(random)).isEqualTo(1);
	}

	@Example
	void filterInteger() {
		NArbitrary<Integer> arbitrary = new NArbitraryWheel<>(1, 2, 3, 4, 5);
		NArbitrary<Integer> filtered = arbitrary.filter(anInt -> anInt % 2 != 0);
		NShrinkableGenerator<Integer> generator = filtered.generator(10);

		assertThat(generator.next(random)).isEqualTo(1);
		assertThat(generator.next(random)).isEqualTo(3);
		assertThat(generator.next(random)).isEqualTo(5);
		assertThat(generator.next(random)).isEqualTo(1);
	}

	@Example
	void shrinkInteger() {
		NArbitrary<Integer> arbitrary = new NArbitraryWheel<>(1, 2, 3, 4, 5);
		NShrinkableGenerator<Integer> generator = arbitrary.generator(10);

		Set<NShrunkValue<Integer>> shrunkValues = generator.shrink(5);
		assertThat(shrunkValues).hasSize(1);

		NShrunkValue<Integer> shrunkValue = shrunkValues.iterator().next();
		assertThat(shrunkValue.value()).isEqualTo(4);
		assertThat(shrunkValue.distance()).isEqualTo(4);
	}
}
