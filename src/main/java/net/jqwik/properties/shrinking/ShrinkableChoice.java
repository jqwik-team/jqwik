package net.jqwik.properties.shrinking;

import java.util.*;
import java.util.function.*;

public class ShrinkableChoice<T> implements Shrinkable<T> {

	public static <T> ShrinkableChoice<T> empty() {
		return new ShrinkableChoice<>();
	}

	private final List<List<Shrinkable<T>>> routes = new ArrayList<>();

	private final List<Shrinkable<T>> choices = new ArrayList<>();

	@Deprecated
	public void addChoice(List<Shrinkable<T>> route) {
		routes.add(route);
		ShrinkableSequence<T> sequence = new ShrinkableSequence<>(route);
		addChoice(sequence);
	}

	public void addChoice(Shrinkable<T> sequence) {
		choices.add(sequence);
	}

	@Deprecated
	public List<List<Shrinkable<T>>> routes() {
		return routes;
	}

	public List<Shrinkable<T>> choices() {
		return choices;
	}

	public Optional<ShrinkResult<T>> shrink(Predicate<T> falsifier) {
		return choices.stream() //
			.map(choice -> choice.shrink(falsifier)) //
			.filter(Optional::isPresent) //
			.map(Optional::get) //
			.sorted(Comparator.naturalOrder()) //
			.findFirst();
	}

}
