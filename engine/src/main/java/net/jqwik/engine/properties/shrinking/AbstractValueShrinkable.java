package net.jqwik.engine.properties.shrinking;

import net.jqwik.api.*;

public abstract class AbstractValueShrinkable<T> implements Shrinkable<T> {

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

		return value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
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
