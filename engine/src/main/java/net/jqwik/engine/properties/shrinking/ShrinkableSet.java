package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import org.jspecify.annotations.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;

public class ShrinkableSet<E extends @Nullable Object> extends ShrinkableContainer<Set<E>, E> {

	public ShrinkableSet(
		Collection<? extends Shrinkable<E>> elements,
		int minSize, int maxSize,
		Collection<? extends FeatureExtractor<E>> uniquenessExtractors,
		@Nullable Arbitrary<E> elementArbitrary
	) {
		this(new ArrayList<>(elements), minSize, maxSize, uniquenessExtractors, elementArbitrary);
	}

	private ShrinkableSet(
		List<? extends Shrinkable<E>> elements,
		int minSize, int maxSize,
		Collection<? extends FeatureExtractor<E>> uniquenessExtractors,
		@Nullable Arbitrary<E> elementArbitrary
	) {
		super(elements, minSize, maxSize, uniquenessExtractors, elementArbitrary);
	}

	@Override
	public Stream<Shrinkable<Set<E>>> shrink() {
		return super.shrink().filter(shrinkableSet -> shrinkableSet.value().size() >= minSize);
	}

	@Override
	protected boolean hasReallyGrown(Shrinkable<Set<E>> grownShrinkable) {
		return grownShrinkable.value().size() > elements.size();
	}

	@Override
	Set<E> createValue(List<? extends Shrinkable<E>> shrinkables) {
		// See https://richardstartin.github.io/posts/5-java-mundane-performance-tricks#size-hashmaps-whenever-possible
		//     for how to compute initial capacity of hash maps
		int capacityWithLoadFactor = shrinkables.size() * 4 / 3;

		// Using loop instead of stream to make stack traces more readable
		Set<E> values = new LinkedHashSet<>(capacityWithLoadFactor);
		for (Shrinkable<E> shrinkable : shrinkables) {
			values.add(shrinkable.value());
		}
		return values;
	}

	@Override
	Shrinkable<Set<E>> createShrinkable(List<? extends Shrinkable<E>> shrunkElements) {
		return new ShrinkableSet<>(shrunkElements, minSize, maxSize, uniquenessExtractors, elementArbitrary);
	}
}
