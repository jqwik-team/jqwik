package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.*;

// TODO: Move most methods from EdgeCases here
public class EdgeCasesSupport {

	public static <T> EdgeCases<T> choose(final List<T> values) {
		List<Shrinkable<T>> shrinkables = new ArrayList<>();
		if (values.size() > 0) {
			shrinkables.add(new ChooseValueShrinkable<>(values.get(0), values));
		}
		if (values.size() > 1) {
			int lastIndex = values.size() - 1;
			shrinkables.add(new ChooseValueShrinkable<>(values.get(lastIndex), values));
		}
		try {
			if (values.contains(null)) {
				shrinkables.add(Shrinkable.unshrinkable(null));
			}
		} catch (NullPointerException someListsDoNotAllowNullValues) { }
		return EdgeCases.fromShrinkables(shrinkables);
	}

}
