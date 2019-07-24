package net.jqwik.engine.properties.shrinking;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

public class Unshrinkable<T> implements Shrinkable<T> {
	private final T value;

	public Unshrinkable(T value) {this.value = value;}

	@Override
	public T value() {
		return value;
	}

	@Override
	public ShrinkingSequence<T> shrink(Falsifier<T> falsifier) {
		return ShrinkingSequence.dontShrink(this);
	}

	@Override
	public ShrinkingDistance distance() {
		return ShrinkingDistance.of(0);
	}

	@Override
	public String toString() {
		return JqwikStringSupport.displayString(value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Unshrinkable<?> that = (Unshrinkable<?>) o;

		return Objects.equals(value, that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}
}
