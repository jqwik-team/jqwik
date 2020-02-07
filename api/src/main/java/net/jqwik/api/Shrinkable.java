package net.jqwik.api;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;

import org.apiguardian.api.*;
import org.opentest4j.*;

import net.jqwik.api.lifecycle.*;

import static org.apiguardian.api.API.Status.*;

@API(status = STABLE, since = "1.0")
public interface Shrinkable<T> extends Comparable<Shrinkable<T>> {

	@API(status = INTERNAL)
	abstract class ShrinkableFacade {
		private static ShrinkableFacade implementation;

		static  {
			implementation = FacadeLoader.load(ShrinkableFacade.class);
		}

		public abstract <T> Shrinkable<T> unshrinkable(T value, ShrinkingDistance distance);
		public abstract <T, U> Shrinkable<U> map(Shrinkable<T> self, Function<T, U> mapper);
		public abstract <T> Shrinkable<T> filter(Shrinkable<T> self, Predicate<T> filter);
		public abstract <T, U> Shrinkable<U> flatMap(Shrinkable<T> self, Function<T, Arbitrary<U>> flatMapper, int tries, long randomSeed);
	}

	static <T> Shrinkable<T> unshrinkable(T value) {
		return unshrinkable(value, ShrinkingDistance.of(0));
	}

	static <T> Shrinkable<T> unshrinkable(T value, ShrinkingDistance distance) {
		return ShrinkableFacade.implementation.unshrinkable(value, distance);
	}

	T value();

	ShrinkingSequence<T> shrink(Falsifier<T> falsifier);

	ShrinkingDistance distance();

	/**
	 * Sometimes simplifies test writing
	 */
	@SuppressWarnings("unchecked")
	@API(status = INTERNAL, since = "1.2.4")
	default Shrinkable<Object> asGeneric() {
		return (Shrinkable<Object>) this;
	}

	default <U> Shrinkable<U> map(Function<T, U> mapper) {
		return ShrinkableFacade.implementation.map(this, mapper);
	}

	default Shrinkable<T> filter(Predicate<T> filter) {
		return ShrinkableFacade.implementation.filter(this, filter);
	}

	default <U> Shrinkable<U> flatMap(Function<T, Arbitrary<U>> flatMapper, int tries, long randomSeed) {
		return ShrinkableFacade.implementation.flatMap(this, flatMapper, tries, randomSeed);
	}

	@SuppressWarnings("unchecked")
	@Override
	@API(status = INTERNAL)
	default int compareTo(Shrinkable<T> other) {
		int comparison = this.distance().compareTo(other.distance());
		if (comparison == 0) {
			T value = value();
			if (value instanceof Comparable) {
				return ((Comparable<T>) value).compareTo(other.value());
			}
		}
		return comparison;
	}

	@API(status = INTERNAL)
	default boolean isSmallerThan(Shrinkable<T> other) {
		return this.distance().compareTo(other.distance()) < 0;
	}

	/**
	 * Used only when several shrinkables must be shrunk in synchronicity e.g. duplicate values.
	 * Override in mostly all implementations since the default produces only a few values.
	 */
	@API(status = INTERNAL)
	default List<Shrinkable<T>> shrinkingSuggestions() {
		Falsifier<T> falsifier = ignore -> TryExecutionResult.falsified(null);
		ShrinkingSequence<T> allDown = shrink(falsifier);
		List<Shrinkable<T>> suggestions = new ArrayList<>();
		while(allDown.next(() -> {}, result -> {})) {
			suggestions.add(allDown.current().shrinkable());
		}
		suggestions.sort(null);
		return suggestions;
	}


}
