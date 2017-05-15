package net.jqwik.newArbitraries;

public class NShrunkValue<T> {

	private final T value;
	private final int distance;

	public NShrunkValue(T value, int distance) {
		this.value = value;
		this.distance = distance;
	}

	public T value() {
		return value;
	}

	public int distance() {
		return distance;
	}

	@Override
	public String toString() {
		return String.format("ShrunkValue[%s:%d]", value, distance);
	}
}
