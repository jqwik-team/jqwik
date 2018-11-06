package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.properties.arbitraries.randomized.*;
import net.jqwik.properties.shrinking.*;

public interface RandomGenerator<T> {

	/**
	 * @param random the source of randomness. Injected by jqwik itself.
	 *
	 * @return the next generated value wrapped within the Shrinkable interface. The method must ALWAYS return a next value.
	 */
	Shrinkable<T> next(Random random);

	default <U> RandomGenerator<U> map(Function<T, U> mapper) {
		return random -> RandomGenerator.this.next(random).map(mapper);
	}

	default <U> RandomGenerator<U> flatMap(Function<T, Arbitrary<U>> mapper, int genSize) {
		return random -> {
			Shrinkable<T> wrappedShrinkable = RandomGenerator.this.next(random);
			return new FlatMappedShrinkable<>(wrappedShrinkable, mapper, genSize, random.nextLong());
		};
	}

	default RandomGenerator<T> filter(Predicate<T> filterPredicate) {
		return new FilteredGenerator<>(this, filterPredicate);
	}

	default RandomGenerator<T> injectNull(double nullProbability) {
		return random -> {
			if (random.nextDouble() <= nullProbability) return Shrinkable.unshrinkable(null);
			return RandomGenerator.this.next(random);
		};
	}

	default RandomGenerator<T> withEdgeCases(int genSize, List<Shrinkable<T>> edgeCases) {
		if (edgeCases.isEmpty()) {
			return this;
		}

		int baseToEdgeCaseRatio =
			Math.min(
				Math.max(Math.round(genSize / 5), 1),
				100 / edgeCases.size()
			) + 1;

		RandomGenerator<T> edgeCasesGenerator = RandomGenerators.chooseShrinkable(edgeCases);
		RandomGenerator<T> baseGenerator = this;

		return random -> {
			if (random.nextInt(baseToEdgeCaseRatio) == 0) {
				return edgeCasesGenerator.next(random);
			} else {
				return baseGenerator.next(random);
			}
		};
	}

	@SuppressWarnings("unchecked")
	default RandomGenerator<T> withSamples(T... samples) {
		return new WithSamplesGenerator<>(samples, this);
	}

	default RandomGenerator<T> unique() {
		return new UniqueGenerator<>(this);
	}

	default Stream<Shrinkable<T>> stream(Random random) {
		return Stream.generate(() -> this.next(random));
	}
}
