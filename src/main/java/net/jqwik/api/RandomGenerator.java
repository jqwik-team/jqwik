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

	/**
	 * This method can be called whenever a random generator should forget about its state.
	 * This will at least be done <em>before each try</em> of a property method.
	 * <p>
	 * Implementors of this interface are required to call {@code reset()} on embedded generators
	 * if their own {@code reset()} method is being called.
	 */
	default void reset() {
	}

	default <U> RandomGenerator<U> map(Function<T, U> mapper) {
		return new RandomGenerator<U>() {
			@Override
			public Shrinkable<U> next(Random random) {
				return RandomGenerator.this.next(random).map(mapper);
			}

			@Override
			public void reset() {
				RandomGenerator.this.reset();
			}
		};
	}

	default <U> RandomGenerator<U> flatMap(Function<T, Arbitrary<U>> mapper, int genSize) {
		return new RandomGenerator<U>() {
			@Override
			public Shrinkable<U> next(Random random) {
				Shrinkable<T> wrappedShrinkable = RandomGenerator.this.next(random);
				return new FlatMappedShrinkable<>(wrappedShrinkable, mapper, genSize, random.nextLong());
			}

			@Override
			public void reset() {
				RandomGenerator.this.reset();
			}
		};
	}

	default RandomGenerator<T> filter(Predicate<T> filterPredicate) {
		return new FilteredGenerator<>(this, filterPredicate);
	}

	default RandomGenerator<T> injectNull(double nullProbability) {
		return new RandomGenerator<T>() {
			@Override
			public Shrinkable<T> next(Random random) {
				if (random.nextDouble() <= nullProbability) return Shrinkable.unshrinkable(null);
				return RandomGenerator.this.next(random);
			}

			@Override
			public void reset() {
				RandomGenerator.this.reset();
			}
		};
	}

	default RandomGenerator<T> withEdgeCases(int genSize, List<Shrinkable<T>> samples) {
		if (samples.isEmpty()) {
			return this;
		}

		int baseToEdgeCaseRatio =
			Math.min(
				Math.max(Math.round(genSize / 5), 1),
				100
			) + 1;

		RandomGenerator<T> samplesGenerator = RandomGenerators.samplesFromShrinkables(samples);
		RandomGenerator<T> baseGenerator = this;

		return new RandomGenerator<T>() {
			@Override
			public Shrinkable<T> next(Random random) {
				if (random.nextInt(baseToEdgeCaseRatio) == 0) {
					return samplesGenerator.next(random);
				} else {
					return baseGenerator.next(random);
				}
			}

			@Override
			public void reset() {
				samplesGenerator.reset();
				baseGenerator.reset();
			}
		};
	}

	@SuppressWarnings("unchecked")
	default RandomGenerator<T> withSamples(T... samples) {
		return new WithSamplesGenerator<>(samples, this);
	}

	default RandomGenerator<T> unique(Set<T> usedValues) {
		return new UniqueGenerator<>(this, usedValues);
	}

	default Stream<Shrinkable<T>> stream(Random random) {
		return Stream.generate(() -> this.next(random));
	}
}
