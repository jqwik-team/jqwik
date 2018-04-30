package net.jqwik.properties.newShrinking;

import java.util.*;

public class PropertyShrinkingResult {
	private final List<NShrinkable> parameters;
	private final int steps;
	private final Throwable throwable;

	public PropertyShrinkingResult(List<NShrinkable> parameters, int steps, Throwable throwable) {
		this.parameters = parameters;
		this.steps = steps;
		this.throwable = throwable;
	}

	public List<NShrinkable> parameters() {
		return parameters;
	}

	public Optional<Throwable> throwable() {
		return Optional.ofNullable(throwable);
	}

	public int steps() {
		return steps;
	}
}
