package net.jqwik.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.properties.*;

public class NContainerShrinkable<T, E> implements Shrinkable<T> {

	public static final Function<List<Character>, String> CREATE_STRING = list -> list.stream() //
			.map(c -> Character.toString(c)) //
			.collect(Collectors.joining());

	public static Shrinkable<String> stringOf(List<Shrinkable<Character>> chars) {
		return new NContainerShrinkable<>(chars, CREATE_STRING);
	}

	private final NListShrinkCandidates<E> listShrinker = new NListShrinkCandidates<>();

	private final List<Shrinkable<E>> elements;
	private final Function<List<E>, T> containerCreator;
	private final T value;

	public NContainerShrinkable(List<Shrinkable<E>> elements, Function<List<E>, T> containerCreator) {
		this.elements = elements;
		this.containerCreator = containerCreator;
		this.value = createContainer(elements); // premature optimization?
	}

	@Override
	public Set<ShrinkResult<Shrinkable<T>>> shrinkNext(Predicate<T> falsifier) {
		Set<ShrinkResult<Shrinkable<T>>> shrunkList = listShrinker.nextCandidates(elements).stream() //
																  .map(shrunkValue -> SafeFalsifier.falsify(falsifier, new NContainerShrinkable<>(shrunkValue, containerCreator))) //
																  .filter(optional -> optional.isPresent()) //
																  .map(optional -> optional.get()) //
																  .collect(Collectors.toSet());
		if (!shrunkList.isEmpty()) {
			return shrunkList;
		}

		return nextShrinkElements(falsifier) //
											 .map(shrinkResult -> shrinkResult
						.map(shrunkValue -> (Shrinkable<T>) new NContainerShrinkable<>(shrunkValue, containerCreator))) //
											 .collect(Collectors.toSet());
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
		if (this == o) return true;
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
