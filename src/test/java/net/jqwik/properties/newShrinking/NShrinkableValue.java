package net.jqwik.properties.newShrinking;

public abstract class NShrinkableValue<T> implements NShrinkable<T> {

	private final T value;

	public NShrinkableValue(T value) {
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

		NShrinkableValue<?> that = (NShrinkableValue<?>) o;

		return value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}
}
