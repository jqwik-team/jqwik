package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

abstract class ShrinkableContainer<C, E> implements Shrinkable<C> {
	protected final List<Shrinkable<E>> elements;
	protected final int minSize;
	private final ListShrinkingCandidates<Shrinkable<E>> shrinkCandidates;

	ShrinkableContainer(List<Shrinkable<E>> elements, int minSize) {
		this.elements = elements;
		this.shrinkCandidates = new ListShrinkingCandidates<>(minSize);
		this.minSize = minSize;
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
	public ShrinkingSequence<C> shrink(Falsifier<C> falsifier) {
		return new DeepSearchShrinkingSequence<>(this, this::shrinkCandidatesFor, falsifier)
				   .andThen(shrinkableList -> {
					   List<Shrinkable<E>> elements = ((ShrinkableContainer<C, E>) shrinkableList).elements;
					   Falsifier<List<E>> listFalsifier = falsifier.map(this::toContainer);
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

	@Override
	public C createValue() {
		// TODO: Check if the underlying elements are freshly created
		return value();
	}

	@Override
	public Stream<Shrinkable<C>> shrink() {
		return JqwikStreamSupport.lazyConcat(
			this::shrinkSizeOfList,
			this::shrinkElementsOneAfterTheOther,
			this::shrinkPairsOfElements
		);
	}

	private Stream<Shrinkable<C>> shrinkSizeOfList() {
		Set<List<Shrinkable<E>>> shrinkSizeOfListElements = new NEW_ShrinkSizeOfListCandidates<E>(minSize).candidatesFor(elements);
		return shrinkSizeOfListElements
				   .stream()
				   .map(this::createShrinkable)
				   .sorted(Comparator.comparing(Shrinkable::distance));
	}

	private Stream<Shrinkable<C>> shrinkElementsOneAfterTheOther() {
		List<Stream<Shrinkable<C>>> shrinkPerElementStreams = new ArrayList<>();
		for (int i = 0; i < elements.size(); i++) {
			int index = i;
			Shrinkable<E> element = elements.get(i);
			List<Shrinkable<E>> elementsCopy = new ArrayList<>(elements);
			Stream<Shrinkable<C>> shrinkElement = element.shrink().flatMap(shrunkElement -> {
				elementsCopy.set(index, shrunkElement);
				return Stream.of(createShrinkable(elementsCopy));
			});
			shrinkPerElementStreams.add(shrinkElement);
		}
		return JqwikStreamSupport.concat(shrinkPerElementStreams);
	}

	private Stream<Shrinkable<C>> shrinkPairsOfElements() {
		if (elements.size() < 2) {
			return Stream.empty();
		}
		List<Supplier<Stream<Shrinkable<C>>>> suppliers = new ArrayList<>();
		for (Tuple.Tuple2<Integer, Integer> pair : Combinatorics.distinctPairs(elements.size())) {
			Supplier<Stream<Shrinkable<C>>> zip = () -> JqwikStreamSupport.zip(
				elements.get(pair.get1()).shrink(),
				elements.get(pair.get2()).shrink(),
				(Shrinkable<E> s1, Shrinkable<E> s2) -> {
					List<Shrinkable<E>> newElements = new ArrayList<>(elements);
					newElements.set(pair.get1(), s1);
					newElements.set(pair.get2(), s2);
					return createShrinkable(newElements);
				}
			);
			suppliers.add(zip);
		}
		return JqwikStreamSupport.lazyConcat(suppliers);
	}

	private C toContainer(List<E> listOfE) {
		return listOfE.stream().collect(containerCollector());
	}

	private Shrinkable<C> toContainerShrinkable(Shrinkable<List<E>> listOfShrinkable) {
		List<Shrinkable<E>> shrinkableElements =
			listOfShrinkable.value().stream()
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
