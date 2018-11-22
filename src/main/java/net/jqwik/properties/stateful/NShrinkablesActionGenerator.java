package net.jqwik.properties.stateful;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

public class NShrinkablesActionGenerator<T> implements NActionGenerator<T> {

	private final Iterator<Shrinkable<Action<T>>> iterator;
	private List<Shrinkable<Action<T>>> shrinkables = new ArrayList<>();

	public NShrinkablesActionGenerator(List<Shrinkable<Action<T>>> shrinkables) {
		iterator = shrinkables.iterator();
	}

	@Override
	public Action next(Object model) {
		if (iterator.hasNext()) {
			Shrinkable<Action<T>> next = iterator.next();
			shrinkables.add(next);
			return next.value();
		}
		throw new NoSuchElementException("No more actions available");
	}

	@Override
	public List<Shrinkable<Action<T>>> generated() {
		return shrinkables;
	}
}
