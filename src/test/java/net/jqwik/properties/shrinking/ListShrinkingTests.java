package net.jqwik.properties.shrinking;

import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.*;

import java.util.*;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;

public class ListShrinkingTests {

	private final Shrinker<List<Integer>> shrinker = Shrinkers.list(new IntegerArbitrary(-5, 5));

	@Example
	void shrinkFromEmptyListShrinksToEmptyList() {
		ShrinkableList<Integer> shrinkableList = (ShrinkableList<Integer>) shrinker.shrink(Collections.emptyList());

		MockFalsifier<List<Integer>> falsifier = MockFalsifier.falsifyAll();
		Optional<ShrinkResult<List<Integer>>> result = shrinkableList.shrink(falsifier);

		assertThat(result).isPresent();
		assertThat(result.get().value()).isEmpty();
		assertThat(falsifier.visited()).containsExactly(Collections.emptyList());
	}

	@Example
	void underlyingShrinksListFromBothSides() {
		List<Integer> listOf6 = asList(1, 2, 3, 4, 5, 6);
		ShrinkableList<Integer> shrinkable = (ShrinkableList<Integer>) shrinker.shrink(listOf6);
		Shrinkable<List<Integer>> underlying = shrinkable.underlying();

		// Do not falsify empty lists and lists starting with '6'
		MockFalsifier<List<Integer>> falsifier = MockFalsifier.falsifyWhen(l -> l.size() == 0 || l.get(0) == 6);

		Optional<ShrinkResult<List<Integer>>> result = underlying.shrink(falsifier);

		assertThat(result).isPresent();
		assertThat(result.get().value()).isEqualTo(asList(1));
		assertThat(falsifier.visited()).containsExactly( //
			asList(1, 2, 3, 4, 5, 6), //
			asList(1, 2, 3, 4, 5), //
			asList(1, 2, 3, 4), //
			asList(1, 2, 3), //
			asList(1, 2), //
			asList(1), //
			asList(), //
			asList(2, 3, 4, 5, 6), //
			asList(3, 4, 5, 6), //
			asList(4, 5, 6), //
			asList(5, 6), //
			asList(6) //
		);
	}

}
