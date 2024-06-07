package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

import org.jspecify.annotations.*;

public class Unshrinkable<T extends @Nullable Object> implements Shrinkable<T> {
	private final ShrinkingDistance distance;
	private final Supplier<? extends T> valueSupplier;

	public Unshrinkable(Supplier<? extends T> valueSupplier, ShrinkingDistance distance) {
		this.valueSupplier = valueSupplier;
		this.distance = distance;
	}

	@Override
	public T value() {
		return valueSupplier.get();
	}

	@Override
	public Stream<Shrinkable<T>> shrink() {
		return Stream.empty();
	}

	@Override
	public ShrinkingDistance distance() {
		return distance;
	}

	@Override
	public String toString() {
		return JqwikStringSupport.displayString(value());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Unshrinkable<?> that = (Unshrinkable<?>) o;

		return Objects.equals(value(), that.value());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value());
	}
}
