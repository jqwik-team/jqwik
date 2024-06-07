package net.jqwik.engine.properties.shrinking;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

import java.util.*;

public abstract class AbstractValueShrinkable<T extends @Nullable Object> implements Shrinkable<T> {

	private final T value;

	public AbstractValueShrinkable(T value) {
		this.value = value;
	}

	@Override
	public T value() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AbstractValueShrinkable<?> that = (AbstractValueShrinkable<?>) o;

		return Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return String.format("%s<%s>(%s:%s)",
							 getClass().getSimpleName(),
							 value().getClass().getSimpleName(),
							 value(), distance()
		);
	}
}
