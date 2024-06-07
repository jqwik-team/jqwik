package net.jqwik.api;

import java.util.*;
import java.util.stream.*;

import org.apiguardian.api.*;
import org.jspecify.annotations.*;

import static org.apiguardian.api.API.Status.*;

import static net.jqwik.api.ShrinkingDistanceArraysSupport.*;

/**
 * A {@code ShrinkingDistance} is a measure of how close a value is to the minimum value,
 * aka target value.
 *
 * <p>
 *     The distance is used during shrinking to determine if a shrunk value is really closer to the target value.
 *     If it is not, the value is being discarded.
 * </p>
 */
@API(status = STABLE, since = "1.0")
public class ShrinkingDistance implements Comparable<ShrinkingDistance> {

	@API(status = MAINTAINED, since = "1.2.0")
	public static final ShrinkingDistance MAX = ShrinkingDistance.of(Long.MAX_VALUE);

	@API(status = MAINTAINED, since = "1.7.2")
	public static final ShrinkingDistance MIN = ShrinkingDistance.of(0);

	private final long[] distances;

	/**
	 * Create a {@code ShrinkingDistance} with one or more dimensions.
	 *
	 * @param distances a non-empty array of non-negative values.
	 *
	 * @return an immutable instance of {@code ShrinkingDistance}
	 */
	@API(status = MAINTAINED, since = "1.0")
	public static ShrinkingDistance of(long... distances) {
		if (distances.length == 0) {
			throw new IllegalArgumentException("ShrinkingDistance requires at least one value");
		}
		if (Arrays.stream(distances).anyMatch(d -> d < 0)) {
			throw new IllegalArgumentException("ShrinkingDistance does not allow negative values");
		}
		return new ShrinkingDistance(distances);
	}

	@API(status = MAINTAINED, since = "1.0")
	public static <T extends @Nullable Object> ShrinkingDistance forCollection(Collection<? extends Shrinkable<T>> elements) {
		// This is an optimization to avoid creating temporary arrays, which the old streams-based implementation did.
		long[] collectedDistances = sumUp(toDistances(elements));
		ShrinkingDistance sumDistanceOfElements = new ShrinkingDistance(collectedDistances);
		return ShrinkingDistance.of(elements.size()).append(sumDistanceOfElements);
	}

	@API(status = MAINTAINED, since = "1.0")
	public static <T extends @Nullable Object> ShrinkingDistance combine(List<? extends Shrinkable<T>> shrinkables) {
		// This can happen e.g. when using Combinators.combine() with an empty list of arbitraries.
		if (shrinkables.isEmpty()) {
			return ShrinkingDistance.MIN;
		}

		// This is an optimization to avoid creating temporary arrays, which the old streams-based implementation did.
		long[] combinedDistances = concatenate(toDistances(shrinkables));
		return new ShrinkingDistance(combinedDistances);
	}

	private ShrinkingDistance(long[] distances) {
		this.distances = distances;
	}

	@Override
	public boolean equals(@Nullable Object o) {
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

	/**
	 * Compare to distances with each other.
	 * No distance can be greater than {@link #MAX}.
	 * No distance can be smaller than {@link #MIN}.
	 */
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
		long[] summedUpDistances = sumUp(Arrays.asList(distances, other.distances));
		return new ShrinkingDistance(summedUpDistances);
	}

	@API(status = INTERNAL)
	public ShrinkingDistance append(ShrinkingDistance other) {
		long[] appendedDistances = concatenate(Arrays.asList(distances, other.distances));
		return new ShrinkingDistance(appendedDistances);
	}

	private static <T extends @Nullable Object> List<long[]> toDistances(Collection<? extends Shrinkable<T>> shrinkables) {
		List<long[]> listOfDistances = new ArrayList<>(shrinkables.size());
		for (Shrinkable<?> tShrinkable : shrinkables) {
			long[] longs = tShrinkable.distance().distances;
			listOfDistances.add(longs);
		}
		return listOfDistances;
	}

}
