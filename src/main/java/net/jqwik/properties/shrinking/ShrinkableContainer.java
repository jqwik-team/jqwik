package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.*;
import java.util.stream.*;

abstract class ShrinkableContainer<C, E> implements Shrinkable<C> {
	private final List<Shrinkable<E>> elements;
	private final ListShrinkingCandidates<Shrinkable<E>> shrinkCandidates;

	ShrinkableContainer(List<Shrinkable<E>> elements, int minSize) {
		this.elements = elements;
		this.shrinkCandidates = new ListShrinkingCandidates<>(minSize);
	}

	private C createValue(List<Shrinkable<E>> shrinkables) {
		return shrinkables //
						   .stream() //
						   .map(Shrinkable::value) //
						   .collect(containerCollector());
	}

	@Override
	public C value() {
		return createValue(elements);
	}

	@Override
	public ShrinkingSequence<C> shrink(Falsifier<C> falsifier) {
		return new DeepSearchShrinkingSequence<>(this, this::shrinkCandidatesFor, falsifier) //
			.andThen(shrinkableList -> { //
				List<Shrinkable<E>> elements = ((ShrinkableContainer<C, E>) shrinkableList).elements;
				Falsifier<List<E>> listFalsifier = list -> falsifier.test(toContainer(list));
				return new ElementsShrinkingSequence<>(elements, null, listFalsifier, ShrinkingDistance::forCollection)
					.map(this::toContainer);
			});
	}

	private C toContainer(List<E> listOfE) {
		return listOfE.stream().collect(containerCollector());
	}

	private Set<Shrinkable<C>> shrinkCandidatesFor(Shrinkable<C> shrinkable) {
		ShrinkableContainer<C, E> listShrinkable = (ShrinkableContainer<C, E>) shrinkable;
		return shrinkCandidates.candidatesFor(listShrinkable.elements) //
			.stream() //
			.map(this::createShrinkable) //
			.collect(Collectors.toSet());
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
		return String.format("%s<%s>(%s:%s)", //
			getClass().getSimpleName(), //
			value().getClass().getSimpleName(), //
			value(), distance());
	}

	abstract Shrinkable<C> createShrinkable(List<Shrinkable<E>> shrunkElements);

	abstract Collector<E, ?, C> containerCollector();

}
