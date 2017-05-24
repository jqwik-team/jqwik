package net.jqwik.newArbitraries;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;

class NListShrinkerTests {

	@Example
	void unshrinkableListIsReturnedItself() {
		List<NShrinkable<Integer>> list = NArbitraryTestHelper.listOfShrinkableIntegers(1, 2, 3);
		NListShrinker<Integer> shrinker = new NListShrinker<>(list, null);
		NShrinkResult<List<NShrinkable<Integer>>> result = shrinker.shrink(l -> true);

		Assertions.assertThat(result.value()).isSameAs(list);
	}

	@Example
	void shrinkToListHead() {
		List<NShrinkable<Integer>> list = NArbitraryTestHelper.listOfShrinkableIntegers(1, 2, 3);
		NListShrinker<Integer> shrinker = new NListShrinker<>(list, null);
		NShrinkResult<List<NShrinkable<Integer>>> result = shrinker.shrink(l -> l.size() == 0 || l.get(0).value() != 1);

		Assertions.assertThat(result.value()).hasSize(1);
		Assertions.assertThat(result.value().get(0).value()).isEqualTo(1);
	}

	@Example
	void shrinkToListTail() {
		List<NShrinkable<Integer>> list = NArbitraryTestHelper.listOfShrinkableIntegers(3, 2, 1);
		NListShrinker<Integer> shrinker = new NListShrinker<>(list, null);
		NShrinkResult<List<NShrinkable<Integer>>> result = shrinker.shrink(l -> l.size() == 0);

		Assertions.assertThat(result.value()).hasSize(1);
		Assertions.assertThat(result.value().get(0).value()).isEqualTo(1);
	}

	@Example
	void shrinkToShortestFalsifiedList() {
		List<NShrinkable<Integer>> list = NArbitraryTestHelper.listOfShrinkableIntegers(1, 1, 1, 1, 1, 1, 1);
		NListShrinker<Integer> shrinker = new NListShrinker<>(list, null);
		NShrinkResult<List<NShrinkable<Integer>>> result = shrinker.shrink(l -> l.size() < 3);

		Assertions.assertThat(result.value()).hasSize(3);
	}
}
