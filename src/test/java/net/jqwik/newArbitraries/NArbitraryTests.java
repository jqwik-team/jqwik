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

		assertThat(generator.next(random).value()).isEqualTo(1);
		assertThat(generator.next(random).value()).isEqualTo(2);
		assertThat(generator.next(random).value()).isEqualTo(3);
		assertThat(generator.next(random).value()).isEqualTo(4);
		assertThat(generator.next(random).value()).isEqualTo(5);
		assertThat(generator.next(random).value()).isEqualTo(1);
	}

	@Example
	void filterInteger() {
		NArbitrary<Integer> arbitrary = new NArbitraryWheel<>(1, 2, 3, 4, 5);
		NArbitrary<Integer> filtered = arbitrary.filter(anInt -> anInt % 2 != 0);
		NShrinkableGenerator<Integer> generator = filtered.generator(10);

		assertThat(generator.next(random).value()).isEqualTo(1);
		assertThat(generator.next(random).value()).isEqualTo(3);
		assertThat(generator.next(random).value()).isEqualTo(5);
		assertThat(generator.next(random).value()).isEqualTo(1);
	}

	@Example
	void shrinkInteger() {
		NArbitrary<Integer> arbitrary = new NArbitraryWheel<>(1, 2, 3, 4, 5);
		NShrinkableGenerator<Integer> generator = arbitrary.generator(10);

		generator.next(random);
		generator.next(random);
		generator.next(random);
		generator.next(random);

		NShrinkable<Integer> value5 = generator.next(random);
//		value5.shrink()
//		assertThat(shrunkValues).hasSize(1);
//
//		NShrunkValue<Integer> shrunkValue = shrunkValues.iterator().next();
//		assertThat(shrunkValue.value()).isEqualTo(4);
//		assertThat(shrunkValue.distance()).isEqualTo(4);
	}
}
