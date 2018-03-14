package net.jqwik.api;

import net.jqwik.properties.arbitraries.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public interface RandomGenerator<T> {

	/**
	 * @param random the source of randomness. Injected by jqwik itself.
	 *
	 * @return the next generated value wrapped within the Shrinkable interface. The method must ALWAYS return a next value.
	 */
	Shrinkable<T> next(Random random);

	/**
	 * As opposed to {@code next} this method only chooses values from the random part
	 * of a generator. For purely random generators this is the same as calling {@code next}.
	 * <p>
	 * Needed for Arbitraries.flatMap
	 *
	 * @param random the source of randomness. Injected by jqwik itself.
	 * @return the next _randomly_ generated value wrapped within the Shrinkable interface. The method must ALWAYS return a next value.
	 */
	default Shrinkable<T> sampleRandomly(Random random) {
		return this.next(random);
	}

	default <U> RandomGenerator<U> map(Function<T, U> mapper) {
		return random -> this.next(random).map(mapper);
	}

	default <U> RandomGenerator<U> flatMap(Function<T, Arbitrary<U>> mapper, int tries) {
		return random -> {
			Shrinkable<T> wrappedShrinkable = this.next(random);
			return new FlatMappedShrinkable<>(wrappedShrinkable, mapper, tries, random.nextLong());
		};
	}

	default RandomGenerator<T> filter(Predicate<T> filterPredicate) {
		return new FilteredGenerator<>(this, filterPredicate);
	}

	default RandomGenerator<T> injectNull(double nullProbability) {
		return random -> {
			if (random.nextDouble() <= nullProbability)
				return Shrinkable.unshrinkable(null);
			return RandomGenerator.this.next(random);
		};
	}

	default RandomGenerator<T> withShrinkableSamples(List<Shrinkable<T>> samples) {
		RandomGenerator<T> samplesGenerator = RandomGenerators.samples(samples);
		RandomGenerator<T> generator = this;
		AtomicInteger tryCount = new AtomicInteger(0);
		return new RandomGenerator<T>() {
			@Override
			public Shrinkable<T> next(Random random) {
				if (tryCount.getAndIncrement() < samples.size()) return samplesGenerator.next(random);
				return generator.next(random);
			}

			@Override
			public Shrinkable<T> sampleRandomly(Random random) {
				return generator.sampleRandomly(random);
			}
		};
	}

	default RandomGenerator<T> withSamples(T... samples) {
		List<Shrinkable<T>> shrinkables = ShrinkableSample.of(samples);
		return withShrinkableSamples(shrinkables);
	}

}
