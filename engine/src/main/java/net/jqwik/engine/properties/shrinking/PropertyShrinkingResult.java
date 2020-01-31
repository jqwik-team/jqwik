package net.jqwik.engine.properties.shrinking;

import java.util.*;

public class PropertyShrinkingResult {
	private final List<Object> values;
	private final int steps;
	private final Throwable throwable;

	public PropertyShrinkingResult(List<Object> values, int steps, Throwable throwable) {
		this.values = values;
		this.steps = steps;
		this.throwable = throwable;
	}

	public List<Object> values() {
		return values;
	}

	public Optional<Throwable> throwable() {
		return Optional.ofNullable(throwable);
	}

	public int steps() {
		return steps;
	}
}
