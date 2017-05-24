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

	/**
	 * The following kind of shrinking forgoes the standard shrinking in order to first shrink the size of the elements
	 * list and only shrink the individual elements later Problem: When filtering a container, only the size of the
	 * element list will be shrunk because filtering relies on Shrinkable.nextShrinkingCandidates().
	 *
	 * The perfect solution would generate the full search tree for shrinking containers but prune it before trying to
	 * find the smallest solution. But that's for another day (or month or year).
	 **/
	@Override
	public NShrinkResult<NShrinkable<T>> shrink(Predicate<T> falsifier, Throwable originalError) {
		NShrinkResult<List<NShrinkable<E>>> shrunkListResult = shrinkListOfElements(falsifier, originalError);
		NShrinkResult<List<NShrinkable<E>>> shrunkIndividualElementsResult = shrinkIndividualElements(falsifier, shrunkListResult);
		NContainerShrinkable<T, E> shrunkValue = new NContainerShrinkable<>(shrunkIndividualElementsResult.value(), containerCreator);
		return NShrinkResult.of(shrunkValue, shrunkIndividualElementsResult.throwable().orElse(null));
	}

	private NShrinkResult<List<NShrinkable<E>>> shrinkIndividualElements(Predicate<T> falsifier,
			NShrinkResult<List<NShrinkable<E>>> shrunkListResult) {
		Predicate<List<E>> valuesFalsifier = list -> {
			T container = containerCreator.apply(list);
			return falsifier.test(container);
		};

		NParameterListShrinker<E> listElementShrinker = new NParameterListShrinker<>(valuesFalsifier);
		return listElementShrinker.shrinkListElements(shrunkListResult.value(), shrunkListResult.throwable().orElse(null));
	}

	private NShrinkResult<List<NShrinkable<E>>> shrinkListOfElements(Predicate<T> falsifier, Throwable originalError) {
		Predicate<List<NShrinkable<E>>> elementFalsifier = list -> {
			T container = createContainer(list);
			return falsifier.test(container);
		};
		NListShrinker<E> listShrinker = new NListShrinker<>(elements, originalError);
		return listShrinker.shrink(elementFalsifier);
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
