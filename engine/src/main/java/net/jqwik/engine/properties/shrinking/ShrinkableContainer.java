package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

abstract class ShrinkableContainer<C, E> implements Shrinkable<C> {
	private final List<Shrinkable<E>> elements;
	private final ListShrinkingCandidates<Shrinkable<E>> shrinkCandidates;
	private final C value;

	ShrinkableContainer(List<Shrinkable<E>> elements, int minSize) {
		this.elements = elements;
		this.shrinkCandidates = new ListShrinkingCandidates<>(minSize);
		this.value = createValue(elements);
	}

	private C createValue(List<Shrinkable<E>> shrinkables) {
		return shrinkables
				   .stream()
				   .map(Shrinkable::value)
				   .collect(containerCollector());
	}

	@Override
	public C value() {
		return value;
	}

	@Override
	public ShrinkingSequence<C> shrink(Falsifier<C> falsifier) {
		return new DeepSearchShrinkingSequence<>(this, this::shrinkCandidatesFor, falsifier)
				   .andThen(shrinkableList -> {
					   List<Shrinkable<E>> elements = ((ShrinkableContainer<C, E>) shrinkableList).elements;
					   Falsifier<List<E>> listFalsifier = list -> falsifier.executeTry(toContainer(list));
					   return new ContainerShrinkingSequence<>(elements, listFalsifier, ShrinkingDistance::forCollection, this::toContainerShrinkable);
				   }).andThen(shrinkableContainer ->
								  new DeepSearchShrinkingSequence<>(shrinkableContainer, this::shrinkCandidatesFor, falsifier)
			);
	}

	@Override
	public List<Shrinkable<C>> shrinkingSuggestions() {
		ArrayList<Shrinkable<C>> shrinkables = new ArrayList<>(shrinkCandidatesFor(this));
		shrinkables.addAll(Shrinkable.super.shrinkingSuggestions());
		shrinkables.sort(null);
		return shrinkables;
	}

	private C toContainer(List<E> listOfE) {
		return listOfE.stream().collect(containerCollector());
	}

	private Shrinkable<C> toContainerShrinkable(Shrinkable<List<E>> listOfShrinkable) {
		List<Shrinkable<E>> shrinkableElements =
			listOfShrinkable.value().stream()
							// TODO: Can the distance of these unshrinkables be computed?
							.map(Shrinkable::unshrinkable)
							.collect(Collectors.toList());
		return createShrinkable(shrinkableElements);
	}

	private Set<Shrinkable<C>> shrinkCandidatesFor(Shrinkable<C> shrinkable) {
		ShrinkableContainer<C, E> listShrinkable = (ShrinkableContainer<C, E>) shrinkable;
		return shrinkCandidates.candidatesFor(listShrinkable.elements)
							   .stream()
							   .map(this::createShrinkable)
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
