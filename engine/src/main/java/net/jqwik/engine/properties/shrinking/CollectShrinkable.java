package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

public class CollectShrinkable<T> implements Shrinkable<List<T>> {
	private final List<Shrinkable<T>> elements;
	private final Predicate<List<T>> until;

	public CollectShrinkable(List<Shrinkable<T>> elements, Predicate<List<T>> until) {
		this.elements = elements;
		this.until = until;
	}

	@Override
	public List<T> value() {
		return createValue(elements);
	}

	@Override
	public List<T> createValue() {
		return value();
	}

	private List<T> createValue(List<Shrinkable<T>> elements) {
		return elements
				   .stream()
				   .map(Shrinkable::value)
				   .collect(Collectors.toList());
	}

	@Override
	public ShrinkingSequence<List<T>> shrink(Falsifier<List<T>> falsifier) {
		return new CollectShrinkingSequence<>(elements, until, falsifier);
	}

	@Override
	public Stream<Shrinkable<List<T>>> shrink() {
		return shrinkElementsOneAfterTheOther();
	}

	protected Stream<Shrinkable<List<T>>> shrinkElementsOneAfterTheOther() {
		List<Stream<Shrinkable<List<T>>>> shrinkPerPartStreams = new ArrayList<>();
		for (int i = 0; i < elements.size(); i++) {
			int index = i;
			Shrinkable<T> part = elements.get(i);
			Stream<Shrinkable<List<T>>> shrinkElement = part.shrink().flatMap(shrunkElement -> {
				Optional<List<Shrinkable<T>>> shrunkCollection = collectElements(index, shrunkElement);
				return shrunkCollection
						   .map(shrunkElements -> Stream.of(new CollectShrinkable<>(shrunkElements, until)))
						   .orElse(Stream.empty());
			});
			shrinkPerPartStreams.add(shrinkElement);
		}
		return JqwikStreamSupport.concat(shrinkPerPartStreams);
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
