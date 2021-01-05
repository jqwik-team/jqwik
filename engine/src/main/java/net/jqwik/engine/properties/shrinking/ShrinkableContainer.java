package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

abstract class ShrinkableContainer<C, E> implements Shrinkable<C> {
	protected final List<Shrinkable<E>> elements;
	protected final int minSize;
	protected final int maxSize;

	ShrinkableContainer(List<Shrinkable<E>> elements, int minSize, int maxSize) {
		this.elements = elements;
		this.minSize = minSize;
		this.maxSize = maxSize;
	}

	private C createValue(List<Shrinkable<E>> shrinkables) {
		return shrinkables
				   .stream()
				   .map(Shrinkable::value)
				   .collect(containerCollector());
	}

	@Override
	public C value() {
		return createValue(elements);
	}

	@Override
	public Stream<Shrinkable<C>> shrink() {
		return JqwikStreamSupport.concat(
			shrinkSizeOfList(),
			shrinkElementsOneAfterTheOther(),
			shrinkPairsOfElements()
		);
	}

	@Override
	public Optional<Shrinkable<C>> grow(Shrinkable<?> before, Shrinkable<?> after) {
		if (before instanceof ShrinkableContainer && after instanceof ShrinkableContainer) {
			List<Shrinkable<?>> removedShrinkables = new ArrayList<>(((ShrinkableContainer<?, ?>) before).elements);
			removedShrinkables.removeAll(((ShrinkableContainer<?, ?>) after).elements);
			return growBy(removedShrinkables);
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

	protected Stream<Shrinkable<C>> shrinkSizeOfList() {
		return new SizeOfListShrinker<Shrinkable<E>>(minSize)
				   .shrink(elements)
				   .map(this::createShrinkable)
				   .sorted(Comparator.comparing(Shrinkable::distance));
	}

	protected Stream<Shrinkable<C>> shrinkElementsOneAfterTheOther() {
		List<Stream<Shrinkable<C>>> shrinkPerElementStreams = new ArrayList<>();
		for (int i = 0; i < elements.size(); i++) {
			int index = i;
			Shrinkable<E> element = elements.get(i);
			Stream<Shrinkable<C>> shrinkElement = element.shrink().flatMap(shrunkElement -> {
				List<Shrinkable<E>> elementsCopy = new ArrayList<>(elements);
				elementsCopy.set(index, shrunkElement);
				return Stream.of(createShrinkable(elementsCopy));
			});
			shrinkPerElementStreams.add(shrinkElement);
		}
		return JqwikStreamSupport.concat(shrinkPerElementStreams);
	}

	protected Stream<Shrinkable<C>> shrinkPairsOfElements() {
		return Combinatorics
				   .distinctPairs(elements.size())
				   .flatMap(pair -> JqwikStreamSupport.zip(
					   elements.get(pair.get1()).shrink(),
					   elements.get(pair.get2()).shrink(),
					   (Shrinkable<E> s1, Shrinkable<E> s2) -> {
						   List<Shrinkable<E>> newElements = new ArrayList<>(elements);
						   newElements.set(pair.get1(), s1);
						   newElements.set(pair.get2(), s2);
						   return createShrinkable(newElements);
					   }
				   ));
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
		return Objects.hash(elements);
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

	abstract Collector<E, ?, C> containerCollector();

}
