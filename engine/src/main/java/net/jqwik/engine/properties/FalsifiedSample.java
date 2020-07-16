package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;

/**
 * Preparing better reporting by collecting all information about a falsified sample
 * in one place.
 */
public class FalsifiedSample {

	private final List<Object> parameters;
	private final List<Shrinkable<Object>> shrinkables;
	private final Optional<Throwable> falsifyingError;

	public FalsifiedSample(List<Object> parameters, List<Shrinkable<Object>> shrinkables, Optional<Throwable> falsifyingError) {
		this.parameters = parameters;
		this.shrinkables = shrinkables;
		this.falsifyingError = falsifyingError;
	}

	public List<Object> parameters() {
		return parameters;
	}

	public List<Shrinkable<Object>> shrinkables() {
		return shrinkables;
	}

	public Optional<Throwable> falsifyingError() {
		return falsifyingError;
	}
}
