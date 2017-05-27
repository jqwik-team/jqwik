package net.jqwik.properties;

import java.util.*;
import java.util.function.*;

import net.jqwik.properties.arbitraries.*;

public interface Shrinkable<T> {

	static <T> Shrinkable<T> unshrinkable(T value) {
		return new Unshrinkable<>(value);
	}

	Set<ShrinkResult<Shrinkable<T>>> shrinkNext(Predicate<T> falsifier);

	T value();

	int distance();

	default <U> Shrinkable<U> map(Function<T, U> mapper) {
		return new NMappedShrinkable<>(this, mapper);
	}

	class Unshrinkable<T> implements Shrinkable<T> {

		private final T value;

		private Unshrinkable(T value) {
			this.value = value;
		}

		@Override
		public Set<ShrinkResult<Shrinkable<T>>> shrinkNext(Predicate falsifier) {
			return Collections.emptySet();
		}

		@Override
		public T value() {
			return value;
		}

		@Override
		public int distance() {
			return 0;
		}

		@Override
		public String toString() {
			return String.format("Unshrinkable[%s]", value);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || !(o instanceof Shrinkable))
				return false;
			Shrinkable<?> that = (Shrinkable<?>) o;
			return Objects.equals(value, that.value());
		}

		@Override
		public int hashCode() {
			return Objects.hash(value);
		}
	}
}
