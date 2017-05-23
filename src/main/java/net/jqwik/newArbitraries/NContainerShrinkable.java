package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class NContainerShrinkable<T, U, C extends Collection<NShrinkable<U>>> implements NShrinkable<T> {

	public static final Function<Collection<Character>, String> CREATE_STRING = list -> list.stream() //
			.map(c -> Character.toString(c)) //
			.collect(Collectors.joining());

	public static NShrinkable<String> stringOf(List<NShrinkable<Character>> chars) {
		return new NContainerShrinkable<>(chars, CREATE_STRING, new NListShrinker<>());
	}

	private final C elements;
	private final Function<Collection<U>, T> containerFunction;
	private final NShrinker<C> containerShrinker;
	private final T value;

	public NContainerShrinkable(C elements, Function<Collection<U>, T> containerFunction, NShrinker<C> containerShrinker) {
		this.elements = elements;
		this.containerFunction = containerFunction;
		this.containerShrinker = containerShrinker;
		this.value = createContainer(elements); // premature optimization?
	}

	@Override
	public Set<NShrinkable<T>> nextShrinkingCandidates() {
		return containerShrinker.nextShrinkingCandidates(elements) //
								.stream() //
								.map(shrinkables -> new NContainerShrinkable<>(shrinkables, containerFunction, containerShrinker)) //
								.collect(Collectors.toSet());
	}

	@Override
	public T value() {
		return value;
	}

	private T createContainer(Collection<NShrinkable<U>> shrinkables) {
		List<U> values = shrinkables.stream() //
				.map(NShrinkable::value) //
				.collect(Collectors.toList());
		return containerFunction.apply(values);
	}

	@Override
	public int distance() {
		return containerShrinker.distance(elements);
	}
}
