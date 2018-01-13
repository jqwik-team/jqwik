package net.jqwik.properties;

import net.jqwik.api.*;

import java.util.*;
import java.util.stream.*;

public class ShrinkingHelper {

	public static <T> Stream<ShrinkResult<Shrinkable<T>>> minDistanceStream(Set<ShrinkResult<Shrinkable<T>>> shrinkResults) {
		int minDistance = minDistance(shrinkResults);
		return shrinkResults.stream() //
			.filter(shrinkResult -> shrinkResult.shrunkValue().distance() == minDistance);
	}

	public static <T> int minDistance(Set<ShrinkResult<Shrinkable<T>>> shrinkResults) {
		int minDistance = Integer.MAX_VALUE;
		for (ShrinkResult<Shrinkable<T>> shrinkResult : shrinkResults) {
			int distance = shrinkResult.shrunkValue().distance();
			if (distance < minDistance) minDistance = distance;
		}
		return minDistance;
	}
}