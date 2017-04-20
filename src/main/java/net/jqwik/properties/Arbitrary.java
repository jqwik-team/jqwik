package net.jqwik.properties;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

public interface Arbitrary<T> {

	RandomGenerator<T> generator(int tries);

	// Not being used yet
	default List<T> shrink(T value) {
		return Collections.emptyList();
	}

	default Arbitrary<T> filter(Predicate<? super T> predicate) {
		return (tries) -> Arbitrary.this.generator(tries).filter(predicate);
	}

	/**
	 * Maps arbitrary objects T to arbitrary object U.
	 */
	default <U> Arbitrary<U> map(Function<? super T, ? extends U> mapper) {
		return (tries) -> Arbitrary.this.generator(tries).map(mapper);
	}

	default Arbitrary<T> injectNull(double nullProbability) {
		return (tries) -> Arbitrary.this.generator(tries).injectNull(nullProbability);
	}

	default Arbitrary<T> withSamples(T... samples) {
		return new ArbitraryWrapper<T>(this) {
			@Override
			public RandomGenerator<T> generator(int tries) {
				RandomGenerator<T> samplesGenerator = RandomGenerators.samples(samples);
				RandomGenerator<T> generator = super.generator(tries);
				AtomicInteger tryCount = new AtomicInteger(0);
				return random -> {
					if (tryCount.getAndIncrement() < samples.length)
						return samplesGenerator.next(random);
					return generator.next(random);
				};
			}
		};
	};

	default Arbitrary<?> inner() {
		return this;
	}

	static int defaultMaxFromTries(int tries) {
		return Math.max(tries / 2 - 3, 1);
	}
}
