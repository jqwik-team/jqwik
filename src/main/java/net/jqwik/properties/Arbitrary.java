package net.jqwik.properties;

import net.jqwik.properties.shrinking.*;

import java.util.*;
import java.util.function.*;

public interface Arbitrary<T> {

	RandomGenerator<T> generator(int tries);

	default Shrinkable<T> shrinkableFor(T value) {
		return ShrinkTree.empty();
	}

	default Arbitrary<T> filter(Predicate<? super T> predicate) {
		return new ArbitraryWrapper<T>(this) {
			@Override
			public RandomGenerator<T> generator(int tries) {
				return Arbitrary.this.generator(tries).filter(predicate);
			}
		};
	}

	/**
	 * Maps arbitrary objects T to arbitrary object U.
	 */
	default <U> Arbitrary<U> map(Function<? super T, ? extends U> mapper) {
		return (tries) -> Arbitrary.this.generator(tries).map(mapper);
	}

	default Arbitrary<T> injectNull(double nullProbability) {
		return new ArbitraryWrapper<T>(this) {
			@Override
			public RandomGenerator<T> generator(int tries) {
				return Arbitrary.this.generator(tries).injectNull(nullProbability);
			}
		};
	}

	default Arbitrary<T> withSamples(T... samples) {
		return new ArbitraryWrapper<T>(this) {
			@Override
			public RandomGenerator<T> generator(int tries) {
				return Arbitrary.this.generator(tries).withSamples(samples);
			}
		};
	};

	default Arbitrary<?> inner() {
		return this;
	}

	static int defaultMaxFromTries(int tries) {
		return Math.max(tries / 2 - 3, 3);
	}
}
