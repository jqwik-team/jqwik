package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

@API(status = STABLE, since = "1.0")
public interface RandomGenerator<T extends @Nullable Object> {

	@API(status = INTERNAL)
	abstract class RandomGeneratorFacade {
		private static final RandomGeneratorFacade implementation;

		static {
			implementation = FacadeLoader.load(RandomGeneratorFacade.class);
		}

		public abstract <T extends @Nullable Object, U extends @Nullable Object> Shrinkable<U> flatMap(Shrinkable<T> self, Function<? super T, ? extends RandomGenerator<U>> mapper, long nextLong);

		public abstract <T extends @Nullable Object, U extends @Nullable Object> Shrinkable<U> flatMap(
			Shrinkable<T> wrappedShrinkable,
			Function<? super T, ? extends Arbitrary<U>> mapper,
			int genSize,
			long nextLong,
			boolean withEmbeddedEdgeCases
		);

		public abstract <T extends @Nullable Object> RandomGenerator<T> filter(RandomGenerator<T> self, Predicate<? super T> filterPredicate, int maxMisses);

		public abstract <T extends @Nullable Object> RandomGenerator<T> withEdgeCases(RandomGenerator<T> self, int genSize, EdgeCases<T> edgeCases);

		public abstract <T extends @Nullable Object> RandomGenerator<List<T>> collect(RandomGenerator<T> self, Predicate<? super List<? extends T>> until);

		public abstract <T extends @Nullable Object> RandomGenerator<T> injectDuplicates(RandomGenerator<T> self, double duplicateProbability);

		public abstract <T extends @Nullable Object> RandomGenerator<T> ignoreExceptions(
			RandomGenerator<T> self,
			Class<? extends Throwable>[] exceptionTypes,
			int maxThrows
		);
	}

	/**
	 * @param random the source of randomness. Injected by jqwik itself.
	 * @return the next generated value wrapped within the Shrinkable interface. The method must ALWAYS return a next value.
	 */
	Shrinkable<T> next(Random random);

	@API(status = INTERNAL)
	default <U extends @Nullable Object> RandomGenerator<U> map(Function<? super T, ? extends U> mapper) {
		return this.mapShrinkable(s -> s.map(mapper));
	}

	@API(status = INTERNAL)
	default <U extends @Nullable Object> RandomGenerator<U> mapShrinkable(Function<? super Shrinkable<T>, ? extends Shrinkable<U>> mapper) {
		return random -> {
			Shrinkable<T> tShrinkable = RandomGenerator.this.next(random);
			return mapper.apply(tShrinkable);
		};
	}

	@API(status = INTERNAL)
	default <U extends @Nullable Object> RandomGenerator<U> flatMap(Function<? super T, ? extends RandomGenerator<U>> mapper) {
		return random -> {
			Shrinkable<T> wrappedShrinkable = RandomGenerator.this.next(random);
			return RandomGeneratorFacade.implementation.flatMap(wrappedShrinkable, mapper, random.nextLong());
		};
	}

	@API(status = INTERNAL)
	default <U extends @Nullable Object> RandomGenerator<U> flatMap(Function<? super T, ? extends Arbitrary<U>> mapper, int genSize, boolean withEmbeddedEdgeCases) {
		return random -> {
			Shrinkable<T> wrappedShrinkable = RandomGenerator.this.next(random);
			return RandomGeneratorFacade.implementation
					   .flatMap(wrappedShrinkable, mapper, genSize, random.nextLong(), withEmbeddedEdgeCases);
		};
	}

	@API(status = INTERNAL)
	default RandomGenerator<T> filter(Predicate<? super T> filterPredicate, int maxMisses) {
		return RandomGeneratorFacade.implementation.filter(this, filterPredicate, maxMisses);
	}

	@API(status = INTERNAL)
	default RandomGenerator<T> withEdgeCases(int genSize, EdgeCases<T> edgeCases) {
		return RandomGeneratorFacade.implementation.withEdgeCases(this, genSize, edgeCases);
	}

	@API(status = INTERNAL)
	default Stream<Shrinkable<T>> stream(Random random) {
		return Stream.generate(() -> this.next(random));
	}

	@API(status = INTERNAL)
	default RandomGenerator<List<T>> collect(Predicate<? super List<? extends T>> until) {
		return RandomGeneratorFacade.implementation.collect(this, until);
	}

	@API(status = INTERNAL)
	default RandomGenerator<T> injectDuplicates(double duplicateProbability) {
		return RandomGeneratorFacade.implementation.injectDuplicates(this, duplicateProbability);
	}

	@API(status = INTERNAL)
	default RandomGenerator<T> ignoreExceptions(int maxThrows, Class<? extends Throwable>[] exceptionTypes) {
		return RandomGeneratorFacade.implementation.ignoreExceptions(this, exceptionTypes, maxThrows);
	}

	@API(status = INTERNAL)
	default RandomGenerator<T> dontShrink() {
		return random -> {
			Shrinkable<T> shrinkable = RandomGenerator.this.next(random).makeUnshrinkable();
			return shrinkable.makeUnshrinkable();
		};
	}

}
