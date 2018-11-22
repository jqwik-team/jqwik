package net.jqwik.properties.stateful;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.stateful.*;

public class NListActionGenerator<T> implements NActionGenerator<T> {
	private final Iterator<Action<T>> iterator;
	private List<Shrinkable<Action<T>>> generated = new ArrayList<>();

	public NListActionGenerator(List<Action<T>> listOfActions) {
		iterator = listOfActions.iterator();
	}

	@Override
	public Action<T> next(T model) {
		while (iterator.hasNext()) {
			Action<T> next = iterator.next();
			if (!next.precondition(model)) {
				continue;
			}
			generated.add(Shrinkable.unshrinkable(next));
			return next;
		}
		throw new NoSuchElementException("No more actions available");
	}

	@Override
	public List<Shrinkable<Action<T>>> generated() {
		return generated;
	}
}
