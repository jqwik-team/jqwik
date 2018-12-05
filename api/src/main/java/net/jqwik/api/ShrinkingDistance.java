package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

public class ShrinkingDistance implements Comparable<ShrinkingDistance> {

	private final long[] distances;

	public static ShrinkingDistance of(long... distances) {
		return new ShrinkingDistance(distances);
	}

	public static <T> ShrinkingDistance forCollection(Collection<Shrinkable<T>> elements) {
		ShrinkingDistance sumDistanceOfElements = elements
			.stream()
			.map(Shrinkable::distance)
			.reduce(ShrinkingDistance.of(0), ShrinkingDistance::plus);

		return ShrinkingDistance.of(elements.size()).append(sumDistanceOfElements);
	}

	public static <T> ShrinkingDistance combine(List<Shrinkable<T>> shrinkables) {
		return shrinkables
			.stream()
			.map(Shrinkable::distance)
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
		return String.format("ShrinkingDistance:%s", Arrays.toString(distances));
	}

	@Override
	public int compareTo(ShrinkingDistance other) {
		int dimensionsToCompare = Math.max(size(), other.size());
		for (int i = 0; i < dimensionsToCompare; i++) {
			int compareDimensionResult = compareDimension(other, i);
			if (compareDimensionResult != 0)
				return compareDimensionResult;
		}
		return 0;
	}

	public List<ShrinkingDistance> dimensions() {
		return Arrays.stream(distances).mapToObj(ShrinkingDistance::of).collect(Collectors.toList());
	}

	public int size() {
		return distances.length;
	}

	private int compareDimension(ShrinkingDistance other, int i) {
		long left = at(distances, i);
		long right = at(other.distances, i);
		return Long.compare(left, right);
	}

	private long at(long[] array, int i) {
		return array.length > i ? array[i] : 0;
	}

	public ShrinkingDistance plus(ShrinkingDistance other) {
		long[] summedUpDistances = sumUpArrays(distances, other.distances);
		return new ShrinkingDistance(summedUpDistances);
	}

	private long[] sumUpArrays(long[] left, long[] right) {
		long[] sum = new long[Math.max(left.length, right.length)];
		for (int i = 0; i < sum.length; i++) {
			long summedValue = at(left, i) + at(right, i);
			if (summedValue < 0) {
				summedValue = Long.MAX_VALUE;
			}
			sum[i] = summedValue;
		}
		return sum;
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
