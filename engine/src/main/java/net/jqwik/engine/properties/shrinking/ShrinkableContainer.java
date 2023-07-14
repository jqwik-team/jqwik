package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import org.jspecify.annotations.*;

import net.jqwik.api.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.support.*;

import static net.jqwik.engine.properties.UniquenessChecker.*;

abstract class ShrinkableContainer<C, E> implements Shrinkable<C> {
	protected final List<Shrinkable<E>> elements;
	protected final int minSize;
	protected final int maxSize;
	protected final Collection<FeatureExtractor<E>> uniquenessExtractors;

	@Nullable
	protected final Arbitrary<E> elementArbitrary;

	ShrinkableContainer(
		List<Shrinkable<E>> elements,
		int minSize, int maxSize,
		Collection<FeatureExtractor<E>> uniquenessExtractors,
		@Nullable Arbitrary<E> elementArbitrary
	) {
		this.elements = elements;
		this.minSize = minSize;
		this.maxSize = maxSize;
		this.uniquenessExtractors = uniquenessExtractors;
		this.elementArbitrary = elementArbitrary;
	}

	abstract C createValue(List<Shrinkable<E>> shrinkables);

	@Override
	public C value() {
		return createValue(elements);
	}

	@Override
	public Stream<Shrinkable<C>> shrink() {
		return JqwikStreamSupport.concat(
			shrinkSizeOfList(),
			shrinkElementsOneAfterTheOther(0),
			shrinkPairsOfElements()
		);
	}

	@Override
	public Optional<Shrinkable<C>> grow(Shrinkable<?> before, Shrinkable<?> after) {
		if (before instanceof ShrinkableContainer && after instanceof ShrinkableContainer) {
			ShrinkableContainer<?, ?> beforeContainer = (ShrinkableContainer<?, ?>) before;
			ShrinkableContainer<?, ?> afterContainer = (ShrinkableContainer<?, ?>) after;
			// Moving shrinkable from one container to another is only allowed if both contain elements
			// created by the same arbitrary
			if (Objects.equals(beforeContainer.elementArbitrary, afterContainer.elementArbitrary)) {
				List<Shrinkable<?>> removedShrinkables = new ArrayList<>((beforeContainer).elements);
				removedShrinkables.removeAll((afterContainer).elements);
				return growBy(removedShrinkables);
			}
		}
		return Optional.empty();
	}

	@Override
	public Stream<Shrinkable<C>> grow() {
		return growOneElementAfterTheOther();
	}

	private Stream<Shrinkable<C>> growOneElementAfterTheOther() {
		List<Stream<Shrinkable<C>>> growPerElementStreams = new ArrayList<>();
		for (int i = 0; i < elements.size(); i++) {
			int index = i;
			Shrinkable<E> element = elements.get(i);
			Stream<Shrinkable<C>> shrinkElement = element.grow().flatMap(shrunkElement -> {
				List<Shrinkable<E>> elementsCopy = new ArrayList<>(elements);
				elementsCopy.set(index, shrunkElement);
				return Stream.of(createShrinkable(elementsCopy));
			});
			growPerElementStreams.add(shrinkElement);
		}
		return JqwikStreamSupport.concat(growPerElementStreams);
	}

	private Optional<Shrinkable<C>> growBy(List<Shrinkable<?>> shrinkables) {
		if (elements.size() + shrinkables.size() <= maxSize) {
			List<Shrinkable<E>> grownElements = new ArrayList<>(elements);
			for (Shrinkable<?> shrinkable : shrinkables) {
				try {
					@SuppressWarnings("unchecked")
					Shrinkable<E> castShrinkable = (Shrinkable<E>) shrinkable;
					grownElements.add(0, castShrinkable);
				} catch (Throwable classCastException) {
					return Optional.empty();
				}
			}
			Shrinkable<C> grownShrinkable = createShrinkable(grownElements);
			if (hasReallyGrown(grownShrinkable)) {
				return Optional.of(grownShrinkable);
			}
		}
		return Optional.empty();
	}

	protected boolean hasReallyGrown(Shrinkable<C> grownShrinkable) {
		return true;
	}

	protected Stream<Shrinkable<C>> shrinkSizeAggressively() {
		return new AggressiveSizeOfListShrinker<Shrinkable<E>>(minSize)
				   .shrink(elements)
				   .map(this::createShrinkable)
				   .sorted(Comparator.comparing(Shrinkable::distance));
	}

	protected Stream<Shrinkable<C>> shrinkSizeOfList() {
		return new SizeOfListShrinker<Shrinkable<E>>(minSize)
				   .shrink(elements)
				   .map(this::createShrinkable)
				   .sorted(Comparator.comparing(Shrinkable::distance));
	}

	protected Stream<Shrinkable<C>> shrinkElementsOneAfterTheOther(int maxToShrink) {
		List<Stream<Shrinkable<C>>> shrinkPerElementStreams = new ArrayList<>();
		for (int i = 0; i < elements.size(); i++) {
			if (maxToShrink > 0 && i >= maxToShrink) {
				break;
			}
			int index = i;
			Shrinkable<E> element = elements.get(i);
			Stream<Shrinkable<C>> shrinkElement = element.shrink().flatMap(shrunkElement -> {
				List<Shrinkable<E>> elementsCopy = new ArrayList<>(elements);
				elementsCopy.remove(index);
				if (!checkShrinkableUniqueIn(uniquenessExtractors, shrunkElement, elementsCopy)) {
					return Stream.empty();
				}
				elementsCopy.add(index, shrunkElement);
				return Stream.of(createShrinkable(elementsCopy));
			});
			shrinkPerElementStreams.add(shrinkElement);
		}
		return JqwikStreamSupport.concat(shrinkPerElementStreams);
	}

	protected Stream<Shrinkable<C>> shrinkPairsOfElements() {
		ShrinkingCommons.ContainerCreator<C, E> createContainer = newElements -> {
			if (checkUniquenessOfShrinkables(uniquenessExtractors, newElements)) {
				return createShrinkable(newElements);
			} else {
				// null value will skip the entry in zipped stream
				return null;
			}
		};
		return ShrinkingCommons.shrinkPairsOfElements(elements, createContainer);
	}

	protected Stream<Shrinkable<C>> sortElements() {
		return ShrinkingCommons.sortElements(elements, this::createShrinkable);
	}

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.forCollection(elements);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ShrinkableContainer<?, ?> that = (ShrinkableContainer<?, ?>) o;
		return value().equals(that.value());
	}

	@Override
	public int hashCode() {
		return HashCodeSupport.hash(elements);
	}

	@Override
	public String toString() {
		return String.format(
			"%s<%s>(%s:%s)",
			getClass().getSimpleName(),
			value().getClass().getSimpleName(),
			value(), distance()
		);
	}

	abstract Shrinkable<C> createShrinkable(List<Shrinkable<E>> shrunkElements);

}
