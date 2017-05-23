package net.jqwik.newArbitraries;

import net.jqwik.api.*;
import org.assertj.core.api.*;

import java.util.*;

class NContainerShrinkingTests {

	@Example
	void shrinkFirstNumberOfElementsThenIndividualElements() {
		NShrinkable<List<Integer>> list = NArbitraryTestHelper.shrinkableListOfIntegers(1, 2, 3, 4, 5);

		NShrinkResult<NShrinkable<List<Integer>>> shrinkResult = list.shrink(listToShrink -> {
			if (listToShrink.size() < 3)
				return true;
			return listToShrink.stream().allMatch(anInt -> anInt < 2);

		}, null);

		Assertions.assertThat(shrinkResult.value().value()).containsExactly(1, 1, 1);
		Assertions.assertThat(shrinkResult.value().distance()).isEqualTo(6);

	}
}
