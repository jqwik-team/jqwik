package net.jqwik.properties;

import java.util.*;
import java.util.function.*;

import net.jqwik.properties.arbitraries.*;

public interface NShrinkable<T> {

	static <T> NShrinkable<T> unshrinkable(T value) {
		return new Unshrinkable<>(value);
	}

	Set<NShrinkResult<NShrinkable<T>>> shrinkNext(Predicate<T> falsifier);

	T value();

	int distance();

	default <U> NShrinkable<U> map(Function<T, U> mapper) {
		return new NMappedShrinkable<>(this, mapper);
	}

	class Unshrinkable<T> implements NShrinkable<T> {

		private final T value;

		private Unshrinkable(T value) {
			this.value = value;
		}

		@Override
		public Set<NShrinkResult<NShrinkable<T>>> shrinkNext(Predicate falsifier) {
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
			if (o == null || !(o instanceof NShrinkable))
				return false;
			NShrinkable<?> that = (NShrinkable<?>) o;
			return Objects.equals(value, that.value());
		}

		@Override
		public int hashCode() {
			return Objects.hash(value);
		}
	}
}
