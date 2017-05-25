package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class NContainerShrinkable<T, E> implements NShrinkable<T> {

	public static final Function<List<Character>, String> CREATE_STRING = list -> list.stream() //
			.map(c -> Character.toString(c)) //
			.collect(Collectors.joining());

	public static NShrinkable<String> stringOf(List<NShrinkable<Character>> chars) {
		return new NContainerShrinkable<>(chars, CREATE_STRING);
	}

	private final NListShrinkCandidates<E> listShrinker = new NListShrinkCandidates<>();

	private final List<NShrinkable<E>> elements;
	private final Function<List<E>, T> containerCreator;
	private final T value;

	public NContainerShrinkable(List<NShrinkable<E>> elements, Function<List<E>, T> containerCreator) {
		this.elements = elements;
		this.containerCreator = containerCreator;
		this.value = createContainer(elements); // premature optimization?
	}

	@Override
	public Set<NShrinkResult<NShrinkable<T>>> shrinkNext(Predicate<T> falsifier) {
		Set<NShrinkResult<NShrinkable<T>>> shrunkList = listShrinker.nextCandidates(elements).stream() //
				.map(shrunkValue -> NSafeFalsifier.falsify(falsifier, new NContainerShrinkable<>(shrunkValue, containerCreator))) //
				.filter(optional -> optional.isPresent()) //
				.map(optional -> optional.get()) //
				.collect(Collectors.toSet());
		if (!shrunkList.isEmpty()) {
			return shrunkList;
		}

		return nextShrinkElements(falsifier) //
				.map(shrinkResult -> shrinkResult
						.map(shrunkValue -> (NShrinkable<T>) new NContainerShrinkable<>(shrunkValue, containerCreator))) //
				.collect(Collectors.toSet());
	}

	private Stream<NShrinkResult<List<NShrinkable<E>>>> nextShrinkElements(Predicate<T> falsifier) {
		Predicate<List<E>> valuesFalsifier = list -> {
			T container = containerCreator.apply(list);
			return falsifier.test(container);
		};
		NParameterListShrinker<E> listElementShrinker = new NParameterListShrinker<>(elements);
		Set<NShrinkResult<List<NShrinkable<E>>>> shrunkElements = listElementShrinker.shrinkNext(valuesFalsifier);
		return shrunkElements.stream();
	}

	@Override
	public T value() {
		return value;
	}

	private T createContainer(List<NShrinkable<E>> shrinkables) {
		List<E> values = shrinkables.stream() //
				.map(NShrinkable::value) //
				.collect(Collectors.toList());
		return containerCreator.apply(values);
	}

	@Override
	public int distance() {
		return listShrinker.distance(elements);
	}
}
