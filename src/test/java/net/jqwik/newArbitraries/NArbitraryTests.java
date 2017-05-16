package net.jqwik.newArbitraries;

import static org.assertj.core.api.Assertions.*;

import java.util.*;

import net.jqwik.api.*;

@Group
class NArbitraryTests {

	private Random random = new Random();

	@Example
	void generateInteger() {
		NArbitrary<Integer> arbitrary = new ArbitraryWheelForTests<>(1, 2, 3, 4, 5);
		NShrinkableGenerator<Integer> generator = arbitrary.generator(10);

		assertThat(generator.next(random).value()).isEqualTo(1);
		assertThat(generator.next(random).value()).isEqualTo(2);
		assertThat(generator.next(random).value()).isEqualTo(3);
		assertThat(generator.next(random).value()).isEqualTo(4);
		assertThat(generator.next(random).value()).isEqualTo(5);
		assertThat(generator.next(random).value()).isEqualTo(1);
	}

	@Example
	void shrinkInteger() {
		NArbitrary<Integer> arbitrary = new ArbitraryWheelForTests<>(1, 2, 3, 4, 5);
		NShrinkableGenerator<Integer> generator = arbitrary.generator(10);

		NShrinkable<Integer> value5 = generateNth(generator, 5);
		assertThat(value5.value()).isEqualTo(5);
		Set<NShrinkable<Integer>> shrunkValues = value5.shrink();
		assertThat(shrunkValues).hasSize(1);

		NShrinkable<Integer> shrunkValue = shrunkValues.iterator().next();
		assertThat(shrunkValue.value()).isEqualTo(4);
		assertThat(shrunkValue.distance()).isEqualTo(4);
	}

	@Example
	void generateList() {
		NArbitrary<List<Integer>> arbitrary = new ListArbitraryForTests(5);
		NShrinkableGenerator<List<Integer>> generator = arbitrary.generator(10);

		assertThat(generator.next(random).value()).isEmpty();
		assertThat(generator.next(random).value()).containsExactly(1);
		assertThat(generator.next(random).value()).containsExactly(1, 2);
		assertThat(generator.next(random).value()).containsExactly(1, 2, 3);
		assertThat(generator.next(random).value()).containsExactly(1, 2, 3, 4);
		assertThat(generator.next(random).value()).containsExactly(1, 2, 3, 4, 5);
		assertThat(generator.next(random).value()).isEmpty();
		assertThat(generator.next(random).value()).containsExactly(1);
	}

	@Example
	void shrinkList() {
		NArbitrary<List<Integer>> arbitrary = new ListArbitraryForTests(5);
		NShrinkableGenerator<List<Integer>> generator = arbitrary.generator(10);

		NShrinkable<List<Integer>> value5 = generateNth(generator, 6);
		assertThat(value5.value()).containsExactly(1, 2, 3, 4, 5);

		Set<NShrinkable<List<Integer>>> shrunkValues = value5.shrink();
		assertThat(shrunkValues).hasSize(2);
		shrunkValues.forEach(shrunkValue -> {
			assertThat(shrunkValue.value()).hasSize(4);
			assertThat(shrunkValue.distance()).isEqualTo(4);
		});
	}

	@Group
	class Filtering {
		@Example
		void filterInteger() {
			NArbitrary<Integer> arbitrary = new ArbitraryWheelForTests<>(1, 2, 3, 4, 5);
			NArbitrary<Integer> filtered = arbitrary.filter(anInt -> anInt % 2 != 0);
			NShrinkableGenerator<Integer> generator = filtered.generator(10);

			assertThat(generator.next(random).value()).isEqualTo(1);
			assertThat(generator.next(random).value()).isEqualTo(3);
			assertThat(generator.next(random).value()).isEqualTo(5);
			assertThat(generator.next(random).value()).isEqualTo(1);
		}

		@Example
		void shrinkFilteredInteger() {
			NArbitrary<Integer> arbitrary = new ArbitraryWheelForTests<>(1, 2, 3, 4, 5);
			NArbitrary<Integer> filtered = arbitrary.filter(anInt -> anInt % 2 != 0);
			NShrinkableGenerator<Integer> generator = filtered.generator(10);

			NShrinkable<Integer> value5 = generateNth(generator, 3);
			assertThat(value5.value()).isEqualTo(5);
			Set<NShrinkable<Integer>> shrunkValues = value5.shrink();
			assertThat(shrunkValues).hasSize(1);

			NShrinkable<Integer> shrunkValue = shrunkValues.iterator().next();
			assertThat(shrunkValue.value()).isEqualTo(3);
			assertThat(shrunkValue.distance()).isEqualTo(3);
		}

		@Example
		void filterList() {
			NArbitrary<List<Integer>> arbitrary = new ListArbitraryForTests(5);
			NArbitrary<List<Integer>> filtered = arbitrary.filter(aList -> aList.size() % 2 != 0);
			NShrinkableGenerator<List<Integer>> generator = filtered.generator(10);

			assertThat(generator.next(random).value()).containsExactly(1);
			assertThat(generator.next(random).value()).containsExactly(1, 2, 3);
			assertThat(generator.next(random).value()).containsExactly(1, 2, 3, 4, 5);
			assertThat(generator.next(random).value()).containsExactly(1);
		}

		@Example
		void shrinkFilteredList() {
			NArbitrary<List<Integer>> arbitrary = new ListArbitraryForTests(5);
			NArbitrary<List<Integer>> filtered = arbitrary.filter(aList -> aList.size() % 2 != 0);
			NShrinkableGenerator<List<Integer>> generator = filtered.generator(10);

			NShrinkable<List<Integer>> value5 = generateNth(generator, 3);
			assertThat(value5.value()).containsExactly(1, 2, 3, 4, 5);

			Set<NShrinkable<List<Integer>>> shrunkValues = value5.shrink();
			assertThat(shrunkValues).hasSize(3); // [1,2,3] [2,3,4] [3,4,5]
			shrunkValues.forEach(shrunkValue -> {
				assertThat(shrunkValue.value()).hasSize(3);
				assertThat(shrunkValue.distance()).isEqualTo(3);
			});
		}

	}

	@Group
	class Mapping {

		@Example
		void mapIntegerToString() {
			NArbitrary<Integer> arbitrary = new ArbitraryWheelForTests<>(1, 2, 3, 4, 5);
			NArbitrary<String> mapped = arbitrary.map(anInt -> "value=" + anInt);
			NShrinkableGenerator<String> generator = mapped.generator(10);

			assertThat(generator.next(random).value()).isEqualTo("value=1");
			assertThat(generator.next(random).value()).isEqualTo("value=2");
			assertThat(generator.next(random).value()).isEqualTo("value=3");
			assertThat(generator.next(random).value()).isEqualTo("value=4");
			assertThat(generator.next(random).value()).isEqualTo("value=5");
			assertThat(generator.next(random).value()).isEqualTo("value=1");
		}

	}

	private <T> NShrinkable<T> generateNth(NShrinkableGenerator<T> generator, int n) {
		NShrinkable<T> generated = null;
		for (int i = 0; i < n; i++) {
			generated = generator.next(random);
		}
		return generated;
	}

}
