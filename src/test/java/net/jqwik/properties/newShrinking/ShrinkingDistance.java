package net.jqwik.properties.newShrinking;

import net.jqwik.support.*;

import java.util.*;

public class ShrinkingDistance implements Comparable<ShrinkingDistance> {

	private final long[] distances;

	public static ShrinkingDistance of(long... distances) {
		return new ShrinkingDistance(distances);
	}

	private ShrinkingDistance(long[] distances) {
		this.distances = distances;
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
		return Arrays.hashCode(distances);
	}

	@Override
	public String toString() {
		return String.format("%s", JqwikStringSupport.displayString(distances));
	}

	@Override
	public int compareTo(ShrinkingDistance other) {
		int compareLengthResult = compareLength(other);
		if (compareLengthResult != 0)
			return compareLengthResult;

		for (int i = 0; i < distances.length; i++) {
			int compareDimensionResult = compareDimension(other, i);
			if (compareDimensionResult != 0)
				return compareDimensionResult;
		}
		return 0;
	}

	private int compareDimension(ShrinkingDistance other, int i) {
		long left = distances[i];
		long right = other.distances[i];
		return Long.compare(left, right);
	}

	private int compareLength(ShrinkingDistance other) {
		return Integer.compare(distances.length, other.distances.length);
	}

	public ShrinkingDistance plus(ShrinkingDistance other) {
		//TODO: Only works if this and other have one dimension
		long[] summedUpDistances = new long[]{this.distances[0] + other.distances[0]};
		return new ShrinkingDistance(summedUpDistances);
	}

	public ShrinkingDistance append(ShrinkingDistance distance) {
		//TODO: Only works if distance has one dimension
		long[] appendedDistances = Arrays.copyOf(distances, distances.length + 1);
		appendedDistances[distances.length] = distance.distances[0];
		return new ShrinkingDistance(appendedDistances);
	}
}
