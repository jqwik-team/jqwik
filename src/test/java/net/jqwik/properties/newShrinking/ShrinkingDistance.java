package net.jqwik.properties.newShrinking;

import net.jqwik.support.*;

import java.util.*;

public class ShrinkingDistance implements Comparable<ShrinkingDistance> {

	private final long[] distances;

	public static ShrinkingDistance of(long... distances) {
		return new ShrinkingDistance(distances);
	}

	public static <T> ShrinkingDistance forCollection(Collection<NShrinkable<T>> elements) {
		ShrinkingDistance sumDistanceOfElements = elements
			.stream()
			.map(NShrinkable::distance)
			.reduce(ShrinkingDistance.of(0), ShrinkingDistance::plus);

		return ShrinkingDistance.of(elements.size()).append(sumDistanceOfElements);
	}

	public static <T> ShrinkingDistance combine(List<NShrinkable<T>> shrinkables) {
		return shrinkables
			.stream()
			.map(NShrinkable::distance)
			.reduce(new ShrinkingDistance(new long[0]), ShrinkingDistance::append);
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
		//TODO: What should happen if dimension of this or other > 1?
		long[] summedUpDistances = sumUpArrays(distances, other.distances);
		return new ShrinkingDistance(summedUpDistances);
	}

	private long[] sumUpArrays(long[] left, long[] right) {
		return new long[]{left[0] + right[0]};
	}

	public ShrinkingDistance append(ShrinkingDistance other) {
		long[] appendedDistances = concatArrays(distances, other.distances);
		return new ShrinkingDistance(appendedDistances);
	}

	private long[] concatArrays(long[] left, long[] right) {
		long[] concatenated = Arrays.copyOf(left, left.length + right.length);
		System.arraycopy(right, 0, concatenated, left.length, right.length);
		return concatenated;
	}

}
