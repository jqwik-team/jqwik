package net.jqwik.api;

import java.util.function.*;

import net.jqwik.properties.RandomGenerator;
import net.jqwik.properties.arbitraries.*;

public interface Arbitrary<T> {
	RandomGenerator<T> generator(int tries);

	default Arbitrary<?> inner() {
		return this;
	}

	default Arbitrary<T> filter(Predicate<T> filterPredicate) {
		return new ArbitraryWrapper<T, T>(this) {
			@Override
			public RandomGenerator<T> generator(int tries) {
				return new FilteredGenerator<T>(wrapped.generator(tries), filterPredicate);
			}
		};
	}

	default <U> Arbitrary<U> map(Function<T, U> mapper) {
		return new ArbitraryWrapper<T, U>(this) {
			@Override
			public RandomGenerator<U> generator(int tries) {
				return wrapped.generator(tries).map(mapper);
			}
		};
	}

	default <U> Arbitrary<U> flatMap(Function<T, Arbitrary<U>> mapper) {
		return new ArbitraryWrapper<T, U>(this) {
			@Override
			public RandomGenerator<U> generator(int tries) {
				return wrapped.generator(tries).flatMap(mapper, tries);
			}
		};
	}

	default Arbitrary<T> injectNull(double nullProbability) {
		return new ArbitraryWrapper<T, T>(this) {
			@Override
			public RandomGenerator<T> generator(int tries) {
				return wrapped.generator(tries).injectNull(nullProbability);
			}
		};
	}

	@SuppressWarnings("unchecked")
	default Arbitrary<T> withSamples(T... samples) {
		return new ArbitraryWrapper<T, T>(this) {
			@Override
			public RandomGenerator<T> generator(int tries) {
				return wrapped.generator(tries).withSamples(samples);
			}
		};
	}

	static int defaultMaxFromTries(int tries) {
		return Math.max(tries / 2 - 3, 3);
	}

	static int defaultCollectionSizeFromTries(int tries) {
		return (int) Math.max(Math.round(Math.sqrt(tries)), 3);
	}
}
