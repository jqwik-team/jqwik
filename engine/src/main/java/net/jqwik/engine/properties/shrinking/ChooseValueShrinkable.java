package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

public class ChooseValueShrinkable<T extends @Nullable Object> extends AbstractValueShrinkable<T> {

	private final List<? extends T> values;

	public ChooseValueShrinkable(T value, List<? extends T> values) {
		super(value);
		this.values = values;
	}

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.of(values.indexOf(value()));
	}

	@Override
	public Stream<Shrinkable<T>> shrink() {
		int index = values.indexOf(this.value());
		if (index == 0) {
			return Stream.empty();
		}
		return values.subList(0, index)
					 .stream()
					 .map(value -> new ChooseValueShrinkable<>(value, values));
	}

	@Override
	public Stream<Shrinkable<T>> grow() {
		int index = values.indexOf(this.value());
		if (index == values.size() - 1) {
			return Stream.empty();
		}
		return values.subList(index + 1, values.size())
					 .stream()
					 .map(value -> new ChooseValueShrinkable<>(value, values));
	}

}
