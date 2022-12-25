package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

import org.apiguardian.api.*;
import org.jetbrains.annotations.*;

import static org.apiguardian.api.API.Status.*;

import static net.jqwik.api.support.LongArraysSupport.*;

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
													  .reduce(ShrinkingDistance.MIN, ShrinkingDistance::plus);

		return ShrinkingDistance.of(elements.size()).append(sumDistanceOfElements);
	}

	@API(status = MAINTAINED, since = "1.0")
	public static <T> ShrinkingDistance combine(List<Shrinkable<T>> shrinkables) {
		if (shrinkables.isEmpty()) {
			throw new IllegalArgumentException("At least one shrinkable is required");
		}
		// This is an optimization to avoid creating many temporary arrays, which the old streams-based implementation did.
		List<long[]> shrinkableDistances = toDistances(shrinkables);
		long[] combinedDistances = concatenate(shrinkableDistances);
		return new ShrinkingDistance(combinedDistances);
	}

	private ShrinkingDistance(long[] distances) {
		this.distances = distances;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ShrinkingDistance that = (ShrinkingDistance) o;
		return Arrays.equals(this.distances, that.distances);
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

	@API(status = INTERNAL)
	public ShrinkingDistance plus(ShrinkingDistance other) {
		long[] summedUpDistances = sumUp(distances, other.distances);
		return new ShrinkingDistance(summedUpDistances);
	}

	@API(status = INTERNAL)
	public ShrinkingDistance append(ShrinkingDistance other) {
		long[] appendedDistances = concatenate(Arrays.asList(distances, other.distances));
		return new ShrinkingDistance(appendedDistances);
	}

	@NotNull
	private static <T> List<long[]> toDistances(List<Shrinkable<T>> shrinkables) {
		List<long[]> listOfDistances = new ArrayList<>(shrinkables.size());
		for (Shrinkable<?> tShrinkable : shrinkables) {
			long[] longs = tShrinkable.distance().distances;
			listOfDistances.add(longs);
		}
		return listOfDistances;
	}

}
