package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

public class ShrinkElementsSequence {

	/**
	 * This can be used to shrink the individual shrinkable elements in a list
	 * without shrinking the size of the list and keeping the order:
	 * <ul>
	 *     <li>The actual elements of a container (list, set, action sequence)</li>
	 *     <li>All shrinkable parameters of a property</li>
	 * </ul>
	 */
	public static <T> ShrinkingSequence<List<T>> shrinkElements(
		List<Shrinkable<T>> currentElements,
		Falsifier<List<T>> listFalsifier,
		Function<List<Shrinkable<T>>, ShrinkingDistance> distanceFunction
	) {
		return new ShrinkOneElementAfterTheOtherSequence<>(currentElements, listFalsifier, distanceFunction);
	}
}
