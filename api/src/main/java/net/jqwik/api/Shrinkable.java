package net.jqwik.api;

import java.util.function.*;

public interface Shrinkable<T> extends Comparable<Shrinkable<T>> {

	abstract class ShrinkableFactoryFacade {
		private static final String SHRINKABLE_FACTORY_FACADE_IMPL = "net.jqwik.engine.facades.ShrinkableFactoryFacadeImpl";
		private static ShrinkableFactoryFacade implementation;

		static  {
			try {
				implementation = (ShrinkableFactoryFacade) Class.forName(SHRINKABLE_FACTORY_FACADE_IMPL).newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public abstract <T> Shrinkable<T> unshrinkable(T value);
		public abstract <T, U> Shrinkable<U> map(Shrinkable<T> self, Function<T, U> mapper);
		public abstract <T> Shrinkable<T> filter(Shrinkable<T> self, Predicate<T> filter);
		public abstract <T, U> Shrinkable<U> flatMap(Shrinkable<T> self, Function<T, Arbitrary<U>> flatMapper, int tries, long randomSeed);
	}

	static <T> Shrinkable<T> unshrinkable(T value) {
		return ShrinkableFactoryFacade.implementation.unshrinkable(value);
	}

	T value();

	ShrinkingSequence<T> shrink(Falsifier<T> falsifier);

	ShrinkingDistance distance();

	default <U> Shrinkable<U> map(Function<T, U> mapper) {
		return ShrinkableFactoryFacade.implementation.map(this, mapper);
	}

	default Shrinkable<T> filter(Predicate<T> filter) {
		return ShrinkableFactoryFacade.implementation.filter(this, filter);
	}

	@Override
	default int compareTo(Shrinkable<T> other) {
		return this.distance().compareTo(other.distance());
	}

	default boolean isSmallerThan(Shrinkable<T> other) {
		return this.distance().compareTo(other.distance()) < 0;
	}

	default <U> Shrinkable<U> flatMap(Function<T, Arbitrary<U>> flatMapper, int tries, long randomSeed) {
		return ShrinkableFactoryFacade.implementation.flatMap(this, flatMapper, tries, randomSeed);
	}
}
