package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.ShrinkingTestHelper.*;

@Group
class NEW_ShrinkableProperties {

	@Property(tries = 100)
	void allShrinkingFinallyEnds(@ForAll("anyShrinkable") Shrinkable<?> shrinkable) {
		shrinkToMinimal(shrinkable, ignore -> TryExecutionResult.falsified(null), null);
	}

	@Property(tries = 100)
	void allShrinkingShrinksToSmallerValues(@ForAll("anyShrinkable") Shrinkable<?> shrinkable) {
		shrinkable.shrink().forEach(shrunk -> {
			assertThat(shrunk.distance().compareTo(shrinkable.distance())).isLessThanOrEqualTo(0);
		});
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
			listShrinkable(),
			setShrinkable()
		);
	}

	@SuppressWarnings("unchecked")
	private Arbitrary<Shrinkable> listShrinkable() {
		return Arbitraries.integers().between(0, 5).flatMap(size -> {
			List<Arbitrary<Shrinkable>> elementArbitraries =
				IntStream.range(0, size)
						 .mapToObj(ignore -> Arbitraries.lazy(this::anyShrinkable))
						 .collect(Collectors.toList());
			return Combinators.combine(elementArbitraries).as(elements -> new ShrinkableList(elements, 0));
		});
	}

	@SuppressWarnings("unchecked")
	private Arbitrary<Shrinkable> setShrinkable() {
		return Arbitraries.integers().between(0, 5).flatMap(size -> {
			List<Arbitrary<Shrinkable>> elementArbitraries =
				IntStream.range(0, size)
						 .mapToObj(ignore -> Arbitraries.lazy(this::anyShrinkable))
						 .collect(Collectors.toList());
			return Combinators.combine(elementArbitraries).as(elements -> {
				Set<Shrinkable> elementSet = new HashSet<>(elements);
				return new ShrinkableSet(elementSet, 0);
			});
		});
	}

	private Arbitrary<Shrinkable> oneStepShrinkable() {
		return Arbitraries.integers().between(0, 1000).map(ShrinkableTypesForTest.OneStepShrinkable::new);
	}

	private Arbitrary<Shrinkable> partialShrinkable() {
		return Arbitraries.integers().between(0, 1000).map(ShrinkableTypesForTest.PartialShrinkable::new);
	}
}
