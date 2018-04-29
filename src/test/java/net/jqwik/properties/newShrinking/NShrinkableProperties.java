package net.jqwik.properties.newShrinking;

import net.jqwik.api.*;

import java.util.*;
import java.util.stream.*;

import static org.assertj.core.api.Assertions.assertThat;

@Group
class NShrinkableProperties {

	@Property //(reporting = Reporting.GENERATED)
	boolean allShrinkingFinallyEnds(@ForAll("anyShrinkable") NShrinkable<?> aShrinkable) {
		ShrinkingSequence<?> sequence = aShrinkable.shrink(ignore -> false);
		while (sequence.next(() -> {}, ignore -> {})) {}
		return true;
	}

	@Property
	boolean allShrinkingShrinksToSmallerValues(@ForAll("anyShrinkable") NShrinkable<?> aShrinkable) {
		ShrinkingSequence<?> sequence = aShrinkable.shrink(ignore -> false);
		NShrinkable<?> current = sequence.current();
		while (sequence.next(() -> {}, ignore -> {})) {
			assertThat(sequence.current().distance()).isLessThanOrEqualTo(current.distance());
			current = sequence.current();
		}
		return true;
	}

	@Provide
	Arbitrary<NShrinkable> anyShrinkable() {
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

	private Arbitrary<NShrinkable> listShrinkable() {
		return Arbitraries.integers().between(0, 10).flatMap(size -> {
			List<Arbitrary<NShrinkable>> elementArbitraries =
				IntStream.range(0, size)
						 .mapToObj(ignore -> Arbitraries.lazy(this::anyShrinkable))
						 .collect(Collectors.toList());
			return Combinators.combine(elementArbitraries).as(elements -> new NListShrinkable(elements));
		});
	}

	private Arbitrary<NShrinkable> oneStepShrinkable() {
		return Arbitraries.integers().between(0, 1000).map(ShrinkableTypesForTest.OneStepShrinkable::new);
	}

	private Arbitrary<NShrinkable> partialShrinkable() {
		return Arbitraries.integers().between(0, 1000).map(ShrinkableTypesForTest.PartialShrinkable::new);
	}
}
