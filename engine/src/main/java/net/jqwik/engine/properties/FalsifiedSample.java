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

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FalsifiedSample that = (FalsifiedSample) o;
		return parameters.equals(that.parameters) &&
				   shrinkables.equals(that.shrinkables) &&
				   falsifyingError.equals(that.falsifyingError);
	}

	@Override
	public int hashCode() {
		return Objects.hash(parameters);
	}

	public int size() {
		return shrinkables.size();
	}
}
