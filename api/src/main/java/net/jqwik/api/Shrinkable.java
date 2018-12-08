package net.jqwik.api;

import java.util.function.*;
import java.util.logging.*;

public interface Shrinkable<T> extends Comparable<Shrinkable<T>> {

	abstract class ShrinkableFacade {
		private static final Logger LOG = Logger.getLogger(ShrinkableFacade.class.getName());
		private static final String SHRINKABLE_FACTORY_FACADE_IMPL = "net.jqwik.engine.facades.ShrinkableFacadeImpl";
		private static ShrinkableFacade implementation;

		static  {
			try {
				implementation = (ShrinkableFacade) Class.forName(SHRINKABLE_FACTORY_FACADE_IMPL).newInstance();
			} catch (Exception e) {
				LOG.log(
					Level.SEVERE,
					"Cannot load implementation for " + ShrinkableFacade.class.getName(),
					e
				);
			}
		}

		public abstract <T> Shrinkable<T> unshrinkable(T value);
		public abstract <T, U> Shrinkable<U> map(Shrinkable<T> self, Function<T, U> mapper);
		public abstract <T> Shrinkable<T> filter(Shrinkable<T> self, Predicate<T> filter);
		public abstract <T, U> Shrinkable<U> flatMap(Shrinkable<T> self, Function<T, Arbitrary<U>> flatMapper, int tries, long randomSeed);
	}

	static <T> Shrinkable<T> unshrinkable(T value) {
		return ShrinkableFacade.implementation.unshrinkable(value);
	}

	T value();

	ShrinkingSequence<T> shrink(Falsifier<T> falsifier);

	ShrinkingDistance distance();

	default <U> Shrinkable<U> map(Function<T, U> mapper) {
		return ShrinkableFacade.implementation.map(this, mapper);
	}

	default Shrinkable<T> filter(Predicate<T> filter) {
		return ShrinkableFacade.implementation.filter(this, filter);
	}

	@Override
	default int compareTo(Shrinkable<T> other) {
		return this.distance().compareTo(other.distance());
	}

	default boolean isSmallerThan(Shrinkable<T> other) {
		return this.distance().compareTo(other.distance()) < 0;
	}

	default <U> Shrinkable<U> flatMap(Function<T, Arbitrary<U>> flatMapper, int tries, long randomSeed) {
		return ShrinkableFacade.implementation.flatMap(this, flatMapper, tries, randomSeed);
	}
}
