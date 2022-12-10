package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;

@SuppressWarnings({"rawtypes", "unchecked"})
@Group
class ShrinkableProperties {

	@Property
	void allShrinkingFinallyEnds(@ForAll("anyShrinkable") Shrinkable<?> shrinkable) {
		shrink(shrinkable, ignore -> TryExecutionResult.falsified(null), null);
	}

	// Fixed seed since it can take very long
	@Property(tries = 1000, seed = "42")
	void allShrinkingShrinksToSmallerValues(@ForAll("anyShrinkable") Shrinkable<?> shrinkable) {
		shrinkable.shrink().forEach(shrunk -> {
			assertThat(shrunk.distance().compareTo(shrinkable.distance())).isLessThanOrEqualTo(0);
		});
	}

	@Provide
	Arbitrary<Shrinkable> anyShrinkable() {
		// TODO: Enhance the list of shrinkables.
		return Arbitraries.lazyOf(
			this::integerShrinkable,
			this::integerShrinkable,
			this::mapToNumberShrinkable,
			this::mapToNumberShrinkable,
			this::oneStepShrinkable,
			this::partialShrinkable,
			this::listShrinkable,
			this::setShrinkable
		);
	}

	private Arbitrary<Shrinkable> mapToNumberShrinkable() {
		return anyShrinkable().map(s -> {
			if (s instanceof ShrinkableContainer) {
				return Shrinkable.unshrinkable(((ShrinkableContainer) s).elements.size());
			}
			if (s instanceof Number) {
				return Shrinkable.unshrinkable(((Number) s).intValue());
			}
			return Shrinkable.unshrinkable(42);
		});
	}

	private Arbitrary<Shrinkable> listShrinkable() {
		return Arbitraries.integers().between(0, 5).flatMap(size -> {
			List<Arbitrary<Shrinkable>> elementArbitraries =
				IntStream.range(0, size)
						 .mapToObj(ignore -> Arbitraries.lazy(this::anyShrinkable))
						 .collect(Collectors.toList());
			return Combinators.combine(elementArbitraries).as(elements -> new ShrinkableList(elements, 0, size));
		});
	}

	private Arbitrary<Shrinkable> setShrinkable() {
		return Arbitraries.integers().between(0, 5).flatMap(size -> {
			List<Arbitrary<Shrinkable>> elementArbitraries =
				IntStream.range(0, size)
						 .mapToObj(ignore -> Arbitraries.lazy(this::anyShrinkable))
						 .collect(Collectors.toList());
			return Combinators.combine(elementArbitraries).as(elements -> {
				Set<Shrinkable> elementSet = new LinkedHashSet<>(elements);
				return new ShrinkableSet(elementSet, 0, size, Collections.emptySet());
			});
		});
	}

	private Arbitrary<Shrinkable> integerShrinkable() {
		Arbitrary<Integer> firsts = Arbitraries.integers();
		Arbitrary<Integer> seconds = Arbitraries.integers();
		Arbitrary<JqwikRandom> randoms = Arbitraries.randoms();
		return Combinators.combine(firsts, seconds, randoms)
						  .as((first, second, random) -> {
							  int min = Math.min(first, second);
							  int max = Math.max(first, second);
							  return Arbitraries
											 .integers().between(min, max)
											 .generator(100, true).next(random);
						  });
	}

	private Arbitrary<Shrinkable> oneStepShrinkable() {
		return Arbitraries.integers().between(0, 1000).map(ShrinkableTypesForTest.OneStepShrinkable::new);
	}

	private Arbitrary<Shrinkable> partialShrinkable() {
		return Arbitraries.integers().between(0, 1000).map(ShrinkableTypesForTest.PartialShrinkable::new);
	}
}
