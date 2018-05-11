package net.jqwik.properties.shrinking;

import net.jqwik.api.*;
import net.jqwik.support.*;

public class NUnshrinkable<T> implements NShrinkable<T> {
	private final T value;

	public NUnshrinkable(T value) {this.value = value;}

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
}
