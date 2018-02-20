package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.arbitraries.*;
import net.jqwik.properties.arbitraries.*;

public interface Arbitrary<T> {
	RandomGenerator<T> generator(int tries);

	default Arbitrary<T> filter(Predicate<T> filterPredicate) {
		return tries -> new FilteredGenerator<T>(Arbitrary.this.generator(tries), filterPredicate);
	}

	default <U> Arbitrary<U> map(Function<T, U> mapper) {
		return tries -> Arbitrary.this.generator(tries).map(mapper);
	}

	default <U> Arbitrary<U> flatMap(Function<T, Arbitrary<U>> mapper) {
		return tries -> Arbitrary.this.generator(tries).flatMap(mapper, tries);
	}

	default Arbitrary<T> injectNull(double nullProbability) {
		if (nullProbability <= 0.0) {
			return this;
		}
		return tries -> Arbitrary.this.generator(tries).injectNull(nullProbability);
	}

	@SuppressWarnings("unchecked")
	default Arbitrary<T> withSamples(T... samples) {
		return tries -> Arbitrary.this.generator(tries).withSamples(samples);
	}

	default SizableArbitrary<List<T>> list() {
		return new ListArbitrary<T>(this);
	}

	default SizableArbitrary<Set<T>> set() {
		return new SetArbitrary<>(this);
	}

	default SizableArbitrary<Stream<T>> stream() {
		return new StreamArbitrary<>(this);
	}

	default <A> SizableArbitrary<A> array(Class<A> arrayClass) {
		return new ArrayArbitrary<A, T>(arrayClass, this);
	}

	default Arbitrary<Optional<T>> optional() {
		return this.injectNull(0.05).map(Optional::ofNullable);
	}

	static int defaultMaxFromTries(int tries) {
		return Math.max(tries / 2 - 3, 3);
	}

	static int defaultCollectionSizeFromTries(int tries) {
		return (int) Math.max(Math.round(Math.sqrt(tries)), 3);
	}
}
