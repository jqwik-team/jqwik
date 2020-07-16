package net.jqwik.engine.properties.shrinking;

import net.jqwik.engine.properties.*;

public class PropertyShrinkingResult {
	private final FalsifiedSample sample;
	private final int steps;

	public PropertyShrinkingResult(FalsifiedSample sample, int steps) {
		this.sample = sample;
		this.steps = steps;
	}

	public FalsifiedSample sample() {
		return sample;
	}

	public int steps() {
		return steps;
	}
}
