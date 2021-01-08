package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;

class ChooseValueShrinkableTests {

	@Example
	void creation() {
		Shrinkable<Integer> shrinkable = new ChooseValueShrinkable<>(3, Arrays.asList(1, 2, 3, 4, 5));
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(2));
		assertThat(shrinkable.value()).isEqualTo(3);
	}

	@Example
	void shrinking() {
		Shrinkable<Integer> shrinkable = new ChooseValueShrinkable<>(4, Arrays.asList(1, 2, 3, 4, 5));
		Integer shrunkValue = shrink(shrinkable, (TestingFalsifier<Integer>) ignore -> false, null);
		assertThat(shrunkValue).isEqualTo(1);
	}

	@Example
	void growing() {
		Shrinkable<Integer> shrinkable = new ChooseValueShrinkable<>(2, Arrays.asList(1, 2, 3, 4, 5));
		Stream<Shrinkable<Integer>> grown = shrinkable.grow();
		Stream<Integer> grownValues = grown.map(Shrinkable::value);
		assertThat(grownValues).containsExactly(3, 4, 5);
	}
}
