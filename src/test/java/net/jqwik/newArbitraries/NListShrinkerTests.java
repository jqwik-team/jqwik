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
		NShrinkResult<List<NShrinkable<Integer>>> result = shrinker.shrink(l -> l.get(0).value() == 1);

		Assertions.assertThat(result.value()).containsExactly(new NShrinkableValue<>(1, ignore -> Collections.emptySet()));
	}
}
