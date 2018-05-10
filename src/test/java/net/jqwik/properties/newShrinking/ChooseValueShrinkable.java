package net.jqwik.properties.newShrinking;

import java.util.*;
import java.util.stream.*;

class ChooseValueShrinkable<T> extends AbstractShrinkable<T> {

	private final List<T> values;

	ChooseValueShrinkable(T value, List<T> values) {
		super(value);
		this.values = values;
	}

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.of(values.indexOf(value()));
	}

	@Override
	public Set<NShrinkable<T>> shrinkCandidatesFor(NShrinkable<T> shrinkable) {
		int index = values.indexOf(shrinkable.value());
		if (index == 0) {
			return Collections.emptySet();
		}
		return values.subList(0, index) //
					 .stream() //
					 .map(value -> new ChooseValueShrinkable<>(value, values)) //
					 .collect(Collectors.toSet());
	}

}
