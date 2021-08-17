package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

public class FalsifiedSampleImpl implements FalsifiedSample {

	private final List<Object> parameters;
	private final List<Shrinkable<Object>> shrinkables;
	private final Optional<Throwable> falsifyingError;
	private final List<String> footnotes;

	public FalsifiedSampleImpl(
		List<Object> parameters,
		List<Shrinkable<Object>> shrinkables,
		Optional<Throwable> falsifyingError,
		List<String> footnotes
	) {
		this.parameters = parameters;
		this.shrinkables = shrinkables;
		this.falsifyingError = falsifyingError;
		this.footnotes = footnotes;
	}

	@Override
	public List<Object> parameters() {
		return parameters;
	}

	@Override
	public List<Shrinkable<Object>> shrinkables() {
		return shrinkables;
	}

	@Override
	public Optional<Throwable> falsifyingError() {
		return falsifyingError;
	}

	@Override
	public List<String> footnotes() {
		return footnotes;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (!(o instanceof FalsifiedSample)) return false;
		FalsifiedSample that = (FalsifiedSample) o;
		return parameters.equals(that.parameters()) &&
				   shrinkables.equals(that.shrinkables()) &&
				   falsifyingError.equals(that.falsifyingError());
	}

	@Override
	public int hashCode() {
		return Objects.hash(parameters);
	}

}
