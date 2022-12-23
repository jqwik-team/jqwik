package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = STABLE, since = "1.0")
public class ShrinkingDistance implements Comparable<ShrinkingDistance> {

	@API(status = MAINTAINED, since = "1.2.0")
	public static final ShrinkingDistance MAX = ShrinkingDistance.of(Long.MAX_VALUE);

	@API(status = MAINTAINED, since = "1.7.2")
	public static final ShrinkingDistance MIN = ShrinkingDistance.of(0);

	private final long[] distances;

	@API(status = MAINTAINED, since = "1.0")
	public static ShrinkingDistance of(long... distances) {
		return new ShrinkingDistance(distances);
	}

	@API(status = MAINTAINED, since = "1.0")
	public static <T> ShrinkingDistance forCollection(Collection<Shrinkable<T>> elements) {
		ShrinkingDistance sumDistanceOfElements = elements
			.stream()
			.map(Shrinkable::distance)
			.reduce(ShrinkingDistance.of(0), ShrinkingDistance::plus);

		return ShrinkingDistance.of(elements.size()).append(sumDistanceOfElements);
	}

	@API(status = MAINTAINED, since = "1.0")
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
		if (this == MAX) {
			if (other == MAX) {
				return 0;
			} else {
				return 1;
			}
		}
		if (other == MAX) {
			return -1;
		}
		int dimensionsToCompare = Math.max(size(), other.size());
		for (int i = 0; i < dimensionsToCompare; i++) {
			int compareDimensionResult = compareDimension(other, i);
			if (compareDimensionResult != 0)
				return compareDimensionResult;
		}
		return 0;
	}

	@API(status = INTERNAL)
	public List<ShrinkingDistance> dimensions() {
		return Arrays.stream(distances).mapToObj(ShrinkingDistance::of).collect(Collectors.toList());
	}

	@API(status = INTERNAL)
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

	@API(status = INTERNAL)
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

	@API(status = INTERNAL)
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
