package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.properties.*;

public class ContainerShrinkable<T, E> implements Shrinkable<T> {

	public static final Function<List<Character>, String> CREATE_STRING = list -> list.stream() //
			.map(c -> Character.toString(c)) //
			.collect(Collectors.joining());

	public static Shrinkable<String> stringOf(List<Shrinkable<Character>> chars, int minSize) {
		return new ContainerShrinkable<>(chars, CREATE_STRING, minSize);
	}

	private final ListShrinkCandidates<E> listShrinker;
	private final List<Shrinkable<E>> elements;
	private final Function<List<E>, T> containerCreator;
	private final T value;
	private final int minSize;

	public ContainerShrinkable(List<Shrinkable<E>> elements, Function<List<E>, T> containerCreator, int minSize) {
		this.elements = elements;
		this.containerCreator = containerCreator;
		this.value = createContainer(elements); // premature optimization?
		this.minSize = minSize;
		this.listShrinker = new ListShrinkCandidates<>(this.minSize);
	}

	@Override
	public Set<ShrinkResult<Shrinkable<T>>> shrinkNext(Predicate<T> falsifier) {
		Set<List<Shrinkable<E>>> candidates = listShrinker.nextCandidates(elements);
		Set<ShrinkResult<Shrinkable<T>>> shrunkList = candidates.stream() //
				.map(shrunkValue -> SafeFalsifier.falsify(falsifier, new ContainerShrinkable<>(shrunkValue, containerCreator, minSize))) //
				.filter(optional -> optional.isPresent()) //
				.map(optional -> optional.get()) //
				.collect(Collectors.toSet());
		nextShrinkElements(falsifier) //
				.map(shrinkResult -> shrinkResult
						.map(shrunkValue -> (Shrinkable<T>) new ContainerShrinkable<>(shrunkValue, containerCreator, minSize))) //
				.forEach(shrunkList::add);
		return shrunkList;
	}

	private Stream<ShrinkResult<List<Shrinkable<E>>>> nextShrinkElements(Predicate<T> falsifier) {
		Predicate<List<E>> valuesFalsifier = list -> {
			T container = containerCreator.apply(list);
			return falsifier.test(container);
		};
		ParameterListShrinker<E> listElementShrinker = new ParameterListShrinker<>(elements);
		Set<ShrinkResult<List<Shrinkable<E>>>> shrunkElements = listElementShrinker.shrinkNext(valuesFalsifier);
		return shrunkElements.stream();
	}

	@Override
	public T value() {
		return value;
	}

	private T createContainer(List<Shrinkable<E>> shrinkables) {
		List<E> values = shrinkables.stream() //
				.map(Shrinkable::value) //
				.collect(Collectors.toList());
		return containerCreator.apply(values);
	}

	@Override
	public int distance() {
		return listShrinker.distance(elements);
	}

	@Override
	public String toString() {
		return String.format("ContainerShrinkable[%s:%d]", value(), distance());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || !(o instanceof Shrinkable))
			return false;
		Shrinkable<?> that = (Shrinkable<?>) o;
		return Objects.equals(value, that.value());
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}
