package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.assertThat;

@Group
class ShrinkableProperties {

	@Property //(reporting = Reporting.GENERATED)
	boolean allShrinkingFinallyEnds(@ForAll("anyShrinkable") Shrinkable<?> aShrinkable) {
		ShrinkingSequence<?> sequence = aShrinkable.shrink(ignore -> false);
		while (sequence.nextValue(() -> {}, ignore -> {}));
		return true;
	}

	@Property
	boolean allShrinkingShrinksToSmallerValues(@ForAll("anyShrinkable") Shrinkable<?> aShrinkable) {
		ShrinkingSequence<?> sequence = aShrinkable.shrink(ignore -> false);
		FalsificationResult<?> current = sequence.current();
		while (sequence.nextValue(() -> {}, ignore -> {})) {
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
