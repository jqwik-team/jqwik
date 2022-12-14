package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

import org.apiguardian.api.*;
import org.jetbrains.annotations.*;

import static org.apiguardian.api.API.Status.*;

@API(status = STABLE, since = "1.0")
public class ShrinkingDistance implements Comparable<ShrinkingDistance> {

	@API(status = MAINTAINED, since = "1.2.0")
	public static final ShrinkingDistance MAX = ShrinkingDistance.of(Long.MAX_VALUE);

	private static final ShrinkingDistance MIN = ShrinkingDistance.of(0);

	private final long[] distances;

	@API(status = MAINTAINED, since = "1.0")
	public static ShrinkingDistance of(long... distances) {
		return new ShrinkingDistance(distances);
	}

	@API(status = MAINTAINED, since = "1.0")
	public static <T> ShrinkingDistance forCollection(Collection<Shrinkable<T>> elements) {
		if (elements.isEmpty()) {
			return MIN;
		}

		long[] data = null;
		for (Shrinkable<T> element : elements) {
			long[] next = element.distance().distances;
			if (data == null) {
				data = Arrays.copyOf(next, next.length);
				continue;
			}
			data = sumUpArrays(data, data, next);

		}
		return new ShrinkingDistance(concatArrays(new long[]{elements.size()}, data));
	}

	@API(status = MAINTAINED, since = "1.0")
	public static <T> ShrinkingDistance combine(List<Shrinkable<T>> shrinkables) {
		if (shrinkables.isEmpty()) {
			return MIN;
		}
		// Compute the total size of the required array
		List<long[]> distances = new ArrayList<>(shrinkables.size());
		int totalLength = 0;
		for (Shrinkable<T> shrinkable : shrinkables) {
			long[] next = shrinkable.distance().distances;
			totalLength += next.length;
			distances.add(next);
		}
		// Append all the arrays together
		long[] data = new long[totalLength];
		int index = 0;
		for (long[] distance : distances) {
			System.arraycopy(distance, 0, data, index, distance.length);
			index += distance.length;
		}
		return new ShrinkingDistance(data);
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

	private static long at(long[] array, int i) {
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

	private static long[] sumUpArrays(long @Nullable [] sum, long[] left, long[] right) {
		if (sum == null || sum.length < left.length || sum.length < right.length) {
			sum = new long[Math.max(left.length, right.length)];
		}
		for (int i = 0; i < sum.length; i++) {
			sum[i] = saturatedAdd(at(left, i), at(right, i));
		}
		return sum;
	}

	private static long saturatedAdd(long a, long b) {
		long sum = a + b;
		if (((a ^ sum) & (b ^ sum)) < 0) {
			return Long.MAX_VALUE;
		}
		return sum;
	}

	@API(status = INTERNAL)
	public ShrinkingDistance append(ShrinkingDistance other) {
		long[] appendedDistances = concatArrays(distances, other.distances);
		return new ShrinkingDistance(appendedDistances);
	}

	private static long[] concatArrays(long[] left, long[] right) {
		long[] concatenated = Arrays.copyOf(left, left.length + right.length);
		System.arraycopy(right, 0, concatenated, left.length, right.length);
		return concatenated;
	}

}
