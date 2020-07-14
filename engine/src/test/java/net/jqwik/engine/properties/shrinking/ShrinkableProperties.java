package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ShrinkingTestHelper.*;

@Group
class ShrinkableProperties {

	@Property
	void allShrinkingFinallyEnds(@ForAll("anyShrinkable") Shrinkable<?> shrinkable) {
		shrinkToEnd(shrinkable, ignore -> TryExecutionResult.falsified(null), null);
	}

	@Property(tries = 100)
	boolean allShrinkingShrinksToSmallerValues(@ForAll("anyShrinkable") Shrinkable<?> shrinkable) {
		// TODO: Replace with test to show that all shrinking suggestions are "smaller" than value to shrink
		ShrinkingSequence<?> sequence = shrinkable.shrink(ignore -> TryExecutionResult.falsified(null));
		FalsificationResult<?> current = sequence.current();
		while (sequence.next(() -> {}, ignore -> {})) {
			assertThat(sequence.current().distance()).isLessThanOrEqualTo(current.distance());
			current = sequence.current();
		}
		return true;
	}

	@Provide
	Arbitrary<Shrinkable> anyShrinkable() {
		// TODO: Enhance the list of shrinkables.
		return Arbitraries.oneOf(
			oneStepShrinkable(),
			partialShrinkable(),
			oneStepShrinkable(),
			partialShrinkable(),
			oneStepShrinkable(),
			partialShrinkable(),
			listShrinkable()
		);
	}

	@SuppressWarnings("unchecked")
	private Arbitrary<Shrinkable> listShrinkable() {
		return Arbitraries.integers().between(0, 10).flatMap(size -> {
			List<Arbitrary<Shrinkable>> elementArbitraries =
				IntStream.range(0, size)
						 .mapToObj(ignore -> Arbitraries.lazy(this::anyShrinkable))
						 .collect(Collectors.toList());
			return Combinators.combine(elementArbitraries).as(elements -> new ShrinkableList(elements, 0));
		});
	}

	private Arbitrary<Shrinkable> oneStepShrinkable() {
		return Arbitraries.integers().between(0, 1000).map(ShrinkableTypesForTest.OneStepShrinkable::new);
	}

	private Arbitrary<Shrinkable> partialShrinkable() {
		return Arbitraries.integers().between(0, 1000).map(ShrinkableTypesForTest.PartialShrinkable::new);
	}
}
