package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

public class CollectShrinkable<T> implements Shrinkable<List<T>> {
	private final List<? extends Shrinkable<T>> elements;
	private final Predicate<? super List<? extends T>> until;

	public CollectShrinkable(List<? extends Shrinkable<T>> elements, Predicate<? super List<? extends T>> until) {
		this.elements = elements;
		this.until = until;
	}

	@Override
	public List<T> value() {
		return createValue(elements);
	}

	private List<T> createValue(List<? extends Shrinkable<T>> elements) {
		return elements
				   .stream()
				   .map(Shrinkable::value)
				   .collect(Collectors.toList());
	}

	@Override
	public Stream<Shrinkable<List<T>>> shrink() {
		return JqwikStreamSupport.concat(
			shrinkElementsOneAfterTheOther(),
			sortElements()
		).filter(s -> until.test(s.value()));
	}

	private Stream<Shrinkable<List<T>>> shrinkElementsOneAfterTheOther() {
		List<Stream<Shrinkable<List<T>>>> shrinkPerPartStreams = new ArrayList<>();
		for (int i = 0; i < elements.size(); i++) {
			int index = i;
			Shrinkable<T> part = elements.get(i);
			Stream<Shrinkable<List<T>>> shrinkElement = part.shrink().flatMap(shrunkElement -> {
				Optional<List<Shrinkable<T>>> shrunkCollection = collectElements(index, shrunkElement);
				return shrunkCollection
						   .map(shrunkElements -> Stream.of(createShrinkable(shrunkElements)))
						   .orElse(Stream.empty());
			});
			shrinkPerPartStreams.add(shrinkElement);
		}
		return JqwikStreamSupport.concat(shrinkPerPartStreams);
	}

	private Stream<Shrinkable<List<T>>> sortElements() {
		return ShrinkingCommons.sortElements(elements, (ShrinkingCommons.ContainerCreator<List<T>, T>) this::createShrinkable);
	}

	private CollectShrinkable<T> createShrinkable(List<? extends Shrinkable<T>> pairSwap) {
		return new CollectShrinkable<>(pairSwap, until);
	}

	private Optional<List<Shrinkable<T>>> collectElements(int replaceIndex, Shrinkable<T> shrunkElement) {
		List<Shrinkable<T>> newElements = new ArrayList<>();
		for (int i = 0; i < elements.size(); i++) {
			if (i == replaceIndex) {
				newElements.add(shrunkElement);
			} else {
				newElements.add(elements.get(i));
			}
			if (until.test(createValue(newElements))) {
				return Optional.of(newElements);
			}
		}
		return Optional.empty();
	}

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.forCollection(elements);
	}
}
