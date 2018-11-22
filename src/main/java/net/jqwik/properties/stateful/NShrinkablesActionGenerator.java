package net.jqwik.properties.stateful;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

public class NShrinkablesActionGenerator<T> implements NActionGenerator {

	private final Iterator<Shrinkable<Action<T>>> iterator;

	public NShrinkablesActionGenerator(List<Shrinkable<Action<T>>> shrinkables) {
		iterator = shrinkables.iterator();
	}

	@Override
	public Action next(Object model) {
		if (iterator.hasNext()) {
			return iterator.next().value();
		}
		throw new NoSuchElementException("No more actions available");
	}
}
