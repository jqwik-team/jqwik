package net.jqwik.newArbitraries;

import java.util.function.*;

public interface NArbitrary<T> {
	NShrinkableGenerator<T> generator(int tries);

	default NArbitrary<?> inner() {
		return this;
	}

	default NArbitrary<T> filter(Predicate<T> filterPredicate) {
		return new NArbitraryWrapper<T, T>(this) {
			@Override
			public NShrinkableGenerator<T> generator(int tries) {
				return new NFilteredGenerator<T>(wrapped.generator(tries), filterPredicate);
			}
		};
	}

	default <U> NArbitrary<U> map(Function<T, U> mapper) {
		return new NArbitraryWrapper<T, U>(this) {
			@Override
			public NShrinkableGenerator<U> generator(int tries) {
				return wrapped.generator(tries).map(mapper);
			}
		};
	}

	default NArbitrary<T> injectNull(double nullProbability) {
		return new NArbitraryWrapper<T, T>(this) {
			@Override
			public NShrinkableGenerator<T> generator(int tries) {
				return wrapped.generator(tries).injectNull(nullProbability);
			}
		};
	}

	@SuppressWarnings("unchecked")
	default NArbitrary<T> withSamples(T... samples) {
		return new NArbitraryWrapper<T, T>(this) {
			@Override
			public NShrinkableGenerator<T> generator(int tries) {
				return wrapped.generator(tries).withSamples(samples);
			}
		};
	};

	static int defaultMaxFromTries(int tries) {
		return Math.max(tries / 2 - 3, 3);
	}

	static int defaultCollectionSizeFromTries(int tries) {
		return (int) Math.max(Math.round(Math.sqrt(tries)), 3);
	}
}
