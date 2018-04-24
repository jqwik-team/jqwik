package net.jqwik.properties.newShrinking;

public class ShrinkingDistance implements Comparable<ShrinkingDistance> {

	private final long distance;

	public static ShrinkingDistance of(long distance) {
		return new ShrinkingDistance(distance);
	}

	private ShrinkingDistance(long distance) {
		this.distance = distance;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ShrinkingDistance that = (ShrinkingDistance) o;
		return this.compareTo(that) == 0;
	}

	@Override
	public int hashCode() {
		return (int) (distance ^ (distance >>> 32));
	}

	@Override
	public String toString() {
		return String.format("%s", distance);
	}

	@Override
	public int compareTo(ShrinkingDistance other) {
		return Long.compare(distance, other.distance);
	}
}
