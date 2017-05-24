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
	public Set<NShrinkable<T>> nextShrinkingCandidates() {
		return listShrinker.nextCandidates(elements) //
						   .stream() //
						   .map(shrinkables -> new NContainerShrinkable<>(shrinkables, containerCreator)) //
						   .collect(Collectors.toSet());
	}

	@Override
	public NShrinkResult<NShrinkable<T>> shrink(Predicate<T> falsifier, Throwable originalError) {
		NShrinkResult<NShrinkable<T>> shrunkBySize = new NSingleValueShrinker<>(this, originalError).shrink(falsifier);
		//new NParameterListShrinker<>(falsifier);
		return shrunkBySize;
	}

	@Override
	public T value() {
		return value;
	}

	private T createContainer(Collection<NShrinkable<E>> shrinkables) {
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
