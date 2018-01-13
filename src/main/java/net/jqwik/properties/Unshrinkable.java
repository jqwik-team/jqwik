package net.jqwik.properties;

import net.jqwik.api.*;

import java.util.*;
import java.util.function.*;

public class Unshrinkable<T> implements Shrinkable<T> {

	private final T value;

	public Unshrinkable(T value) {
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
