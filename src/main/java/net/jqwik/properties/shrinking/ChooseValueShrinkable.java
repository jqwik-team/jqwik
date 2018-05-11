package net.jqwik.properties.shrinking;

import net.jqwik.api.*;

import java.util.*;
import java.util.stream.*;

public class ChooseValueShrinkable<T> extends AbstractShrinkable<T> {

	private final List<T> values;

	public ChooseValueShrinkable(T value, List<T> values) {
		super(value);
		this.values = values;
	}

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.of(values.indexOf(value()));
	}

	@Override
	public Set<Shrinkable<T>> shrinkCandidatesFor(Shrinkable<T> shrinkable) {
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
